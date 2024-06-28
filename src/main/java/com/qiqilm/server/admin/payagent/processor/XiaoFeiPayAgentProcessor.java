package com.qiqilm.server.admin.payagent.processor;

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
import org.apache.commons.lang3.StringUtils;
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

@Repository(value = ConstantsPayAgent.XIAO_FEI + "PayAgentProcessor")
@Log4j2
public class XiaoFeiPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        Map<String, String> dataMap = new TreeMap<>();
        dataMap.put("amount", withdrawLog.getWithdrawMoney().setScale(2,
                BigDecimal.ROUND_HALF_UP).toString());
        dataMap.put("outOrderNum", withdrawLog.getOrderNo());
        dataMap.put("mchNum", payAgentPlatform.getMerId());
        dataMap.put("timestamp", DateFormatUtils.formate(new Date(), DateFormatUtils.TIGHT_PATTERN_DATETIME));
        dataMap.put("payType", "bank");
        dataMap.put("account", withdrawLog.getBankAccount().trim());
        dataMap.put("accountName", withdrawLog.getBankUserName().trim());

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String signStr = this.assemblyUrl(dataMap) + signMd5;
        dataMap.put("sign", DigestUtils.md5Hex(signStr).toUpperCase());
        dataMap.put("bankName", withdrawLog.getBankName());
        dataMap.put("notifyUrl", sysConfigCacheUtil.getConf("payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        dataMap.forEach((k, v) -> map.add(k, v));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, httpHeaders);

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
        }
        log.info(payAgentPlatform.getName()+"下单结果{},订单号:{}", JsonUtil.object2Json(resultMap),withdrawLog.getOrderNo());
        if (!CollectionUtils.isEmpty(resultMap)) {
            if (StringUtils.equals("200", String.valueOf(resultMap.get("code")))) {
                Map<String, Object> attrDataMap = (Map<String, Object>) resultMap.get("attrData");
                if (StringUtils.equals("wait", String.valueOf(attrDataMap.get("status"))) ||
                        StringUtils.equals("assign", String.valueOf(attrDataMap.get("status")))) {
                    log.warn("小飞代付下单成功:" + JsonUtil.object2Json(resultMap));
                    return true;
                }
            } else {
                if (com.qiqilm.server.admin.utils.StringUtils.isNotBlank(resultMap.get("message").toString())) {
                    reqPayAgent.setFailReason(resultMap.getOrDefault("message", "").toString());
                } else {
                    reqPayAgent.setFailReason(resultMap.getOrDefault("msg", "").toString());
                }
            }
        }
        log.warn("小飞代付下单失败,orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }

    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        if (this.checkWhiteIp(payAgentPlatform.getPlatWhiteIpList(), realIp)) {
            log.warn("请求ip非白名单:{},request:{}", realIp, JsonUtil.object2Json(requestMap));
        }
        SortedMap<String, Object> sortedMap = new TreeMap<>(requestMap);
        sortedMap.remove("errMsg");
        String sign = sortedMap.remove("sign").toString();

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY);

        String signStr = this.assemblyUrl(sortedMap) + signMd5;
        if (StringUtils.equals(sign, DigestUtils.md5Hex(signStr).toUpperCase())) {
            String status = requestMap.getOrDefault("status", "").toString();
            String outOrderNum = requestMap.getOrDefault("outOrderNum", "").toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(outOrderNum);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", outOrderNum);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", outOrderNum);
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(outOrderNum);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, requestMap.get("orderNum").toString(),
                    payAgentPlatform, StringUtils.equals(status, "success"));
            return "success";
        }
        return "fail";
    }

    @Override
    public Map<String, Object> reverseCheckOrderPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap,
                                                    String realIp) {
        return null;
    }

    @Override
    public String queryOrderPay(PayAgentLog payAgentLog) throws Exception {
        MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(payAgentLog.getWithdrawOrderNo());
        PayAgentPlatform payAgentPlatform =
                payAgentPlatformMapper.selectPayAgentPlatformById(payAgentLog.getPayAgentPlatId());

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.getForObject(payAgentPlatform.getPayOrderQueryAddr() + "?outOrderNum="
                    + withdrawLog.getOrderNo(), Map.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.warn("小飞代付查询结果:{}", JsonUtil.object2Json(resultMap));
        if (!CollectionUtils.isEmpty(resultMap)) {
            if (StringUtils.equals("200", String.valueOf(resultMap.get("code")))) {
                Map<String, Object> attrDataMap = (Map<String, Object>) resultMap.get("attrData");
                String statusCode = String.valueOf(attrDataMap.get("status"));
                int status = 4;
                int orderState = 0;
                if ("success".equals(statusCode)) {
                    status = 6;
                    orderState = 1;
                } else {
                    status = 5;
                    orderState = 2;
                }
                payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderState);
            }
            return resultMap.getOrDefault("msg", "").toString();
        }
        return "小飞代付查询失败,订单号:"+withdrawLog.getOrderNo();
    }
}
