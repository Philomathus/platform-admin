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
import com.qiqilm.server.admin.mapper.ServerSmsMapper;
import com.qiqilm.server.admin.service.IServerSmsService;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * SMS短信服务配置Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Log4j2
@Service
public class ServerSmsServiceImpl implements IServerSmsService {
	@Autowired
	private ServerSmsMapper    serverSmsMapper;
	@Autowired
	private ServerSmsCacheUtil serverSmsCacheUtil;
	@Autowired
	private TokenService       tokenService;

	/**
	 * 查询SMS短信服务配置
	 *
	 * @param id SMS短信服务配置ID
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
	 * @return 结果
	 */
	@Override
	public int insertServerSms( ServerSms serverSms ) {
		serverSms.setIsEffect( 0 );
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		serverSms.setIdentify( loginUser.getUsername() );
		serverSms.setUpdateTime(new Date());
		return serverSmsMapper.insertServerSms( serverSms );
	}

	/**
	 * 修改SMS短信服务配置
	 *
	 * @param serverSms SMS短信服务配置
	 * @return 结果
	 */
	@Override
	public int updateServerSms( ServerSms serverSms ) {
		serverSms.setUpdateTime(new Date());
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
		update.setUpdateTime(new Date());
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
		update.setUpdateTime(new Date());
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
			return AjaxResult.success( "短信发送成功", code );
		} catch ( BaseException e ) {
			return AjaxResult.error( e.getMessage() );
		}
	}

	public String sendSms( String phone, long id ) {
		ServerSms serverSms = this.selectServerSmsById( id );
		switch ( serverSms.getProvider() ) {
		case 0:
			return this.sendSmsTencent( serverSms, phone );
		case 1:
			return this.sendSmsAliyun( serverSms, phone );
		case 2:
			return this.sendSmsBaidu( serverSms, phone );
		}
		return null;
	}

	private String sendSmsTencent( ServerSms serverSms, String phone ) {
		final String code = String.valueOf( ( int ) ( Math.random() * 9999 ) );
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
				throw new BaseException( com.tencentcloudapi.sms.v20190711.models.SendSmsResponse.toJsonString( res ) );
			}
		} catch ( TencentCloudSDKException e ) {
			throw new BaseException( e );
		}
	}

	private String sendSmsAliyun( ServerSms serverSms, String phone ) {

		final String code = String.valueOf( ( int ) ( Math.random() * 9999 ) );

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
		smsRequest.setTemplateParam( "{\"code\":" + code + "}" );

		try {
			SendSmsResponse sendSmsResponse = acsClient.getAcsResponse( smsRequest );
			if ( sendSmsResponse.getCode() != null && "OK".equals( sendSmsResponse.getCode() ) ) {
				return code;
			} else {
				// {"requestId":"01C0231F-AF35-4AE8-A92B-BC10ACDB90C6","bizId":null,"code":"isv.SMS_TEMPLATE_ILLEGAL",
				// "message":"模板不合法(不存在或被拉黑)"}
				log.warn( "阿里云短信发送失败:{}", JsonUtil.object2Json( sendSmsResponse ) );
				throw new BaseException( JsonUtil.object2Json( sendSmsResponse ) );
			}
		} catch ( ClientException e ) {
			throw new BaseException( e );
		}
	}

	private String sendSmsBaidu( ServerSms serverSms, String phone ) {
		final String           code   = String.valueOf( ( int ) ( Math.random() * 9999 ) );
		SmsClientConfiguration config = new SmsClientConfiguration();
		config.setCredentials( new DefaultBceCredentials( serverSms.getAppKey(), serverSms.getAppAccess() ) );
		config.setEndpoint( serverSms.getRegion() );
		SmsClient client = new SmsClient( config );

		SendMessageV3Request request = new SendMessageV3Request();
		request.setMobile( phone );
		request.setSignatureId( serverSms.getSignature() );
		request.setTemplate( serverSms.getTemplate() );
		Map<String, String> contentVar = new HashMap<>();
		contentVar.put( "code", code );
		contentVar.put( "minute", "1" );
		request.setContentVar( contentVar );
		try {
			SendMessageV3Response sendSmsResponse = client.sendMessage( request );
			// 解析请求响应 response.isSuccess()为true 表示成功
			if ( sendSmsResponse != null && sendSmsResponse.isSuccess() ) {
				return code;
			} else {
				log.warn( "百度云短信发送失败:{}", JsonUtil.object2Json( sendSmsResponse ) );
				throw new BaseException( JsonUtil.object2Json( sendSmsResponse ) );
			}
		} catch ( Exception e ) {
			throw new BaseException( e );
		}
	}
}
