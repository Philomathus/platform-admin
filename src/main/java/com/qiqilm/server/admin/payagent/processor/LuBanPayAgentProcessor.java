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

@Repository(value = ConstantsPayAgent.LUBAN + "PayAgentProcessor")
@Log4j2
public class LuBanPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("account_id", payAgentPlatform.getMerId());
        bodyMap.put("out_trade_no", withdrawLog.getOrderNo());
        bodyMap.put("amount", withdrawLog.getWithdrawMoney().setScale(2, BigDecimal.ROUND_HALF_UP));
        bodyMap.put("bank_name", withdrawLog.getBankName().trim());
        bodyMap.put("bank_user", withdrawLog.getBankUserName().trim());
        bodyMap.put("bank_id", withdrawLog.getBankAccount().trim());
        bodyMap.put("callback_url", sysConfigCacheUtil.getConf("payAgentNotifyUrl") + ConstantsPayAgent.LUBAN);
        bodyMap.put("withdraw_type", "1");

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        //$sign = md5(md5($account_id.$out_trade_no.$bank_id).$user_key);
        String tempStr= payAgentPlatform.getMerId() + withdrawLog.getOrderNo() + withdrawLog.getBankAccount().trim() + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        bodyMap.put("sign", DigestUtils.md5Hex(sign));

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
        log.info("鲁班代付下单结果 - result:{}", res);
        if (StringUtils.isNotBlank(res)) {
            Map<String, Object> resultMap = JsonUtil.json2Map(res);
            if (!CollectionUtils.isEmpty(resultMap)) {
                String code = resultMap.getOrDefault("code", "").toString();
                if ("0000".equals(code)) {
                    log.info("鲁班代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                    return true;
                } else {
                    reqPayAgent.setFailReason(resultMap.getOrDefault("msg", "").toString());

                    payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
                }
            }
        }
        log.warn("鲁班代付订单提交失败 - result:{}", res);
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String sign = requestMap.remove("sign").toString();
        String call_type = requestMap.getOrDefault("call_type", "").toString();
        String flow_no = requestMap.getOrDefault("flow_no", "").toString();
        String call_time = requestMap.getOrDefault("call_time", "").toString();

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = flow_no + call_time + signMd5;
        String signStr = DigestUtils.md5Hex(tempStr).toLowerCase();
        log.info("鲁班代付回调签名:" + tempStr + "_" + sign);
        if (sign.equalsIgnoreCase(signStr)) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(flow_no);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", flow_no);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", flow_no);
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(flow_no);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "1".equals(call_type));
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
        Map<String, String> paramsMap = new TreeMap<>();
        paramsMap.put("ddh", withdrawLog.getOrderNo());

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(paramsMap);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

        String res = null;
        try {
            res = restTemplate.postForObject(payAgentPlatform.getPayOrderQueryAddr(), httpEntity, String.class);
            log.info("鲁班代付查询结果- result:{}", res);
            if(StringUtils.isNotBlank(res)) {
                Map<String, Object> resultMap = JsonUtil.json2Map(res);
                if (!CollectionUtils.isEmpty(resultMap)) {
                    String code = resultMap.getOrDefault("code", "").toString();
                    if ("200".equals(code)) {
                        int msg = Integer.parseInt(resultMap.getOrDefault("msg", "").toString());
                        if (msg > 1) {
                            // status 4代付中 5代付失败 6代付成功
                            // statusType  1打款中2提现已到账3提现已驳回
                            int status = 4;
                            if (msg == 2) {
                                status = 6;
                            } else if (msg == 3) {
                                status = 5;
                            }
                            payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, msg);
                        }
                    }
                    return res;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "鲁班代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
