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
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

@Repository(value = ConstantsPayAgent.SHANDE21 + "PayAgentProcessor")
@Log4j2
public class ShanDe21PayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("app_id", payAgentPlatform.getMerId());
        dataMap.put("order_sn", withdrawLog.getOrderNo());
        dataMap.put("amount", withdrawLog.getWithdrawMoney().setScale(0, BigDecimal.ROUND_HALF_UP));
        dataMap.put("bank_name", withdrawLog.getBankName());
        dataMap.put("user_name", withdrawLog.getBankUserName().trim());
        dataMap.put("user_account", withdrawLog.getBankAccount().trim());
        dataMap.put("province", "广东省");
        dataMap.put("city", "深圳市");
        dataMap.put("branchbank", withdrawLog.getBankName());
        dataMap.put("wallet_id", StringUtils.isEmpty(payAgentPlatform.getHeaderKey()) ? "2" : payAgentPlatform.getHeaderKey());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        String tempStr = this.assemblyUrl(dataMap) + "&" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        dataMap.put("signature", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(dataMap);
        log.warn(payAgentPlatform.getName() + "下单请求参数{}", JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestMap, httpHeaders);

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
        }
        log.info(payAgentPlatform.getName() + "下单结果{},订单号:{}", JsonUtil.object2Json(resultMap), withdrawLog.getOrderNo());

        if (!CollectionUtils.isEmpty(resultMap)) {
            if (!"2".equals(resultMap.getOrDefault("err", -1).toString())) {
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
        return null;
    }

    @Override
    public String queryOrderPay(PayAgentLog payAgentLog) throws Exception {
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(payAgentLog.getWithdrawOrderNo());
        PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById(payAgentLog.getPayAgentPlatId());
        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("app_id", payAgentPlatform.getMerId());
        dataMap.put("order_sn", withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        String tempStr = this.assemblyUrl(dataMap) + "&" + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        dataMap.put("signature", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(dataMap);
        log.warn(payAgentPlatform.getName() + "查询代付状态接口请求参数{}", JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestMap, httpHeaders);

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
            log.warn(payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json(resultMap));

            if (!CollectionUtils.isEmpty(resultMap)) {
                String code = resultMap.getOrDefault("err", "").toString();
                Map msgMap = (Map) resultMap.get("msg");

                //  status 4代付中 5代付失败 6代付成功
                int status = 4;
                //  statusCode 1成功，2失败，3取消，4未支付，5打款中，7队列提交
                String statusCode = msgMap.getOrDefault("pay_status", "").toString();

                if (!"1".equals(code)) {
                    statusCode = "2";
                }

                if ("1".equals(statusCode) || "2".equals(statusCode) || "3".equals(statusCode)) {
                    if ("1".equals(statusCode)) {
                        status = 6;
                    } else {
                        status = 5;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, Integer.parseInt(statusCode));
                }

                String msg = null;
                if ("1".equals(code)) {
                    if ("1".equals(statusCode)) {
                        msg = "成功";
                    } else if ("2".equals(statusCode)) {
                        msg = "失败";
                    } else if ("3".equals(statusCode)) {
                        msg = "取消";
                    } else if ("4".equals(statusCode)) {
                        msg = "未支付";
                    } else if ("5".equals(statusCode)) {
                        msg = "打款中";
                    } else if ("7".equals(statusCode)) {
                        msg = "队列提交";
                    }
                } else if ("2".equals(code)) {
                    msg = resultMap.getOrDefault("msg", "").toString();
                }
                return msg;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }

}
