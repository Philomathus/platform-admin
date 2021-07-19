package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeLangYaType;
import com.qiqilm.server.admin.enums.BankCodeYinLianType;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.AuthUtil;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RSACoder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.*;

@Repository(value = ConstantsPayAgent.GONGFU + "PayAgentProcessor")
@Log4j2
public class GongFuPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent) throws Exception {
        //工商银行、农业银行、中国银行、建设银行、中信银行、上海银行、中国邮政、平安银行、浙商银行、渤海银行，光大银行，
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("工商银行", "2");
        typeMap.put("中国工商银行", "2");
        typeMap.put("农业银行", "3");
        typeMap.put("中国农业银行", "3");
        typeMap.put("中国银行", "4");
        typeMap.put("建设银行", "5");
        typeMap.put("中国建设银行", "5");
        typeMap.put("中信银行", "8");
        typeMap.put("上海银行", "48");
        typeMap.put("中国邮政储蓄银行", "1");
        typeMap.put("中国邮政银行", "1");
        typeMap.put("邮政储蓄银行", "1");
        typeMap.put("邮政银行", "1");
        typeMap.put("中国邮政", "1");
        typeMap.put("平安银行", "13");
        typeMap.put("浙商银行", "18");
        typeMap.put("渤海银行", "19");
        typeMap.put("光大银行", "9");
        typeMap.put("中国光大银行", "9");

        String bank_id = typeMap.getOrDefault(withdrawLog.getBankName(),"");

        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("merchant_no", payAgentPlatform.getMerId());
        bodyMap.put("amount", withdrawLog.getWithdrawMoney().multiply(BigDecimal.valueOf(100)).setScale(0,
                BigDecimal.ROUND_HALF_UP));
        bodyMap.put("order_no", withdrawLog.getOrderNo());

        if (!StringUtils.isNotBlank(bank_id)) {
            log.warn("功付代付订单提交失败,{}银行不支持,请联系技术", withdrawLog.getBankName());
            return false;
        } else if(withdrawLog.getBankName().length() > 4){
            log.warn("功付代付订单提交失败,因三方代付系统限制,银行名称只能是4个字,请联系用户修改");
            return false;
        } else {
            bodyMap.put("bank_id", bank_id);
        }

        bodyMap.put("payee_name", URLEncoder.encode(withdrawLog.getBankUserName().trim(),"utf-8"));
        bodyMap.put("bank_name", URLEncoder.encode(withdrawLog.getBankName().trim(),"utf-8"));
        bodyMap.put("bank_account", withdrawLog.getBankAccount().trim());
//        bodyMap.put("bank_branch_name", "bank_branch_name");
//        bodyMap.put("bank_sub_branch_name", "bank_sub_branch_name");
//        bodyMap.put("province", "province");
//        bodyMap.put("city", "city");
        bodyMap.put("sign_type", "MD5");
        bodyMap.put("sign_ts", System.currentTimeMillis()/1000);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(bodyMap) + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        bodyMap.put("sign", sign);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity(bodyMap, httpHeaders);

        Map<String, Object> resultMap = null;
        String url = payAgentPlatform.getPayOrderAddr() + payAgentPlatform.getMerId();
        try {
            resultMap = restTemplate.execute( url, HttpMethod.POST,
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
        log.info("功付代付下单结果 - result:{}", JsonUtil.object2Json(resultMap));
        if (!CollectionUtils.isEmpty(resultMap)) {
            //WAITING 等待处理
            //PROCESSING 处理中
            //SUCCESSFUL 处理成功
            //FAILURE 处理失败
            log.info("功付代付订单提交成功 - result:{}", JsonUtil.object2Json(resultMap));
            return true;
        }
        log.info("功付代付订单提交失败 - orderNo:{}", withdrawLog.getOrderNo());
        return false;
    }


    @Override
    public String callbackPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
        String sign = requestMap.remove("sign").toString();
        String state = requestMap.getOrDefault("state", "").toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>(requestMap);
        String notify_time = bodyMap.remove("notify_time").toString();
        bodyMap.put("notify_time",URLEncoder.encode(notify_time,"utf-8"));

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(bodyMap) + signMd5;
        String signStr = DigestUtils.md5Hex(tempStr);

        log.info("功付代付回调签名字符串:" + sign + "_" + signStr);
        if (sign.equalsIgnoreCase(signStr)) {
            String order_no = (String) requestMap.get("order_no");
            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo(order_no);
            if (withdrawLog == null) {
                log.error("提现相关记录丢失 - merOrderNo:{}", order_no);
                return "fail";
            }
            if (withdrawLog.getStatus() == 6) {
                log.error("已有代付记录 - merOrderNo:{}", order_no);
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo(order_no);
            payAgentService.processOrderPay(withdrawLog, payAgentLog, "", payAgentPlatform, "SUCCESSFUL".equals(state));
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
        Map<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put("merchant_no", payAgentPlatform.getMerId());
        bodyMap.put("order_no", withdrawLog.getOrderNo());
        bodyMap.put("sign_type", "MD5");
        bodyMap.put("sign_ts", System.currentTimeMillis()/1000);

        String signMd5 = RSACoder.decryptByPrivateKey(payAgentPlatform.getSignMd5(), AuthUtil.getSecurityKeyStr(
                "secretkey/payAgentPrivateKey"));

        String tempStr = this.assemblyUrl(bodyMap) + signMd5;
        String sign = DigestUtils.md5Hex(tempStr);
        bodyMap.put("sign", sign);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity(bodyMap, httpHeaders);

        Map<String, Object> resultMap = null;
        String url = payAgentPlatform.getPayOrderQueryAddr() + payAgentPlatform.getMerId() + "/" + withdrawLog.getOrderNo();
        try {
            resultMap = restTemplate.execute( url, HttpMethod.POST,
                    restTemplate.httpEntityCallback( httpEntity ), response -> {
                        InputStream bodyStream = response.getBody();
                        String      text;
                        try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                            text = CharStreams.toString( reader );
                        }
                        return JsonUtil.json2Map( text );
                    } );
            log.info("功付代付查询结果- result:{}", JsonUtil.object2Json(resultMap));
            if (!CollectionUtils.isEmpty(resultMap)) {
                String state = resultMap.getOrDefault("state", "").toString();
                // status 4代付中 5代付失败 6代付成功
                //state: WAITING 等待处理,PROCESSING 处理中,SUCCESSFUL 处理成功,FAILURE 处理失败
                if("SUCCESSFUL".equals(state) || "FAILURE".equals(state)) {
                    int status = 4;
                    if ("SUCCESSFUL".equals(state)) {
                        status = 6;
                    } else {
                        status = 5;
                    }
                    payAgentService.processOrder(payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, 1);
                }
                return JsonUtil.object2Json(resultMap);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "功付代付查询失败,订单号:" + withdrawLog.getOrderNo();
    }
}
