package com.qiqilm.server.admin.payagent.processor;

import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

import static com.qiqilm.server.admin.utils.RsaUtil.getPrivateKey;
import static com.qiqilm.server.admin.utils.RsaUtil.getPublicKey;


@Repository(value = ConstantsPayAgent.QUANFU + "PayAgentProcessor")
@Log4j2
public class QuanFuPayAgentProcessor extends AbstractPayAgent {
    private static final String ALGORITHMS_SHA1WithRSA = "SHA1WithRSA";
    private static final String ALGORITHMS_SHA256WithRSA = "SHA256WithRSA";
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static String getAlgorithms(boolean isRsa2) {
        return isRsa2 ? ALGORITHMS_SHA256WithRSA : ALGORITHMS_SHA1WithRSA;
    }

    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("agtId", payAgentPlatform.getMerId());
        dataMap.put("tranCode", "2101");
        dataMap.put("orderId", withdrawLog.getOrderNo());
        dataMap.put("tranDate", DateFormatUtils.formate(new Date(), "yyyyMMdd"));
        dataMap.put("nonceStr", RANDOM(16, "0"));
        dataMap.put("txnAmt", withdrawLog.getWithdrawMoney().multiply(BigDecimal.valueOf(100)).setScale(0,BigDecimal.ROUND_HALF_UP));
        dataMap.put("accountNo", withdrawLog.getBankAccount());
        dataMap.put("bankName", STR2HEX(withdrawLog.getBankName()));
        dataMap.put("accountName", STR2HEX(withdrawLog.getBankUserName()));
        dataMap.put("cnaps", "308290003298");
        dataMap.put("accountType", "1");
        dataMap.put("notifyUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl") + ConstantsPayAgent.QUANFU);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        String tempStr = this.assemblyUrl(dataMap) + "&key=" + signMd5;
        String sign = encryption(tempStr).toUpperCase();
        sign = sign(sign,payAgentPlatform.getSignPrivateKey(),true);
        dataMap.put("sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(dataMap);
        log.warn(payAgentPlatform.getName() + "下单请求参数:{}", JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestMap, httpHeaders);

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.postForObject(payAgentPlatform.getPayOrderAddr(), httpEntity, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        log.info( payAgentPlatform.getName() + "下单结果 - result:{}", JsonUtil.object2Json( resultMap ) );
        if (!CollectionUtils.isEmpty(resultMap)) {
            if ("000000".equals(resultMap.getOrDefault("rspcode", "").toString())) {
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
        String rspSign = requestMap.remove("sign").toString();
        String orderSn = requestMap.getOrDefault("order_sn", "").toString();
        String orderNo = requestMap.getOrDefault("order_no", "").toString();
        String createTime = requestMap.getOrDefault("create_time", "").toString();
        String operationTime = requestMap.getOrDefault("operation_time", "").toString();
        String status = requestMap.getOrDefault("status", "").toString();
        String time = requestMap.getOrDefault("time", "").toString();

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        String tempStr = orderSn + orderNo + createTime + operationTime + status + time + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);

        log.info(payAgentPlatform.getName() + "回调签名:" + rspSign + "_" + sign);
        if (rspSign.equalsIgnoreCase(sign)) {
            String order_num = requestMap.getOrDefault("order_sn", "").toString();
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(order_num);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", order_num);
                return "fail";
            }
            if (withdrawLog.getStatus() == 2) {
                log.error("订单已拒绝，无需回调 - merOrderNo:{}", order_num);
                return "success";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", order_num);
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(order_num);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "1".equals(status));
            log.info(payAgentPlatform.getName() + "订单号:{},回调状态:{},", order_num, "1".equals(status) ? "成功" : "失败");
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

        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("store_id", payAgentPlatform.getMerId());
        dataMap.put("order_sn", withdrawLog.getOrderNo());
        dataMap.put("time", System.currentTimeMillis() / 1000);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        StringBuilder sb = new StringBuilder();
        dataMap.forEach((k, v) -> sb.append(v));
        String tempStr = sb.substring(0, sb.length()) + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        dataMap.put("sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(dataMap);
        log.warn(payAgentPlatform.getName() + "下单请求参数:{}", JsonUtil.object2Json(requestMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestMap, httpHeaders);

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.postForObject(payAgentPlatform.getPayOrderQueryAddr(), httpEntity, Map.class);
            log.warn(payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json(resultMap));

            if (!CollectionUtils.isEmpty(resultMap)) {
                //  status
                //  4代付中 5代付失败 6代付成功
                int status = 4;

                //  statusCode
                //  1=打款成功，2=待打款，3=打款失败
                String statusCode = null;

                String code = resultMap.getOrDefault("code", "").toString();
                if (!"1".equals(code)) {
                    statusCode = "3";
                }

                Map<String, Object> map = (Map<String, Object>) resultMap.getOrDefault("data", "");
                if (!CollectionUtils.isEmpty(map)) {
                    statusCode = map.getOrDefault("status", "").toString();
                }

                if ("1".equals(statusCode) || "3".equals(statusCode)) {
                    if ("1".equals(statusCode)) {
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

    /**
     *
     * @param plainText
     *            明文
     * @return 32位密文
     */
    public static String encryption(String plainText) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            re_md5 = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re_md5;
    }

    /**
     * <p>
     * 将普通字符串转换成十六进制字符串
     * </p>
     * STR2HEX(123456)返回结果为313233343536 <br>
     *
     * @param args
     *            [0] : 普通字符串
     * @return 十六进制字符串 @
     */
    public static String STR2HEX(String args) {
        if (StringUtils.isEmpty(args))
            throw new RuntimeException("HEX2STR");
        return new String(Hex.encodeHex(args.getBytes()));
    }

    /**
     * 取随机字符串 <br>
     *
     * @param args1
     *            构造指定长度的随机字符串
     * @param args2
     *            指明是否包含字母，0-包含字母,数字和字母混合,默认是2 1-不包含数字,只有字母 2－不包含字母,只有数字
     * @return @
     */
    public static String RANDOM(int args1, String args2) {
        if (StringUtils.isEmpty(args2))
            throw new RuntimeException("RANDOM");
        int len = args1;
        args2 = StringUtils.trim(args2);

        if (StringUtils.equals(args2, "0")) {
            return RandomStringUtils.randomAlphanumeric(len);
        } else if (StringUtils.equals(args2, "1")) {
            return RandomStringUtils.randomAlphabetic(len);
        } else {
            return RandomStringUtils.randomNumeric(len);
        }

    }

    /**
     * 私钥签名
     * @throws InvalidKeySpecException
     * @throws Exception
     */
    public static String sign(String content, String privateKey, boolean isRsa2) throws Exception {
//        PrivateKey priKey = getPrivateKey(privateKey);
//
//        Signature signature = Signature.getInstance("SHA1withRSA");
//        signature.initSign(priKey);
//        signature.update(content.getBytes(DEFAULT_CHARSET));
////        signature.sign();
////        return  byte2hex(signature.sign());
//        byte[] signed = signature.sign();
//        return Base64.getEncoder().encodeToString(signed);
        PrivateKey priKey = getPrivateKey(privateKey);
        java.security.Signature signature = java.security.Signature.getInstance(getAlgorithms(isRsa2));
        signature.initSign(priKey);
        signature.update(content.getBytes(DEFAULT_CHARSET));
        byte[] signed = signature.sign();
        return Base64.getEncoder().encodeToString(signed);
    }

    /**
     * 公钥验签
     */
    public static boolean verify(String content,String sign,String publicKey,boolean isRsa2) throws Exception {
        PublicKey pubKey = getPublicKey(publicKey);
        java.security.Signature signature = java.security.Signature.getInstance(getAlgorithms(isRsa2));
        signature.initVerify(pubKey);
        signature.update(content.getBytes(DEFAULT_CHARSET));
        return signature.verify(Base64.getDecoder().decode(sign));
    }

}
