package com.qiqilm.server.admin.payagent.processor;


import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.*;

@Repository(value = ConstantsPayAgent.XIN_HUA_ZI + "PayAgentProcessor")
@Log4j2
public class XinHuaZiPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        Map<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("mch_id", payAgentPlatform.getMerId());
        bodyMap.put( "pay_date", DateFormatUtils.formate( reqPayAgent.getCurrentTime(),
                DateFormatUtils.SPLIT_PATTERN_DATETIME ) );
        bodyMap.put( "out_trade_no", withdrawLog.getOrderNo() );
        bodyMap.put("total_fee", withdrawLog.getWithdrawMoney().setScale(0,
                BigDecimal.ROUND_HALF_UP));
        bodyMap.put("accNo", withdrawLog.getBankAccount().trim());
        bodyMap.put("accName", withdrawLog.getBankUserName().trim());

        bodyMap.put("bank_name", withdrawLog.getBankName().trim());
        bodyMap.put("notify_url", sysConfigCacheUtil.getConf("payAgentNotifyUrl") + ConstantsPayAgent.XIN_HUA_ZI);
        bodyMap.put("remark", "daifu");
        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        String tempStr = this.assemblyUrl(bodyMap) + "&api_key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();
        bodyMap.put("sign", sign);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity( bodyMap, httpHeaders );
        System.out.println("请求参数:"+httpEntity);
        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.postForObject(payAgentPlatform.getPayOrderAddr(), httpEntity, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            reqPayAgent.setFailReason(e.getMessage());
        }
        log.warn("钱宝代付下单结果:" + JsonUtil.object2Json(resultMap));
        if (!CollectionUtils.isEmpty(resultMap)) {
            if ("0000".equals(resultMap.getOrDefault("code", "").toString())) {
                log.info("钱宝代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("msg", "").toString());
            }
        }
        log.warn("钱宝代付订单提交失败 - result:{}", JsonUtil.object2Json(resultMap));
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        if (this.checkWhiteIp(payAgentPlatform.getPlatWhiteIpList(), realIp)) {
            log.warn("请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json(requestMap));
            return "fail";
        }
        String sign = requestMap.remove("sign").toString();

        SortedMap<String, Object> signMap = new TreeMap<>(requestMap);
        String tempStr = this.assemblyUrl(requestMap);

        //RSA 2048 SHA256 公钥验签
        if (RSACoder.verifySha256Rsa(tempStr, payAgentPlatform.getSignPublicKey(), sign)) {
            String state = signMap.getOrDefault("state", "").toString();
            String orderNo = signMap.getOrDefault("mchOrderNo", "").toString();
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(orderNo);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", orderNo);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", orderNo);
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(orderNo);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, orderNo, payAgentPlatform,
                    "2".equals(state));
            return "SUCCESS";
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
        Map<String, String> dataMap = new LinkedHashMap<>();
        dataMap.put( "merid", payAgentPlatform.getMerId());
        dataMap.put( "pay_date", DateFormatUtils.formate( payAgentLog.getCreateTime(),
                DateFormatUtils.SPLIT_PATTERN_DATETIME ) );
        dataMap.put("out_trade_no", withdrawLog.getOrderNo());
        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        String tempStr = this.assemblyUrl(dataMap) + "&api_key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();
        dataMap.put("sign", sign);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity( dataMap, httpHeaders );
        System.out.println("请求参数:"+httpEntity);
        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.postForObject(payAgentPlatform.getPayOrderQueryAddr(), httpEntity, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.warn("华子代付查询结果:" + resultMap);
        if (!CollectionUtils.isEmpty(resultMap)) {

            if ("交易成功".equals(resultMap.getOrDefault("pay_result", "").toString())) {
                String state = resultMap.getOrDefault("code", "").toString();

                // status 4代付中 5代付失败 6代付成功
                // state 1处理中 2支付成功 3支付失败
                int status = 4;
                if ("0000".equals(state)) {
                    status = 6;
                } else if (status == 3) {
                    status = 5;
                }
                log.warn("state:{}", resultMap);
                payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, 0);
                return;
            }
        }
        log.warn("代付订单查询失败 - result:{}", resultMap);
    }
}
