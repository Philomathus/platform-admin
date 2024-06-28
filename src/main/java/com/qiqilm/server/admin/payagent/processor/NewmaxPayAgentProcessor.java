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
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository(value = ConstantsPayAgent.NEWMAX + "PayAgentProcessor")
@Log4j2
public class NewmaxPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("amount", withdrawLog.getWithdrawMoney().setScale(2, RoundingMode.HALF_UP));
        bodyMap.put("out_trade_id", withdrawLog.getOrderNo());
        bodyMap.put("bank_name", withdrawLog.getBankName().trim());
        bodyMap.put("account", withdrawLog.getBankAccount().trim());
        bodyMap.put("holder_name", withdrawLog.getBankUserName().trim());
        bodyMap.put("province", "广东省");
        bodyMap.put("city", "广州市");
        bodyMap.put("merchant_id", payAgentPlatform.getMerId());
        bodyMap.put("notify_url", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toLowerCase();
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
            reqPayAgent.setFailReason("鑫max代付代付下单报错原因:" + e);
        }
        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            String code = resultMap.getOrDefault("code", "").toString();
            if ("1".equals(code)) {
                log.info("newmax代付鑫max订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("msg", "").toString());

                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn("newmax代付鑫max订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String sign = requestMap.remove("sign").toString();
        String status = requestMap.getOrDefault("status", "").toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>(requestMap);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String signStr = DigestUtils.md5Hex(tempStr).toLowerCase();

        log.info("newmax代付鑫max回调签名字符串:" + sign + "_" + signStr);
        if (sign.equalsIgnoreCase(signStr)) {
            String out_trade_id = requestMap.getOrDefault("out_trade_id", "").toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(out_trade_id);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", out_trade_id);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", out_trade_id);
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(out_trade_id);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "2".equals(status));
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
        Map<String, Object> paramsMap = new TreeMap<>();
        paramsMap.put("merchant_id", payAgentPlatform.getMerId());
        paramsMap.put("out_trade_id", withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String tempStr = this.assemblyUrl(paramsMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr).toLowerCase();
        paramsMap.put("sign", sign);

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
            log.info("newmax代付鑫max查询结果:{}", JsonUtil.object2Json(resultMap));
            if (!CollectionUtils.isEmpty(resultMap)) {
                Map dataMap = (Map) resultMap.getOrDefault("data", new HashMap<>() );
                int statusType = Integer.parseInt(dataMap.getOrDefault("status", "").toString());
                int status = 4;
                if (statusType == -1 || statusType == 2) {
                    // status 4代付中 5代付失败 6代付成功
                    // state-1：被拒绝；1：未支付；2支付成功
                    if (statusType == 2) {
                        status = 6;
                    } else {
                        status = 5;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, statusType);
                }
                return resultMap.getOrDefault("msg", "").toString();
            }
        } catch (
                Exception e) {
            log.error(e.getMessage(), e);
        }
        return "newmax代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
