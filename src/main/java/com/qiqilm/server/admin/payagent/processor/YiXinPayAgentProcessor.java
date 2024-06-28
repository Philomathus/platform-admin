package com.qiqilm.server.admin.payagent.processor;


import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeYiXinType;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.AuthUtil;
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
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository(value = ConstantsPayAgent.YIXIN + "PayAgentProcessor")
@Log4j2
public class YiXinPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        BankCodeYiXinType bankCodeType = BankCodeYiXinType.getCodeByDesc(withdrawLog.getBankName());
        if (bankCodeType == null) {
            payAgentService.callBackOrder( withdrawLog,payAgentPlatform );
            log.warn("此代付无法支持的银行类型 - 银行类型:{}", withdrawLog.getBankName());
            throw new BusinessException("此代付无法支持的银行类型：" + withdrawLog.getBankName());
        }
        withdrawLog.setBankCode(bankCodeType.name());
        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("order_no", withdrawLog.getOrderNo());
        dataMap.put("mch_account", payAgentPlatform.getMerId());
        dataMap.put("amount", withdrawLog.getWithdrawMoney().multiply(new BigDecimal(10000)).setScale(0, BigDecimal.ROUND_HALF_UP));
        dataMap.put("account_type", 0);
        dataMap.put("account_no", withdrawLog.getBankAccount().trim());
        dataMap.put("account_name", withdrawLog.getBankUserName().trim());
        dataMap.put("bank_code", withdrawLog.getBankCode());
        dataMap.put("bank_province", "广东省");
        dataMap.put("bank_city", "广州市");
        dataMap.put("bank_name", withdrawLog.getBankName());
        dataMap.put("call_back_url", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(dataMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();
        dataMap.put("sign", sign);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity(dataMap, httpHeaders);

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
            reqPayAgent.setFailReason("亿信代付下单报错原因:" + e);
        }
        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            if ("200".equals(resultMap.getOrDefault("ret", "").toString())) {
                String status = resultMap.getOrDefault("status", "").toString();
                if ("0".equals(status) || "1".equals(status)) {
                    log.info("亿信代付订单提交成功 - listResult:{}", JsonUtil.object2Json(resultMap));
                    return true;
                }
                reqPayAgent.setFailReason(resultMap.getOrDefault("msg", "").toString());

                payAgentService.callBackOrder( withdrawLog,payAgentPlatform );
            }
        }
        log.warn("亿信代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String rspSign = requestMap.remove("sign").toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>(requestMap);
        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();

        log.info("亿信代付回调签名:" + sign + "_" + rspSign);
        if (rspSign.equalsIgnoreCase(sign)) {
            String order_no = requestMap.getOrDefault("order_no", "").toString();
            int status = Integer.parseInt(requestMap.getOrDefault("status", "").toString());

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(order_no);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", order_no);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", order_no);
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(order_no);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, status == 2);
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
        dataMap.put("mch_account", payAgentPlatform.getMerId());
        dataMap.put("order_no", withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(dataMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();
        dataMap.put("sign", sign);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity(dataMap, httpHeaders);

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
            log.info("亿信代付查询结果 - listResult:{}", JsonUtil.object2Json(resultMap));
            if (!CollectionUtils.isEmpty(resultMap)){
                if("200".equals(resultMap.getOrDefault("ret", "").toString())) {
                    int statusType = Integer.parseInt(resultMap.getOrDefault("status", "").toString());
                    // status 4代付中 5代付失败 6代付成功
                    // statusType 订单状态: 0.代付中 1.代付中 2.代付成功 3.代付失败 4.审核失败
                    if (statusType == 3 || statusType == 4 || statusType == 2) {
                        int status = 4;
                        if (statusType == 2) {
                            status = 6;
                        } else {
                            status = 5;
                        }
                        payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, statusType);
                    }
                }
                return resultMap.getOrDefault("msg", "").toString();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "亿信代付查询失败,订单号:"+withdrawLog.getOrderNo();
    }
}
