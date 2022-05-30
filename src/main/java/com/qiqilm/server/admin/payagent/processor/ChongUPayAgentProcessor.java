package com.qiqilm.server.admin.payagent.processor;

import com.alibaba.fastjson.JSON;
import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.AuthUtil;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
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

@Repository(value = ConstantsPayAgent.CHONGU + "PayAgentProcessor")
@Log4j2
public class ChongUPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();

        bodyMap.put("MerchantId", payAgentPlatform.getMerId());
        bodyMap.put("Amount", withdrawLog.getWithdrawMoney().setScale(2,BigDecimal.ROUND_HALF_UP));
        bodyMap.put("BankCardBankName",withdrawLog.getBankName());
        bodyMap.put("BankCardNumber",withdrawLog.getBankAccount());
        bodyMap.put("BankCardRealName",withdrawLog.getBankUserName());
        bodyMap.put("MerchantUniqueOrderId",bodyMap.put("MerchantUniqueOrderId",UUID.randomUUID().toString().replace("-", "")));
        bodyMap.put("NotifyUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl") + ConstantsPayAgent.CHONGU);
        bodyMap.put("Timestamp", reqPayAgent.getCurrentTime().getTime());
        bodyMap.put("WithdrawTypeId", 0);


        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        bodyMap.put("sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(bodyMap);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

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
            reqPayAgent.setFailReason(payAgentPlatform.getName() + "代付下单报错原因:" + e);
        }
        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            String return_code = resultMap.getOrDefault("Code", "").toString();
            String withdrawOrderStatus = resultMap.getOrDefault("WithdrawOrderStatus", "").toString();
            if ("0".equals(return_code) && "0".equals(withdrawOrderStatus)) {
                log.info(payAgentPlatform.getName() + "代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("Message", "").toString());
                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn(payAgentPlatform.getName() + "代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String sign = requestMap.remove("Sign").toString();

        String solt = requestMap.getOrDefault("Solt", "").toString();

        String withdrawOrderId = requestMap.getOrDefault("WithdrawOrderId", "").toString();
        String return_code = requestMap.getOrDefault("Code", "").toString();
        String status = requestMap.getOrDefault("Status", "").toString();
        String WithdrawOrderStatus = requestMap.getOrDefault("WithdrawOrderStatus","").toString();


        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        SortedMap<String, Object> bodyMap = new TreeMap<>(requestMap);

        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String signStr = DigestUtils.md5Hex(tempStr);

        log.info(payAgentPlatform.getName() + "代付回调签名:" + sign + "_" + signStr);
        if (sign.equalsIgnoreCase(signStr) && "0".equals(return_code) && "100".equals(WithdrawOrderStatus)) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(withdrawOrderId);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", withdrawOrderId);
                return "fail";
            }
            if(solt.length() >=16 && solt.length() <=20){
                log.error("16-32位随机字符串 - solt:{}", solt);
                return "fail";
            }
            if (withdrawLog.getStatus() == 0) {
                log.error("已有代付记录 - merOrderNo:{}", withdrawOrderId);
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(withdrawOrderId);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "0".equals(status));
            return "SUCCESS";
        }
        return "fail";
    }

    @Override
    public Map<String, Object> reverseCheckOrderPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        return null;
    }

    @Override
    public String queryOrderPay(PayAgentLog payAgentLog) throws Exception {
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(payAgentLog.getWithdrawOrderNo());
        PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById(payAgentLog.getPayAgentPlatId());

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("MerchantId", payAgentPlatform.getMerId());

        Map<String, Object> orderMap = new LinkedHashMap<>();
        orderMap.put("MerchantUniqueOrderId",withdrawLog.getOrderNo());
        String data = JSON.toJSONString(orderMap);
        dataMap.put("data", data);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        String tempStr = data + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();
        dataMap.put("Sign", sign);
        log.warn(payAgentPlatform.getName()+"查询代付状态接口请求参数{}",JsonUtil.object2Json(dataMap));

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.postForObject(payAgentPlatform.getPayOrderQueryAddr(), dataMap, Map.class);
            log.warn(payAgentPlatform.getName()+"查询结果 - result:{}", JsonUtil.object2Json(resultMap));

            if (!CollectionUtils.isEmpty(resultMap)) {
                String return_code = resultMap.getOrDefault("Code", "").toString();
                if ("0".equals(return_code)) {
                    String trade_state = resultMap.getOrDefault("Status", "").toString();
                    if ("100".equals(trade_state) || "FAIL".equals(trade_state)) {
                        int status = 4;
                        int WithdrawOrderStatus = 0;
                        if ("-99".equals(return_code)) {
                            status = -10;
                            WithdrawOrderStatus = -10;
                        } else {
                            status = 0;
                        }
                        payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, WithdrawOrderStatus);
                    }
                }
                return resultMap.getOrDefault("return_msg", "").toString() + "," + resultMap.getOrDefault("Message", "").toString();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return payAgentPlatform.getName()+"查询失败,订单号:"+withdrawLog.getOrderNo();
    }
}
