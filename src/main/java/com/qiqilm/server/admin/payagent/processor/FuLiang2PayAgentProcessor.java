package com.qiqilm.server.admin.payagent.processor;

import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.AuthUtil;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository(value = ConstantsPayAgent.FULIANG2 + "PayAgentProcessor")
@Log4j2
public class FuLiang2PayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("cpid", payAgentPlatform.getMerId());
        dataMap.put("cp_df_orderno", withdrawLog.getOrderNo());
        dataMap.put("pay_type", "52");
        dataMap.put("pay_day", DateFormatUtils.formate(new Date(), "yyyy-MM-dd"));
        dataMap.put("acount_name", withdrawLog.getBankUserName());
        dataMap.put("acount_bank", withdrawLog.getBankName());
        dataMap.put("acount_province", "广东省");
        dataMap.put("acount_city", "深圳市");
        dataMap.put("acount_num", withdrawLog.getBankAccount());
        dataMap.put("acount_type", "0");
        dataMap.put("acount_bank_branch_no", "123");
        dataMap.put("apply_fee", withdrawLog.getWithdrawMoney().multiply(BigDecimal.valueOf(100)).setScale(0, BigDecimal.ROUND_HALF_UP));
        dataMap.put("apply_type", "301");
        dataMap.put("notify_url", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(dataMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();
        dataMap.put("sign", sign);

        log.warn(payAgentPlatform.getName() + "下单请求参数{}", JsonUtil.object2Json(dataMap));
        String resultStr = null;
        try {
            resultStr = restTemplate.postForObject(payAgentPlatform.getPayOrderAddr(), dataMap, String.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e.getMessage().contains("failed to respond") || e.getMessage().contains("connect timed out")) {
                reqPayAgent.setFailReason("三方网络异常:" + e.getMessage());

                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
                return false;
            }
        }

        log.info(payAgentPlatform.getName() + "下单结果{},订单号:{}", resultStr, withdrawLog.getOrderNo());
        Map<String, Object> resultMap = null;
        if (StringUtils.isNotBlank(resultStr)) {
            resultMap = JsonUtil.json2Map(resultStr);
            if ("0".equals(resultMap.getOrDefault("result_code", "").toString())) {
                log.info(payAgentPlatform.getName() + "订单提交成功 - listResult:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("msg", "").toString());
                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn(payAgentPlatform.getName() + "订单提交失败 - result:{}", resultStr);
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {

        String rspSign = requestMap.remove("sign").toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>(requestMap);
        bodyMap.values().removeIf(value -> StringUtils.isBlank(value.toString()));

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();

        log.info(payAgentPlatform.getName() + "回调签名:" + rspSign + "_" + sign);
        if (rspSign.equalsIgnoreCase(sign)) {
            String order_num = requestMap.getOrDefault("cp_df_orderno", "").toString();
            String status = requestMap.getOrDefault("result_code", "").toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(order_num);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", order_num);
                return "fail";
            }
            if (withdrawLog.getStatus() == 2) {
                log.error("订单已拒绝，无需回调 - merOrderNo:{}", order_num);
                return "success";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", order_num);
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(order_num);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "0".equals(status));
            log.info(payAgentPlatform.getName() + "订单号:{},回调状态:{},", order_num, "0".equals(status) ? "成功" : "失败");
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

        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("cpid", payAgentPlatform.getMerId());
        dataMap.put("pay_day", DateFormatUtils.formate(new Date(), "yyyy-MM-dd"));
        dataMap.put("cp_df_orderno", withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(dataMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();
        dataMap.put("sign", sign);
        log.warn(payAgentPlatform.getName() + "查询代付状态接口请求参数{}", JsonUtil.object2Json(dataMap));

        String resultStr = null;
        try {
            resultStr = restTemplate.postForObject(payAgentPlatform.getPayOrderQueryAddr(), dataMap, String.class);
            log.warn(payAgentPlatform.getName() + "查询结果 - result:{}", resultStr);

            if (StringUtils.isNotBlank(resultStr)) {
                Map<String, Object> resultMap = JsonUtil.json2Map(resultStr);
                //  status 4代付中 5代付失败 6代付成功
                int status = 4;
                //  statusCode 0成功，1待结算，2结算失败，3无请求，4签名失败
                String statusCode = resultMap.getOrDefault("result_code", "").toString();

                if ("0".equals(statusCode) || "2".equals(statusCode) || "3".equals(statusCode) || "4".equals(statusCode)) {
                    if ("0".equals(statusCode)) {
                        status = 6;
                    } else {
                        status = 5;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, Integer.parseInt(statusCode));
                }
                return resultMap.getOrDefault("result_msg", "").toString();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }

}
