package com.qiqilm.server.admin.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.sms.SmsClient;
import com.baidubce.services.sms.SmsClientConfiguration;
import com.baidubce.services.sms.model.SendMessageV3Request;
import com.baidubce.services.sms.model.SendMessageV3Response;
import com.qiqilm.server.admin.domain.ServerSms;
import com.qiqilm.server.admin.domain.SmsFailLog;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.ServerSmsMapper;
import com.qiqilm.server.admin.service.ISmsFailLogService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Log4j2
@Component
public class SmsApi {
    @Autowired
    private ISmsFailLogService smsFailLogService;
    @Autowired
    private ServerSmsMapper    serverSmsMapper;
    @Resource
    private RestTemplate       restTemplate;

    //无需修改,用于格式化鉴权头域,给"X-WSSE"参数赋值
    private static final String WSSE_HEADER_FORMAT =
            "UsernameToken Username=\"%s\",PasswordDigest=\"%s\",Nonce=\"%s\"," + "Created=\"%s\"";
    //无需修改,用于格式化鉴权头域,给"Authorization"参数赋值
    private static final String AUTH_HEADER_VALUE  = "WSSE realm=\"SDP\",profile=\"UsernameToken\",type=\"Appkey\"";


    public String sendMemSms( String phone, String msg ) {
        if ( StringUtils.isEmpty( phone ) ) {
            throw new BusinessException( "手机号不能为空" );
        }
        if ( StringUtils.isEmpty( msg ) ) {
            throw new BusinessException( "发送信息不能为空" );
        }
        ServerSms serverSms1 = new ServerSms();
        serverSms1.setName( "会员通知" );
        List<ServerSms> serverSmsList = serverSmsMapper.selectServerSmsList( serverSms1 );
        if ( serverSmsList.isEmpty() ) {
            throw new BusinessException( "会员sms通道不存在,无法发送" );
        } else {
            ServerSms serverSms = serverSmsList.get( 0 );
            switch ( serverSms.getProvider() ) {
            case 0:
                msg = this.sendSmsTencent( serverSms, phone, msg );
                break;
            case 1:
                msg = this.sendSmsAliyun( serverSms, phone, msg );
                break;
            case 2:
                msg = this.sendSmsBaidu( serverSms, phone, msg );
                break;
            case 3:
                msg = this.sendSmsHuawei( serverSms, phone, msg );
                break;
            case 4:
                msg = this.sendSmsYunJi( serverSms, phone, msg );
                break;
            case 5:
                msg = this.sendSmsBao( serverSms, phone, msg );
                break;
            default:
                throw new BusinessException( "不支持的短信运营商类型" );
            }
        }
        return msg;
    }

    private String sendSmsTencent( ServerSms serverSms, String phone, String msg ) {
        try {
            Credential  cred        = new Credential( serverSms.getAppKey(), serverSms.getAppAccess() );
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setReqMethod( "POST" );
            httpProfile.setEndpoint( "sms.tencentcloudapi.com" );
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile( httpProfile );
            com.tencentcloudapi.sms.v20190711.SmsClient client = new com.tencentcloudapi.sms.v20190711.SmsClient( cred,
                    serverSms.getRegion(), clientProfile );
            com.tencentcloudapi.sms.v20190711.models.SendSmsRequest req =
                    new com.tencentcloudapi.sms.v20190711.models.SendSmsRequest();
            req.setSmsSdkAppid( serverSms.getSmsSdkAppid() );
            req.setSign( serverSms.getSignature() );
            req.setTemplateID( serverSms.getTemplate() );
            /* 下发手机号码，采用 e.164 标准，+[国家或地区码][手机号]
             * 例如+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号*/
            String[] phoneNumbers = { "+86" + phone };
            req.setPhoneNumberSet( phoneNumbers );
            String[] templateParams = { msg };
            req.setTemplateParamSet( templateParams );
            com.tencentcloudapi.sms.v20190711.models.SendSmsResponse res = client.SendSms( req );
            if ( res.getSendStatusSet() != null && "Ok".equalsIgnoreCase( res.getSendStatusSet()[ 0 ].getCode() ) ) {
                return msg;
            } else {
                String rspCode    = res.getSendStatusSet()[ 0 ].getCode();
                String rspMessage = res.getSendStatusSet()[ 0 ].getMessage();
                String smsName    = "腾讯云";
                String subname    = serverSms.getName();
                errorLog( rspCode, rspMessage, phone, smsName, subname );
                log.warn( "短信发送失败:{}", JsonUtil.object2Json( res ) );
                throw new BusinessException( com.tencentcloudapi.sms.v20190711.models.SendSmsResponse.toJsonString( res ) );
            }
        } catch ( TencentCloudSDKException e ) {
            throw new BusinessException( e.getMessage() );
        }
    }

    private String sendSmsAliyun( ServerSms serverSms, String phone, String msg ) {
        System.setProperty( "sun.net.client.defaultConnectTimeout", "10000" );
        System.setProperty( "sun.net.client.defaultReadTimeout", "10000" );
        final String   regionId = serverSms.getRegion();
        IClientProfile profile  = DefaultProfile.getProfile( regionId, serverSms.getAppKey(), serverSms.getAppAccess() );
        DefaultProfile.addEndpoint( regionId, "Dysmsapi", "dysmsapi.aliyuncs.com" );
        IAcsClient acsClient = new DefaultAcsClient( profile );

        //组装请求对象
        SendSmsRequest smsRequest = new SendSmsRequest();
        smsRequest.setSysMethod( MethodType.POST );
        smsRequest.setPhoneNumbers( phone );
        smsRequest.setSignName( serverSms.getSignature() );
        smsRequest.setTemplateCode( serverSms.getTemplate() );
        smsRequest.setTemplateParam( "{\"msg\":" + msg + "}" );

        try {
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse( smsRequest );
            if ( sendSmsResponse.getCode() != null && "OK".equals( sendSmsResponse.getCode() ) ) {
                return msg;
            } else {
                String rspCode    = sendSmsResponse.getCode();
                String rspMessage = sendSmsResponse.getMessage();
                String smsName    = "阿里云";
                String subname    = serverSms.getName();
                errorLog( rspCode, rspMessage, phone, smsName, subname );
                // {"requestId":"01C0231F-AF35-4AE8-A92B-BC10ACDB90C6","bizId":null,"msg":"isv.SMS_TEMPLATE_ILLEGAL",
                // "message":"模板不合法(不存在或被拉黑)"}
                log.warn( "阿里云短信发送失败:{}", JsonUtil.object2Json( sendSmsResponse ) );
                throw new BusinessException( JsonUtil.object2Json( sendSmsResponse ) );
            }
        } catch ( ClientException e ) {
            throw new BusinessException( e.getErrMsg() );
        }
    }

    private String sendSmsBaidu( ServerSms serverSms, String phone, String msg ) {
        SmsClientConfiguration config = new SmsClientConfiguration();
        config.setCredentials( new DefaultBceCredentials( serverSms.getAppKey(), serverSms.getAppAccess() ) );
        config.setEndpoint( serverSms.getRegion() );
        SmsClient client = new SmsClient( config );

        SendMessageV3Request request = new SendMessageV3Request();
        request.setMobile( phone );
        request.setSignatureId( serverSms.getSignature() );
        request.setTemplate( serverSms.getTemplate() );
        Map<String, String> contentVar = new HashMap<>();
        contentVar.put( "msg", msg );
        //contentVar.put( "minute", "1" );
        request.setContentVar( contentVar );
        try {
            SendMessageV3Response sendSmsResponse = client.sendMessage( request );
            // 解析请求响应 response.isSuccess()为true 表示成功
            if ( sendSmsResponse != null && sendSmsResponse.isSuccess() ) {
                return msg;
            } else {
                String rspCode    = sendSmsResponse.getCode();
                String rspMessage = sendSmsResponse.getMessage();
                String smsName    = "百度云";
                String subname    = serverSms.getName();
                errorLog( rspCode, rspMessage, phone, smsName, subname );
                log.warn( "百度云短信发送失败:{}", JsonUtil.object2Json( sendSmsResponse ) );
                throw new BusinessException( JsonUtil.object2Json( sendSmsResponse ) );
            }
        } catch ( BusinessException e ) {
            throw new BusinessException( e.getMessage() );
        }
    }

    private String sendSmsHuawei( ServerSms serverSms, String phone, String code ) {
        String receiver      = "+86" + phone;
        String templateParas = "[\"" + code + "\"]";

        Map<String, String> params = new HashMap<>();
        params.put( "from", serverSms.getSignature() );
        params.put( "to", receiver );
        params.put( "templateId", serverSms.getTemplate() );
        params.put( "templateParas", templateParas );
        params.put( "signature", serverSms.getName() );

        StringBuilder sb = new StringBuilder();
        params.forEach( ( k, v ) -> {
            try {
                sb.append( k ).append( "=" ).append( URLEncoder.encode( v, "UTF-8" ) ).append( "&" );
            } catch ( UnsupportedEncodingException e ) {
                throw new RuntimeException( e );
            }
        } );
        String body = sb.substring( 0, sb.length() - 1 );

        //请求Headers中的X-WSSE参数值
        String wsseHeader = buildWsseHeader( serverSms.getAppKey(), serverSms.getAppAccess() );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
        httpHeaders.add( "Authorization", AUTH_HEADER_VALUE );
        httpHeaders.add( "X-WSSE", wsseHeader );
        HttpEntity<String> httpEntity = new HttpEntity<>( body, httpHeaders );

        try {
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(
                    serverSms.getEndpoint() + "/sms/batchSendSms/v1", httpEntity, Map.class );
            Map<String, Object> entityBody = responseEntity.getBody();
            if ( responseEntity.getStatusCode().is2xxSuccessful() ) {
                if ( !CollectionUtils.isEmpty( entityBody ) ) {
                    String rspCode = entityBody.getOrDefault( "code", "" ).toString();
                    if ( "000000".equals( rspCode ) ) {
                        return code;
                    }
                }
            }
            log.error( JsonUtil.object2Json( entityBody ) );
        } catch ( Exception e ) {
            log.error( "短信发送失败" + e.getMessage(), e );

        }
        throw new BusinessException( "短信发送失败,请联系客服" );
    }

    /**
     * 构造X-WSSE参数值 Construct X-WSSE parameter value
     *
     * @param appKey
     * @param appSecret
     */
    private static String buildWsseHeader( String appKey, String appSecret ) {
        try {
            SimpleDateFormat sdf   = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'Z'" );
            String           time  = sdf.format( new Date() );
            String           nonce = UuidUtil.getRandomUuidWithoutSeparator();
            MessageDigest    md    = MessageDigest.getInstance( "SHA-256" );
            md.update( ( nonce + time + appSecret ).getBytes() );
            String passwordDigestBase64Str = Base64Utils.encodeToString( md.digest() );
            return String.format( WSSE_HEADER_FORMAT, appKey, passwordDigestBase64Str, nonce, time );
        } catch ( NoSuchAlgorithmException e ) {
            log.error( e.getMessage(), e );
        }
        return null;
    }

    private String sendSmsYunJi( ServerSms serverSms, String phone, String code ) {
        Map<String, Object> body = new HashMap<>();
        body.put( "accessKey", serverSms.getAppKey() );
        body.put( "accessSecret", serverSms.getAppAccess() );
        body.put( "classificationSecret", serverSms.getClassificationKey() );
        body.put( "signCode", serverSms.getSignature() );
        body.put( "templateCode", serverSms.getTemplate() );
        body.put( "phone", phone );

        // 变量参数用map存
        Map<String, String> params = new HashMap<>();
        // 验证码参数示例
        params.put( "code", code );
        body.put( "params", params );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>( body, httpHeaders );

        try {
            Map<String, Object> resultMap = restTemplate.postForObject( serverSms.getEndpoint(), httpEntity, Map.class );
            if ( !CollectionUtils.isEmpty( resultMap ) ) {
                Map<String, Object> businessData = ( Map<String, Object> ) resultMap.getOrDefault( "BusinessData",
                        new HashMap<>() );

                String rspCode    = businessData.getOrDefault( "code", "0" ).toString();
                String rspMessage = businessData.getOrDefault( "msg", "" ).toString();
                if ( !CollectionUtils.isEmpty( businessData ) && "10000".equals( rspCode ) ) {
                    return code;
                }
                String smsName = "云极";
                String subname = serverSms.getName();
                errorLog( rspCode, rspMessage, phone, smsName, subname );
            }
            log.warn( "云极短信发送失败:{}", JsonUtil.object2Json( resultMap ) );


        } catch ( Exception e ) {
            throw new RuntimeException( e.getMessage(), e );
        }

        return null;
    }

    private String sendSmsBao( ServerSms serverSms, String phone , String code ) {

        try {
            String httpArg = "u=" + serverSms.getAppKey() + "&" +
                    "p=" + serverSms.getAppAccess() + "&" +
                    "m=" + phone + "&" +
                    "c=" + "【诗顾兔】您的验证码是 " + code  + "。如非本人操作，请忽略本短信";

            String httpUrl = serverSms.getEndpoint() + "?" + httpArg;

            ResponseEntity<Object> responseEntity = restTemplate.getForEntity(
                    httpUrl, Object.class );
            Object returnCode = responseEntity.getBody();

            if ( ! Objects.isNull( returnCode ) && org.apache.commons.lang3.StringUtils.isNotBlank( returnCode.toString() ) && "0".equals( returnCode.toString() ) ) {
                log.info( "send code success {} " ,  code  );
                return "【诗顾兔】您的验证码是 " + code  + "。如非本人操作，请忽略本短信";
            }
            return  null;
        } catch (Exception e) {
            log.error( e.getMessage() );
        }
        return null;
    }

    //记录短信登录异常日志
    private void errorLog( String rspCode, String rspMessage, String phone, String smsName, String subname ) {
        SmsFailLog smsFainLog = new SmsFailLog();
        smsFainLog.setCode( rspCode );
        smsFainLog.setMessage( rspMessage );
        smsFainLog.setPhone( phone );
        smsFainLog.setSmsName( smsName );
        smsFainLog.setSmsSubname( subname );
        Date date = new Date();
        smsFainLog.setCreateTime( date );
        smsFailLogService.insertSmsFailLog( smsFainLog );
    }
}
