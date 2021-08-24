package com.qiqilm.server.admin.payagent.processor;

import com.alibaba.fastjson.JSON;
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
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Repository(value = ConstantsPayAgent.AINONG2 + "PayAgentProcessor")
@Log4j2
public class AiNong2PayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {

        // 基本参数
        String service = "applyAgentPay"; // 接口名称
        String signType = "MD5"; // 签名类型 MD5/RSA/CA 目前支持MD5
        String inputCharset = "UTF-8"; // 系统之间交互信息时使用的编码字符集 通常默认使用UTF-8
        String sysMerchNo = payAgentPlatform.getMerId();
        String finaCode = "CMB"; // 转出银行编码

        //业务参数
        Map<String, Object> param = new TreeMap<>();
        param.put("service", service);
        param.put("inputCharset", inputCharset);
        param.put("sysMerchNo", sysMerchNo);
        param.put("outOrderNo", withdrawLog.getOrderNo());
        param.put("orderTime",  DateFormatUtils.formate(new Date(), "yyyyMMddHHmmss"));
        param.put("finaCode",finaCode);
        param.put("payeeAcct",withdrawLog.getBankAccount());
        param.put("payeeName",withdrawLog.getBankUserName());
        param.put("applyAmt",withdrawLog.getWithdrawMoney().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        param.put("payeeAcctAttr","PRIVATE");
        param.put("bankName",withdrawLog.getBankName());
        param.put("bankProvince","广东省");
        param.put("bankCity","深圳市");
        param.put("applyReason","test");
        param.put("backUrl",sysConfigCacheUtil.getConf("payAgentNotifyUrl") + payAgentPlatform.getCode());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        String tempStr = this.assemblyUrl(param) + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        param.put("sign", sign);
        param.put("signType", signType);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(param);
        log.warn(payAgentPlatform.getName()+"下单请求参数{}",JsonUtil.object2Json(requestMap));
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
            reqPayAgent.setFailReason(payAgentPlatform.getName()+"下单报错原因:" + e);
        }
        log.warn(payAgentPlatform.getName()+"下单结果 - result:{}", JsonUtil.object2Json(resultMap));
        if (!CollectionUtils.isEmpty(resultMap)) {
            String retCode = resultMap.getOrDefault("retCode", "").toString();
            String orderStatus = resultMap.getOrDefault("orderStatus", "").toString();
            if ("0000".equals(retCode)) {
                if (!orderStatus.equals("02") && !orderStatus.equals("06")) {
                    log.info(payAgentPlatform.getName()+"订单提交成功 - listResult:{}", JsonUtil.object2Json(resultMap));
                    return true;
                }
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("retMsg", "").toString());
                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn(payAgentPlatform.getName()+"订单提交失败 - result:{}", JsonUtil.object2Json(resultMap));
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        requestMap.values().removeIf(value -> !org.springframework.util.StringUtils.hasText(value.toString()));
        String rspSign = requestMap.remove("sign").toString();
        requestMap.remove("signType");

        SortedMap<String, Object> bodyMap = new TreeMap<>(requestMap);
        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        String tempStr = this.assemblyUrl(bodyMap) + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);

        log.info(payAgentPlatform.getName()+"回调签名:" + rspSign + "_" + sign);
        if (rspSign.equalsIgnoreCase(sign)) {
            String merOrderNo = requestMap.getOrDefault("outOrderNo", "").toString();
            String tranResult = requestMap.getOrDefault("tranResult", "").toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(merOrderNo);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", merOrderNo);
                return "fail";
            }
            if ( withdrawLog.getStatus() == 2 ) {
                log.error( "订单已拒绝，无需回调 - merOrderNo:{}", merOrderNo );
                return "SUCCESS";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", merOrderNo);
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(merOrderNo);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "SUCCESS".equals(tranResult));
            log.info(payAgentPlatform.getName() + "订单号:{},回调状态:{},", merOrderNo, "SUCCESS".equals(tranResult)? "成功" : "失败");
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

        // 基本参数
        String service = "queryAgentPay"; // 接口名称
        String signType = "MD5"; // 签名类型 MD5/RSA/CA 目前支持MD5
        String inputCharset = "UTF-8"; // 系统之间交互信息时使用的编码字符集 通常默认使用UTF-8
        String sysMerchNo = payAgentPlatform.getMerId();

        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("service", service);
        dataMap.put("inputCharset", inputCharset);
        dataMap.put("sysMerchNo", sysMerchNo);
        dataMap.put("outOrderNo", withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        String tempStr = this.assemblyUrl(dataMap) + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        dataMap.put("sign", sign);
        dataMap.put("signType", signType);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(dataMap);
        log.warn(payAgentPlatform.getName()+"查询订单请求参数:{}", JsonUtil.object2Json( requestMap ) );
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestMap, httpHeaders);

        String jsonStr = null;
        try {
            jsonStr = restTemplate.postForObject(payAgentPlatform.getPayOrderQueryAddr(), httpEntity, String.class);
            Map resultMap = (Map) JSON.parse(jsonStr);
            log.warn(payAgentPlatform.getName()+"查询结果 - result:{}", JsonUtil.object2Json(resultMap));

            if (!CollectionUtils.isEmpty(resultMap)) {
                //  status 4代付中 5代付失败 6代付成功
                int status = 4;
                //  statusCode 00 提交申请，01 审核通过，02 申请被拒绝，03 已打批次，04 提交到渠道，05 代付成功，06 代付失败
                String statusCode = resultMap.getOrDefault("orderStatus", "").toString();
                String retCode = resultMap.getOrDefault("retCode", "").toString();

                if(retCode.equals("ORDER_NOT_EXIST")){
                    statusCode = "06";
                }

                if("02".equals(statusCode) || "05".equals(statusCode)  || "06".equals(statusCode)){
                    if ("05".equals(statusCode)) {
                        status = 6;
                    } else {
                        status = 5;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, Integer.parseInt(statusCode));
                }

                return JsonUtil.object2Json(resultMap);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return payAgentPlatform.getName()+"查询失败,订单号:"+withdrawLog.getOrderNo();
    }

}
