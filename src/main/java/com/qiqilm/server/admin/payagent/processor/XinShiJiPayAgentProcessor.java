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

import java.math.RoundingMode;
import java.util.*;

@Repository(value = ConstantsPayAgent.NEWSHIJI + "PayAgentProcessor")
@Log4j2
public class XinShiJiPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("mchid", payAgentPlatform.getMerId());
        bodyMap.put("out_trade_no", withdrawLog.getOrderNo());
        bodyMap.put("money", withdrawLog.getWithdrawMoney().setScale(2, RoundingMode.HALF_UP));
        bodyMap.put("bankname", withdrawLog.getBankName().trim());
        bodyMap.put("subbranch", withdrawLog.getBankName().trim());
        bodyMap.put("accountname", withdrawLog.getBankUserName().trim());
        bodyMap.put("cardnumber", withdrawLog.getBankAccount().trim());
        bodyMap.put("province", "广东省");
        bodyMap.put("city", "广州市");
        bodyMap.put("notifyurl", sysConfigCacheUtil.getConf("payAgentNotifyUrl") + ConstantsPayAgent.NEWSHIJI);

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey" ) );

        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();
        bodyMap.put("pay_md5sign", sign);



        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(bodyMap);
        log.warn(JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.postForObject(payAgentPlatform.getPayOrderAddr(), httpEntity, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("新世纪代付下单结果 - result:{}", JsonUtil.object2Json(resultMap));
        if (!CollectionUtils.isEmpty(resultMap)) {
            if ("success".equals(resultMap.getOrDefault("status", "").toString())) {
                log.info("新世纪代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("msg", "").toString());
            }
        }
        log.warn("新世纪代付订单提交失败 - result:{}", JsonUtil.object2Json(resultMap));
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String pay_md5sign = requestMap.remove("pay_md5sign").toString();
        String status = requestMap.getOrDefault("status", "").toString();
        if(!StringUtils.hasText(requestMap.getOrDefault("msg","").toString())) {
            requestMap.remove("msg");
        }
        SortedMap<String, Object> bodyMap = new TreeMap<>(requestMap);

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey" ) );

        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();

        log.info("新世纪代付回调待签名字符串:" + pay_md5sign + "_" +sign);
        if (pay_md5sign.equalsIgnoreCase(sign)) {
            String out_trade_no = (String) requestMap.get("out_trade_no");

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(out_trade_no);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", out_trade_no);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", out_trade_no);
                return "ok";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(out_trade_no);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "success".equals(status));
            return "ok";
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
        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("out_trade_no", withdrawLog.getOrderNo());
        dataMap.put("mchid", payAgentPlatform.getMerId());

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey" ) );

        String tempStr = this.assemblyUrl(dataMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();
        dataMap.put("pay_md5sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(dataMap);
        log.warn(JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

        String res = null;
        try {
            res = restTemplate.postForObject(payAgentPlatform.getPayOrderQueryAddr(), httpEntity, String.class);
            log.info("新世纪代付下单结果 - result:{}", res);
            Map<String, Object> resultMap = JsonUtil.json2Map(res);
            if (!CollectionUtils.isEmpty(resultMap)) {
                String success = resultMap.getOrDefault("status", "").toString();
                int refCode = Integer.parseInt(resultMap.getOrDefault("refCode", "").toString());
                if ("success".equals(success)) {
                    // status 4代付中 5代付失败 6代付成功
                    // refCode 1成功 2失败 3处理中 4待处理
                    int status = 4;
                    if (refCode == 1) {
                        status = 6;
                        refCode = 1;
                    } else if (refCode == 2 || refCode == 5 || refCode == 7 || refCode == 8) {
                        status = 5;
                        refCode = 2;
                    } else{
                        refCode = 3;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, refCode);
                    return;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
}
