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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Repository(value = ConstantsPayAgent.LELE + "PayAgentProcessor")
@Log4j2
public class LeLePayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        Map<String, Object> bodyMap = new HashMap<>();
        String mchId = payAgentPlatform.getMerId();
        String mchOrderId = withdrawLog.getOrderNo();
        String currency = "CNY";
        String amount = withdrawLog.getWithdrawMoney().setScale(2, RoundingMode.HALF_UP).toString();
        String account = withdrawLog.getBankAccount().trim();
        String accountRemark = withdrawLog.getBankName().trim();
        String accountOwner = withdrawLog.getBankUserName().trim();

        bodyMap.put("mchId", mchId);
        bodyMap.put("amount", withdrawLog.getWithdrawMoney().setScale(2, RoundingMode.HALF_UP));
        bodyMap.put("account", account);
        bodyMap.put("mchOrderId", mchOrderId);
        bodyMap.put("accountOwner", accountOwner);
        bodyMap.put("accountRemark", accountRemark);
        bodyMap.put("currency", "CNY");
        bodyMap.put("notifyUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        log.info("请求报文:"+JsonUtil.object2Json(bodyMap));

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        //注意⾦额amount必须保留两位⼩数
        //mchId + mchOrderId + currency + amount + account + accountRemark + accountOwner + privateKey;

        String tempStr = mchId + mchOrderId + currency + amount + account + accountRemark + accountOwner + signMd5;
        log.info("签名前字符串:"+tempStr);
        String sign = threeMd5(tempStr);
        bodyMap.put("sign", sign);
        log.info("签名字符串:"+sign);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity(bodyMap, httpHeaders);

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
            reqPayAgent.setFailReason(payAgentPlatform.getName() + "下单报错原因:" + e);
        }
        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            String code = resultMap.getOrDefault("code", "").toString();
            if ("0".equals(code)) {
                log.info(payAgentPlatform.getName() + "订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("msg", "").toString());

                payAgentService.callBackOrder(withdrawLog, payAgentPlatform);
            }
        }
        log.warn(payAgentPlatform.getName() + "订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String sign = requestMap.remove("sign").toString();
        //状态： W（审核中）、Y(成功)、N(驳回)
        String payStatus = requestMap.getOrDefault("payStatus", "").toString();
        //商户订单号
        String mchOrderId = requestMap.getOrDefault("mchOrderId", "").toString();

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        //String singSrc = mchOrderId + payStatus + amount + privateKey
        String tempStr = mchOrderId + payStatus + requestMap.get("amount").toString() + signMd5;
        String signStr = threeMd5(tempStr);

        log.info(payAgentPlatform.getName() + "回调签名字符串:" + sign + "_" + signStr);
        if (sign.equalsIgnoreCase(signStr)) {
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(mchOrderId);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", mchOrderId);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", mchOrderId);
                return "ok";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(mchOrderId);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "Y".equals(payStatus));
            return "ok";
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

        Map<String, Object> resultMap = null;
        String url = payAgentPlatform.getPayOrderQueryAddr() + withdrawLog.getOrderNo();
        try {
            resultMap = restTemplate.getForObject(url, Map.class);
            log.info(payAgentPlatform.getName() + "查询结果- result:{}", JsonUtil.object2Json(resultMap));
            if (!CollectionUtils.isEmpty(resultMap)) {
                String code = resultMap.getOrDefault("code", "").toString();
                if ("0".equals(code)) {
                    String data = resultMap.getOrDefault("data", "").toString();
                    if ("Y".equals(data) || "N".equals(data)) {
                        // status 4代付中 5代付失败 6代付成功
                        // data 状态： W（审核中）、Y(审核通过-已下发)、N(驳回)
                        int status = 4;
                        if ("Y".equals(data)) {
                            status = 6;
                        } else {
                            status = 5;
                        }
                        payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, 1);
                    }
                }
                return resultMap.getOrDefault("msg", "").toString();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }

    //三次MD5计算
    public static String threeMd5(String src) {
        String s1 = DigestUtils.md5Hex(src).toUpperCase();//此处转为⼤写
        String s2 = DigestUtils.md5Hex(s1).toUpperCase();//此处转为⼤写
        return  DigestUtils.md5Hex(s2).toUpperCase();//此处转为⼤写
    }
}
