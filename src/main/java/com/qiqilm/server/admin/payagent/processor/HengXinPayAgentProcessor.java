package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeHengXinType;
import com.qiqilm.server.admin.exception.BaseException;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.AuthUtil;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;

@Repository(value = ConstantsPayAgent.HENG_XIN + "PayAgentProcessor")
@Log4j2
public class HengXinPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        BankCodeHengXinType bankCodeType = BankCodeHengXinType.getCodeByDesc(withdrawLog.getBankName());
        if (bankCodeType == null) {
            payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            log.warn("此代付无法支持的银行类型 - 银行类型:{}", withdrawLog.getBankName());
            throw new BusinessException("此代付无法支持的银行类型：" + withdrawLog.getBankName());
        }
        withdrawLog.setBankCode(bankCodeType.name());

        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("merOrderNo", withdrawLog.getOrderNo());
        bodyMap.put("amount", withdrawLog.getWithdrawMoney().setScale(0, RoundingMode.HALF_UP));
        bodyMap.put("notifyUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put("bankCode", withdrawLog.getBankCode());
        bodyMap.put("submitTime", reqPayAgent.getCurrentTime().getTime());
        bodyMap.put("bankAccountNo", withdrawLog.getBankAccount().trim());
        bodyMap.put("bankAccountName", withdrawLog.getBankUserName().trim());
        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String signStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;

        String sign = DigestUtils.md5Hex(signStr).toUpperCase();
        bodyMap.put("sign", sign);

        String orderJson = JsonUtil.object2Json(bodyMap);
        log.info("非对称加密加密前:" + orderJson);

        // 使用非对称加密加密此dataMap
        String data = RSACoder.encryptByPublicKey(orderJson, payAgentPlatform.getSignPublicKey());
        log.info("非对称加密加密后:" + data);
        // 封装请求协议
        Map<String, String> dataMap = new LinkedHashMap<>();
        dataMap.put("merId", payAgentPlatform.getMerId());
        dataMap.put("version", "1.1");
        dataMap.put("data", data);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(dataMap, httpHeaders);

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute( payAgentPlatform.getPayOrderAddr(), HttpMethod.POST,
                    restTemplate.httpEntityCallback( httpEntity ), response -> {
                        InputStream bodyStream = response.getBody();
                        String      text;
                        try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                            text = CharStreams.toString( reader );
                        }
                        return JsonUtil.json2Map( text );
                    } );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            if ("200".equals(resultMap.getOrDefault("code", "").toString())) {
                log.info("恒星代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("message", "").toString());

                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn("恒星代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        if (this.checkWhiteIp(payAgentPlatform.getPlatWhiteIpList(), realIp)) {
            log.warn("请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json(requestMap));
            return "fail";
        }

        String dataStr = requestMap.getOrDefault("data", "").toString();

        String data = RSACoder.decryptByPrivateKey(dataStr, payAgentPlatform.getSignPrivateKey());
        log.info(data);
        Map<String, Object> resultMap = JsonUtil.json2Map(data);

        String merOrderNo = resultMap.getOrDefault("merOrderNo", "").toString();
        String orderNo = resultMap.getOrDefault("orderNo", "").toString();
        int orderState = Integer.parseInt(resultMap.getOrDefault("orderState", -1).toString());

        // 解密后对签名验证
        SortedMap<String, Object> signMap = new TreeMap<>();
        signMap.put("merOrderNo", merOrderNo);
        signMap.put("orderState", orderState);
        signMap.put("orderNo", orderNo);
        signMap.put("amount", resultMap.get("amount"));

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String signStr = this.assemblyUrl(signMap) + "&key=" + signMd5;
        log.info(signStr);
        String sign = DigestUtils.md5Hex(signStr);
        log.warn(sign + " : " + resultMap.get("sign").toString());
        if (sign.equalsIgnoreCase(resultMap.get("sign").toString())) {
            if (orderState > 0) {
                MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(merOrderNo);
                if (withdrawLog == null) {
                    log.error("提现相关记录丢失 - merOrderNo:{}", merOrderNo);
                    return "fail";
                }
                if (withdrawLog.getStatus() == 6) {
                    log.error("已有代付记录 - merOrderNo:{}", merOrderNo);
                    return "success";
                }
                PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(merOrderNo);
                payAgentService.processOrderPay(withdrawLog, payAgentLog, orderNo, payAgentPlatform, orderState == 1);
            }
            return "success";
        }
        return "fail";
    }

    @Override
    public Map<String, Object> reverseCheckOrderPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap,
                                                    String realIp) throws Exception {
        if (this.checkWhiteIp(payAgentPlatform.getPlatWhiteIpList(), realIp)) {
            log.warn("请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json(requestMap));
            return null;
        }

        SortedMap<String, Object> requestSignMap = new TreeMap<>(requestMap);
        String sign = requestSignMap.remove("sign").toString();
        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String signStr = this.assemblyUrl(requestSignMap) + "&key=" + signMd5;
        String mySign = DigestUtils.md5Hex(signStr);

        String merId = requestSignMap.getOrDefault("merId", "").toString();
        String merOrderNo = requestSignMap.getOrDefault("merOrderNo", "").toString();
        BigDecimal amount = new BigDecimal(requestSignMap.getOrDefault("amount", "0").toString());
        String bankAccountNo = requestSignMap.getOrDefault("bankAccountNo", "").toString();

        SortedMap<String, Object> signMap = new TreeMap<>();
        signMap.put("submitTime", String.valueOf(System.currentTimeMillis()));
        signMap.put("code", "1001");
        signMap.put("message", "签名错误");
        signMap.put("merId", payAgentPlatform.getMerId());
        signMap.put("merOrderNo", merOrderNo);
        if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(sign, mySign)) {
            MemberWithdrawLog memberWithdrawLog = withdrawLogMapper.selectByOrderNo(merOrderNo);
            if (memberWithdrawLog == null) {
                signMap.put("code", "1002");
                signMap.put("message", "订单不存在");
                return signMap;
            } else if (amount.compareTo(memberWithdrawLog.getWithdrawMoney()) != 0) {
                signMap.put("code", "1004");
                signMap.put("message", "金额不匹配");
                return signMap;
            } else if (!bankAccountNo.equals(memberWithdrawLog.getBankAccount())) {
                signMap.put("code", "1003");
                signMap.put("message", "银行卡号不匹配");
                return signMap;
            } else if (!merId.equals(payAgentPlatform.getMerId())) {
                signMap.put("code", "9999");
                signMap.put("message", "商户号错误");
            } else {
                signMap.put("code", "0");
                signMap.put("message", "成功");
            }
        }
        String resultSignStr = this.assemblyUrl(signMap) + "&key=" + signMd5;
        signMap.put("sign", DigestUtils.md5Hex(resultSignStr));
        return signMap;
    }

    @Override
    public String queryOrderPay(PayAgentLog payAgentLog) throws Exception {
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(payAgentLog.getWithdrawOrderNo());
        PayAgentPlatform payAgentPlatform =
                payAgentPlatformMapper.selectPayAgentPlatformById(payAgentLog.getPayAgentPlatId());
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("merOrderNo", withdrawLog.getOrderNo());
        bodyMap.put("submitTime", withdrawLog.getUpdateTime().getTime());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        // 生成签名信息
        String signStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(signStr);
        bodyMap.put("sign", sign);

        // 使用非对称加密加密此dataMap
        String data = RSACoder.encryptByPublicKey(JsonUtil.object2Json(bodyMap), payAgentPlatform.getSignPublicKey());

        // 封装请求协议
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("merId", payAgentPlatform.getMerId());
        dataMap.put("version", "1.1");
        dataMap.put("data", data);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(dataMap, httpHeaders);

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute( payAgentPlatform.getPayOrderQueryAddr(), HttpMethod.POST,
                    restTemplate.httpEntityCallback( httpEntity ), response -> {
                        InputStream bodyStream = response.getBody();
                        String      text;
                        try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                            text = CharStreams.toString( reader );
                        }
                        return JsonUtil.json2Map( text );
                    } );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("恒星代付查询结果 - result:{}", JsonUtil.object2Json(resultMap));
        if (!CollectionUtils.isEmpty(resultMap)) {
            if ("200".equals(resultMap.getOrDefault("code", "").toString())) {
                Map<String, Object> resultDataMap = (Map<String, Object>) resultMap.getOrDefault("data", new HashMap<>());
                int orderState = Integer.parseInt(resultDataMap.getOrDefault("orderState", 0).toString());
                // status 4代付中5代付失败6代付成功
                // orderState (0=处理中，1=成功，2=失败)
                int status = 4;
                switch (orderState) {
                    case 1:
                        status = 6;
                        break;
                    case 2:
                        status = 5;
                        break;
                    default:
                        break;
                }
                payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderState);
            }
            return resultMap.getOrDefault("msg", "").toString();
        }
        return "恒星代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
