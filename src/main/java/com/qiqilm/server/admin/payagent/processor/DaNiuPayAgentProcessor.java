package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeDaNiuType;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.AuthUtil;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository(value = ConstantsPayAgent.DANIU + "PayAgentProcessor")
@Log4j2
public class DaNiuPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        BankCodeDaNiuType bankCodeType = BankCodeDaNiuType.getCodeByDesc(withdrawLog.getBankName());
        if (bankCodeType == null) {
            log.warn("大牛银联代付无法支持的银行类型 - 银行类型:{}", withdrawLog.getBankName());
            payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            throw new BusinessException("此代付无法支持的银行类型：" + withdrawLog.getBankName());
        }
        withdrawLog.setBankCode(bankCodeType.name().substring(1));

        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("MerchNo", payAgentPlatform.getMerId());
        dataMap.put("OrderNo", withdrawLog.getOrderNo());
        dataMap.put("Amount", withdrawLog.getWithdrawMoney().setScale(2,
                RoundingMode.HALF_UP));
        dataMap.put("Receiver", withdrawLog.getBankUserName().trim());
        dataMap.put("ToAccount", withdrawLog.getBankAccount().trim());
        dataMap.put("ToBankCode", withdrawLog.getBankCode());
        dataMap.put("Time", System.currentTimeMillis() / 1000);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String tempStr = this.assemblyUrl(dataMap) + "&SecretKey=" + signMd5;
        log.warn(tempStr);
        String sign = RSACoder.signMd5Rsa(tempStr, payAgentPlatform.getSignPrivateKey());
        dataMap.put("Sign", sign);
        dataMap.put("BackUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        log.warn(JsonUtil.object2Json(dataMap));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(dataMap, httpHeaders);

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute(payAgentPlatform.getPayOrderAddr(), HttpMethod.POST,
                    restTemplate.httpEntityCallback(httpEntity), response -> {
                        InputStream bodyStream = response.getBody();
                        String text;
                        try (Reader reader = new InputStreamReader(bodyStream)) {
                            text = CharStreams.toString(reader);
                        }
                        log.warn(text);
                        return JsonUtil.json2Map(text);
                    });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            reqPayAgent.setFailReason(payAgentPlatform.getName() + "下单报错原因:" + e.getMessage());
        }
        log.info(payAgentPlatform.getName() + "下单结果 - result:{}", JsonUtil.object2Json(resultMap));
        if (!CollectionUtils.isEmpty(resultMap)) {
            String return_code = resultMap.getOrDefault("Code", "").toString();
            if ("0".equals(return_code)) {
                log.info(payAgentPlatform.getName() + "订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("Msg", "").toString());

                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn(payAgentPlatform.getName() + "订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        if (this.checkWhiteIp(payAgentPlatform.getPlatWhiteIpList(), realIp)) {
            log.warn("请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json(requestMap));
            return "{\"Data\": false,\"Code\": 0,\"Msg\": null}";
        }

        Map<String, Object> resultMap = (Map<String, Object>) requestMap.getOrDefault("Data", new HashMap<>());

        String BillNo = resultMap.getOrDefault("BillNo", "").toString();
        String OrderNo = resultMap.getOrDefault("OrderNo", "").toString();
        BigDecimal Amount = new BigDecimal(resultMap.getOrDefault("Amount", "0").toString()).setScale(2, RoundingMode.HALF_UP);

        // 解密后对签名验证
        SortedMap<String, Object> signMap = new TreeMap<>();
        signMap.put("BillNo", BillNo);
        signMap.put("OrderNo", OrderNo);
        signMap.put("Amount", Amount);
        signMap.put("Account", resultMap.get("Account"));
        signMap.put("ToAccount", resultMap.get("ToAccount"));

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String signStr = this.assemblyUrl(signMap) + "&SecretKey=" + signMd5;

        if (RSACoder.verifyMd5Rsa(signStr, payAgentPlatform.getSignPublicKey(), requestMap.get("Sign").toString())) {
            int Status = Integer.parseInt(resultMap.getOrDefault("Status", -1).toString());
            if (Status >= 2 && Status <= 4) {
                MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(OrderNo);
                if (withdrawLog == null) {
                    log.error("提现相关记录丢失 - OrderNo:{}", OrderNo);
                    return "{\"Data\": false,\"Code\": 0,\"Msg\": null}";
                }
                if (withdrawLog.getStatus() == 6) {
                    log.error("已有代付记录 - OrderNo:{}", OrderNo);
                    return "{\"Data\": true,\"Code\": 0,\"Msg\": null}";
                }
                PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(OrderNo);
                payAgentService.processOrderPay(withdrawLog, payAgentLog, BillNo, payAgentPlatform, Status == 2);
                log.info(payAgentPlatform.getName() + "订单号:{},回调状态:{},", OrderNo, Status == 2 ? "成功" : "失败");
            }
            return "{\"Data\": true,\"Code\": 0,\"Msg\": null}";
        }
        log.info(payAgentPlatform.getName() + "回调验签失败");
        return "{\"Data\": false,\"Code\": 0,\"Msg\": null}";
    }

    @Override
    public Map<String, Object> reverseCheckOrderPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap,
                                                    String realIp) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("Data", false);
        resultMap.put("Code", 0);
        resultMap.put("Msg", null);
        if (this.checkWhiteIp(payAgentPlatform.getPlatWhiteIpList(), realIp)) {
            log.warn("请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json(requestMap));
            resultMap.put("Msg", "请求ip非白名单:" + realIp);
            return resultMap;
        }
        log.warn("反查数据:" + JsonUtil.object2Json(requestMap));
        String OrderNo = requestMap.get("OrderNo").toString();
        BigDecimal Amount = new BigDecimal(requestMap.getOrDefault("Amount", 0).toString());
        String MerchNo = resultMap.get("MerchNo").toString();

        SortedMap<String, Object> signMap = new TreeMap<>();
        signMap.put("OrderNo", OrderNo);
        signMap.put("Amount", Amount);
        signMap.put("MerchNo", MerchNo);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String signStr = this.assemblyUrl(signMap) + "&SecretKey=" + signMd5;

        if (RSACoder.verifyMd5Rsa(signStr, payAgentPlatform.getSignPublicKey(), requestMap.get("sign").toString())) {
            MemberWithdrawLog memberWithdrawLog = withdrawLogMapper.selectByOrderNo(OrderNo);
            if (memberWithdrawLog == null || Amount.compareTo(memberWithdrawLog.getWithdrawMoney()) != 0
                    || !MerchNo.equals(payAgentPlatform.getMerId())) {
                resultMap.put("Msg", "订单不匹配");
                return resultMap;
            }
            resultMap.put("Data", true);
            resultMap.put("Msg", "验证成功");
            return resultMap;
        }
        resultMap.put("msg", "验签失败");
        return resultMap;
    }

    @Override
    public String queryOrderPay(PayAgentLog payAgentLog) throws Exception {
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(payAgentLog.getWithdrawOrderNo());
        PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById(payAgentLog.getPayAgentPlatId());
        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("MerchNo", payAgentPlatform.getMerId());
        dataMap.put("OrderNo", withdrawLog.getOrderNo());
        dataMap.put("Time", System.currentTimeMillis() / 1000);
        dataMap.put("Amount", withdrawLog.getWithdrawMoney().setScale(2, RoundingMode.HALF_UP));
        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        // 生成签名信息
        String signStr = this.assemblyUrl(dataMap) + "&SecretKey=" + signMd5;
        String sign = RSACoder.signMd5Rsa(signStr, payAgentPlatform.getSignPrivateKey());
        dataMap.put("sign", sign);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(dataMap, httpHeaders);

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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info(payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json(resultMap));
        if (!CollectionUtils.isEmpty(resultMap)) {
            if ("0".equals(resultMap.getOrDefault("Code", "").toString())) {
                Map<String, Object> resultDataMap = (Map<String, Object>) resultMap.getOrDefault("Data", new HashMap<>());
                int orderState = Integer.parseInt(resultDataMap.getOrDefault("Status", 0).toString());
                // status 4代付中5代付失败6代付成功
                // orderState 2 成功 3，4 失败 1，5 处理中
                int status = 4;
                switch (orderState) {
                    case 2:
                        status = 6;
                        break;
                    case 3:
                    case 4:
                        status = 5;
                        break;
                    default:
                        break;
                }
                payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderState);
                return resultDataMap.getOrDefault("ProcessRemark", "").toString();
            }
            return resultMap.getOrDefault("Msg", "").toString();
        }
        return payAgentPlatform.getName() + "代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
