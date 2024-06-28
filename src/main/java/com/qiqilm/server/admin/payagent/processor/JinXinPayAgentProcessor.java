package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeJinXinType;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Hex;
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
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Repository(value = ConstantsPayAgent.JINXIN + "PayAgentProcessor")
@Log4j2
public class JinXinPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        BankCodeJinXinType bankCodeType = BankCodeJinXinType.getCodeByDesc(withdrawLog.getBankName());
        if (bankCodeType == null) {
            payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            log.warn(payAgentPlatform.getName()+"代付无法支持的银行类型 - 银行类型:{}", withdrawLog.getBankName());
            throw new BusinessException(payAgentPlatform.getName()+"代付无法支持的银行类型：" + withdrawLog.getBankName());
        }

        TreeMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("merchant", payAgentPlatform.getMerId());
        bodyMap.put("requestReference", withdrawLog.getOrderNo());
        bodyMap.put("merchantBank", bankCodeType);
        bodyMap.put("merchantBankCardRealName", withdrawLog.getBankUserName().trim());
        bodyMap.put("merchantBankCardAccount", withdrawLog.getBankAccount().trim());
        bodyMap.put("merchantBankCardProvince", "广东省");
        bodyMap.put("merchantBankCardCity", "深圳市");
        bodyMap.put("merchantBankCardBranch", "深圳支行");
        bodyMap.put("amount", withdrawLog.getWithdrawMoney().setScale(2, BigDecimal.ROUND_HALF_UP));
        bodyMap.put("callback", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String sign = sign(bodyMap,signMd5);
        bodyMap.put("sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(bodyMap);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

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
            reqPayAgent.setFailReason(payAgentPlatform.getName()+"代付下单报错原因:" + e);
        }
        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            String code = resultMap.getOrDefault("code", "").toString();
            String success = resultMap.getOrDefault("success", "").toString();
            if ("0".equals(code) && "true".equals(success)) {
                Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
                if (!CollectionUtils.isEmpty(dataMap)) {
                    //代付状态: -1提现失败 0申请中 1提现成功 2:处理中 (注：只有值为-1时才可以回滚数据，其它状态值视为处理中，不要回滚数据)
                    String defrayStatus = dataMap.getOrDefault("defrayStatus", "").toString();
                    if (!"-1".equals(defrayStatus)) {
                        log.info(payAgentPlatform.getName()+"代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                        return true;
                    } else {
                        reqPayAgent.setFailReason(resultMap.getOrDefault("message", "").toString());
                        payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
                    }
                }
            }
        }
        log.info(payAgentPlatform.getName()+"代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String sign = requestMap.remove("sign").toString();
        String success = requestMap.getOrDefault("success ", "").toString();
        TreeMap<String, Object> bodyMap = new TreeMap<>(requestMap);
        bodyMap.remove("remark");

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );

        String signStr = sign(bodyMap,signMd5);

        log.info(payAgentPlatform.getName()+"代付回调签名字符串:" + sign + "_" + signStr);
        if (sign.equalsIgnoreCase(signStr)) {
            String requestReference  = (String) requestMap.get("requestReference ");
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(requestReference );
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", requestReference );
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", requestReference );
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(requestReference );
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "true".equals(success));
            return "success";
        }
        return "fail";
    }

    @Override
    public Map<String, Object> reverseCheckOrderPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap,
                                                    String realIp) throws Exception {
        return null;
    }

    @Override
    public String queryOrderPay(PayAgentLog payAgentLog) throws Exception {
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(payAgentLog.getWithdrawOrderNo());
        PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById(payAgentLog.getPayAgentPlatId());
        TreeMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("merchant", payAgentPlatform.getMerId());
        bodyMap.put("requestReference", withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String sign = sign(bodyMap,signMd5);
        bodyMap.put("sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(bodyMap);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

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
            log.info(payAgentPlatform.getName()+"代付查询结果- result:{}", JsonUtil.object2Json(resultMap));
            if (!CollectionUtils.isEmpty(resultMap)) {
                String code = resultMap.getOrDefault("code", "").toString();
                String success = resultMap.getOrDefault("success", "").toString();
                if ("0".equals(code) && "true".equals(success)) {
                    Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
                    int statusType = Integer.parseInt(dataMap.getOrDefault("status", "").toString());
                    // status 4代付中 5代付失败 6代付成功
                    //statusType: 0成功 1失败 15退回(退回意味着平台无法再处理这笔代付单了，驳回此订单任务给商户，商户可自行决定是否当作失败处理)
                    if (statusType == 0 || statusType == 1 || statusType == 15) {
                        int status = 4;
                        if (statusType == 0) {
                            status = 6;
                        } else {
                            status = 5;
                        }
                        payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, statusType);
                    }
                    return resultMap.getOrDefault("msg", "").toString();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return payAgentPlatform.getName()+"代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }

    // 大写
    public static String hex(byte[] data) {
        return Hex.encodeHexString(data, false);
    }

    public static byte[] md5(String data) {
        return DigestUtils.md5(data.getBytes(StandardCharsets.UTF_8));
    }

    public static String sign(String data) {
        log.info("签名前字符串"+data);
        return hex(md5(data));
    }

    public static String sign(TreeMap<String, ?> data, String key) {
        Set<String> names = data.keySet();
        StringBuffer sb = new StringBuffer();
        for (String name : names) {
            sb.append(name).append("=").append(data.get(name)).append("&");
        }
        sb.append("key=").append(key);
        return sign(sb.toString());
    }
}
