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
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Repository(value = ConstantsPayAgent.CG + "PayAgentProcessor")
@Log4j2
public class CGPayAgentProcessor extends AbstractPayAgent {

    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        Map<String, Object> bodyMap = new TreeMap<>(Comparator.comparing(String::toLowerCase));

        bodyMap.put("MerchantId", payAgentPlatform.getMerId());
        bodyMap.put("MerchantUserId", withdrawLog.getMemberId().substring(2).replace("_", ""));
        bodyMap.put("MerchantWithdrawId", withdrawLog.getOrderNo());
        bodyMap.put("UserWallet", withdrawLog.getBankAccount().trim());
        bodyMap.put("Symbol", "CGP");
        bodyMap.put("RMBAmount", withdrawLog.getWithdrawMoney().setScale(2, RoundingMode.HALF_UP));
        bodyMap.put("CryptoAmount", withdrawLog.getWithdrawMoney().multiply(new BigDecimal(100000000L))
                .setScale(0, RoundingMode.HALF_UP).toString());
        bodyMap.put("CallBackUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put("AutoWithdraw", "AUTO");

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        StringBuilder sd = new StringBuilder();
        bodyMap.forEach((k, v) -> sd.append(v).append(","));
        String sn = sd.append(signMd5).toString();
        String sign = DigestUtils.md5Hex(sn);
        bodyMap.put("Sign", sign);
        log.warn(payAgentPlatform.getName() + "下单请求参数{}", JsonUtil.object2Json(bodyMap));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(bodyMap, httpHeaders);

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
            String code = resultMap.getOrDefault("ReturnCode", "").toString();
            if ("0".equals(code)) {
                log.info(payAgentPlatform.getName() + "订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("ReturnMessage", "").toString());
                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn(payAgentPlatform.getName() + "订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String sign = requestMap.remove("Sign").toString();
        //商户订单号
        String mchOrderId = requestMap.getOrDefault("MerchantWithdrawId", "").toString();

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        Map<String, Object> bodyMap = new TreeMap<>(Comparator.comparing(String::toLowerCase));
        bodyMap.putAll(requestMap);

        StringBuilder sd = new StringBuilder();
        bodyMap.values().stream().filter(Objects::nonNull).filter(x -> StringUtils.isNotEmpty(x.toString()))
                .collect(Collectors.toList()).forEach(x -> sd.append(x).append(","));
        //treeMap.forEach((k, v) -> sd.append(v).append(","));
        String sn = sd.append(signMd5).toString();
        String mySign = DigestUtils.md5Hex(sn).toUpperCase();

        log.info(payAgentPlatform.getName() + "回调签名字符串:" + sign + "_" + mySign);
        if (sign.equalsIgnoreCase(mySign)) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(mchOrderId);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", mchOrderId);
                return "error:提现相关记录丢失";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", mchOrderId);
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(mchOrderId);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, true);
            return "success";
        }
        return "error:验签失败";
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

        Map<String, Object> bodyMap = new TreeMap<>(Comparator.comparing(String::toLowerCase));

        bodyMap.put("MerchantId", payAgentPlatform.getMerId());
        bodyMap.put("MerchantWithdrawId", withdrawLog.getOrderNo());
        bodyMap.put("Symbol", "CGP");

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        StringBuilder sd = new StringBuilder();
        bodyMap.forEach((k, v) -> sd.append(v).append(","));
        String sn = sd.append(signMd5).toString();
        String sign = DigestUtils.md5Hex(sn);
        bodyMap.put("Sign", sign);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(bodyMap, httpHeaders);

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
            log.info(payAgentPlatform.getName() + "查询结果:{}", JsonUtil.object2Json(resultMap));
            if (!CollectionUtils.isEmpty(resultMap)) {
                String code = resultMap.getOrDefault("ReturnCode", "").toString();
                if ("0".equals(code)) {
                    String data = resultMap.getOrDefault("Status", "").toString();
                    if ("SUCCESS".equals(data) || "FAIL".equals(data)) {
                        // status 4代付中 5代付失败 6代付成功
                        int status = 4;
                        if ("SUCCESS".equals(data)) {
                            status = 6;
                        } else {
                            status = 5;
                        }
                        payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, 1);
                    }
                }
                return resultMap.getOrDefault("ReturnMessage", "").toString();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }

}
