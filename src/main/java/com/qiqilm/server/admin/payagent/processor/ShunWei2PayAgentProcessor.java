package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeShunWeiType;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.*;
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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Repository(value = ConstantsPayAgent.SHUN_WEI2 + "PayAgentProcessor")
@Log4j2
public class ShunWei2PayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        BankCodeShunWeiType bankCodeType = BankCodeShunWeiType.getCodeByDesc(withdrawLog.getBankName());
        if (bankCodeType == null) {
            bankCodeType = BankCodeShunWeiType.QTBC;
        }
        withdrawLog.setBankCode(bankCodeType.name());

        Map<String, String> dataMap = new TreeMap<>();
        dataMap.put("client_num", payAgentPlatform.getMerId());
        dataMap.put("order_num", withdrawLog.getOrderNo());
        dataMap.put("amount", withdrawLog.getWithdrawMoney().multiply(new BigDecimal(100)).setScale(0,
                BigDecimal.ROUND_HALF_EVEN).toString());
        dataMap.put("bank_account_name", withdrawLog.getBankUserName().trim());
        dataMap.put("bank_account_no", withdrawLog.getBankAccount().trim());
        dataMap.put("bank_code", withdrawLog.getBankCode());
        String randStr = this.generateRandNum(dataMap.size() + 1);
        dataMap.put("random_str", randStr);
        // 签名
        Map<String, String> paramMap = paramSort(dataMap, randStr);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String sign = DigestUtils.md5Hex(JsonUtil.object2Json(paramMap).concat(signMd5));

        paramMap.put("request_sign", sign);
        paramMap.put("callback_url", sysConfigCacheUtil.getConf("payAgentNotifyUrl") + payAgentPlatform.getCode());

        // 参数加密
        String encryptData = RSACoder.encryptByPublicKey(JsonUtil.object2Json(paramMap),
                payAgentPlatform.getSignPublicKey());
        // 请求参数封装
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("request_body", URLEncoder.encode(encryptData, "utf-8"));
        params.add("interface_version", DigestUtils.md5Hex("1.0.0".concat(payAgentPlatform.getHeaderKey())));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("security_header_key", payAgentPlatform.getHeaderKey());
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(null, httpHeaders);

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(payAgentPlatform.getPayOrderAddr())
                .queryParams(params).build();

        Map<String, String> resultMap = null;
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
            if (e instanceof HttpServerErrorException) {
                reqPayAgent.setFailReason("三方网络异常:" + e.getMessage());

                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
                return false;
            }
        }
        log.info(payAgentPlatform.getName() + "下单结果{},订单号:{}", JsonUtil.object2Json(resultMap), withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            if ("200".equals(resultMap.get("state_code"))) {
                resultMap.remove("state_code");
                resultMap.remove("message");
                String resultSign = resultMap.remove("sign");
                String randNum = resultMap.get("random_str");
                Map<String, String> param = paramSort(resultMap, randNum);
                String temp = JsonUtil.object2Json(param);
                String reSign = DigestUtils.md5Hex(temp.concat(signMd5));
                if (!org.apache.commons.lang3.StringUtils.equalsIgnoreCase(resultSign, reSign)) {
                    return false;
                }
                log.info("顺为代付订单提交成功，orderNo：{}", withdrawLog.getOrderNo());
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("message", ""));
                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn("顺为代付订单提交失败 - orderNo:{},result:{}", withdrawLog.getOrderNo(), JsonUtil.object2Json(resultMap));
        return false;
    }

    private String generateRandNum(int size) {
        StringBuilder randStr = new StringBuilder();
        Random randDom = new Random();
        do {
            String tmpChar = String.valueOf(randDom.nextInt(size));
            if (randStr.indexOf(tmpChar) == -1) {
                randStr.append(tmpChar);
            }
        }
        while (randStr.length() < size);
        return randStr.toString();
    }

    private Map<String, String> paramSort(Map<String, ?> map, String indexStr) {
        Map<String, String> sortMap = new LinkedHashMap<>();
        String[] keys = map.keySet().toArray(new String[]{});
        Arrays.sort(keys);
        char[] indexs = indexStr.toCharArray();
        for (char i : indexs) {
            int index = Integer.parseInt(String.valueOf(i));
            sortMap.put(keys[index], map.get(keys[index]).toString());
        }
        return sortMap;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String data = requestMap.getOrDefault("data", "").toString();
        String str = RSACoder.decryptByPrivateKey(data, payAgentPlatform.getSignPrivateKey());
        Map<String, Object> resultMap = JsonUtil.json2Map(str);
        String reSign = resultMap.remove("sign").toString();
        SortedMap<String, Object> signMap = new TreeMap<>(resultMap);
        Map<String, String> map = paramSort(signMap, signMap.get("random_str").toString());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String sign = DigestUtils.md5Hex(JsonUtil.object2Json(map).concat(signMd5));
        if ((reSign).equalsIgnoreCase(sign)) {
            String order_num = (String) signMap.get("order_num");
            String remit_result = (String) signMap.get("remit_result");

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(order_num);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", order_num);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", order_num);
                return "ok";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(order_num);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "SUCCESS".equals(remit_result));

            return "ok";
        }
        log.info("ShunWei:" + "顺为解密失败");
        return "fail";
    }

    @Override
    public Map<String, Object> reverseCheckOrderPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap,
                                                    String realIp) throws Exception {
        if (this.checkWhiteIp(payAgentPlatform.getPlatWhiteIpList(), realIp)) {
            log.warn("请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json(requestMap));
        }
        log.warn("反查数据:" + JsonUtil.object2Json(requestMap));
        String reSign = requestMap.remove("sign").toString();

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String mySign = this.assemblyUrl(requestMap) + "&key=" + signMd5;
        log.warn(mySign);
        BigDecimal amount = new BigDecimal(requestMap.remove("amount").toString());
        String bankAccountNo = requestMap.remove("bankAccountNo").toString();
        String clientCode = requestMap.get("clientCode").toString();
        String clientOrderNo = requestMap.get("clientOrderNo").toString();
        mySign = DigestUtils.md5Hex(mySign);
        requestMap.put("dateTime", DateFormatUtils.formate(new Date(), DateFormatUtils.TIGHT_PATTERN_DATETIME));
        requestMap.put("sign", "");
        requestMap.put("code", "99");
        if ((reSign).equalsIgnoreCase(mySign)) {
            MemberWithdrawLog memberWithdrawLog = withdrawLogMapper.selectByOrderNo(clientOrderNo);
            if (memberWithdrawLog == null || amount.compareTo(memberWithdrawLog.getWithdrawMoney()) != 0
                    || !bankAccountNo.equals(memberWithdrawLog.getBankAccount())
                    || !clientCode.equals(payAgentPlatform.getMerId())) {
                requestMap.put("msg", "订单不匹配");
                return requestMap;
            }
            requestMap.put("code", "00");
            requestMap.put("msg", "验证成功");

            mySign = this.assemblyUrl(requestMap) + "&key=" + signMd5;
            mySign = DigestUtils.md5Hex(mySign);

            requestMap.put("sign", mySign);
            log.warn(JsonUtil.object2Json(requestMap));
            return requestMap;
        }
        requestMap.put("msg", "验签失败");
        return requestMap;
    }

    @Override
    public String queryOrderPay(PayAgentLog payAgentLog) throws Exception {
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(payAgentLog.getWithdrawOrderNo());
        PayAgentPlatform payAgentPlatform =
                payAgentPlatformMapper.selectPayAgentPlatformById(payAgentLog.getPayAgentPlatId());
        Map<String, String> dataMap = new TreeMap<>();
        dataMap.put("client_num", payAgentPlatform.getMerId());
        dataMap.put("order_num", withdrawLog.getOrderNo());
        String randStr = generateRandNum(dataMap.size() + 1);
        dataMap.put("random_str", randStr);
        Map<String, String> paramMap = paramSort(dataMap, randStr);
        log.info("签名原文串：{}", JsonUtil.object2Json(paramMap));

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String sign = DigestUtils.md5Hex(JsonUtil.object2Json(paramMap).concat(signMd5));
        paramMap.put("request_sign", sign);
        // 参数加密
        String encryptData = RSACoder.encryptByPublicKey(JsonUtil.object2Json(paramMap),
                payAgentPlatform.getSignPublicKey());
        // 请求参数封装
        Map<String, String> params = new HashMap<>();
        params.put("request_body", URLEncoder.encode(encryptData, "utf-8"));
        params.put("interface_version", DigestUtils.md5Hex("1.0.0".concat(payAgentPlatform.getHeaderKey())));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("security_header_key", payAgentPlatform.getHeaderKey());
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(new HashMap<>(), httpHeaders);

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(params);
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(payAgentPlatform.getPayOrderQueryAddr())
                .queryParams(requestMap).build();

        Map<String, String> resultMap = null;
        try {
            resultMap = restTemplate.execute(uriComponents.toUri(), HttpMethod.POST,
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
        log.warn("顺为2代付查询结果 - result:{}", JsonUtil.object2Json(resultMap));
        if (!CollectionUtils.isEmpty(resultMap)) {
            String stateCode = resultMap.remove("state_code");
            if (org.apache.commons.lang3.StringUtils.equals("200", stateCode)) {
                String resultSign = resultMap.remove("sign");
                Map<String, String> signMap = new TreeMap<>(resultMap);
                String randNum = signMap.getOrDefault("random_str", "");
                Map<String, String> param = paramSort(signMap, randNum);
                String signStr = JsonUtil.object2Json(param);
                String reSign = DigestUtils.md5Hex(signStr.concat(signMd5));
                if (org.apache.commons.lang3.StringUtils.equals(resultSign, reSign)) {
                    String remit_state_code = resultMap.getOrDefault("remit_state_code", "");
                    // status 4代付中5代付失败6代付成功
                    // orderState (0=处理中，1=成功，2=失败)
                    int status = 4;
                    int orderState = 0;
                    if ("SUCCESS".equals(remit_state_code)) {
                        status = 6;
                        orderState = 1;
                    }
                    if ("FAILED".equals(remit_state_code)) {
                        status = 5;
                        orderState = 2;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderState);
                }
            }
            return resultMap.getOrDefault("msg", "");
        }
        return "顺为2代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
