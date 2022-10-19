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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.*;


@Repository(value = ConstantsPayAgent.HEIMA + "PayAgentProcessor")
@Log4j2
public class HeiMaPayAgentProcessor extends AbstractPayAgent {

    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {



        SortedMap<String, Object> map = new TreeMap<>();
        map.put("out_trade_no", withdrawLog.getOrderNo());
        map.put("amount", withdrawLog.getWithdrawMoney().setScale(2, BigDecimal.ROUND_HALF_UP));
        map.put("accountname", withdrawLog.getBankUserName());
        map.put("bankname", withdrawLog.getBankName());
        map.put("cardnumber", withdrawLog.getBankAccount());
        map.put("subbranch", "");
        map.put("province", "");
        map.put("city", "");
        map.put("mobile", "");
        map.put("attach", "");

        String list = JsonUtil.object2Json(map);

        SortedMap<String, Object> dataMap = new TreeMap<>();
        dataMap.put("mchid", payAgentPlatform.getMerId());
        dataMap.put("addtime", System.currentTimeMillis() / 1000);
        dataMap.put("bankcode", "unionpay");
        dataMap.put("list", list);
        dataMap.put("callback_url", sysConfigCacheUtil.getConf("payAgentNotifyUrl") + payAgentPlatform.getCode());

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
            if ("success".equals(resultMap.getOrDefault("status", "").toString())) {
                Map listMap = (Map)resultMap.get("list");
                if("1".equals(listMap.getOrDefault("status", "").toString())) {
                    log.info(payAgentPlatform.getName() + "订单提交成功 - listResult:{}", JsonUtil.object2Json(resultMap));
                    return true;
                }
                reqPayAgent.setFailReason(listMap.getOrDefault("msg", "").toString() + resultMap.getOrDefault("msg", "").toString());
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
        Map<String, Object> treeMap = new TreeMap<>(requestMap);
        String rspSign = treeMap.remove("sign").toString();
        treeMap.values().removeIf(value -> StringUtils.isBlank(value.toString()));

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(treeMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);

        log.info(payAgentPlatform.getName() + "回调签名:" + rspSign + "_" + sign);
        if ( rspSign.equalsIgnoreCase( sign ) ) {
            String order_num = requestMap.getOrDefault( "out_trade_no", "" ).toString();
            String status    = requestMap.getOrDefault( "status", "" ).toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( order_num );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", order_num );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 2 ) {
                log.error( "订单已拒绝，无需回调 - merOrderNo:{}", order_num );
                return "ok";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", order_num );
                return "ok";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( order_num );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, "2".equals( status ) );
            log.info( payAgentPlatform.getName() + "订单号:{},回调状态:{},", order_num, "2".equals( status ) ? "成功" : "失败" );
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
    public String queryOrderPay(PayAgentLog payAgentLog) throws Exception {
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(payAgentLog.getWithdrawOrderNo());
        PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById(payAgentLog.getPayAgentPlatId());

        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("mchid", payAgentPlatform.getMerId());
        dataMap.put("out_trade_no", withdrawLog.getOrderNo());
        dataMap.put("applytime", System.currentTimeMillis() / 1000);

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
                //  0或1为等待付款,2为已完成,3为处理中,4为已驳回,5为待核对
                String statusCode = null;

                String code = resultMap.getOrDefault("status", "").toString();
                if (!"1".equals(code)) {
                    statusCode = "4";
                }

                Map<String, Object> map = (Map<String, Object>) resultMap.getOrDefault("data", "");
                if (!CollectionUtils.isEmpty(map)) {
                    statusCode = map.getOrDefault("status", "").toString();
                }

                if ("2".equals(statusCode) || "4".equals(statusCode)) {
                    if ("2".equals(statusCode)) {
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
