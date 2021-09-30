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
import com.qiqilm.server.admin.cache.ServerSmsCacheUtil;
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
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class SmsApi {
	@Autowired
	private ServerSmsCacheUtil serverSmsCacheUtil;
	@Autowired
	private ISmsFailLogService smsFailLogService;
    @Autowired
    private ServerSmsMapper serverSmsMapper;

	private static String createPhoneCode() {
		StringBuilder code = new StringBuilder();
		for ( int i = 1; i <= 4; i++ ) {
			code.append( RandomUtils.randomIntWithMax( 0, 9 ) );
		}
		return code.toString();
	}

	public String sendSms( String phone, int index ) {
		long countCache = serverSmsCacheUtil.countCache();
		if ( index > ( countCache - 1 ) ) {
			index = 0;
		}
		ServerSms serverSms = serverSmsCacheUtil.getServerSmsCache( index );
		String    code = createPhoneCode() + index;
		switch ( serverSms.getProvider() ) {
		case 0:
			code = this.sendSmsTencent( serverSms, phone, code );
			break;
		case 1:
			code = this.sendSmsAliyun( serverSms, phone, code );
			break;
		case 2:
			code = this.sendSmsBaidu( serverSms, phone,code );
			break;
		default:
			throw new BusinessException( "不支持的短信运营商类型" );
		}
		return code;
	}

	public String sendMemSms( String phone, String msg ) {
        if (StringUtils.isEmpty(phone)) {
            throw new BusinessException( "手机号不能为空" );
        }
        if (StringUtils.isEmpty(msg)) {
            throw new BusinessException( "发送信息不能为空" );
        }
        ServerSms serverSms1 = new ServerSms();
        serverSms1.setName("会员通知");
        List<ServerSms> serverSmsList = serverSmsMapper.selectServerSmsList(serverSms1);
        if (serverSmsList.isEmpty()) {
            throw new BusinessException( "会员sms通道不存在,无法发送" );
        }else {
            ServerSms serverSms = serverSmsList.get(0);
            switch ( serverSms.getProvider() ) {
                case 0:
                    msg = this.sendSmsTencent( serverSms, phone, msg );
                    break;
                case 1:
                    msg = this.sendSmsAliyun( serverSms, phone, msg );
                    break;
                case 2:
                    msg = this.sendSmsBaidu( serverSms, phone,msg );
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
		final String regionId = serverSms.getRegion();
		IClientProfile profile = DefaultProfile.getProfile( regionId, serverSms.getAppKey(),
				serverSms.getAppAccess() );
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
