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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository(value = ConstantsPayAgent.JIUJIU + "PayAgentProcessor")
@Log4j2
public class JiuJiuPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("merchant_id", payAgentPlatform.getMerId());
        bodyMap.put("type", "df");
        bodyMap.put("amount", withdrawLog.getWithdrawMoney().setScale(2, RoundingMode.HALF_UP));
        bodyMap.put("orderid", withdrawLog.getOrderNo());
        bodyMap.put("uid", UUID.randomUUID().toString().replace("-", ""));
        bodyMap.put("attach", "attach");
        bodyMap.put("sendtype", "POST");
        bodyMap.put("callbackurl", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );
        bodyMap.put("cardno", withdrawLog.getBankAccount().trim());
        bodyMap.put("bankname", withdrawLog.getBankName().trim());
        bodyMap.put("payee", withdrawLog.getBankUserName().trim());

        String md5key = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        List<Map.Entry<String, Object>> itmes = new ArrayList<>(bodyMap.entrySet());
        Stream<Map.Entry<String, Object>> sm = itmes.stream().sorted(Comparator.comparing(o -> o.getKey()));
        sm = sm.filter(entry -> (entry.getValue() != null && !entry.getValue().toString().isEmpty()));
        List<Map.Entry<String, Object>> list = sm.collect(Collectors.toList());
        String content = list.stream().map(i -> i.toString()).collect(Collectors.joining("&"));
        content = new StringBuffer(content).append("&").append("key").append("=").append(md5key).toString();

        String sign = DigestUtils.md5Hex(content);
        bodyMap.put("sign", sign);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll(bodyMap);
        log.warn(JsonUtil.object2Json(requestMap));
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
            reqPayAgent.setFailReason("久久代付下单报错原因:" + e);
        }
        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            String code = resultMap.getOrDefault("code", "").toString();
            if ("0".equals(code)) {
                log.info("久久代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
                return true;
            } else {
                reqPayAgent.setFailReason(resultMap.getOrDefault("message", "").toString());

                payAgentService.callBackOrder( withdrawLog,payAgentPlatform );
            }
        }
        log.warn("久久代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String sign = requestMap.remove("sign").toString();
        String state = requestMap.getOrDefault("state", "").toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>(requestMap);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String tempStr = this.assemblyUrl(bodyMap) + "&key=" + signMd5;
        String signStr = DigestUtils.md5Hex(tempStr);

        log.info("久久代付回调签名字符串:" + sign + "_" + signStr);
        if (sign.equalsIgnoreCase(signStr)) {
            String orderid = requestMap.getOrDefault("orderid", "").toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(orderid);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", orderid);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", orderid);
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(orderid);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "1".equals(state));
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
        try {
            Map<String, Object> resultMap = null;
            resultMap = restTemplate.execute(payAgentPlatform.getPayOrderQueryAddr() + "?order_id=" + withdrawLog.getOrderNo(), HttpMethod.GET,
                    restTemplate.httpEntityCallback(null), response -> {
                        InputStream bodyStream = response.getBody();
                        String text;
                        try (Reader reader = new InputStreamReader(bodyStream)) {
                            text = CharStreams.toString(reader);
                        }
                        return JsonUtil.json2Map(text);
                    });
            log.info("久久代付查询结果- result:{}", JsonUtil.object2Json(resultMap));
            if (!CollectionUtils.isEmpty(resultMap)) {
                String code = String.valueOf(resultMap.getOrDefault("code", "").toString());
                int data = Integer.parseInt((resultMap.getOrDefault("data", -1).toString()));
                //status 4代付中 5代付失败 6代付成功
                //code: 0成功(已支付)   1失败(订单未找到,未支付,已驳回,冲正驳回)
                //data: 订单状态(-1:订单未找到 0:未支付 1:已支付 2:已驳回 3:冲正驳回)
                int status = 4;
                if ("0".equals(code) && data == 1) {
                    status = 6;
                } else if ("1".equals(code) && data != 1 && data != 0) {
                    status = 5;
                }
                payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, data);
                return resultMap.getOrDefault("msg", "").toString();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "久久代付查询失败,订单号:"+withdrawLog.getOrderNo();
    }
}
