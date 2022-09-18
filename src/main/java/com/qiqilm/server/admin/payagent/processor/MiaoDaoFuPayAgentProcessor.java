package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeMiaoDaoFuType;
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
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository(value = ConstantsPayAgent.MIAODAOFU + "PayAgentProcessor")
@Log4j2
public class MiaoDaoFuPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        BankCodeMiaoDaoFuType bankCodeType = BankCodeMiaoDaoFuType.getCodeByDesc(withdrawLog.getBankName());
        if (bankCodeType == null) {
            log.warn(payAgentPlatform.getName() + "代付无法支持的银行类型 - 银行类型:{}", withdrawLog.getBankName());
            payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            throw new BusinessException("此代付无法支持的银行类型：" + withdrawLog.getBankName());
        }
        withdrawLog.setBankCode(bankCodeType.name().substring(1));

        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("merchant_no", payAgentPlatform.getMerId());
        bodyMap.put("amount", withdrawLog.getWithdrawMoney().multiply(BigDecimal.valueOf(100)).setScale(0,
                RoundingMode.HALF_UP));
        bodyMap.put("order_no", withdrawLog.getOrderNo());
        bodyMap.put("bank_id", withdrawLog.getBankCode());
        bodyMap.put("payee_name", URLEncoder.encode(withdrawLog.getBankUserName().trim(), "utf-8"));
        bodyMap.put("bank_name", URLEncoder.encode(withdrawLog.getBankName().trim(), "utf-8"));
        bodyMap.put("bank_account", withdrawLog.getBankAccount().trim());
        bodyMap.put("sign_type", "SHA");
        bodyMap.put("sign_ts", System.currentTimeMillis() / 1000);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(bodyMap) + signMd5;
        String sign = DigestUtils.sha1Hex(tempStr);
        bodyMap.put("sign", sign);
        bodyMap.remove("bank_name");
        bodyMap.put("bank_name", withdrawLog.getBankName().trim());
        bodyMap.remove("payee_name");
        bodyMap.put("payee_name", withdrawLog.getBankUserName().trim());
        log.warn(payAgentPlatform.getName() + "下单请求参数{}", JsonUtil.object2Json(bodyMap));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity(bodyMap, httpHeaders);

        Map<String, Object> resultMap = null;
        String url = payAgentPlatform.getPayOrderAddr() + payAgentPlatform.getMerId();
        try {
            resultMap = restTemplate.execute(url, HttpMethod.POST,
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
            reqPayAgent.setFailReason(payAgentPlatform.getName() + "代付下单报错原因:" + e);
        }
        log.info(payAgentPlatform.getName() + "下单结果{},订单号:{}", JsonUtil.object2Json(resultMap), withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            //WAITING 等待处理
            //PROCESSING 处理中
            //SUCCESSFUL 处理成功
            //FAILURE 处理失败
            String state = resultMap.getOrDefault("state", "").toString();
            if ("WAITING".equals(state) || "PROCESSING".equals(state)) {
                log.info(payAgentPlatform.getName() + "代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.info(payAgentPlatform.getName() + "代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String sign = requestMap.remove("sign").toString();
        String state = requestMap.getOrDefault("state", "").toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>(requestMap);
        String notify_time = bodyMap.remove("notify_time").toString();
        bodyMap.put("notify_time", URLEncoder.encode(notify_time, "utf-8"));
        bodyMap.put("bank_name", URLEncoder.encode(requestMap.remove("bank_name").toString(), "utf-8"));
        bodyMap.put("payee_name", URLEncoder.encode(requestMap.remove("payee_name").toString(), "utf-8"));

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(bodyMap) + signMd5;
        String signStr = DigestUtils.sha1Hex(tempStr);

        log.info(payAgentPlatform.getName() + "代付回调签名字符串:" + sign + "_" + signStr);
        if (sign.equalsIgnoreCase(signStr)) {
            String order_no = (String) requestMap.get("order_no");
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
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "SUCCESSFUL".equals(state));
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
        Map<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("merchant_no", payAgentPlatform.getMerId());
        bodyMap.put("order_no", withdrawLog.getOrderNo());
        bodyMap.put("sign_type", "SHA");
        bodyMap.put("sign_ts", System.currentTimeMillis() / 1000);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(bodyMap) + signMd5;
        String sign = DigestUtils.sha1Hex(tempStr);
        bodyMap.put("sign", sign);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity(bodyMap, httpHeaders);

        Map<String, Object> resultMap = null;
        String url = payAgentPlatform.getPayOrderQueryAddr() + payAgentPlatform.getMerId() + "/" + withdrawLog.getOrderNo();
        try {
            resultMap = restTemplate.execute(url, HttpMethod.POST,
                    restTemplate.httpEntityCallback(httpEntity), response -> {
                        InputStream bodyStream = response.getBody();
                        String text;
                        try (Reader reader = new InputStreamReader(bodyStream)) {
                            text = CharStreams.toString(reader);
                        }
                        return JsonUtil.json2Map(text);
                    });
            log.info(payAgentPlatform.getName() + "代付查询结果- result:{}", JsonUtil.object2Json(resultMap));
            if (!CollectionUtils.isEmpty(resultMap)) {
                String state = resultMap.getOrDefault("state", "").toString();
                // status 4代付中 5代付失败 6代付成功
                //state: WAITING 等待处理,PROCESSING 处理中,SUCCESSFUL 处理成功,FAILURE 处理失败
                if ("SUCCESSFUL".equals(state) || "FAILURE".equals(state)) {
                    int status = 4;
                    if ("SUCCESSFUL".equals(state)) {
                        status = 6;
                    } else {
                        status = 5;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, 1);
                }
                return resultMap.getOrDefault("msg", "").toString();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return payAgentPlatform.getName() + "代付查询失败" + e;
        }
        return payAgentPlatform.getName() + "代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
