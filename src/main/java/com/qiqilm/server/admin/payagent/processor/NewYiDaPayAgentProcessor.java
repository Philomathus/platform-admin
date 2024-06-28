package com.qiqilm.server.admin.payagent.processor;

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
import com.qiqilm.server.admin.utils.lvJianPayAgentUtils.HttpClientTools;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository(value = ConstantsPayAgent.NEWYIDA + "PayAgentProcessor")
@Log4j2
public class NewYiDaPayAgentProcessor extends AbstractPayAgent {

    public static SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        String md5key = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        Map<String, String> params = new HashMap<>();
        params.put("merchNo", payAgentPlatform.getMerId());
        params.put("method", "cmd.transfer.order");//请求接口名称
        params.put("ipaddress", "192.168.0.1");
        params.put("timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        params.put("version", "2.0");
        params.put("format", "JSON");
        params.put("charset", "utf-8");
        params.put("signType", "MD5");
        params.put("outTradeNo", withdrawLog.getOrderNo());
        params.put("amount", withdrawLog.getWithdrawMoney().setScale(2, RoundingMode.HALF_UP).toString());
        params.put("idCardNo", "431111111111111111");
        params.put("accountName", withdrawLog.getBankUserName().trim());
        params.put("bankCard", withdrawLog.getBankAccount().trim());
        params.put("bankName", withdrawLog.getBankName().trim());
        params.put("bankSubName", "龙华支行");
        params.put("province", "广东省");
        params.put("city", "深圳市");
        params.put("bankLinked", "305584018192");
        params.put("mobile", "15114741145");
        params.put("notifyUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        String sign = null;
        try {
            sign = HttpClientTools.md5ascii(params, md5key);
        } catch (Exception e) {
            log.info("新益达代付md5加密过程错误:{}", withdrawLog.getOrderNo());
            log.error(e.getMessage(), e);
        }
        params.put("sign", sign);
        String responseData = null;
        try {
            responseData = HttpClientTools.httpSendPostForm(payAgentPlatform.getPayOrderAddr(), params);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            reqPayAgent.setFailReason("新益达代付下单报错原因:" + e);
        }
        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", responseData,withdrawLog.getOrderNo());
        Map<String, Object> resultMap = JsonUtil.json2Map(responseData);
        if (!CollectionUtils.isEmpty(resultMap)) {
            String code = resultMap.getOrDefault("code", "").toString();
            if ("10000".equals(code)) {
                log.info("新益达代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("message", "").toString());
                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn("新益达代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String sign = requestMap.remove("sign").toString();
        String code = requestMap.getOrDefault("code", "").toString();
        String status = requestMap.getOrDefault("status", "").toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>(requestMap);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String signStr = DigestUtils.md5Hex(tempStr);

        log.info("新益达代付回调签名字符串:" + sign + "_" + signStr);
        if (sign.equalsIgnoreCase(signStr) && "10000".equals(code)) {
            String outTradeNo = requestMap.getOrDefault("outTradeNo", "").toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(outTradeNo);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", outTradeNo);
                return "ERROR";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", outTradeNo);
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(outTradeNo);
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
    public String queryOrderPay(PayAgentLog payAgentLog) throws Exception {
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(payAgentLog.getWithdrawOrderNo());
        PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById(payAgentLog.getPayAgentPlatId());

        String md5key = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        Map<String, String> prams = new HashMap<>();
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
        log.info("新益达代付查询结果- result:{}", responseData);
        if ( StringUtils.isNotBlank(responseData)) {
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
                    }
                }
                return resultMap.getOrDefault("msg", "").toString();
            }
        }
        return "新益达代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
