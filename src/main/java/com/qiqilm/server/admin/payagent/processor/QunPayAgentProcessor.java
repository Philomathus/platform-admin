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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Repository(value = ConstantsPayAgent.QUN_ZHI_FU + "PayAgentProcessor")
@Log4j2
public class QunPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("batchnumber", withdrawLog.getOrderNo());
        bodyMap.put("cardNumber", withdrawLog.getBankAccount().trim());
        bodyMap.put("method", "Gt.online.pay");
        bodyMap.put("partner", payAgentPlatform.getMerId());
        bodyMap.put("paymoney", withdrawLog.getWithdrawMoney().setScale(2,
                BigDecimal.ROUND_HALF_UP));
        bodyMap.put("version", "3.0");

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String signStr = this.assemblyUrl(bodyMap) + signMd5;
        String sign = DigestUtils.md5Hex(signStr);
        bodyMap.put("cardName", withdrawLog.getBankUserName().trim());
        bodyMap.put("bankName", withdrawLog.getBankName().trim());
        bodyMap.put("notifyUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put("remarks", withdrawLog.getOrderNo());

        bodyMap.put("sign", sign);
        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(bodyMap);
        log.warn(JsonUtil.object2Json(requestMap));
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
            reqPayAgent.setFailReason(e.getMessage());
        }
        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            if ("1000".equals(resultMap.getOrDefault("code", "").toString())) {
                Map dataMap = (Map) resultMap.getOrDefault("data", "");
                String status = dataMap.getOrDefault("status", "").toString();
                if ("3".equals(status)) {
                    log.info("群支付代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                    return true;
                }
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("message", "").toString());

                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn("群支付代付订单提交失败 - orderNo:{}", JsonUtil.object2Json(resultMap));
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        if (this.checkWhiteIp(payAgentPlatform.getPlatWhiteIpList(), realIp)) {
            log.warn("请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json(requestMap));
            return "fail";
        }
        String version = requestMap.getOrDefault("version", "").toString();
        String partner = requestMap.getOrDefault("partner", "").toString();
        String batchnumber = requestMap.getOrDefault("batchnumber", "").toString();
        String status = requestMap.getOrDefault("status", "").toString();
        String paymoney = requestMap.getOrDefault("paymoney", "").toString();
        String signRes = requestMap.getOrDefault("sign", "").toString();
        Map map = new LinkedHashMap();
        map.put("version", version);
        map.put("partner", partner);
        map.put("batchnumber", batchnumber);
        map.put("status", status);
        map.put("paymoney", paymoney);
        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String signStr = this.assemblyUrl(map) + signMd5;
        String sign = DigestUtils.md5Hex(signStr);

        //RSA 2048 SHA256 公钥验签
        log.info("群支付代付回调签名字符串:" + signRes + "_" + sign);
        if (signRes.equals(sign)) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(batchnumber);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", batchnumber);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", batchnumber);
                return "ok";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(batchnumber);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, batchnumber, payAgentPlatform,
                    "1".equals(status));
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
        Map<String, String> bodyMap = new LinkedHashMap<>();
        bodyMap.put("version", "3.0");
        bodyMap.put("method", "Gt.online.payquery");
        bodyMap.put("partner", payAgentPlatform.getMerId());
        bodyMap.put("batchnumber", withdrawLog.getOrderNo());


        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String signStr = this.assemblyUrl(bodyMap) + signMd5;
        String sign = DigestUtils.md5Hex(signStr);
        bodyMap.put("sign", sign);

        log.warn("群支付代付查询" + JsonUtil.object2Json(bodyMap));
        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(bodyMap);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(payAgentPlatform.getPayOrderQueryAddr());

        String res = null;
        try {
            res = restTemplate.getForObject(builder.queryParams(requestMap).build().toUri(), String.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.warn("群支付代付查询结果:" + res);
        if (StringUtils.isNoneBlank(res)) {
            Map<String, Object> resultMap = JsonUtil.json2Map(res);
            if ("1000".equals(resultMap.getOrDefault("code", "").toString())) {
                Map<String, Object> dataMap = (Map<String, Object>) resultMap.getOrDefault("data", new HashMap<>());
                if (!CollectionUtils.isEmpty(dataMap)) {
                    int state = Integer.parseInt(dataMap.getOrDefault("status", -1).toString());
                    // status 4代付中 5代付失败 6代付成功
                    // state 1处理中 2支付成功 3支付失败
                    int status = 4;
                    if (state == 1) {
                        status = 6;
                    } else if (status == 3) {
                        status = 5;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, state);
                    return resultMap.getOrDefault("msg", "").toString();
                }
            }
        }
        return "群支付代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
