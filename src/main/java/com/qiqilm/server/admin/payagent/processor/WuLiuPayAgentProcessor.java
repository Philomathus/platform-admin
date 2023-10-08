package com.qiqilm.server.admin.payagent.processor;


import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
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
import java.util.*;

@Repository(value = ConstantsPayAgent.WULIU + "PayAgentProcessor")
@Log4j2
public class WuLiuPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("orderNo", withdrawLog.getOrderNo());
        bodyMap.put("timestamp", System.currentTimeMillis() + "");
        bodyMap.put("name", withdrawLog.getBankUserName().trim());
        bodyMap.put("money", withdrawLog.getWithdrawMoney().setScale(2, BigDecimal.ROUND_HALF_UP));
        bodyMap.put("bankNumber", withdrawLog.getBankAccount().trim());
        bodyMap.put("bankName", withdrawLog.getBankName().trim());

        String paramJson = JsonUtil.object2Json(bodyMap);
        // RSA 2048 PKCS8 公钥加密
        String encrypt = RSACoder.encryptByPublicKey(paramJson, payAgentPlatform.getSignPublicKey());

        Map<String, String> params = new HashMap<>();
        params.put("merchantNo", payAgentPlatform.getMerId());
        params.put("encrypt", encrypt);

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(params);
        log.warn(JsonUtil.object2Json(requestMap));
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
            reqPayAgent.setFailReason("五六代付下单报错原因:" + e);
        }
        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            if ("200".equals(resultMap.getOrDefault("code", "").toString())) {
                log.info("五六代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("msg", "").toString());

                payAgentService.callBackOrder( withdrawLog,payAgentPlatform );
            }
        }
        log.warn("五六代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        if (this.checkWhiteIp(payAgentPlatform.getPlatWhiteIpList(), realIp)) {
            log.warn("请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json(requestMap));
            return "fail";
        }
        String sign = requestMap.remove("sign").toString();
        SortedMap<String, Object> signMap = new TreeMap<>(requestMap);
        String signData = JsonUtil.object2Json(signMap);

        //RSA 2048 SHA256 公钥验签
        if (RSACoder.verifySha256Rsa(signData,payAgentPlatform.getSignPublicKey(), sign)) {
            String state = signMap.getOrDefault("state", "").toString();
            String orderNo = signMap.getOrDefault("orderNo", "").toString();
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(orderNo);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", orderNo);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", orderNo);
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(orderNo);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, orderNo, payAgentPlatform,
                    "2".equals(state));
            return "SUCCESS";
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
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("orderNo", withdrawLog.getOrderNo());
        bodyMap.put("timestamp", System.currentTimeMillis() + "");

        String paramJson = JsonUtil.object2Json(bodyMap);
        // RSA 2048 PKCS8 公钥加密
        String encrypt = RSACoder.encryptByPublicKey(paramJson, payAgentPlatform.getSignPublicKey());

        Map<String, String> params = new HashMap<>();
        params.put("merchantNo", payAgentPlatform.getMerId());
        params.put("encrypt", encrypt);

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(params);
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.warn("五六代付查询结果:" + JsonUtil.object2Json(resultMap));
        if (!CollectionUtils.isEmpty(resultMap)) {
            if ("200".equals(resultMap.getOrDefault("code", "").toString())) {
                Map<String, Object> dataMap = (Map<String, Object>) resultMap.getOrDefault("data", new HashMap<>());
                int state = Integer.parseInt(dataMap.getOrDefault("state", -1).toString());
                // status 4代付中 5代付失败 6代付成功
                // state 1处理中 2支付成功 3支付失败
                if (state == 2 || state == 3) {
                    int status = 4;
                    if (state == 2) {
                        status = 6;
                    } else if (status == 3) {
                        status = 5;
                    }
                    log.warn("state:{}", state);
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, state);
                }
            }
            return resultMap.getOrDefault("msg", "").toString();
        }
        return "五六代付查询失败,订单号:"+withdrawLog.getOrderNo();
    }
}
