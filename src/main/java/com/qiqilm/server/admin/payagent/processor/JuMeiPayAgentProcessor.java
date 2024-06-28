package com.qiqilm.server.admin.payagent.processor;


import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeFeiYueType;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.RoundingMode;
import java.util.*;

@Repository(value = ConstantsPayAgent.JUMEI + "PayAgentProcessor")
@Log4j2
public class JuMeiPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        Map<String, String> bodyMap = new HashMap<>(6);
        bodyMap.put("pid", payAgentPlatform.getMerId());
        bodyMap.put("money", withdrawLog.getWithdrawMoney().setScale(2, RoundingMode.HALF_UP).toString());
        bodyMap.put("sn", withdrawLog.getOrderNo());
        bodyMap.put("bc_name", withdrawLog.getBankName().trim());
        bodyMap.put("bc_num", withdrawLog.getBankAccount().trim());
        bodyMap.put("bc_user", withdrawLog.getBankUserName().trim());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String sign = createSign(bodyMap, signMd5);
        bodyMap.put("sign", sign);
        bodyMap.put("notify_url", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
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
            reqPayAgent.setFailReason("聚美代付下单报错原因:" + e);
        }
        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            String code = resultMap.getOrDefault("code", "").toString();
            if ("200".equals(code)) {
                log.info("聚美代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("message", "").toString());

                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn("聚美代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String sn = requestMap.getOrDefault("sn", "").toString();
        String out_sn = requestMap.getOrDefault("out_sn", "").toString();
        String money = requestMap.getOrDefault("money", "").toString();
        String trade_status = requestMap.getOrDefault("trade_status", "").toString();
        String encryption = requestMap.remove("encryption").toString();

        Map<String, String> bodyMap = new HashMap(4);
        bodyMap.put("sn", sn);
        bodyMap.put("out_sn", out_sn);
        bodyMap.put("money", money);
        bodyMap.put("trade_status", trade_status);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String sign = createSign(bodyMap, signMd5);

        log.info("聚美代付回调签名字符串:" + encryption + "_" + sign);
        if (encryption.equalsIgnoreCase(sign)) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(out_sn);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", out_sn);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", out_sn);
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(out_sn);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "TRADE_SUCCESS".equals(trade_status));
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
        Map<String, String> paramsMap = new HashMap<>(2);
        paramsMap.put("pid", payAgentPlatform.getMerId());
        paramsMap.put("sn", withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String sign = createSign(paramsMap, signMd5);
        paramsMap.put("sign", sign);

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(paramsMap);
        log.warn(JsonUtil.object2Json(requestMap));
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
            log.info("聚美代付查询结果- result:{}", JsonUtil.object2Json(resultMap));
            if (!CollectionUtils.isEmpty(resultMap)) {
                String code = resultMap.getOrDefault("code", "").toString();
                if ("200".equals(code)) {
                    Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
                    int statusType = Integer.parseInt(dataMap.getOrDefault("status", "").toString());
                    if (statusType > 0) {
                        // status 4代付中 5代付失败 6代付成功
                        // statusType  0：处理中 1:审核成功 3:驳回
                        int status = 4;
                        if (statusType == 1) {
                            status = 6;
                        } else if (statusType == 3) {
                            status = 5;
                        }
                        payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, statusType);
                    }
                }
                return resultMap.getOrDefault("msg", "").toString();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "聚美代付查询失败,订单号:"+withdrawLog.getOrderNo();
    }

    public static String createSign(Map<String, String> paramsMap, String signPrivate) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!StringUtils.isEmpty(value)) {
                sb.append("&").append(key).append("=").append(value);
            }
        }
        String StringA = sb.toString().replaceFirst("&", "");
        String StringTemp = StringA + "&key=" + signPrivate;
        String signValue = DigestUtils.md5Hex(StringTemp).toUpperCase();
        return signValue;
    }
}