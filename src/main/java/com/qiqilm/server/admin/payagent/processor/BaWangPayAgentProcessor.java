package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.JsonUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository(value = ConstantsPayAgent.BAWANG + "PayAgentProcessor")
@Log4j2
public class BaWangPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("mchId", payAgentPlatform.getMerId());
        dataMap.put("mchOrderNo", withdrawLog.getOrderNo());
        dataMap.put("amount", withdrawLog.getWithdrawMoney().multiply(BigDecimal.valueOf(100)).setScale(2,BigDecimal.ROUND_HALF_UP).intValue());
        dataMap.put("accountName", withdrawLog.getBankUserName().trim());
        dataMap.put("accountNo", withdrawLog.getBankAccount().trim());
        dataMap.put("bankName", withdrawLog.getBankName());
        dataMap.put("notifyUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        dataMap.put("remark", withdrawLog.getBankName());
        dataMap.put("reqTime", DateFormatUtils.formate(new Date(), "yyyyMMddHHmmss"));

        String signMd5 = payAgentPlatform.getHeaderKey();
        String tempStr = this.assemblyUrl(dataMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();
        dataMap.put("sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(dataMap);
        log.warn(payAgentPlatform.getName()+"下单请求参数{}",JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestMap, httpHeaders);

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute( payAgentPlatform.getPayOrderAddr(), HttpMethod.POST,
                    restTemplate.httpEntityCallback( httpEntity ), response -> {
                        InputStream bodyStream = response.getBody();
                        String      text;
                        try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                            text = CharStreams.toString( reader );
                        }
                        return JsonUtil.json2Map( text );
                    } );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());

        if (!CollectionUtils.isEmpty(resultMap)) {
            if ("SUCCESS".equals(resultMap.getOrDefault("retCode", "").toString())) {
                log.info(payAgentPlatform.getName()+"订单提交成功 - listResult:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("retMsg", "").toString());
                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn(payAgentPlatform.getName()+"订单提交失败 - result:{}", JsonUtil.object2Json(resultMap));
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {

        String rspSign = requestMap.remove("sign").toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>(requestMap);
        String signMd5 = payAgentPlatform.getHeaderKey();
        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();

        log.info(payAgentPlatform.getName()+"回调签名:" + rspSign + "_" + sign);
        if (rspSign.equalsIgnoreCase(sign)) {
            String order_num = requestMap.getOrDefault("mchOrderNo", "").toString();
            String status = requestMap.getOrDefault("status", "").toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(order_num);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", order_num);
                return "fail";
            }
            if ( withdrawLog.getStatus() == 2 ) {
                log.error( "订单已拒绝，无需回调 - merOrderNo:{}", order_num );
                return "success";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", order_num);
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(order_num);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "2".equals(status));
            log.info(payAgentPlatform.getName() + "订单号:{},回调状态:{},", order_num, "2".equals(status)? "成功" : "失败");
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
        dataMap.put("mchId", payAgentPlatform.getMerId());
        dataMap.put("mchOrderNo", withdrawLog.getOrderNo());
        dataMap.put("reqTime", DateFormatUtils.formate(new Date(), "yyyyMMddHHmmss"));
        String signMd5 = payAgentPlatform.getHeaderKey();
        String tempStr = this.assemblyUrl(dataMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();
        dataMap.put("sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(dataMap);
        log.warn(payAgentPlatform.getName()+"查询代付状态接口请求参数{}",JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestMap, httpHeaders);

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute( payAgentPlatform.getPayOrderQueryAddr(), HttpMethod.POST,
                    restTemplate.httpEntityCallback( httpEntity ), response -> {
                        InputStream bodyStream = response.getBody();
                        String      text;
                        try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                            text = CharStreams.toString( reader );
                        }
                        return JsonUtil.json2Map( text );
                    } );
            log.warn(payAgentPlatform.getName()+"查询结果 - result:{}", JsonUtil.object2Json(resultMap));

            if (!CollectionUtils.isEmpty(resultMap)) {
                //  status 4代付中 5代付失败 6代付成功
                int status = 4;
                //  statusCode 0-待处理,1-处理中,2-成功,3-失败
                String statusCode = resultMap.getOrDefault("status", "").toString();

                if("2".equals(statusCode) || "3".equals(statusCode)){
                    if ("2".equals(statusCode)) {
                        status = 6;
                    } else {
                        status = 5;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, Integer.parseInt(statusCode));
                }
                return resultMap.getOrDefault("transMsg", "").toString();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return payAgentPlatform.getName()+"查询失败,订单号:"+withdrawLog.getOrderNo();
    }

}
