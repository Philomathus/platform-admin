package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeShangYinType;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.AuthUtil;
import com.qiqilm.server.admin.utils.DateFormatUtils;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Repository(value = ConstantsPayAgent.SHANG_YIN + "PayAgentProcessor")
@Log4j2
public class ShangYinPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        BankCodeShangYinType bankCodeType = BankCodeShangYinType.getCodeByDesc(withdrawLog.getBankName());
        if (bankCodeType == null) {
            payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            log.warn("此代付无法支持的银行类型 - 银行类型:{}", withdrawLog.getBankName());
            throw new BusinessException("此代付无法支持的银行类型：" + withdrawLog.getBankName());
        }
        withdrawLog.setBankCode(bankCodeType.name());
        Map<String, String> dataMap = new LinkedHashMap<>();
        dataMap.put("cmd", "transfer");
        dataMap.put("ver", "1.1");
        dataMap.put("payType", "0");
        dataMap.put("mchId", payAgentPlatform.getMerId());
        dataMap.put("mchOrderId", withdrawLog.getOrderNo());
        dataMap.put("payAmt", withdrawLog.getWithdrawMoney().setScale(2,
                BigDecimal.ROUND_HALF_UP).toString());
        dataMap.put("accNo", withdrawLog.getBankAccount().trim());
        dataMap.put("accName", withdrawLog.getBankUserName().trim());
        dataMap.put("accType", "1");
        dataMap.put("fee_Type", "0");
        dataMap.put("urgency", "0");
        dataMap.put("bankCode", withdrawLog.getBankCode());
        dataMap.put("province", "广东省");
        dataMap.put("city", "深圳市");
        dataMap.put("openBank", "南油支行");
        dataMap.put("clientTime", DateFormatUtils.formate(reqPayAgent.getCurrentTime(),
                DateFormatUtils.TIGHT_PATTERN_DATETIME));
        dataMap.put("schTime", "");
        dataMap.put("rmk", "");
        dataMap.put("tel", "");
        dataMap.put("Email", "");
        dataMap.put("smsFlag", "0");
        dataMap.put("bankPayPurpose", "");
        dataMap.put("Leave_word", "");
        dataMap.put("Ext", "");
        dataMap.put("notifyUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        dataMap.put("userId", payAgentPlatform.getHeaderKey());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String signStr = this.assemblyUrl(dataMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(signStr);
        dataMap.put("sign", sign);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        dataMap.forEach(map::add);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, httpHeaders);

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute(payAgentPlatform.getPayOrderAddr(), HttpMethod.POST,
                    restTemplate.httpEntityCallback(httpEntity), response -> {
                        InputStream bodyStream = response.getBody();
                        String text;
                        try (Reader reader = new InputStreamReader(bodyStream)) {
                            text = CharStreams.toString(reader);
                        }
                        return JsonUtil.json2Map(text);
                    });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            reqPayAgent.setFailReason(e.getMessage());
        }
        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            if ("0000".equals(resultMap.getOrDefault("status", "").toString())) {
                log.info(payAgentPlatform.getName()+"订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("msg", "").toString());

                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn(payAgentPlatform.getName()+"订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        if (this.checkWhiteIp(payAgentPlatform.getPlatWhiteIpList(), realIp)) {
            log.warn("请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json(requestMap));
            return "fail";
        }
        String sign = requestMap.remove("sign").toString();
        String bankstate = requestMap.getOrDefault("bankstate", "").toString();
        String orderid = requestMap.getOrDefault("orderid", "").toString();

        Map<String, String> dataMap = new LinkedHashMap<>();
        dataMap.put("cmd", requestMap.getOrDefault("cmd", "").toString());
        dataMap.put("ver", requestMap.getOrDefault("ver", "").toString());
        dataMap.put("apiid", requestMap.getOrDefault("apiid", "").toString());
        dataMap.put("orderid", orderid);
        dataMap.put("accno", requestMap.getOrDefault("accno", "").toString());
        dataMap.put("payamt", requestMap.getOrDefault("payamt", "").toString());
        dataMap.put("bankstate", bankstate);
        dataMap.put("orderstate", requestMap.getOrDefault("orderstate", "").toString());
        dataMap.put("servertime", requestMap.getOrDefault("servertime", "").toString());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        dataMap.put("key", signMd5);

        String mySign = DigestUtils.md5Hex(this.assemblyUrl(dataMap));

        if (mySign.equalsIgnoreCase(sign)) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(orderid);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", orderid);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", orderid);
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(orderid);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, orderid, payAgentPlatform,
                    "1".equals(bankstate));
            return "SUCCESS";
        }

        return null;
    }

    @Override
    public Map<String, Object> reverseCheckOrderPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap,
                                                    String realIp) throws Exception {
        if (this.checkWhiteIp(payAgentPlatform.getPlatWhiteIpList(), realIp)) {
            log.warn("请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json(requestMap));
            return null;
        }
        Map<String, Object> resultMap = new HashMap<>();
        String merchantNo = requestMap.getOrDefault("merchantNo", "").toString();
        String orderNo = requestMap.getOrDefault("orderNo", "").toString();
        MemberWithdrawLog memberWithdrawLog = withdrawLogMapper.selectByOrderNo(orderNo);
        if (memberWithdrawLog == null) {
            resultMap.put("code", "ERROR");
            resultMap.put("message", "订单不存在");
            resultMap.put("orderNo", orderNo);
        } else if (!merchantNo.equals(payAgentPlatform.getMerId())) {
            resultMap.put("code", "ERROR");
            resultMap.put("message", "商户号错误");
            resultMap.put("orderNo", orderNo);
        } else {
            resultMap.put("code", "SUCCESS");
            resultMap.put("message", "信息正确");
            resultMap.put("orderNo", orderNo);
        }
        return resultMap;
    }

    @Override
    public String queryOrderPay(PayAgentLog payAgentLog) throws Exception {
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(payAgentLog.getWithdrawOrderNo());
        PayAgentPlatform payAgentPlatform =
                payAgentPlatformMapper.selectPayAgentPlatformById(payAgentLog.getPayAgentPlatId());
        Map<String, String> dataMap = new LinkedHashMap<>();
        dataMap.put("cmd", "transferquery");
        dataMap.put("ver", "1.2");
        dataMap.put("mchId", payAgentPlatform.getMerId());
        dataMap.put("mchOrderId", withdrawLog.getOrderNo());
        dataMap.put("clientTime", DateFormatUtils.formate(new Date(),
                DateFormatUtils.TIGHT_PATTERN_DATETIME));

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(dataMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        dataMap.put("sign", sign);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        dataMap.forEach(map::add);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, httpHeaders);

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute(payAgentPlatform.getPayOrderQueryAddr(), HttpMethod.POST,
                    restTemplate.httpEntityCallback(httpEntity), response -> {
                        InputStream bodyStream = response.getBody();
                        String text;
                        try (Reader reader = new InputStreamReader(bodyStream)) {
                            text = CharStreams.toString(reader);
                        }
                        return JsonUtil.json2Map(text);
                    });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info(payAgentPlatform.getName()+"查询结果 - result:{}", JsonUtil.object2Json(resultMap));
        if (!CollectionUtils.isEmpty(resultMap)) {
            if ("1".equals(resultMap.getOrDefault("queryStatus", "").toString())) {
                int orderState = Integer.parseInt(resultMap.getOrDefault("orderStatus", 0).toString());
                // status 4代付中5代付失败6代付成功
                // orderState (0待处理，1处理中，2处理成功，3处理失败,4未知)
                int status = 4;
                switch (orderState) {
                    case 2:
                        status = 6;
                        break;
                    case 3:
                        status = 5;
                        break;
                    default:
                        break;
                }
                payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderState);
            }
            return resultMap.getOrDefault("msg", "").toString();
        }
        return payAgentPlatform.getName()+"查询失败,订单号:"+withdrawLog.getOrderNo();
    }
}
