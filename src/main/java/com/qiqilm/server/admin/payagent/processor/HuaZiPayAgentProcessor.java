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
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.RoundingMode;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository(value = ConstantsPayAgent.HUA_ZI + "PayAgentProcessor")
@Log4j2
public class HuaZiPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        SortedMap<String, String> bodyMap = new TreeMap<>();
        bodyMap.put("merid", payAgentPlatform.getMerId());
        bodyMap.put("out_trade_no", withdrawLog.getOrderNo());
        bodyMap.put("amount_str", withdrawLog.getWithdrawMoney().setScale(0, RoundingMode.HALF_UP).toString());
        bodyMap.put("service", "agent_distribution");
        bodyMap.put("bank_account_name", withdrawLog.getBankUserName().trim());
        bodyMap.put("bank_account_no", withdrawLog.getBankAccount().trim());
        bodyMap.put("bank_name", "邮政银行".equals(withdrawLog.getBankName()) ? "邮政储蓄银行" : withdrawLog.getBankName());
        bodyMap.put("bank_site_name", withdrawLog.getBankAddress());
        bodyMap.put("input_charset", "UTF-8");
        bodyMap.put("agent_time", DateFormatUtils.formate(reqPayAgent.getCurrentTime(),
                DateFormatUtils.TIGHT_PATTERN_DATETIME));
        bodyMap.put("sign_type", "MD5");
        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String signStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;

        String sign = DigestUtils.md5Hex(signStr);
        bodyMap.put("sign", sign);
        bodyMap.put("notify_url", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put("remark", "fbjsdhf");

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(bodyMap);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(payAgentPlatform.getPayOrderAddr());
        URI uri = builder.queryParams(requestMap).build().toUri();
        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute(uri, HttpMethod.GET,
                    restTemplate.httpEntityCallback(new HttpEntity<>(null)), response -> {
                        InputStream bodyStream = response.getBody();
                        String text;
                        try (Reader reader = new InputStreamReader(bodyStream, "GB2312")) {
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
            if ("T".equals(resultMap.getOrDefault("status", "").toString())) {
                log.info("华子代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("errmsg", "").toString());

                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn("华子代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        if (this.checkWhiteIp(payAgentPlatform.getPlatWhiteIpList(), realIp)) {
            log.warn("请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json(requestMap));
            return "fail";
        }
        String rspSign = requestMap.remove("sign").toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>(requestMap);
        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        if (rspSign.equalsIgnoreCase(sign)) {
            String outTradeNo = requestMap.getOrDefault("out_trade_no", "").toString();
            String tradeStatus = requestMap.getOrDefault("trade_status", "").toString();
            String notifyId = requestMap.getOrDefault("notify_id", "").toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(outTradeNo);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", outTradeNo);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("代付已成功，无需继续回调 - merOrderNo:{}", outTradeNo);
                return notifyId;
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(outTradeNo);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "2".equals(tradeStatus));
            return notifyId;
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
        PayAgentPlatform payAgentPlatform =
                payAgentPlatformMapper.selectPayAgentPlatformById(payAgentLog.getPayAgentPlatId());
        Map<String, String> dataMap = new TreeMap<>();
        dataMap.put("merid", payAgentPlatform.getMerId());
        dataMap.put("out_trade_no", withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(dataMap) + "&key=" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        dataMap.put("sign", sign);

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(dataMap);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(payAgentPlatform.getPayOrderQueryAddr());
        URI uri = builder.queryParams(requestMap).build().toUri();
        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute(uri, HttpMethod.GET,
                    restTemplate.httpEntityCallback(new HttpEntity<>(null)), response -> {
                        InputStream bodyStream = response.getBody();
                        String text;
                        try (Reader reader = new InputStreamReader(bodyStream, "GB2312")) {
                            text = CharStreams.toString(reader);
                        }
                        return JsonUtil.json2Map(text);
                    });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.warn("华子代付查询结果 - orderNo:{};result:{}", withdrawLog.getOrderNo(), JsonUtil.object2Json(resultMap));
        if (!CollectionUtils.isEmpty(resultMap)) {
            String querySign = resultMap.getOrDefault("sign", "").toString();
            String orderState = resultMap.getOrDefault("status", "").toString();
            Map<String, String> signMap = new LinkedHashMap<>();
            signMap.put("merid", resultMap.getOrDefault("merid", "").toString());
            signMap.put("out_trade_no", resultMap.getOrDefault("out_trade_no", "").toString());
            signMap.put("sy_trade_no", resultMap.getOrDefault("sy_trade_no", "").toString());
            signMap.put("status", orderState);
            signMap.put("amount", resultMap.getOrDefault("amount", "").toString());
            signMap.put("key", signMd5);
            if (DigestUtils.md5Hex(this.assemblyUrl(signMap)).equalsIgnoreCase(querySign)) {
                // status 4代付中5代付失败6代付成功
                // orderState (-1-审核拒绝 0-未审核 1-处理中 2-处理成功 3-处理失败)
                int status = 4;
                switch (orderState) {
                    case "2":
                        status = 6;
                        break;
                    case "-1":
                    case "3":
                        status = 5;
                        break;
                    default:
                        break;
                }
                payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status,
                        Integer.parseInt(orderState));
            }
            return resultMap.getOrDefault("msg", "").toString();
        }
        return "华子代付查询失败,订单号:"+withdrawLog.getOrderNo();
    }
}
