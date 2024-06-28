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
import org.apache.logging.log4j.util.Strings;
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
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository(value = ConstantsPayAgent.YIXIN2 + "PayAgentProcessor")
@Log4j2
public class YiXin2PayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        BankCodeYiXinType bankCodeType = BankCodeYiXinType.getCodeByDesc(withdrawLog.getBankName());
        if (bankCodeType == null) {
            payAgentService.callBackOrder( withdrawLog,payAgentPlatform );
            log.warn("此代付无法支持的银行类型 - 银行类型:{}", withdrawLog.getBankName());
            throw new BusinessException("此代付无法支持的银行类型：" + withdrawLog.getBankName());
        }
        withdrawLog.setBankCode(bankCodeType.name());
        Map<String, String> dataMap = new TreeMap<>();
        dataMap.put("app_id", payAgentPlatform.getMerId());
        dataMap.put("out_trade_no", withdrawLog.getOrderNo());
        dataMap.put("money", withdrawLog.getWithdrawMoney().toString());
        dataMap.put("card_number", withdrawLog.getBankAccount().trim());
        dataMap.put("card_name", withdrawLog.getBankUserName().trim());
        dataMap.put("bank_name", withdrawLog.getBankName());
        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(dataMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();
        dataMap.put("sign", sign);
        dataMap.put("bank_code", withdrawLog.getBankCode());
        dataMap.put("notify_url", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        dataMap.put("channel", "SK1");

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(dataMap);
        log.info("亿信代付申请参数:{}",JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

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
            log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
                if (!CollectionUtils.isEmpty(resultMap)) {
                    if ("200".equals(resultMap.getOrDefault("code", "").toString())) {
                        if ("success".equals(resultMap.getOrDefault("data", "").toString())) {
                            log.info("亿信代付订单提交成功 - listResult:{}", JsonUtil.object2Json(resultMap));
                            return true;
                        }
                    } else {
                        reqPayAgent.setFailReason(resultMap.getOrDefault("msg", "").toString());

                        payAgentService.callBackOrder( withdrawLog,payAgentPlatform );
                    }
                }
            log.error("亿信代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
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
            String order_no = requestMap.getOrDefault("out_trade_no", "").toString();
            int status = Integer.parseInt(requestMap.getOrDefault("status_code", "").toString());

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
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, status == 1);
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
        dataMap.put("app_id", payAgentPlatform.getMerId());
        dataMap.put("out_trade_no", withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(dataMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toUpperCase();
        dataMap.put("sign", sign);


        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(dataMap);
        log.info("亿信代付查询参数:{}",JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(requestMap, httpHeaders);

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
            log.info("亿信代付查询返回结果:{}",JsonUtil.object2Json(resultMap));
                if (!CollectionUtils.isEmpty(resultMap)) {
                    if ("200".equals(resultMap.getOrDefault("code", "").toString())) {
                        Map detailMap = (Map) resultMap.get("data");
                        int statusType = (int) detailMap.get("Status");
                        int status = 5;
                        if (statusType == 1){
                            status = 6;
                        }
                        payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, statusType);
                    }
                    return resultMap.getOrDefault("msg", "").toString();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "亿信代付查询失败,订单号:"+withdrawLog.getOrderNo();
    }
}
