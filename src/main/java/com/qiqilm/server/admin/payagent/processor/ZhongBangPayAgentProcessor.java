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
import org.apache.hadoop.hbase.io.crypto.aes.AES;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.qiqilm.server.admin.utils.RsaUtil.getPrivateKey;


@Repository(value = ConstantsPayAgent.ZHONGBANG + "PayAgentProcessor")
@Log4j2
public class ZhongBangPayAgentProcessor extends AbstractPayAgent {
    //填充类型
    public static final String AES_TYPE = "AES/ECB/PKCS5Padding";

    //编码方式
    public static final String CODE_TYPE = "UTF-8";
    static Logger logger = LogManager.getLogger(AES.class);

    private static final String ALGORITHMS_SHA1WithRSA = "SHA1WithRSA";
    private static final String ALGORITHMS_SHA256WithRSA = "SHA256WithRSA";
    private static final String DEFAULT_CHARSET = "UTF-8";

    private static String getAlgorithms(boolean isRsa2) {
        return isRsa2 ? ALGORITHMS_SHA256WithRSA : ALGORITHMS_SHA1WithRSA;
    }

    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("merchantId", payAgentPlatform.getMerId());
        dataMap.put("orderId", withdrawLog.getOrderNo());
        dataMap.put("amount", withdrawLog.getWithdrawMoney().multiply(BigDecimal.valueOf(100)).setScale(0, BigDecimal.ROUND_HALF_UP));
        dataMap.put("callbackUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl") + ConstantsPayAgent.ZHONGBANG);
        dataMap.put("accountName", withdrawLog.getBankUserName());
        dataMap.put("bankCardNo", withdrawLog.getBankAccount());
        dataMap.put("ebankEnName", getBankCode(withdrawLog.getBankName()));

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        String tempStr = this.assemblyUrl(dataMap);
        String sign = sign(tempStr, payAgentPlatform.getSignPrivateKey(), false);
        dataMap.put("sign", sign);

        String jsonStr = JsonUtil.object2Json(dataMap);
        String requestContent = encrypts(jsonStr,signMd5);

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("merchantId",payAgentPlatform.getMerId());
        hashMap.put("requestContent",requestContent);

        log.warn(payAgentPlatform.getName() + "下单请求参数{}", JsonUtil.object2Json(hashMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(hashMap, httpHeaders);

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
        log.info(payAgentPlatform.getName() + "下单结果 - result:{}", JsonUtil.object2Json(resultMap));

        if (!CollectionUtils.isEmpty(resultMap)) {
            String code = resultMap.getOrDefault("code", "").toString();
            if ("200".equals(code) || "201".equals(code) || "202".equals(code)) {
                log.info(payAgentPlatform.getName() + "订单提交成功 - listResult:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("message", "").toString());
                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn(payAgentPlatform.getName() + "订单提交失败 - result:{}", JsonUtil.object2Json(resultMap));
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {

        String rspSign = requestMap.remove("sign").toString();
        requestMap.values().removeIf(value -> !org.springframework.util.StringUtils.hasText(value.toString()));

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        StringBuilder sb = new StringBuilder();
        requestMap.forEach((k, v) -> sb.append(v).append("&"));
        String sign = signMd5 + "&" + sb.substring(0, sb.length() - 1);
        sign = DigestUtils.md5Hex(sign);

        log.info(payAgentPlatform.getName() + "回调签名:" + rspSign + "_" + sign);
        if (rspSign.equalsIgnoreCase(sign)) {
            String order_num = requestMap.getOrDefault("orderNo", "").toString();
            String status = requestMap.getOrDefault("status", "").toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(order_num);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", order_num);
                return "fail";
            }
            if (withdrawLog.getStatus() == 2) {
                log.error("订单已拒绝，无需回调 - merOrderNo:{}", order_num);
                return "SUCCESS";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", order_num);
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(order_num);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "SUCCESS".equals(status));
            log.info(payAgentPlatform.getName() + "订单号:{},回调状态:{},", order_num, "SUCCESS".equals(status) ? "成功" : "失败");
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
        PayAgentPlatform payAgentPlatform =
                payAgentPlatformMapper.selectPayAgentPlatformById(payAgentLog.getPayAgentPlatId());

        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("merchantId", payAgentPlatform.getMerId());
        dataMap.put("orderId", withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));
        String tempStr = this.assemblyUrl(dataMap);
        String sign = sign(tempStr, payAgentPlatform.getSignPrivateKey(), false);
        dataMap.put("sign", sign);

        log.warn(payAgentPlatform.getName() + "查询请求参数{}", JsonUtil.object2Json(dataMap));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(dataMap, httpHeaders);

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
                //  status
                //  4代付中 5代付失败 6代付成功
                int status = 4;

                //  statusCode
                //  1-成功，2-失败，3-处理中，4-订单不存在 5-审核拒绝
                String statusCode = resultMap.getOrDefault("status", "").toString();

                if ("1".equals(statusCode) || "2".equals(statusCode) || "4".equals(statusCode) || "5".equals(statusCode)) {
                    if ("1".equals(statusCode)) {
                        status = 6;
                    } else {
                        status = 5;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status,
                            Integer.parseInt(statusCode));
                }
                return resultMap.getOrDefault("msg", "").toString();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }

    /**
     * 加密
     *
     * @return
     */
    public static String encrypt(String cleartext, String aesKey) {
        try {
            SecretKeySpec key = new SecretKeySpec(aesKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance(AES_TYPE);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedData = cipher.doFinal(cleartext.getBytes(CODE_TYPE));
            return Base64Utils.encodeToString(encryptedData);
        } catch (Exception e) {
            logger.warn(e);
            return cleartext;
        }
    }

    /**
     * 私钥签名
     *
     * @throws
     * @throws Exception
     */
    private static String sign(String content, String privateKey, boolean isRsa2) throws Exception {
        PrivateKey priKey = getPrivateKey(privateKey);
        java.security.Signature signature = java.security.Signature.getInstance(getAlgorithms(isRsa2));
        signature.initSign(priKey);
        signature.update(content.getBytes(DEFAULT_CHARSET));
        byte[] signed = signature.sign();
        return Base64.getEncoder().encodeToString(signed);
    }

    /**
     * 加密
     *
     * @return
     */
    private static String encrypts( String cleartext, String aesKey ) {
        try {
            SecretKeySpec key    = new SecretKeySpec( aesKey.getBytes(), "AES" );
            Cipher        cipher = Cipher.getInstance( AES_TYPE );
            cipher.init( Cipher.ENCRYPT_MODE, key );
            byte[] encryptedData = cipher.doFinal( cleartext.getBytes( CODE_TYPE ) );
            return Base64Utils.encodeToString( encryptedData );
        } catch ( Exception e ) {
            logger.warn( e );
            return cleartext;
        }
    }

    private String getBankCode(String bankName){
        switch(bankName)
        {
            case "中国农业银行" :
                return "ABC";
            case "北京银行" :
                return "BCCB";
            case "东亚银行" :
                return "BEAI";
            case "中国银行" :
                return "BOC";
            case "交通银行" :
                return "BOCOM";
            case "渤海银行" :
                return "BOHC";
            case "上海银行" :
                return "BOS";
            case "中国建设银行" :
                return "CCB";
            case "光大银行" :
                return "CEB";
            case "兴业银行" :
                return "CIB";
            case "中国招商银行" :
                return "CMB";
            case "中国民生银行" :
                return "CMBC";
            case "浙商银行" :
                return "CZSB";
            case "中信银行" :
                return "ECITIC";
            case "广发银行" :
                return "GDB";
            case "徽商银行" :
                return "HSCB";
            case "华夏银行" :
                return "HXB";
            case "杭州银行" :
                return "HZCB";
            case "中国工商银行" :
                return "ICBC";
            case "宁波银行" :
                return "NBCB";
            case "南京银行" :
                return "NJCB";
            case "平安银行" :
                return "PAB";
            case "中国邮政银行" :
                return "PSBC";
            case "深圳发展银行" :
                return "SDB";
            case "浦发银行" :
                return "SPDB";
            default :
                return "ABC1";
        }
    }

}

