package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeYuZhouType;
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.*;

@Repository(value = ConstantsPayAgent.YUZHOU + "PayAgentProcessor")
@Log4j2
public class YuZhouPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        BankCodeYuZhouType bankCodeType = BankCodeYuZhouType.getCodeByDesc(withdrawLog.getBankName());
        if (bankCodeType == null) {
            payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            log.warn(payAgentPlatform.getName() + "无法支持的银行类型 - 银行类型:{}", withdrawLog.getBankName());
            throw new BusinessException(payAgentPlatform.getName() + "无法支持的银行类型：" + withdrawLog.getBankName());
        }
        withdrawLog.setBankCode(bankCodeType.name());

        String outTradeNo = withdrawLog.getOrderNo();
        String totalFee = withdrawLog.getWithdrawMoney().multiply(BigDecimal.valueOf(100)).setScale(0, BigDecimal.ROUND_HALF_UP).toString();
        String bankAccount = withdrawLog.getBankAccount();
        String accountHolder = withdrawLog.getBankUserName();
        String depositBank = withdrawLog.getBankName();
        String accountHolderMobile = "18611111111";
        String mchId = payAgentPlatform.getMerId();

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("mch_id", payAgentPlatform.getMerId());
        dataMap.put("out_trade_no", outTradeNo);
        dataMap.put("total_fee", totalFee);
        dataMap.put("accountName", withdrawLog.getBankUserName().trim());
        dataMap.put("pay_type", "86");
        dataMap.put("body", "body");
        dataMap.put("bank_account", bankAccount);
        dataMap.put("account_holder", accountHolder);
        dataMap.put("deposit_bank_code", withdrawLog.getBankCode());
        dataMap.put("deposit_bank", depositBank);
        dataMap.put("acct_type", "1");
        dataMap.put("account_holder_mobile", accountHolderMobile);
        dataMap.put("province", "广东省");
        dataMap.put("city", "广州市");
        dataMap.put("sub_branch", withdrawLog.getBankName());
        dataMap.put("account_holder_id", "341124200001010101");

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        String tempStr = outTradeNo + totalFee + bankAccount + accountHolder + depositBank + accountHolderMobile + mchId + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        dataMap.put("sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(dataMap);
        log.warn(payAgentPlatform.getName() + "下单请求参数{}", JsonUtil.object2Json(requestMap));
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
            reqPayAgent.setFailReason(payAgentPlatform.getName() + "下单报错原因:" + e);
            payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
        }
        log.info(payAgentPlatform.getName() + "下单结果{},订单号:{}", JsonUtil.object2Json(resultMap), withdrawLog.getOrderNo());

        if (!CollectionUtils.isEmpty(resultMap)) {
            if ("0".equals(resultMap.getOrDefault("result_code", "").toString())) {
                log.info(payAgentPlatform.getName() + "订单提交成功 - listResult:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("err_msg", "").toString());
                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn(payAgentPlatform.getName() + "订单提交失败 - result:{}", JsonUtil.object2Json(resultMap));
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String orderNo = requestMap.getOrDefault("out_trade_no", "").toString();
        String transactionId = requestMap.getOrDefault("transaction_id", "").toString();
        String timeEnd = requestMap.getOrDefault("time_end", "").toString();
        String mchId = requestMap.getOrDefault("mch_id", "").toString();
        String status = requestMap.getOrDefault("status", "").toString();

        String rspSign = requestMap.remove("sign").toString();
        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        String tempStr = orderNo + transactionId + timeEnd + mchId + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);

        log.info(payAgentPlatform.getName() + "回调签名:" + rspSign + "_" + sign);
        if (rspSign.equalsIgnoreCase(sign)) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(orderNo);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", orderNo);
                return "fail";
            }
            if (withdrawLog.getStatus() == 2) {
                log.error("订单已拒绝，无需回调 - merOrderNo:{}", orderNo);
                return "success";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", orderNo);
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(orderNo);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "3".equals(status));
            log.info(payAgentPlatform.getName() + "订单号:{},回调状态:{},", orderNo, "3".equals(status) ? "成功" : "失败");
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

        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("mch_id", payAgentPlatform.getMerId());
        dataMap.put("out_trade_no", withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        String tempStr = withdrawLog.getOrderNo() + payAgentPlatform.getMerId() + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        dataMap.put("sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(dataMap);
        log.warn(payAgentPlatform.getName() + "查询代付状态接口请求参数{}", JsonUtil.object2Json(requestMap));
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
                //  status 4代付中 5代付失败 6代付成功
                int status = 4;
                //  statusCode
                //  0:审核中; 1:审核完成; 2:打款进行中; 3:打款成功; 4:订单关闭; 5:打款失败; 6:订单不存在;
                String resultCode = resultMap.getOrDefault("result_code", "").toString();

                if ("3".equals(resultCode) || "5".equals(resultCode)) {
                    if ("3".equals(resultCode)) {
                        status = 6;
                    } else {
                        status = 5;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, 1);
                }

                if("-1".equals(resultMap.getOrDefault("result_code", "").toString())){
                    return resultMap.getOrDefault("err_msg", "").toString();
                }else {
                    switch(resultCode)
                    {
                        case "0" :
                            return "审核中";
                        case "1" :
                            return "审核完成";
                        case "2" :
                            return "打款进行中";
                        case "3" :
                            return "打款成功";
                        case "4" :
                            return "订单关闭";
                        case "5" :
                            return "打款失败";
                        case "6" :
                            return "订单不存在";
                        case "-1" :
                            return "签名验证失败,查询失败";
                        case "-2" :
                            return "提交上游中";
                        case "-3" :
                            return "代付异常,联系管理员";
                    }
                }

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }

}
