package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeHyType;
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.*;

@Repository(value = ConstantsPayAgent.HY + "PayAgentProcessor")
@Log4j2
public class HyPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {

        BankCodeHyType bankType = BankCodeHyType.getCodeByDesc(withdrawLog.getBankName());

        if (bankType == null) {
            payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            log.warn(payAgentPlatform.getName() + "代付无法支持的银行类型 - 银行类型:{}", withdrawLog.getBankName());
            throw new BusinessException(payAgentPlatform.getName() + "代付无法支持的银行类型：" + withdrawLog.getBankName());
        }

        Map<String, Object> bodyMap = new LinkedHashMap<>();

        bodyMap.put("merchantId", payAgentPlatform.getMerId());
        bodyMap.put("merchantOrderId", withdrawLog.getOrderNo());
        bodyMap.put("orderAmount", withdrawLog.getWithdrawMoney().setScale(2, BigDecimal.ROUND_HALF_UP));
        bodyMap.put("payType", "1");
        bodyMap.put("accountHolderName", withdrawLog.getBankUserName().trim());
        bodyMap.put("accountNumber", withdrawLog.getBankAccount().trim());
        bodyMap.put("bankType", bankType.name().substring(1));
        bodyMap.put("notifyUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put("reverseUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put("submitIp", "192.168.0.1");

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(bodyMap) + signMd5;

        log.warn(tempStr);

        String sign = DigestUtils.md5Hex(tempStr).toLowerCase();
        bodyMap.put("sign", sign);

        bodyMap.put("subBranch", withdrawLog.getBankName());

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
            Object error_code = resultMap.get("ErrorCode");
            Object error_message = resultMap.get("ErrorMessage");

            if (Objects.isNull(error_message) && Objects.isNull(error_code)) {
                log.info(payAgentPlatform.getName() + "代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(error_message.toString());
                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn(payAgentPlatform.getName() + "代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {

        String sign = requestMap.remove("sign").toString();
        String merchantOrderId = requestMap.getOrDefault("merchantOrderId", "").toString();

        String status = requestMap.getOrDefault("status", "").toString();

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("merchantId", requestMap.get("merchantId"));
        bodyMap.put("merchantOrderId", merchantOrderId);
        bodyMap.put("status", status);
        bodyMap.put("orderType", requestMap.get("orderType"));
        bodyMap.put("orderAmount", requestMap.get("orderAmount"));
        bodyMap.put("systemOrderId", requestMap.get("systemOrderId"));
        bodyMap.put("remark", requestMap.get("remark"));
        bodyMap.put("submitIp", requestMap.get("submitIp"));

        String tempStr = this.assemblyUrl(bodyMap) + signMd5;
        String signStr = DigestUtils.md5Hex(tempStr);

        log.info(payAgentPlatform.getName() + "代付回调签名:" + sign + "_" + signStr);
        if (sign.equalsIgnoreCase(signStr)) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(merchantOrderId);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", merchantOrderId);
                return "fail";
            }
            if (withdrawLog.getStatus() == 1) {
                if (withdrawLog.getStatus() == 2) {
                    if (withdrawLog.getStatus() == 3) {
                        log.error("已有代付记录 - merOrderNo:{}", merchantOrderId);
                        return "OK";
                    }
                    if (withdrawLog.getStatus() == 4) {
                        log.error("提现相关记录丢失 - merOrderNo:{}", merchantOrderId);
                        return "fail";
                    }
                }
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(merchantOrderId);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, requestMap.getOrDefault("systemOrderId", "").toString(),
                    payAgentPlatform, "3".equals(status));
            return "OK";
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
        dataMap.put("merchantId", payAgentPlatform.getMerId());
        dataMap.put("merchantOrderId", withdrawLog.getOrderNo());
        dataMap.put("orderAmount", withdrawLog.getWithdrawMoney().setScale(2, BigDecimal.ROUND_HALF_UP));

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(dataMap) + signMd5;

        log.warn(tempStr);

        String sign = DigestUtils.md5Hex(tempStr).toLowerCase();
        dataMap.put("sign", sign);
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
                String errorCode = resultMap.getOrDefault("ErrorCode", "").toString();
                String errorMessage = resultMap.getOrDefault("ErrorMessage", "").toString();
                if (StringUtils.isEmpty(errorCode) && StringUtils.isEmpty(errorMessage)) {
                    String trade_state = resultMap.getOrDefault("Status", "").toString();
                    if ("1".equals(trade_state) || "2".equals(trade_state) || "3".equals(trade_state) || "4".equals(trade_state)) {
                        // status 4代付中 5代付失败 6代付成功
                        // trade_state  1等待处理 2准备打款,3已打款,4已拒绝 處理中,需繼續查詢
                        int status = 4;
                        int orderStatus = 0;
                        if ("3".equals(trade_state)) {
                            status = 6;
                            orderStatus = 1;
                        } else if ("4".equals(trade_state)) {
                            status = 5;
                            orderStatus = 2;
                        }
                        payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderStatus);
                    }
                }
                return resultMap.getOrDefault("ErrorMessage", "").toString();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
