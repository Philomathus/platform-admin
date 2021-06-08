package com.qiqilm.server.admin.payagent.processor;

import com.alibaba.fastjson.JSONObject;
import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeLangYaType;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.AuthUtil;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import com.qiqilm.server.admin.utils.lvJianPayAgentUtils.HttpClientTools;
import com.qiqilm.server.admin.utils.nanKaiPayAgentUtils.HttpClientUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.HttpPost;
import org.springframework.beans.factory.support.ManagedArray;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository(value = ConstantsPayAgent.LVJIAN + "PayAgentProcessor")
@Log4j2
public class LvJianPayAgentProcessor extends AbstractPayAgent {

    public static SimpleDateFormat yyyyMMddHHmmss = new java.text.SimpleDateFormat("yyyyMMddHHmmss");

    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        String md5key = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        //1 授权token获取
        JSONObject paramsToGetToken = new JSONObject();
        paramsToGetToken.put("merchantId", payAgentPlatform.getMerId());
        String toReqTokenSign = HttpClientTools.md5ascii(paramsToGetToken, md5key);
        paramsToGetToken.put("sign", toReqTokenSign);
        //授权token的url写在header_key里
        String resultData = null;
        try {
            resultData = HttpClientTools.baseHttpSendPost(payAgentPlatform.getHeaderKey(), paramsToGetToken);
        } catch (Exception e) {
            log.info("绿箭代付获取授权token错误:{}", withdrawLog.getOrderNo());
            log.error(e.getMessage(), e);
        }

        JSONObject resultDataJsonObj = new JSONObject().parseObject(resultData);
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Content-Type", "application/json;charset=utf-8");
        headerMap.put("accToken", resultDataJsonObj.getString("accToken"));

        JSONObject params = new JSONObject();
        params.put("merchNo", payAgentPlatform.getMerId());
        params.put("method", "cmd.transfer.order");//请求接口名称
        params.put("ipaddress", "192.168.0.1");
        params.put("timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        params.put("version", "2.0");
        params.put("format", "JSON");
        params.put("charset", "utf-8");
        params.put("signType", "MD5");
        params.put("outTradeNo", withdrawLog.getOrderNo());
        params.put("amount", withdrawLog.getWithdrawMoney().setScale(2, RoundingMode.HALF_UP));
        params.put("idCardNo", "431111111111111111");
        params.put("accountName", withdrawLog.getBankUserName().trim());
        params.put("bankCard", withdrawLog.getBankAccount().trim());
        params.put("bankName", withdrawLog.getBankName().trim());
        params.put("bankSubName", "龙华支行");
        params.put("province", "广东省");
        params.put("city", "深圳市");
        params.put("bankLinked", "305584018192");
        params.put("mobile", "15114741145");
        params.put("notifyUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + ConstantsPayAgent.LVJIAN);
        String sign = null;
        try {
            sign = HttpClientTools.md5ascii(params, md5key);
        } catch (Exception e) {
            log.info("绿箭代付md5加密过程错误:{}", withdrawLog.getOrderNo());
            log.error(e.getMessage(), e);
        }
        params.put("sign", sign);
        String responseData = null;
        try {
            responseData = HttpClientTools.httpSendPostForm(payAgentPlatform.getPayOrderAddr(), params, headerMap);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("绿箭代付下单结果 - result:{}", responseData);
        Map<String, Object> resultMap = JsonUtil.json2Map(responseData);
        if (!CollectionUtils.isEmpty(resultMap)) {
            String code = resultMap.getOrDefault("code", "").toString();
            if ("10000".equals(code)) {
                log.info("绿箭代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("message", "").toString());
            }
        }
        log.warn("绿箭代付订单提交失败 - result:{}", JsonUtil.object2Json(resultMap));
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String sign = requestMap.remove("sign").toString();
        String code = requestMap.getOrDefault("code", "").toString();
        String status = requestMap.getOrDefault("status", "").toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>(requestMap);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String signStr = DigestUtils.md5Hex(tempStr);

        log.info("绿箭代付回调签名字符串:" + sign + "_" + signStr);
        if (sign.equalsIgnoreCase(signStr) && "10000".equals(code)) {
            String orderid = requestMap.getOrDefault("orderid", "").toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(orderid);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", orderid);
                return "ERROR";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", orderid);
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(orderid);
            String orderNo = requestMap.getOrDefault("orderNo", "").toString();
            payAgentService.processOrderPay(withdrawLog, payAgentLog, orderNo, payAgentPlatform, "1".equals(status));
            return "SUCCESS";
        }
        return "ERROR";
    }

    @Override
    public Map<String, Object> reverseCheckOrderPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap,
                                                    String realIp) throws Exception {
        return null;
    }

    @Override
    public void queryOrderPay(PayAgentLog payAgentLog) throws Exception {
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(payAgentLog.getWithdrawOrderNo());
        PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById(payAgentLog.getPayAgentPlatId());

        String md5key = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        JSONObject prams = new JSONObject();
        prams.put("merchNo", payAgentPlatform.getMerId());
        prams.put("method", "cmd.query.transfer");//请求接口名称
        prams.put("ipaddress", "192.168.0.1");
        prams.put("timestamp", yyyyMMddHHmmss.format(new Date()));
        prams.put("version", "2.0");
        prams.put("format", "JSON");
        prams.put("charset", "utf-8");
        prams.put("signType", "MD5");
        prams.put("outTradeNo", withdrawLog.getOrderNo());
        prams.put("tradeNo", payAgentLog.getPayAgentOrderNo());

        String sign = HttpClientTools.md5ascii(prams, md5key);
        prams.put("sign", sign);
        String responseData = null;
        try {
            responseData = HttpClientTools.httpSendPostForm(payAgentPlatform.getPayOrderQueryAddr(), prams);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("绿箭代付查询结果- result:{}", responseData);
        Map<String, Object> resultMap = JsonUtil.json2Map(responseData);
        if (!CollectionUtils.isEmpty(resultMap)) {
            String code = resultMap.getOrDefault("code", "").toString();
            if ("10000".equals(code)) {
                int statusType = Integer.parseInt((resultMap.getOrDefault("status", "").toString()));
                //status 4代付中 5代付失败 6代付成功
                //statusType:1代付成功  2代付中  3代付失败   6状态未知   9代付退汇,出款处理退回
                if (statusType == 1 || statusType == 3 || statusType == 9) {
                    int status = 4;
                    if (statusType == 1) {
                        status = 6;
                    } else {
                        status = 5;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, statusType);
                    return;
                }
            }
        }
    }
}
