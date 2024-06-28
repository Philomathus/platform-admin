package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
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
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository(value = ConstantsPayAgent.ZHUO_YUE + "PayAgentProcessor")
@Log4j2
public class ZhuoYuePayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();

        bodyMap.put("MerchantId", payAgentPlatform.getMerId());
        bodyMap.put("Amount", withdrawLog.getWithdrawMoney().setScale(2, BigDecimal.ROUND_HALF_UP));
        bodyMap.put("BankCardBankName", withdrawLog.getBankName());
        bodyMap.put("BankCardNumber", withdrawLog.getBankAccount());
        bodyMap.put("BankCardRealName", withdrawLog.getBankUserName());
        bodyMap.put("MerchantUniqueOrderId", withdrawLog.getOrderNo());
        bodyMap.put("NotifyUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put("Timestamp", DateFormatUtils.formate(reqPayAgent.getCurrentTime(), DateFormatUtils.TIGHT_PATTERN_DATETIME));
        bodyMap.put("WithdrawTypeId", 0);


        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(bodyMap) + signMd5;

        log.warn(tempStr);

        String sign = DigestUtils.md5Hex(tempStr);
        bodyMap.put("Sign", sign);

        log.warn(JsonUtil.object2Json(bodyMap));

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(bodyMap);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestMap, httpHeaders);

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
        log.info(payAgentPlatform.getName() + "下单结果{},订单号:{}", JsonUtil.object2Json(resultMap), withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            String return_code = resultMap.getOrDefault("Code", "").toString();
            if ("0".equals(return_code)) {
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

        String withdrawOrderId = requestMap.getOrDefault("MerchantUniqueOrderId", "").toString();
        String status = requestMap.getOrDefault("Status", "").toString();

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        SortedMap<String, Object> bodyMap = new TreeMap<>(requestMap);

        String tempStr = this.assemblyUrl(bodyMap) + signMd5;
        String signStr = DigestUtils.md5Hex(tempStr);

        log.info(payAgentPlatform.getName() + "代付回调签名:" + sign + "_" + signStr);
        if (sign.equalsIgnoreCase(signStr)) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(withdrawOrderId);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", withdrawOrderId);
                return "fail";
            }
            if (withdrawLog.getStatus() == 0) {
                log.error("已有代付记录 - merOrderNo:{}", withdrawOrderId);
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(withdrawOrderId);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, requestMap.getOrDefault("WithdrawOrderId", "").toString(),
                    payAgentPlatform, "100".equals(status));
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

        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("MerchantId", payAgentPlatform.getMerId());
        dataMap.put("Timestamp", DateFormatUtils.formate(new Date(), DateFormatUtils.TIGHT_PATTERN_DATETIME));
        dataMap.put("MerchantUniqueOrderId", withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(dataMap) + signMd5;

        log.warn(tempStr);

        String sign = DigestUtils.md5Hex(tempStr);
        dataMap.put("Sign", sign);
        log.warn(payAgentPlatform.getName() + "查询代付状态接口请求参数{}", JsonUtil.object2Json(dataMap));

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(dataMap);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestMap, httpHeaders);

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
            log.warn(payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json(resultMap));

            if (!CollectionUtils.isEmpty(resultMap)) {
                String return_code = resultMap.getOrDefault("Code", "").toString();
                if ("0".equals(return_code)) {
                    String trade_state = resultMap.getOrDefault("WithdrawOrderStatus", "").toString();
                    if ("100".equals(trade_state) || "0".equals(trade_state) || "-90".equals(trade_state)) {
                        // status 4代付中 5代付失败 6代付成功
                        // trade_state  100成功 -90失败 0 處理中,需繼續查詢
                        int status = 4;
                        int orderStatus = 0;
                        if ("100".equals(trade_state)) {
                            status = 6;
                            orderStatus = 1;
                        } else if("-90".equals(trade_state)) {
                            status = 5;
                            orderStatus = 2;
                        }
                        payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderStatus);
                    }
                }
                return resultMap.getOrDefault("Message", "").toString();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
