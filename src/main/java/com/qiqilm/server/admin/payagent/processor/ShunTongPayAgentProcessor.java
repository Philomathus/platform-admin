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
import com.qiqilm.server.admin.utils.StringUtils;
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
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository(value = ConstantsPayAgent.SHUNTONG + "PayAgentProcessor")
@Log4j2
public class ShunTongPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("mchno", payAgentPlatform.getMerId());
        bodyMap.put("obid", withdrawLog.getOrderNo());
        bodyMap.put("amount", withdrawLog.getWithdrawMoney().setScale(2, RoundingMode.HALF_UP));
        bodyMap.put("accno", withdrawLog.getBankAccount().trim());
        bodyMap.put("accnm", withdrawLog.getBankUserName().trim());
        bodyMap.put("banknm", withdrawLog.getBankName().trim());
        bodyMap.put("acctype", "unionpay");
        bodyMap.put("notice_url", sysConfigCacheUtil.getConf("payAgentNotifyUrl") + payAgentPlatform.getCode());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        bodyMap.put("sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(bodyMap);
        log.warn(JsonUtil.object2Json(requestMap));
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            reqPayAgent.setFailReason("顺通代付下单报错原因:" + e);
        }
        log.info("顺通代付下单结果- result:{}", JsonUtil.object2Json(resultMap));
        if (!CollectionUtils.isEmpty(resultMap)) {
            String status = resultMap.getOrDefault("status", "").toString();
            if ("1".equals(status)) {
                log.info("顺通代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                if (StringUtils.isNotBlank(resultMap.getOrDefault("message", "").toString())) {
                    reqPayAgent.setFailReason(resultMap.getOrDefault("message", "").toString());
                } else {
                    reqPayAgent.setFailReason(resultMap.getOrDefault("msg", "").toString());
                }

                payAgentService.callBackOrder( withdrawLog,payAgentPlatform );
            }
        }
        log.warn("顺通代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String sign = requestMap.remove("sign").toString();
        String status = requestMap.getOrDefault("status", "").toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>(requestMap);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String signStr = DigestUtils.md5Hex(tempStr);

        log.info("顺通代付回调签名:" + tempStr + "_" + sign);
        if (sign.equalsIgnoreCase(signStr)) {
            String shOrderId = (String) requestMap.get("obid");

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(shOrderId);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", shOrderId);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", shOrderId);
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(shOrderId);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "0".equals(status));
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
        paramsMap.put("obid", withdrawLog.getOrderNo());
        paramsMap.put("mchno", payAgentPlatform.getMerId());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(paramsMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        paramsMap.put("sign", sign);

        String url = "?" + "mchno=" + paramsMap.get("mchno") + "&obid=" + paramsMap.get("obid") + "&sign=" + paramsMap.get("sign");

        String res = null;
        try {
            res = restTemplate.getForObject( payAgentPlatform.getPayOrderQueryAddr() + url, String.class );
            log.warn( "顺通代付查询结果:" + res );
            if(StringUtils.isNotBlank(res)) {
                Map<String, Object> resultMap = JsonUtil.json2Map(res);
                if (!CollectionUtils.isEmpty(resultMap)) {
                    int statusType = Integer.parseInt(resultMap.getOrDefault("status", "").toString());
                    if (statusType == 10 || statusType == 0) {
                        // status 4代付中 5代付失败 6代付成功
                        // statusType 1申请受理中，2代付下发中，10交易失败，0下发成功
                        int status = 4;
                        if (statusType == 0) {
                            status = 6;
                            statusType = 0;
                        } else {
                            status = 5;
                            statusType = 10;
                        }
                        payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status,
                                statusType);
                    }
                    return resultMap.getOrDefault("msg", "").toString();
                }
            }
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        return "顺通代付查询失败,订单号:"+withdrawLog.getOrderNo();
    }
}
