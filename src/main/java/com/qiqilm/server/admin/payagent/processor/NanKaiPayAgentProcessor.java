package com.qiqilm.server.admin.payagent.processor;


import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.*;
import com.qiqilm.server.admin.utils.nanKaiPayAgentUtils.HttpClientUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository(value = ConstantsPayAgent.NANKAI + "PayAgentProcessor")
@Log4j2
public class NanKaiPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        //南开代付:header_key = paykey, MD5_key = paySecret
        String notifyUrl = sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("payKey", payAgentPlatform.getHeaderKey());
        bodyMap.put("cardNo", withdrawLog.getBankAccount().trim());
        bodyMap.put("cardName", withdrawLog.getBankUserName().trim());
        bodyMap.put("noticeUrl", notifyUrl);
        bodyMap.put("orderNo", withdrawLog.getOrderNo());
        bodyMap.put("tranTime", df.format(new Date()));
        bodyMap.put("tranAmt", withdrawLog.getWithdrawMoney().multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP));//精确到分

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String httpOrgCreateTestRtn = null;
        try {
            //paySecret为MD5密钥,pubKey为公钥,priKey为私钥
            // doPost(String url, String merchantNo, JSONObject data, String paySecret, String pubKey, String priKey) {
            httpOrgCreateTestRtn = HttpClientUtils.doPost(payAgentPlatform.getPayOrderAddr(),
                    payAgentPlatform.getMerId(), bodyMap, signMd5, payAgentPlatform.getSignPublicKey(), payAgentPlatform.getSignPrivateKey());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            reqPayAgent.setFailReason("南开代付下单报错原因:" + e);
        }
        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", httpOrgCreateTestRtn,withdrawLog.getOrderNo());
        if (StringUtils.isNoneBlank(httpOrgCreateTestRtn)) {
            Map<String, Object> resultMap = JsonUtil.json2Map(httpOrgCreateTestRtn);
            String respCode = resultMap.getOrDefault("respCode", "").toString();
            String resultFlag = resultMap.getOrDefault("resultFlag", "").toString();
            if (("0000".equals(respCode) && "0".equals(resultFlag)) || (("0001".equals(respCode) || "0002".equals(respCode)) && "2".equals(resultFlag))) {
                log.info("南开代付订单提交成功 - result:{}", httpOrgCreateTestRtn);
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("msg", "").toString());

                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn("南开代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        if (this.checkWhiteIp(payAgentPlatform.getPlatWhiteIpList(), realIp)) {
            log.warn("请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json(requestMap));
            return "fail";
        }

        String request = requestMap.getOrDefault("transData", "").toString();
        try {
            String res = HttpClientUtils.decrypt(request, payAgentPlatform.getSignPrivateKey());
            Map<String, Object> resultMap = JsonUtil.json2Map(res);
            String orderNo = resultMap.getOrDefault("orderNo", "").toString();
            String resultFlag = resultMap.getOrDefault("resultFlag", "").toString();
            log.info("南开代付回调,resultFlag的值为: {}", resultFlag);
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(orderNo);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", orderNo);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", orderNo);
                return "0";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(orderNo);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, orderNo, payAgentPlatform, "0".equals(resultFlag));
            return "0";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
        //南开代付:header_key = paykey, MD5_key = paySecret
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("payKey", payAgentPlatform.getHeaderKey());
        bodyMap.put("orderNo", withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String httpOrgCreateTestRtn = null;
        try {
            httpOrgCreateTestRtn = HttpClientUtils.doPost(payAgentPlatform.getPayOrderQueryAddr(), payAgentPlatform.getMerId(), bodyMap,
                    signMd5, payAgentPlatform.getSignPublicKey(), payAgentPlatform.getSignPrivateKey());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.warn("南开代付查询结果:" + httpOrgCreateTestRtn);
        if (StringUtils.isNoneBlank(httpOrgCreateTestRtn)) {
            Map<String, Object> resultMap = JsonUtil.json2Map(httpOrgCreateTestRtn);
            if (!CollectionUtils.isEmpty(resultMap)) {
                if ("0000".equals(resultMap.getOrDefault("respCode", "").toString())) {
                    int resultFlag = Integer.parseInt(resultMap.getOrDefault("resultFlag", "").toString());
                    // status 4代付中 5代付失败 6代付成功
                    // resultFlag 0-成功 1-失败 2-处理中
                    if (resultFlag == 0 || resultFlag == 1) {
                        int status = 4;
                        if (resultFlag == 0) {
                            resultFlag = 6;
                        } else {
                            resultFlag = 5;
                        }
                        log.warn("state:{}", resultFlag);
                        payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, resultFlag);
                    }
                }
                return resultMap.getOrDefault("msg", "").toString();
            }
            return httpOrgCreateTestRtn;
        }
        return "南开代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
