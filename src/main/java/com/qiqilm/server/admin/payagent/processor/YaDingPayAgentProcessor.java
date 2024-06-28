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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

@Repository(value = ConstantsPayAgent.YADING + "PayAgentProcessor")
@Log4j2
public class YaDingPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("username", payAgentPlatform.getMerId());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("CardName",withdrawLog.getBankUserName());
        map.put("Phone",withdrawLog.getBankUserName());
        map.put("OpenBank",withdrawLog.getBankName());
        map.put("CardNum",withdrawLog.getBankAccount());
        map.put("Amount",withdrawLog.getWithdrawMoney().setScale(2,BigDecimal.ROUND_HALF_UP));
        map.put("ReturnUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        map.put("OutOrderId", withdrawLog.getOrderNo());
        List<Map<String,Object>> dataList = new LinkedList<>();
        dataList.add(map);
        dataMap.put("data", JsonUtil.object2Json(dataList));

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = JsonUtil.object2Json(dataList) + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();
        dataMap.put("sign", sign);

        log.warn(payAgentPlatform.getName()+"下单请求参数{}",JsonUtil.object2Json(dataMap));
        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.postForObject(payAgentPlatform.getPayOrderAddr(), dataMap, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            reqPayAgent.setFailReason(payAgentPlatform.getName()+"下单报错原因:" + e);
        }

        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            String dataStr = resultMap.getOrDefault("data", "").toString();
            String code = resultMap.getOrDefault("code", "").toString();
            String success = resultMap.getOrDefault("success", "").toString();

            if ("1".equals(code) && !"0".equals(success)) {
                log.info(payAgentPlatform.getName()+"订单提交成功 - listResult:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                Map data = (Map) resultMap.getOrDefault("data", new HashMap<>());
                reqPayAgent.setFailReason(data.get("failMsg").toString());
                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn(payAgentPlatform.getName()+"订单提交失败 - result:{}", JsonUtil.object2Json(resultMap));
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String rspSign = requestMap.remove("sign").toString();
        requestMap.remove("code");
        requestMap.values().removeIf(value -> StringUtils.isBlank(value.toString()));
        Double amount = (Double)requestMap.get("amount");
        requestMap.put("amount",BigDecimal.valueOf(amount).setScale(2,BigDecimal.ROUND_HALF_UP));

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(requestMap) + "&access_token=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();

        log.info(payAgentPlatform.getName()+"回调签名:" + rspSign + "_" + sign);
        if (rspSign.equalsIgnoreCase(sign)) {
            String order_num = requestMap.getOrDefault("outOrderId", "").toString();
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
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "2".equals(status));
            log.info(payAgentPlatform.getName() + "订单号:{},回调状态:{},", order_num, "2".equals(status)? "成功" : "失败");
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

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("username", payAgentPlatform.getMerId());

        Map<String, Object> orderMap = new LinkedHashMap<>();
        orderMap.put("outOrderId",withdrawLog.getOrderNo());
        String data = JsonUtil.object2Json(orderMap);
        dataMap.put("data", data);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = data + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();
        dataMap.put("sign", sign);
        log.warn(payAgentPlatform.getName()+"查询代付状态接口请求参数{}",JsonUtil.object2Json(dataMap));

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.postForObject(payAgentPlatform.getPayOrderQueryAddr(), dataMap, Map.class);
            log.warn(payAgentPlatform.getName()+"查询结果 - result:{}", JsonUtil.object2Json(resultMap));

            if (!CollectionUtils.isEmpty(resultMap)) {
                //  status
                //  4代付中 5代付失败 6代付成功
                int status = 4;

                //  statusCode
                //  1-代付中 2-代付完成 3-代付失败 4-通过审核
                String statusCode = null;

                String code = resultMap.getOrDefault("code", "").toString();
                if(!"1".equals(code)){
                    statusCode = "3";
                }

                Map<String,Object> map = (Map<String,Object>)resultMap.getOrDefault("data", "");
                if(!CollectionUtils.isEmpty(map)){
                    statusCode = map.getOrDefault("status", "").toString();
                }

                if("2".equals(statusCode) || "3".equals(statusCode)){
                    if ("2".equals(statusCode)) {
                        status = 6;
                    } else {
                        status = 5;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, Integer.parseInt(statusCode));
                }
                return resultMap.getOrDefault("msg","").toString();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return payAgentPlatform.getName()+"查询失败,订单号:"+withdrawLog.getOrderNo();
    }

}
