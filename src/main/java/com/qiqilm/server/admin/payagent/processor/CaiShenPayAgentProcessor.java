package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.*;

@Repository(value = ConstantsPayAgent.CAISHEN + "PayAgentProcessor")
@Log4j2
public class CaiShenPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String merchant_no = payAgentPlatform.getMerId();
        String tran_flow = withdrawLog.getOrderNo();
        String amount = withdrawLog.getWithdrawMoney().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        String acc_no = withdrawLog.getBankAccount();
        String acc_name = withdrawLog.getBankUserName().trim();

        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("version","v4");
        bodyMap.put("merchant_no",merchant_no);
        bodyMap.put("tran_flow",tran_flow);
        bodyMap.put("tran_date", DateFormatUtils.formate(new Date(), "yyyy-MM-dd"));
        bodyMap.put("tran_time", DateFormatUtils.formate(new Date(), "HH:mm:ss"));
        bodyMap.put("acc_no", acc_no);
        bodyMap.put("acc_name", acc_name);
        bodyMap.put("amount", amount);
        bodyMap.put("bank_name", withdrawLog.getBankName().trim());
        bodyMap.put("ext1", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String tempSign = "md5="+signMd5 + "&" + "merchant_no=" + merchant_no + "&" + "tran_flow="
                + tran_flow + "&" + "amount=" + amount + "&" + "acc_no=" + acc_no + "&"
                + "acc_name=" + acc_name;
        String encrypted = DigestUtils.md5Hex(tempSign);
        bodyMap.put("encrypted",encrypted);

        log.warn(payAgentPlatform.getName()+"下单请求参数{}",JsonUtil.object2Json(bodyMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity(bodyMap, httpHeaders);

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
            reqPayAgent.setFailReason(payAgentPlatform.getName()+"下单报错原因:" + e);
        }
        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            String code = resultMap.getOrDefault("rtn_code", "").toString();
            if (code.equals("0001")) {
                log.info(payAgentPlatform.getName()+"提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("rtn_msg", "").toString());
                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn(payAgentPlatform.getName()+"提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String sign = requestMap.remove("sign").toString();
        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tranFlow = requestMap.getOrDefault("tran_flow", "").toString();
        String signStr = DigestUtils.md5Hex(tranFlow + signMd5 );

        log.info("财神代付回调签名字符串:" + sign + "_" + signStr);
        if (sign.equalsIgnoreCase(signStr)) {
            String merOrderNo = requestMap.getOrDefault("tran_flow", "").toString();
            String rtnCode = requestMap.getOrDefault("rtn_code", "").toString();

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
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "0000".equals(rtnCode));
            log.info(payAgentPlatform.getName() + "订单号:{},回调状态:{},", merOrderNo, "0000".equals(rtnCode) ? "成功" : "失败");
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
        paramsMap.put("version","v4");
        paramsMap.put("merchant_no",payAgentPlatform.getMerId());
        paramsMap.put("tran_date", DateFormatUtils.formate(new Date(), "yyyy-MM-dd"));
        paramsMap.put("tran_time", DateFormatUtils.formate(new Date(), "HH:mm:ss"));
        paramsMap.put("tran_flow",withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                        "secretkey/payAgentPrivateKey"));
        paramsMap.put("sign", DigestUtils.md5Hex(signMd5+withdrawLog.getOrderNo()).toUpperCase());

        log.warn(payAgentPlatform.getName()+"查询代付状态接口请求参数{}",JsonUtil.object2Json(paramsMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity(paramsMap, httpHeaders);

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
            log.info(payAgentPlatform.getName()+"查询结果:{}", JsonUtil.object2Json(resultMap));

            if (!CollectionUtils.isEmpty(resultMap)) {
                //  status 4代付中 5代付失败 6代付成功
                int status = 4;
                //  statusCode
                //  0000 交易成功(处理完成，已经到账)  0001 交易成功(处理中)  0002 交易失败  0006 是没有找到订单  9999 系统异常
                String statusCode = resultMap.getOrDefault("rtn_code", "").toString();

                if("0000".equals(statusCode) || "0002".equals(statusCode) || "0006".equals(statusCode) || "9999".equals(statusCode)){
                    if ("0000".equals(statusCode)) {
                        status = 6;
                    } else {
                        status = 5;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, Integer.valueOf(statusCode));
                }
                return resultMap.getOrDefault("rtn_msg", "").toString();
            }
        } catch (
                Exception e) {
            log.error(e.getMessage(), e);
        }
        return payAgentPlatform.getName()+"查询失败,订单号:" + withdrawLog.getOrderNo();
    }

}
