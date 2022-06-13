package com.qiqilm.server.admin.payagent.processor;

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
import com.qiqilm.server.admin.utils.StringUtils;
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

import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository(value = ConstantsPayAgent.ONE_ZERO + "PayAgentProcessor")
@Log4j2
public class OneZeroPayAgentProcessor extends AbstractPayAgent {


    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {

        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("customerNo", payAgentPlatform.getMerId());
        bodyMap.put("channelCode", "1");
        bodyMap.put("amount", withdrawLog.getWithdrawMoney().setScale(2, RoundingMode.HALF_UP));
        bodyMap.put("orderNo", withdrawLog.getOrderNo());
        bodyMap.put("bankCode", withdrawLog.getBankCode());
        bodyMap.put("cardType", withdrawLog.getBankName().trim());

        bodyMap.put("callBackUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl") + ConstantsPayAgent.ONE_ZERO);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toLowerCase();
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
            reqPayAgent.setFailReason("银联代付下单报错原因:" + e);
        }
        log.info(payAgentPlatform.getName() + "下单结果{},订单号:{}", JsonUtil.object2Json(resultMap), withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            String success = resultMap.getOrDefault("success", "").toString();
            if ("true".equals(success)) {
                log.info("银联代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("message", "").toString());
                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn("银联代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String sign = requestMap.remove("sign").toString();
        String merchantOrderId = requestMap.getOrDefault("orderNo", "").toString();

        String status = requestMap.getOrDefault("status", "").toString();
        String amount = requestMap.getOrDefault("amount","").toString();

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("orderNo",merchantOrderId);
        bodyMap.put("amount",amount);
        bodyMap.put("status", status);
        bodyMap.put("transactionalNumber", requestMap.get("transactionalNumber"));


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
                log.error("已有代付记录 - merOrderNo:{}", merchantOrderId);
                return "OK";
            }
            if(withdrawLog.getStatus() == 2) {
                log.error("提现相关记录丢失 - merOrderNo:{}", merchantOrderId);
                return "fail";
            }

            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(merchantOrderId);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, requestMap.getOrDefault("orderNo", "").toString(),
                    payAgentPlatform, "1".equals(status));
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
        Map<String, Object> paramsMap = new TreeMap<>();
        paramsMap.put("customerNo", payAgentPlatform.getMerId());
        paramsMap.put("orderNo", withdrawLog.getOrderNo());


        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(paramsMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toLowerCase();
        paramsMap.put("sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(paramsMap);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

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
            log.info("银联代付查询结果- result:{}", JsonUtil.object2Json(resultMap));
            if (!CollectionUtils.isEmpty(resultMap)) {
                String success = resultMap.getOrDefault("success", "").toString();
                if ("true".equals(success)) {
                    Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
                    String statusType = dataMap.getOrDefault("status", "").toString();
                    // status 4代付中 5代付失败 6代付成功
                    // trade_state  1等待处理 2准备打款,3已打款,4已拒绝 處理中,需繼續查詢
                    int status = 4;
                    int orderStatus = 0;
                    if ("4".equals(statusType)) {
                        status = 6;
                        orderStatus = 1;
                    } else if ("2".equals(statusType) || "3".equals(statusType)) {
                        status = 5;
                        orderStatus = 2;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderStatus);

                }
                return resultMap.getOrDefault("message", "").toString();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "银联代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}



















