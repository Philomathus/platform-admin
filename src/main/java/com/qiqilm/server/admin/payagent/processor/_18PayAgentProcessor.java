package com.qiqilm.server.admin.payagent.processor;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;


@Repository(value = ConstantsPayAgent.EIGHTEEN + "PayAgentProcessor")
@Log4j2
public class _18PayAgentProcessor extends AbstractPayAgent {

    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("merchant_sn", payAgentPlatform.getMerId());
        dataMap.put("down_sn", withdrawLog.getOrderNo());
        dataMap.put("amount", withdrawLog.getWithdrawMoney().multiply(BigDecimal.valueOf(100)).setScale(0, BigDecimal.ROUND_HALF_UP));
        dataMap.put("notify_url", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        dataMap.put("channel_code", "1065");

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(dataMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        dataMap.put("sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(dataMap);
        log.warn(payAgentPlatform.getName() + "下单请求参数:{}", JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestMap, httpHeaders);

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.postForObject(payAgentPlatform.getPayOrderAddr(), httpEntity, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        log.info(payAgentPlatform.getName() + "下单结果 - result:{}", JsonUtil.object2Json(resultMap));
        if (!CollectionUtils.isEmpty(resultMap)) {
            if ("0".equals(resultMap.getOrDefault("code", "").toString())) {
                log.info(payAgentPlatform.getName() + "订单提交成功 - listResult:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("msg", "").toString());
                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn(payAgentPlatform.getName() + "订单提交失败 - result:{}", JsonUtil.object2Json(resultMap));
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String rspSign = requestMap.remove("sign").toString();
        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(requestMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);

        log.info(payAgentPlatform.getName() + "回调签名:" + rspSign + "_" + sign);
        if (rspSign.equalsIgnoreCase(sign)) {
            String order_num = requestMap.getOrDefault("down_sn", "").toString();
            String status = requestMap.getOrDefault("status", "").toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(order_num);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", order_num);
                return "FAIL";
            }
            if ( withdrawLog.getStatus() == 2 ) {
                log.error( "订单已拒绝，无需回调 - merOrderNo:{}", order_num );
                return "SUCCESS";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", order_num);
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(order_num);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "1".equals(status));
            log.info(payAgentPlatform.getName() + "订单号:{},回调状态:{},", order_num, "1".equals(status)? "成功" : "失败");
            return "SUCCESS";
        }
        return "FAIL";
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
        dataMap.put("merchant_sn", payAgentPlatform.getMerId());
        dataMap.put("down_sn", withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(dataMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        dataMap.put("sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(dataMap);
        log.warn(payAgentPlatform.getName() + "下单请求参数:{}", JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestMap, httpHeaders);

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.postForObject(payAgentPlatform.getPayOrderQueryAddr(), httpEntity, Map.class);
            log.warn(payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json(resultMap));

            if (!CollectionUtils.isEmpty(resultMap)) {
                //  status
                //  4代付中 5代付失败 6代付成功
                int status = 4;

                //  statusCode
                //  1：支付成功 2：支付失败 3：未支付 4：支付中
                String statusCode = null;

                String code = resultMap.getOrDefault("code", "").toString();
                if (!"0".equals(code)) {
                    statusCode = "2";
                }

                statusCode = resultMap.getOrDefault("status", "").toString();

                if ("1".equals(statusCode) || "2".equals(statusCode)) {
                    if ("1".equals(statusCode)) {
                        status = 6;
                    } else {
                        status = 5;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, Integer.parseInt(statusCode));
                }
                return resultMap.getOrDefault("msg", "").toString();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }

}
