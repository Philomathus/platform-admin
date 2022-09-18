package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.*;
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

@Repository(value = ConstantsPayAgent.SHENGLIAN + "PayAgentProcessor")
@Log4j2
public class ShengLianPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("工商银行", "102");
        typeMap.put("中国工商银行", "102");
        typeMap.put("农业银行", "103");
        typeMap.put("中国农业银行", "103");
        typeMap.put("中国银行", "104");
        typeMap.put("建设银行", "105");
        typeMap.put("中国建设银行", "105");
        typeMap.put("中信银行", "302");
        typeMap.put("兴业银行", "309");
        typeMap.put("中国邮政储蓄银行", "403");
        typeMap.put("中国邮政银行", "403");
        typeMap.put("邮政储蓄银行", "403");
        typeMap.put("邮政银行", "403");
        typeMap.put("招商银行", "308");
        typeMap.put("广发银行", "306");
        typeMap.put("光大银行", "303");
        typeMap.put("平安银行", "307");
        typeMap.put("交通银行", "301");
        typeMap.put("中国交通银行", "301");
        typeMap.put("民生银行", "305");
        typeMap.put("中国民生银行", "305");
        typeMap.put("北京银行", "370");
        typeMap.put("华夏银行", "304");
        typeMap.put("南京银行", "390");
        typeMap.put("东亚银行", "502");
        typeMap.put("上海浦东发展银行", "310");
        typeMap.put("浦东发展银行", "310");
        typeMap.put("浦发银行", "310");
        typeMap.put("上海银行", "420");
        typeMap.put("兰州银行", "430");
        typeMap.put("徽商银行", "319");
        typeMap.put("青岛银行", "450");
        typeMap.put("浙商银行", "460");
        typeMap.put("国家开发银行", "201");
        typeMap.put("中国进出口银行", "202");
        typeMap.put("中国农业发展银行", "203");
        typeMap.put("渤海银行", "318");
        typeMap.put("北京农商银行", "402");
        typeMap.put("宁波银行", "512");
        typeMap.put("杭州银行", "514");

        if(!withdrawLog.getBankName().contains("银行")){
            withdrawLog.setBankName(withdrawLog.getBankName() + "银行");
        }

        String bankCode = typeMap.getOrDefault(withdrawLog.getBankName(), "");

        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("version", "2.0");
        bodyMap.put("merchantNo", payAgentPlatform.getMerId());
        bodyMap.put("cashNo", withdrawLog.getOrderNo());
        bodyMap.put("orderAmount", withdrawLog.getWithdrawMoney().multiply(BigDecimal.valueOf(100)).setScale(0,
                BigDecimal.ROUND_HALF_UP));
        bodyMap.put("holderName", withdrawLog.getBankUserName().trim());
        bodyMap.put("province", "广东省");
        bodyMap.put("city", "深圳市");

        if (!StringUtils.isNotBlank(bankCode)) {
            log.warn("盛联代付订单提交失败,{}银行不支持,请联系技术", withdrawLog.getBankName());
            reqPayAgent.setFailReason("盛联代付订单提交失败," + withdrawLog.getBankName() + "不支持,请联系技术");
            return false;
        } else {
            bodyMap.put("bankCode", bankCode);
        }

        //异步回调地址
        bodyMap.put("notifyAddr", sysConfigCacheUtil.getConf("payAgentNotifyUrl") + "yiBuShengLian");
        bodyMap.put("bankBranch", withdrawLog.getBankName().trim());
        bodyMap.put("cardNo", withdrawLog.getBankAccount().trim());
        bodyMap.put("cashType", "01");
        bodyMap.put("timestamp",  DateFormatUtils.formate(new Date(), "yyyyMMddHHmmss"));

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(bodyMap) + signMd5;
        log.info("盛联代付请求参数"+JsonUtil.object2Json(bodyMap));
        String sign = DigestUtils.md5Hex(tempStr);
        bodyMap.put("sign", sign);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity(bodyMap, httpHeaders);

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
            reqPayAgent.setFailReason("盛联代付下单失败原因:" + e);
        }
        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            String code = resultMap.getOrDefault("code", "").toString();
            if ("000000".equals(code)) {
                Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
                if (!CollectionUtils.isEmpty(dataMap)) {
                    //代付状态: -1提现失败 0申请中 1提现成功 2:处理中 (注：只有值为-1时才可以回滚数据，其它状态值视为处理中，不要回滚数据)
                    String defrayStatus = dataMap.getOrDefault("defrayStatus", "").toString();
                    if (!"-1".equals(defrayStatus)) {
                        log.info("盛联代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                        return true;
                    } else {
                        payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
                    }
                }
            }
        }
        log.info("盛联代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String sign = requestMap.remove("sign").toString();
        String status = requestMap.getOrDefault("status", "").toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>(requestMap);
        bodyMap.remove("remark");

        String tempStr = this.assemblyUrl(bodyMap) + payAgentPlatform.getHeaderKey();
        String signStr = DigestUtils.md5Hex(tempStr);

        log.info("盛联代付回调签名字符串:" + sign + "_" + signStr);
        if (sign.equalsIgnoreCase(signStr)) {
            String order_no = (String) requestMap.get("order_no");
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(order_no);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", order_no);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", order_no);
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(order_no);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "1".equals(status));
            return "SUCCESS";
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
        bodyMap.put("merchantNo", payAgentPlatform.getMerId());
        bodyMap.put("orderNo", withdrawLog.getOrderNo());
        bodyMap.put("timestamp",  DateFormatUtils.formate(new Date(), "yyyyMMddHHmmss"));

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(bodyMap) + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        bodyMap.put("sign", sign);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity(bodyMap, httpHeaders);

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
            log.info("盛联代付查询结果- result:{}", JsonUtil.object2Json(resultMap));
            if (!CollectionUtils.isEmpty(resultMap)) {
                String code = resultMap.getOrDefault("code", "").toString();
                if ("000000".equals(code)) {
                    Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
                    String defrayStatus = dataMap.getOrDefault("defrayStatus", "").toString();
                    // status 4代付中 5代付失败 6代付成功
                    //state: -1:提现失败 0:申请中 1:提现成功 2:处理中
                    if ("-1".equals(defrayStatus) || "1".equals(defrayStatus)) {
                        int status = 4;
                        if ("1".equals(defrayStatus)) {
                            status = 6;
                        } else {
                            status = 5;
                        }
                        payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, 1);
                    }
                    return resultMap.getOrDefault("msg", "").toString();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "盛联代付查询失败" + e;
        }
        return "盛联代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
