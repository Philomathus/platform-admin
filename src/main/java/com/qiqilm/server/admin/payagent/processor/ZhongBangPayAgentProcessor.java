package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeZhongBangType;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.AuthUtil;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Repository(value = ConstantsPayAgent.ZHONGBANG + "PayAgentProcessor")
@Log4j2
public class ZhongBangPayAgentProcessor extends AbstractPayAgent {
    //填充类型
    public static final String AES_TYPE = "AES/ECB/PKCS5Padding";

    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        BankCodeZhongBangType bankCodeType = BankCodeZhongBangType.getCodeByDesc( withdrawLog.getBankName() );
        if ( bankCodeType == null ) {
            payAgentService.callBackOrder( withdrawLog,payAgentPlatform );
            log.warn( payAgentPlatform.getName()+"无法支持的银行类型 - 银行类型:{}", withdrawLog.getBankName() );
            throw new BusinessException( payAgentPlatform.getName()+"无法支持的银行类型：" + withdrawLog.getBankName() );
        }
        withdrawLog.setBankCode( bankCodeType.name() );

        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("merchantId", payAgentPlatform.getMerId());
        dataMap.put("orderId", withdrawLog.getOrderNo());
        dataMap.put("amount", withdrawLog.getWithdrawMoney().multiply(BigDecimal.valueOf(100)).setScale(0, BigDecimal.ROUND_HALF_UP));
        dataMap.put("callbackUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        dataMap.put("accountName", withdrawLog.getBankUserName());
        dataMap.put("bankCardNo", withdrawLog.getBankAccount());
        dataMap.put("ebankEnName", withdrawLog.getBankCode());
        dataMap.put("payInfo", "payInfo");

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(dataMap);
        String sign = RSACoder.signSha1Rsa(tempStr, payAgentPlatform.getSignPrivateKey());
        dataMap.put("sign", sign);

        String jsonStr = JsonUtil.object2Json(dataMap);
        String requestContent = encrypt(jsonStr,signMd5);

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

        String requestContent = requestMap.getOrDefault("responseContent", "").toString();
        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String content = decrypt(requestContent,signMd5);

        Map<String,Object> dataMap = JsonUtil.json2Map(content);
        String submitAmount = dataMap.getOrDefault("submitAmount", "").toString();
        String orderAmount = dataMap.getOrDefault("orderAmount", "").toString();
        String orderCost = dataMap.getOrDefault("orderCost", "").toString();

        String sign = dataMap.remove("sign").toString();
        String status = dataMap.remove("status").toString();
        dataMap.remove("message");

        Map<String, Object> treeMap = new TreeMap<>(dataMap);
        treeMap.put("submitAmount",new BigDecimal(submitAmount).setScale(2,BigDecimal.ROUND_HALF_UP));
        treeMap.put("orderAmount",new BigDecimal(orderAmount).setScale(2,BigDecimal.ROUND_HALF_UP));
        treeMap.put("orderCost",new BigDecimal(orderCost).setScale(2,BigDecimal.ROUND_HALF_UP));

        String tempStr = this.assemblyUrl(treeMap);
        String rspSign = RSACoder.signSha1Rsa(tempStr, payAgentPlatform.getSignPrivateKey());

        log.info(payAgentPlatform.getName() + "回调签名:" + rspSign + "_" + sign);
        if (rspSign.equalsIgnoreCase(sign)) {
            String orderId = requestMap.getOrDefault("orderId", "").toString();
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(orderId);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", orderId);
                return "fail";
            }
            if (withdrawLog.getStatus() == 2) {
                log.error("订单已拒绝，无需回调 - merOrderNo:{}", orderId);
                return "success";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", orderId);
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(orderId);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "4".equals(status));
            log.info(payAgentPlatform.getName() + "订单号:{},回调状态:{},", orderId, "4".equals(status) ? "成功" : "失败");
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
        PayAgentPlatform payAgentPlatform =
                payAgentPlatformMapper.selectPayAgentPlatformById(payAgentLog.getPayAgentPlatId());

        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("merchantId", payAgentPlatform.getMerId());
        dataMap.put("orderId", withdrawLog.getOrderNo());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);
        String tempStr = this.assemblyUrl(dataMap);
        String sign = RSACoder.signSha1Rsa(tempStr, payAgentPlatform.getSignPrivateKey());
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
     * @param cleartext,aesKey
     * @return String
     */
    private static String encrypt( String cleartext, String aesKey ) {
        try {
            SecretKeySpec key    = new SecretKeySpec( aesKey.getBytes(), "AES" );
            Cipher        cipher = Cipher.getInstance( AES_TYPE );
            cipher.init( Cipher.ENCRYPT_MODE, key );
            byte[] encryptedData = cipher.doFinal( cleartext.getBytes( StandardCharsets.UTF_8 ) );
            return Base64Utils.encodeToString( encryptedData );
        } catch ( Exception e ) {
            log.warn( e );
            return cleartext;
        }
    }

    /**
     * 解密
     *
     * @param encrypted,aesKey
     * @return String
     */
    public static String decrypt(String encrypted, String aesKey) {
        try {
            SecretKeySpec key = new SecretKeySpec(aesKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance(AES_TYPE);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedData = cipher.doFinal(Base64Utils.decodeFromString(encrypted));
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn(e);
            return encrypted;
        }
    }
}

