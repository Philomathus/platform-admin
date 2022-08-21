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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository(value = ConstantsPayAgent.GOPAY + "PayAgentProcessor")
@Log4j2
public class GoPayPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("sendid", payAgentPlatform.getMerId());
        dataMap.put("orderid", withdrawLog.getOrderNo());
        dataMap.put("amount", withdrawLog.getWithdrawMoney());
        dataMap.put("address", withdrawLog.getBankAccount().trim());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        String tempStr = payAgentPlatform.getMerId() + withdrawLog.getOrderNo() +
                withdrawLog.getWithdrawMoney() + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        dataMap.put("sign", sign);

        log.warn(payAgentPlatform.getName() + "下单请求参数{}", JsonUtil.object2Json(dataMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity(dataMap, httpHeaders);

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
            if (e.getMessage().contains("443 failed to respond")) {
                reqPayAgent.setFailReason("三方网络异常:" + e.getMessage());

                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
                return false;
            }
        }
        log.info(payAgentPlatform.getName() + "下单结果{},订单号:{}", JsonUtil.object2Json(resultMap), withdrawLog.getOrderNo());

        if (!CollectionUtils.isEmpty(resultMap)) {
            if ("1".equals(resultMap.getOrDefault("code", "").toString())) {
                Map data = JsonUtil.json2Map(resultMap.getOrDefault("data", "").toString());
                String id = data.getOrDefault("id","").toString();
                PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(withdrawLog.getOrderNo());
                payAgentLog.setPayAgentOrderNo(id);
                payAgentLogMapper.updatePayAgentLog(payAgentLog);
                log.info(payAgentPlatform.getName() + "订单提交成功 - listResult:{}", JsonUtil.object2Json(resultMap));
                return true;
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
        return "fail";
    }

    @Override
    public Map<String, Object> reverseCheckOrderPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap,
                                                    String realIp) throws Exception {
        if (this.checkWhiteIp(payAgentPlatform.getPlatWhiteIpList(), realIp)) {
            log.warn("请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json(requestMap));
            return null;
        }

        SortedMap<String, Object> requestSignMap = new TreeMap<>(requestMap);
        String sign = requestSignMap.remove("sign").toString();

        String merId = requestSignMap.getOrDefault("sendid", "").toString();
        String merOrderNo = requestSignMap.getOrDefault("orderid", "").toString();
        BigDecimal amount = new BigDecimal(requestSignMap.getOrDefault("amount", "0").toString());
        String address = requestSignMap.getOrDefault("address", "").toString();

        String tempSign = merId + merOrderNo + amount + address + payAgentPlatform.getSignPublicKey();
        String mySign = DigestUtils.md5Hex(tempSign);

        SortedMap<String, Object> signMap = new TreeMap<>();

        if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(sign, mySign)) {
            MemberWithdrawLog memberWithdrawLog = withdrawLogMapper.selectByOrderNo(merOrderNo);
            if (memberWithdrawLog == null) {
                signMap.put("code", 1002);
                signMap.put("msg", "订单不存在");
                return signMap;
            } else if (amount.compareTo(memberWithdrawLog.getWithdrawMoney()) != 0) {
                signMap.put("code", 1004);
                signMap.put("msg", "充币数量不匹配");
                return signMap;
            } else if (!merId.equals(payAgentPlatform.getMerId())) {
                signMap.put("code", 9999);
                signMap.put("msg", "商户号错误");
            } else {
                signMap.put("code", 1);
                signMap.put("msg", "success");
            }
        }
        String resultSignStr = sign + payAgentPlatform.getSignPrivateKey();
        signMap.put("retsign", DigestUtils.md5Hex(resultSignStr));
        return signMap;
    }

    @Override
    public String queryOrderPay(PayAgentLog payAgentLog) throws Exception {
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(payAgentLog.getWithdrawOrderNo());
        PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById(payAgentLog.getPayAgentPlatId());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity(null, httpHeaders);

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute(payAgentPlatform.getPayOrderQueryAddr() + "?id=" + payAgentLog.getPayAgentOrderNo(), HttpMethod.GET,
                    restTemplate.httpEntityCallback(httpEntity), response -> {
                        InputStream bodyStream = response.getBody();
                        String text;
                        try (Reader reader = new InputStreamReader(bodyStream)) {
                            text = CharStreams.toString(reader);
                        }
                        return JsonUtil.json2Map(text);
                    });
            log.info(payAgentPlatform.getName() + "查询结果 - 订单号:{} - result:{}", payAgentLog.getWithdrawOrderNo(), JsonUtil.object2Json(resultMap));

            if (!CollectionUtils.isEmpty(resultMap)) {
                String code = resultMap.getOrDefault("code", "").toString();
                if ("467".equals(code)) {
                    return resultMap.getOrDefault("msg", "").toString();
                }

                //  status 4代付中 5代付失败 6代付成功
                int status = 4;
                //  statusCode 1-已创建,4-已转币,8-已取消,99-错误
                Map data = JsonUtil.json2Map(resultMap.getOrDefault("data","").toString());
                String statusCode = data.getOrDefault("state", "").toString();
                if (!"1".equals(code)) {
                    statusCode = "99";
                }
                if ("4".equals(statusCode) || "8".equals(statusCode) || "99".equals(statusCode)) {
                    if ("4".equals(statusCode)) {
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
