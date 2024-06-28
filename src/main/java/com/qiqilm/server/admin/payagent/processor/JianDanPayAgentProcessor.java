package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLEncoder;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository(value = ConstantsPayAgent.JIANDAN + "PayAgentProcessor")
@Log4j2
public class JianDanPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("secret_id",payAgentPlatform.getMerId());
        bodyMap.put("signtime",System.currentTimeMillis()/1000);
        bodyMap.put("ver","1.0");

        bodyMap.put("card_num",withdrawLog.getBankAccount());
        bodyMap.put("holder_name", withdrawLog.getBankUserName().trim());
        bodyMap.put("bank_name", withdrawLog.getBankName().trim());
        bodyMap.put("branch_bank_name", withdrawLog.getBankName().trim());
        bodyMap.put("money", withdrawLog.getWithdrawMoney());
        bodyMap.put("notify_url", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put("extend_info", withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String tempStr = this.assemblyUrl(bodyMap);
        tempStr = URLEncoder.encode(tempStr,"UTF-8").replace("*","%2A").replace("+","%20").replace("%7E","~");
        String sign = DigestUtils.md5Hex(tempStr) + signMd5;
        bodyMap.put("sign", DigestUtils.md5Hex(sign));

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(bodyMap);
        log.warn("简单代付下单请求参数{}",JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute(payAgentPlatform.getPayOrderAddr(), HttpMethod.POST,
                    restTemplate.httpEntityCallback(httpEntity), response -> {
                        InputStream bodyStream = response.getBody();
                        String text;
                        try (Reader reader = new InputStreamReader(bodyStream)) {
                            text = CharStreams.toString(reader);
                        }
                        return JsonUtil.json2Map(text);
                    });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            reqPayAgent.setFailReason("简单代付下单报错原因:" + e);
        }
        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            Boolean boo = (Boolean)resultMap.getOrDefault("ok", "");
            if (boo) {
                String orderCode = resultMap.getOrDefault("order_code", "").toString();
                PayAgentLog payAgentLog = payAgentLogMapper.selectPayAgentLogOrderNo(withdrawLog.getOrderNo());
                PayAgentLog pupdate = new PayAgentLog();
                pupdate.setId(payAgentLog.getId());
                pupdate.setPayAgentOrderNo(orderCode);
                payAgentLogMapper.updatePayAgentLog(pupdate);
                log.info("简单代付提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("error", "").toString());
                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn("简单代付提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String sign = requestMap.remove("sign").toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>(requestMap);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String tempStr = this.assemblyUrl(bodyMap);
        tempStr = URLEncoder.encode(tempStr,"UTF-8").replace("*","%2A").replace("+","%20").replace("%7E","~");
        String signStr = DigestUtils.md5Hex(tempStr) + signMd5;
        signStr = DigestUtils.md5Hex(signStr);

        log.info("简单代付回调签名字符串:" + sign + "_" + signStr);
        if (sign.equalsIgnoreCase(signStr)) {
            String merOrderNo = requestMap.getOrDefault("extend_info", "").toString();
            String status = requestMap.getOrDefault("status", "").toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(merOrderNo);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", merOrderNo);
                return "fail";
            }
            if ( withdrawLog.getStatus() == 2 ) {
                log.error( "订单已拒绝，无需回调 - merOrderNo:{}", merOrderNo );
                return "ok";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", merOrderNo);
                return "ok";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(merOrderNo);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "3".equals(status));
            log.info(payAgentPlatform.getName() + "订单号:{},回调状态:{},", merOrderNo, "3".equals(status)? "成功" : "失败");
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
        Map<String, Object> paramsMap = new TreeMap<>();
        paramsMap.put("secret_id",payAgentPlatform.getMerId());
        paramsMap.put("signtime",System.currentTimeMillis()/1000);
        paramsMap.put("ver","1.0");

        String orderNo = payAgentLog.getPayAgentOrderNo();
        if(StringUtils.isBlank(orderNo)){
            return "简单代付查询失败,订单号:" + withdrawLog.getOrderNo();
        }
        paramsMap.put("order_code",orderNo);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                        "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(paramsMap);
        tempStr = URLEncoder.encode(tempStr,"UTF-8").replace("*","%2A").replace("+","%20").replace("%7E","~");
        String sign = DigestUtils.md5Hex(tempStr) + signMd5;
        paramsMap.put("sign", DigestUtils.md5Hex(sign));

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(paramsMap);
        log.warn(JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute(payAgentPlatform.getPayOrderQueryAddr(), HttpMethod.POST,
                    restTemplate.httpEntityCallback(httpEntity), response -> {
                        InputStream bodyStream = response.getBody();
                        String text;
                        try (Reader reader = new InputStreamReader(bodyStream)) {
                            text = CharStreams.toString(reader);
                        }
                        return JsonUtil.json2Map(text);
                    });
            log.info("简单代付查询结果:{}", JsonUtil.object2Json(resultMap));


            if (!CollectionUtils.isEmpty(resultMap)) {
                //  status 4代付中 5代付失败 6代付成功
                int status = 4;
                //  statusCode 1.待处理，2.处理中，3.结算成功，4.失败，5.冲正
                int statusCode = Integer.parseInt(resultMap.getOrDefault("status", "").toString());

                Boolean boo = (Boolean)resultMap.getOrDefault("ok", "");
                if(!boo){
                    statusCode = 4;
                }

                if(statusCode == 3 || statusCode == 4 || statusCode == 5){
                    if (statusCode == 3) {
                        status = 6;
                    } else {
                        status = 5;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, statusCode);
                }
                return resultMap.getOrDefault("msg", "").toString();
            }
        } catch (
                Exception e) {
            log.error(e.getMessage(), e);
        }
        return "简单代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }

}
