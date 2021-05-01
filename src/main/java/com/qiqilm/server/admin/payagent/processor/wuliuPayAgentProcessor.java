package com.qiqilm.server.admin.payagent.processor;


import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import java.util.*;

@Repository(value = ConstantsPayAgent.WULIU + "PayAgentProcessor")
@Log4j2
public class wuliuPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("orderNo", withdrawLog.getOrderNo());
        bodyMap.put("timestamp", System.currentTimeMillis() + "");
        bodyMap.put("name", withdrawLog.getBankUserName().trim());
        bodyMap.put("money", withdrawLog.getWithdrawMoney().setScale(2,
                BigDecimal.ROUND_HALF_UP).toString());
        bodyMap.put("bankNumber", withdrawLog.getBankAccount().trim());
        bodyMap.put("bankName", withdrawLog.getBankName().trim());

        String paramJson = JsonUtil.object2Json(bodyMap);
        // RSA 2048 PKCS8 公钥加密
        String encrypt = RSACoder.encryptByPublicKeyHex(paramJson, payAgentPlatform.getSignPublicKey());

        Map<String, String> params = new HashMap<>();
        params.put("merchantNo", payAgentPlatform.getMerId());
        params.put("encrypt", encrypt);

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(params);
        log.warn(JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

        String res = null;
        try {
            res = restTemplate.postForObject(payAgentPlatform.getPayOrderAddr(), httpEntity, String.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            reqPayAgent.setFailReason(e.getMessage());
        }
        log.warn("五六代付下单结果:" + res);
        if (StringUtils.isNoneBlank(res)) {
            Map<String, Object> resultMap = JsonUtil.json2Map(res);
            if ("200".equals(resultMap.getOrDefault("code", "").toString())) {
                log.info("五六代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("msg", "").toString());
            }
        }
        log.warn("五六代付订单提交失败 - result:{}", res);

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
        if (RSACoder.verifySha256Rsa(signData,payAgentPlatform.getSignPublicKey(),sign)) {
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
    public void queryOrderPay(PayAgentLog payAgentLog) throws Exception {
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(payAgentLog.getWithdrawOrderNo());
        PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById(payAgentLog.getPayAgentPlatId());
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("orderNo", withdrawLog.getOrderNo());
        bodyMap.put("timestamp", System.currentTimeMillis() + "");

        String paramJson = JsonUtil.object2Json(bodyMap);
        // RSA 2048 PKCS8 公钥加密
        String encrypt = RSACoder.encryptByPublicKeyHex(paramJson, payAgentPlatform.getSignPublicKey());

        Map<String, String> params = new HashMap<>();
        params.put("merchantNo", payAgentPlatform.getMerId());
        params.put("encrypt", encrypt);

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(params);
        log.warn(JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

        String res = null;
        try {
            res = restTemplate.postForObject(payAgentPlatform.getPayOrderQueryAddr(), httpEntity, String.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.warn("五六代付查询结果:" + res);
        if (StringUtils.isNoneBlank(res)) {
            Map<String, Object> resultMap = JsonUtil.json2Map(res);
            if ("200".equals(resultMap.getOrDefault("code", "").toString())) {
                log.info("代付订单查询成功");
                Map<String, Object> dataMap = (Map<String, Object>) resultMap.getOrDefault("data", new HashMap<>());
                int state = Integer.parseInt(dataMap.getOrDefault("state", -1).toString());

                // status 4代付中 5代付失败 6代付成功
                // state 1处理中 2支付成功 3支付失败
                int status = 4;
                if (state == 2) {
                    status = 6;
                } else if (status == 3) {
                    status = 5;
                }
                log.warn("state:{}", state);
                payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, state);
                return;
            }
        }
        log.warn("代付订单查询失败 - result:{}", res);
    }

    public static void main(String[] args) {
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("orderNo", "TX2021041718060950325");
        bodyMap.put("timestamp", System.currentTimeMillis() + "");
        bodyMap.put("name", "哈哈");
        bodyMap.put("money", 100.00f);
        bodyMap.put("bankNumber", "420516354686126448");
        bodyMap.put("bankName", "建设银行");

        String paramJson = JsonUtil.object2Json(bodyMap);
        // 使用非对称加密加密此bodyMap
        String encrypt = null;
        try {
            encrypt = RSACoder.encryptByPublicKeyHex(paramJson, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAukQXwIckf6IBvXhdDr+BRwuANluQvJHYFzBfbY8PT0EwI3l6rt3Tq/kV0En/MHBjlUg8p2YHqFGD+OHV0TWsS837W6a0ITVaaVZrJNdW/HIl8W9WLY1FRCZnYLer5jW8uvYi1D3o3SrPZGTdH11hA/HGV+Bpu8KUHTsb3lxbz+zKVI2n0fhW3jmiMMY8eGqcnkPdzjiqexqHaJji/3fwtu9sAm9JYEbWOqPpn6G9s3zLQLgXXMiDN2CzE579A7LpQkmQREvezURNq/H7ffi7Qq2tecNQXhBf7O7HVOj3AxR7owr862rXuzRrPIknGFbXlqtsw4d7faBlwa9wG5mgyQIDAQAB");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> params = new HashMap<>();
        params.put("merchantNo", "SN20210428220244");
        params.put("encrypt", encrypt);

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(params);
        log.warn(JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

        String res = null;
        Map<String, String> resultMap = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            res = restTemplate.postForObject("https://liufupay8.com/order/betWithoutCheck", httpEntity, String.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.warn("五六代付下单结果:" + JsonUtil.object2Json(resultMap));
    }
}
