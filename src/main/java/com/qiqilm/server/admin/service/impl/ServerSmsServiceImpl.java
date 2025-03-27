package com.qiqilm.server.admin.service.impl;

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
import com.qiqilm.server.admin.cache.ServerSmsCacheUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.ServerSms;
import com.qiqilm.server.admin.exception.BaseException;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.ServerSmsMapper;
import com.qiqilm.server.admin.service.IServerSmsService;
import com.qiqilm.server.admin.service.SmsLoadBalancerService;
import com.qiqilm.server.admin.utils.*;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * SMS短信服务配置Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class ServerSmsServiceImpl implements IServerSmsService {
    private final ServerSmsMapper        serverSmsMapper;
    private final ServerSmsCacheUtil     serverSmsCacheUtil;
    private final TokenService           tokenService;
    private final RestTemplate           restTemplate;
    private final SmsLoadBalancerService smsLoadBalancerService;

    //无需修改,用于格式化鉴权头域,给"X-WSSE"参数赋值
    private static final String WSSE_HEADER_FORMAT =
            "UsernameToken Username=\"%s\",PasswordDigest=\"%s\",Nonce=\"%s\"," + "Created=\"%s\"";
    //无需修改,用于格式化鉴权头域,给"Authorization"参数赋值
    private static final String AUTH_HEADER_VALUE  = "WSSE realm=\"SDP\",profile=\"UsernameToken\",type=\"Appkey\"";

    /**
     * 查询SMS短信服务配置
     *
     * @param id SMS短信服务配置ID
     *
     * @return SMS短信服务配置
     */
    @Override
    public ServerSms selectServerSmsById( Long id ) {
        return serverSmsMapper.selectServerSmsById( id );
    }

    /**
     * 查询SMS短信服务配置列表
     *
     * @param serverSms SMS短信服务配置
     *
     * @return SMS短信服务配置
     */
    @Override
    public List<ServerSms> selectServerSmsList( ServerSms serverSms ) {
        return serverSmsMapper.selectServerSmsList( serverSms );
    }

    /**
     * 新增SMS短信服务配置
     *
     * @param serverSms SMS短信服务配置
     *
     * @return 结果
     */
    @Override
    public int insertServerSms( ServerSms serverSms ) {
        serverSms.setIsEffect( 0 );
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        serverSms.setIdentify( loginUser.getUsername() );
        serverSms.setUpdateTime( new Date() );
        return serverSmsMapper.insertServerSms( serverSms );
    }

    /**
     * 修改SMS短信服务配置
     *
     * @param serverSms SMS短信服务配置
     *
     * @return 结果
     */
    @Override
    public int updateServerSms( ServerSms serverSms ) {
        serverSms.setUpdateTime( new Date() );
        int i = serverSmsMapper.updateServerSms( serverSms );
        if ( i > 0 ) {
            ServerSms newServerSms = serverSmsMapper.selectServerSmsById( serverSms.getId() );
            if ( newServerSms.getIsEffect() == 1 ) {
                serverSmsCacheUtil.setServerSmsCache( newServerSms );
            }
        }
        return i;
    }

    /**
     * 批量删除SMS短信服务配置
     *
     * @param ids 需要删除的SMS短信服务配置ID
     *
     * @return 结果
     */
    @Override
    public int deleteServerSmsByIds( Long[] ids ) {
        int i = serverSmsMapper.deleteServerSmsByIds( ids );
        if ( i > 0 ) {
            for ( Long id : ids ) {
                serverSmsCacheUtil.clearCache( id );
            }
        }
        return i;
    }

    /**
     * 删除SMS短信服务配置信息
     *
     * @param id SMS短信服务配置ID
     *
     * @return 结果
     */
    @Override
    public int deleteServerSmsById( Long id ) {
        int i = serverSmsMapper.deleteServerSmsById( id );
        if ( i > 0 ) {
            serverSmsCacheUtil.clearCache( id );
        }
        return i;
    }

    @Override
    public int effect( long id ) {
        ServerSms update = new ServerSms();
        update.setId( id );
        update.setIsEffect( 1 );
        update.setUpdateTime( new Date() );
        int i = serverSmsMapper.updateServerSms( update );
        if ( i > 0 ) {
            ServerSms newServerSms = serverSmsMapper.selectServerSmsById( id );
            serverSmsCacheUtil.setServerSmsCache( newServerSms );
        }
        return i;
    }

    @Override
    public int noEffect( long id ) {
        ServerSms update = new ServerSms();
        update.setId( id );
        update.setIsEffect( 0 );
        update.setUpdateTime( new Date() );
        int i = serverSmsMapper.updateServerSms( update );
        if ( i > 0 ) {
            serverSmsCacheUtil.clearCache( id );
        }
        return i;
    }

    @Override
    public AjaxResult smsTest( long id, String mobile ) {
        String  regex = "^(1[3-9]\\d{9}$)";
        Pattern p     = Pattern.compile( regex );
        if ( mobile.length() != 11 || !p.matcher( mobile ).matches() ) {
            return AjaxResult.error( "手机号码不正确" );
        }
        try {
            String code = this.sendSms( mobile, id );
            if ( StringUtils.isBlank( code ) ) {
                return AjaxResult.error( "短信发送失败" );
            }
            return AjaxResult.success( "短信发送成功", code );
        } catch ( BaseException e ) {
            return AjaxResult.error( e.getMessage() );
        }
    }

    public String sendSms( String phone, long id ) {
        //SmsProvider smsProvider = smsLoadBalancerService.getProvider();
        ServerSms serverSms = this.selectServerSmsById( id );
        switch ( serverSms.getProvider() ) {
        case 0:
            return this.sendSmsTencent( serverSms, phone );
        case 1:
            return this.sendSmsAliyun( serverSms, phone );
        case 2:
            return this.sendSmsBaidu( serverSms, phone );
        case 3:
            return this.sendSmsHuawei( serverSms, phone );
        case 4:
            return this.sendSmsYunJi( serverSms, phone );
        case 5:
            return this.sendSmsBao( serverSms, phone );
        }
        return null;
    }

    private String sendSmsTencent( ServerSms serverSms, String phone ) {
        String code = createCode();
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
            String[] templateParams = { code };
            req.setTemplateParamSet( templateParams );
            com.tencentcloudapi.sms.v20190711.models.SendSmsResponse res = client.SendSms( req );
            if ( res.getSendStatusSet() != null && "Ok".equalsIgnoreCase( res.getSendStatusSet()[ 0 ].getCode() ) ) {
                return code;
            } else {
                log.warn( "短信发送失败:{}", JsonUtil.object2Json( res ) );
                throw new BusinessException( com.tencentcloudapi.sms.v20190711.models.SendSmsResponse.toJsonString( res ) );
            }
        } catch ( TencentCloudSDKException e ) {
            throw new BusinessException( e );
        }
    }

    private String sendSmsAliyun( ServerSms serverSms, String phone ) {
        String code = createCode();
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
        smsRequest.setTemplateParam( "{\"code\":" + code + "}" );

        try {
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse( smsRequest );
            if ( sendSmsResponse.getCode() != null && "OK".equals( sendSmsResponse.getCode() ) ) {
                return code;
            } else {
                // {"requestId":"01C0231F-AF35-4AE8-A92B-BC10ACDB90C6","bizId":null,"code":"isv.SMS_TEMPLATE_ILLEGAL",
                // "message":"模板不合法(不存在或被拉黑)"}
                log.warn( "阿里云短信发送失败:{}", JsonUtil.object2Json( sendSmsResponse ) );
                throw new BusinessException( JsonUtil.object2Json( sendSmsResponse ) );
            }
        } catch ( ClientException e ) {
            throw new BusinessException( e );
        }
    }

    private String sendSmsBaidu( ServerSms serverSms, String phone ) {
        String                 code   = createCode();
        SmsClientConfiguration config = new SmsClientConfiguration();
        config.setCredentials( new DefaultBceCredentials( serverSms.getAppKey(), serverSms.getAppAccess() ) );
        config.setEndpoint( serverSms.getRegion() );
        SmsClient client = new SmsClient( config );

        SendMessageV3Request request = new SendMessageV3Request();
        request.setMobile( phone );
        request.setSignatureId( serverSms.getSignature() );
        request.setTemplate( serverSms.getTemplate() );
        Map<String, String> contentVar = new HashMap<>();
        contentVar.put( "msg", code );
        //contentVar.put( "minute", "1" );
        request.setContentVar( contentVar );
        try {
            SendMessageV3Response sendSmsResponse = client.sendMessage( request );
            // 解析请求响应 response.isSuccess()为true 表示成功
            if ( sendSmsResponse != null && sendSmsResponse.isSuccess() ) {
                return code;
            } else {
                log.warn( "百度云短信发送失败:{}", JsonUtil.object2Json( sendSmsResponse ) );
                throw new BusinessException( JsonUtil.object2Json( sendSmsResponse ) );
            }
        } catch ( Exception e ) {
            throw new BusinessException( e );
        }
    }

    /**
     * 构造X-WSSE参数值 Construct X-WSSE parameter value
     *
     * @param appKey
     * @param appSecret
     */
    static String buildWsseHeader( String appKey, String appSecret ) {
        try {
            SimpleDateFormat sdf   = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'Z'" );
            String           time  = sdf.format( new Date() );
            String           nonce = UuidUtil.getRandomUuidWithoutSeparator();
            MessageDigest    md    = MessageDigest.getInstance( "SHA-256" );
            md.update( ( nonce + time + appSecret ).getBytes() );
            String passwordDigestBase64Str = Base64Utils.encodeToString( md.digest() );
            return String.format( WSSE_HEADER_FORMAT, appKey, passwordDigestBase64Str, nonce, time );
        } catch ( NoSuchAlgorithmException e ) {
            e.printStackTrace();
        }
        return null;
    }


    private String sendSmsHuawei( ServerSms serverSms, String phone ) {
        String code          = createCode();
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
            if ( responseEntity.getStatusCode().is2xxSuccessful() ) {
                log.warn( JsonUtil.object2Json( responseEntity.getBody() ) );
                return code;
            }
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        return null;
    }

    private String sendSmsYunJi( ServerSms serverSms, String phone ) {
        String              code = createCode();
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

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.postForObject( serverSms.getEndpoint(), httpEntity, Map.class );
            if ( !CollectionUtils.isEmpty( resultMap ) ) {
                Map<String, Object> businessData = ( Map<String, Object> ) resultMap.get( "BusinessData" );
                if ( !CollectionUtils.isEmpty( businessData ) ) {
                    String rspCode = businessData.getOrDefault( "code", "0" ).toString();
                    if ( "10000".equals( rspCode ) ) {
                        return code;
                    }
                }
            }
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        log.warn( "云极短信发送失败:{}", JsonUtil.object2Json( resultMap ) );
        return null;
    }

    private String sendSmsBao( ServerSms serverSms, String phone ) {

        try {
            String code = createCode();
            String template = serverSms.getTemplate().replace( "{code}", code );
            String httpArg = "u=" + serverSms.getAppKey() + "&" +
                    "p=" + serverSms.getAppAccess() + "&" +
                    "m=" + phone + "&" +
                    "c=" + template;

            String httpUrl = serverSms.getEndpoint() + "?" + httpArg;

            ResponseEntity<Object> responseEntity = restTemplate.getForEntity(
                    httpUrl, Object.class );
            Object returnCode = responseEntity.getBody();

            if ( ! Objects.isNull( returnCode ) && org.apache.commons.lang3.StringUtils.isNotBlank( returnCode.toString() ) && "0".equals( returnCode.toString() ) ) {
                log.info( "send code success {} " ,  code  );
                return template;
            }
            return  null;
        } catch (Exception e) {
            log.error( e.getMessage() );
        }
        return null;
    }

    private String createCode() {
        StringBuilder code = new StringBuilder();
        for ( int i = 1; i <= 6; i++ ) {
            code.append( RandomUtils.randomIntWithMax( 1, 9 ) );
        }
        return code.toString();
    }
}
