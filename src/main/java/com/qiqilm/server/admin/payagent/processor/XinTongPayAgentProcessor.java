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

import java.math.RoundingMode;
import java.util.*;

@Repository(value = ConstantsPayAgent.XINTONG + "PayAgentProcessor")
@Log4j2
public class XinTongPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("partnerId", payAgentPlatform.getMerId());
        bodyMap.put("mchOrderNum", withdrawLog.getOrderNo());
        bodyMap.put("amount", withdrawLog.getWithdrawMoney().setScale(2, RoundingMode.HALF_UP).intValue());
        bodyMap.put("bankAccountName", withdrawLog.getBankUserName().trim());
        bodyMap.put("bankCardNum", withdrawLog.getBankAccount().trim());
        bodyMap.put("notifyUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl") + ConstantsPayAgent.XINTONG);
        bodyMap.put("clientIp", "127.0.0.1");
        bodyMap.put("remark", "remark");

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();
        bodyMap.put("sign", sign);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity(bodyMap, httpHeaders);

        String res = null;
        try {
            res = restTemplate.postForObject(payAgentPlatform.getPayOrderAddr(), httpEntity, String.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("新通代付下单结果 - result:{}", res);
        if (StringUtils.isNotBlank(res)) {
            Map<String, Object> resultMap = JsonUtil.json2Map(res);
            if (!CollectionUtils.isEmpty(resultMap)) {
                String code = resultMap.getOrDefault("code", "").toString();
                if ("0000".equals(code)) {
                    log.info("新通代付订单提交成功 - result:{}", res);
                    return true;
                } else {
                    reqPayAgent.setFailReason(resultMap.getOrDefault("msg", "").toString());
                    payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
                }
            }
        }
        log.warn("新通代付订单提交失败 - result:{}", res);
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
        String signStr = DigestUtils.md5Hex(tempStr);

        log.info("新通代付回调签名字符串:" + sign + "_" + signStr);
        if (sign.equalsIgnoreCase(signStr)) {
            String mch_order_num = (String) requestMap.get("mch_order_num");

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(mch_order_num);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", mch_order_num);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", mch_order_num);
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(mch_order_num);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "1".equals(status) || "2".equals(status));
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
        Map<String, Object> paramsMap = new TreeMap<>();
        paramsMap.put("mchOrderNum", withdrawLog.getOrderNo());
        paramsMap.put("partnerId", payAgentPlatform.getMerId());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(paramsMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();
        paramsMap.put("sign", sign);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity(paramsMap, httpHeaders);

        String res = null;
        try {
            res = restTemplate.postForObject(payAgentPlatform.getPayOrderQueryAddr(), httpEntity, String.class);
            log.info("新通代付查询结果- result:{}", res);
            if (StringUtils.isNotBlank(res)) {
                Map<String, Object> resultMap = JsonUtil.json2Map(res);
                if (!CollectionUtils.isEmpty(resultMap)) {
                    String code = resultMap.getOrDefault("code", "").toString();
                    if ("1".equals(code)) {
                        Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
                        int statusType = Integer.parseInt(dataMap.getOrDefault("status", "").toString());
                        if (statusType > 0) {
                            // status 4代付中 5代付失败 6代付成功
                            // statusType 0已提交 1已处理 2已出款 3已退回 4已关闭 5已取消
                            int status = 4;
                            if (statusType == 1 || statusType == 2) {
                                status = 6;
                            } else {
                                status = 5;
                            }
                            payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, statusType);
                        }
                    }
                }
                return res;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "新通代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
