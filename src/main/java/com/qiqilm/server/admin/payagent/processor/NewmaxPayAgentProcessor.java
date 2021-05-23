package com.qiqilm.server.admin.payagent.processor;

import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeLangYaType;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.AuthUtil;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.RoundingMode;
import java.util.*;

@Repository(value = ConstantsPayAgent.NEWMAX + "PayAgentProcessor")
@Log4j2
public class NewmaxPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("amount", withdrawLog.getWithdrawMoney().setScale(2, RoundingMode.HALF_UP));
        bodyMap.put("out_trade_id", withdrawLog.getOrderNo());
        bodyMap.put("bank_name", withdrawLog.getBankName().trim());
        bodyMap.put("account", withdrawLog.getBankAccount().trim());
        bodyMap.put("holder_name", withdrawLog.getBankUserName().trim());
        bodyMap.put("province", "广东省");
        bodyMap.put("city", "广州市");
        bodyMap.put("merchant_id", payAgentPlatform.getMerId());
        bodyMap.put("notify_url", sysConfigCacheUtil.getConf("payAgentNotifyUrl") + ConstantsPayAgent.NEWMAX);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toLowerCase();
        bodyMap.put("sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(bodyMap);
        log.warn(JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

        String res = null;
        try {
            res = restTemplate.postForObject(payAgentPlatform.getPayOrderAddr(), httpEntity, String.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("newmax代付下单结果 - result:{}", res);
        Map<String, Object> resultMap = JsonUtil.json2Map(res);
        if (!CollectionUtils.isEmpty(resultMap)) {
            String code = resultMap.getOrDefault("code", "").toString();
            if ("1".equals(code)) {
                log.info("newmax代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("message", "").toString());
            }
        }
        log.warn("newmax代付订单提交失败 - result:{}", JsonUtil.object2Json(resultMap));
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String sign = requestMap.remove("sign").toString();
        String status = requestMap.getOrDefault("status", "").toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>(requestMap);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String signStr = DigestUtils.md5Hex(tempStr).toLowerCase();

        log.info("newmax代付回调签名字符串:" + sign + "_" + signStr);
        if (sign.equalsIgnoreCase(signStr)) {
            String out_trade_id = requestMap.getOrDefault("out_trade_id", "").toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(out_trade_id);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", out_trade_id);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", out_trade_id);
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(out_trade_id);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "2".equals(status));
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
    public void queryOrderPay(PayAgentLog payAgentLog) throws Exception {
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(payAgentLog.getWithdrawOrderNo());
        PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById(payAgentLog.getPayAgentPlatId());
        Map<String, Object> paramsMap = new TreeMap<>();
        paramsMap.put("merchant_id", payAgentPlatform.getMerId());
        paramsMap.put("out_trade_id", withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(paramsMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toLowerCase();
        paramsMap.put("sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(paramsMap);
        log.warn(JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

        String res  = null;
        try {
            res = restTemplate.postForObject(payAgentPlatform.getPayOrderQueryAddr(), httpEntity, String.class);
            Map<String, Object> resultMap = JsonUtil.json2Map(res);
            log.info("newmax代付查询结果- result:{}", JsonUtil.object2Map(resultMap));
            if (!CollectionUtils.isEmpty(resultMap)) {
                int statusType = Integer.parseInt(resultMap.getOrDefault("status", "").toString());
                if (statusType == -1 || statusType == 2) {
                    // status 4代付中 5代付失败 6代付成功
                    // state-1：被拒绝；1：未支付；2支付成功
                    int status = 4;
                    if (statusType == -1) {
                        status = 5;
                    } else if (statusType == 2) {
                        status = 6;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, statusType);
                    return;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
