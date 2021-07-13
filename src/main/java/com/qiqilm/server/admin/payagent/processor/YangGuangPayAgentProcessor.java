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
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Repository(value = ConstantsPayAgent.YANGGUANG + "PayAgentProcessor")
@Log4j2
public class YangGuangPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("merchant_no", payAgentPlatform.getMerId());
        bodyMap.put("method", "settle");
        bodyMap.put("bank_name", withdrawLog.getBankName().trim());
        bodyMap.put("bank_branch", "深圳支行");
        bodyMap.put("bank_user", withdrawLog.getBankUserName().trim());
        bodyMap.put("bank_card", withdrawLog.getBankAccount().trim());
        bodyMap.put("amount", withdrawLog.getWithdrawMoney().multiply(BigDecimal.valueOf(100)).setScale(0,
                BigDecimal.ROUND_HALF_UP));
        bodyMap.put("out_trade_no", withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(bodyMap) + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        bodyMap.put("sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(bodyMap);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

        String res = null;
        try {
            res = restTemplate.postForObject(payAgentPlatform.getPayOrderAddr(), httpEntity, String.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("阳光代付下单结果 - result:{}", res);
        if (StringUtils.isNotBlank(res)) {
            Map<String, Object> resultMap = JsonUtil.json2Map(res);
            if (!CollectionUtils.isEmpty(resultMap)) {
                String code = resultMap.getOrDefault("code", "").toString();
                String status = resultMap.getOrDefault("status", "").toString();
                if ("0000".equals(code) && "1".equals(status)) {
                    log.info("阳光代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                    return true;
                } else {
                    reqPayAgent.setFailReason(resultMap.getOrDefault("msg", "").toString());

                    payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
                }
            }
        }
        log.warn("阳光代付订单提交失败 - result:{}", res);
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        return "阳光代付无回调";
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
        Map<String, Object> paramsMap = new TreeMap<>();
        paramsMap.put("out_trade_no", withdrawLog.getOrderNo());
        paramsMap.put("merchant_no", payAgentPlatform.getMerId());
        paramsMap.put("method", "settlequery");

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(paramsMap) + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        paramsMap.put("sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(paramsMap);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

        String res = null;
        try {
            res = restTemplate.postForObject(payAgentPlatform.getPayOrderQueryAddr(), httpEntity, String.class);
            log.info("阳光代付查询结果- result:{}", res);
            if(StringUtils.isNotBlank(res)) {
                Map<String, Object> resultMap = JsonUtil.json2Map(res);
                if (!CollectionUtils.isEmpty(resultMap)) {
                    String code = resultMap.getOrDefault("code", "").toString();
                    if ("0000".equals(code)) {
                        int statusType = Integer.parseInt(resultMap.getOrDefault("status", "").toString());
                        if (statusType > 0) {
                            // status 4代付中 5代付失败 6代付成功
                            // statusType  2失败 1成功 0处理中
                            int status = 4;
                            if (statusType == 1) {
                                status = 6;
                            } else if (statusType == 2) {
                                status = 5;
                            }
                            payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, statusType);
                        }
                    }
                    return res;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "阳光代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
