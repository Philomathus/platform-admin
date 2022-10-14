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
import com.qiqilm.server.admin.utils.RandomUtils;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
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
public class ServerSmsServiceImpl implements IServerSmsService {
    @Autowired
    private ServerSmsMapper serverSmsMapper;
    @Autowired
    private ServerSmsCacheUtil serverSmsCacheUtil;
    @Autowired
    private TokenService tokenService;

	//无需修改,用于格式化鉴权头域,给"X-WSSE"参数赋值
	private static final String WSSE_HEADER_FORMAT = "UsernameToken Username=\"%s\",PasswordDigest=\"%s\",Nonce=\"%s\",Created=\"%s\"";
	//无需修改,用于格式化鉴权头域,给"Authorization"参数赋值
	private static final String AUTH_HEADER_VALUE = "WSSE realm=\"SDP\",profile=\"UsernameToken\",type=\"Appkey\"";

    /**
     * 查询SMS短信服务配置
     *
     * @param id SMS短信服务配置ID
     * @return SMS短信服务配置
     */
    @Override
    public ServerSms selectServerSmsById(Long id) {
        return serverSmsMapper.selectServerSmsById(id);
    }

    /**
     * 查询SMS短信服务配置列表
     *
     * @param serverSms SMS短信服务配置
     * @return SMS短信服务配置
     */
    @Override
    public List<ServerSms> selectServerSmsList(ServerSms serverSms) {
        return serverSmsMapper.selectServerSmsList(serverSms);
    }

    /**
     * 新增SMS短信服务配置
     *
     * @param serverSms SMS短信服务配置
     * @return 结果
     */
    @Override
    public int insertServerSms(ServerSms serverSms) {
        serverSms.setIsEffect(0);
        LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
        serverSms.setIdentify(loginUser.getUsername());
        serverSms.setUpdateTime(new Date());
        return serverSmsMapper.insertServerSms(serverSms);
    }

    /**
     * 修改SMS短信服务配置
     *
     * @param serverSms SMS短信服务配置
     * @return 结果
     */
    @Override
    public int updateServerSms(ServerSms serverSms) {
        serverSms.setUpdateTime(new Date());
        int i = serverSmsMapper.updateServerSms(serverSms);
        if (i > 0) {
            ServerSms newServerSms = serverSmsMapper.selectServerSmsById(serverSms.getId());
            if (newServerSms.getIsEffect() == 1) {
                serverSmsCacheUtil.setServerSmsCache(newServerSms);
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
    public int deleteServerSmsByIds(Long[] ids) {
        int i = serverSmsMapper.deleteServerSmsByIds(ids);
        if (i > 0) {
            for (Long id : ids) {
                serverSmsCacheUtil.clearCache(id);
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
    public int deleteServerSmsById(Long id) {
        int i = serverSmsMapper.deleteServerSmsById(id);
        if (i > 0) {
            serverSmsCacheUtil.clearCache(id);
        }
        return i;
    }

    @Override
    public int effect(long id) {
        ServerSms update = new ServerSms();
        update.setId(id);
        update.setIsEffect(1);
        update.setUpdateTime(new Date());
        int i = serverSmsMapper.updateServerSms(update);
        if (i > 0) {
            ServerSms newServerSms = serverSmsMapper.selectServerSmsById(id);
            serverSmsCacheUtil.setServerSmsCache(newServerSms);
        }
        return i;
    }

    @Override
    public int noEffect(long id) {
        ServerSms update = new ServerSms();
        update.setId(id);
        update.setIsEffect(0);
        update.setUpdateTime(new Date());
        int i = serverSmsMapper.updateServerSms(update);
        if (i > 0) {
            serverSmsCacheUtil.clearCache(id);
        }
        return i;
    }

    @Override
    public AjaxResult smsTest(long id, String mobile) {
        String regex = "^(1[3-9]\\d{9}$)";
        Pattern p = Pattern.compile(regex);
        if (mobile.length() != 11 || !p.matcher(mobile).matches()) {
            return AjaxResult.error("手机号码不正确");
        }
        try {
            String code = this.sendSms(mobile, id);
            return AjaxResult.success("短信发送成功", code);
        } catch (BaseException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    public String sendSms(String phone, long id) {
        ServerSms serverSms = this.selectServerSmsById(id);
        switch (serverSms.getProvider()) {
            case 0:
                return this.sendSmsTencent(serverSms, phone);
            case 1:
                return this.sendSmsAliyun(serverSms, phone);
            case 2:
                return this.sendSmsBaidu(serverSms, phone);
            case 3:
                return this.sendSmsHuawei(serverSms, phone);
        }
        return null;
    }

    private String sendSmsTencent(ServerSms serverSms, String phone) {
        String code = createCode();
        try {
            Credential cred = new Credential(serverSms.getAppKey(), serverSms.getAppAccess());
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setReqMethod("POST");
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            com.tencentcloudapi.sms.v20190711.SmsClient client = new com.tencentcloudapi.sms.v20190711.SmsClient(cred,
                    serverSms.getRegion(), clientProfile);
            com.tencentcloudapi.sms.v20190711.models.SendSmsRequest req =
                    new com.tencentcloudapi.sms.v20190711.models.SendSmsRequest();
            req.setSmsSdkAppid(serverSms.getSmsSdkAppid());
            req.setSign(serverSms.getSignature());
            req.setTemplateID(serverSms.getTemplate());
            /* 下发手机号码，采用 e.164 标准，+[国家或地区码][手机号]
             * 例如+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号*/
            String[] phoneNumbers = {"+86" + phone};
            req.setPhoneNumberSet(phoneNumbers);
            String[] templateParams = {code};
            req.setTemplateParamSet(templateParams);
            com.tencentcloudapi.sms.v20190711.models.SendSmsResponse res = client.SendSms(req);
            if (res.getSendStatusSet() != null && "Ok".equalsIgnoreCase(res.getSendStatusSet()[0].getCode())) {
                return code;
            } else {
                log.warn("短信发送失败:{}", JsonUtil.object2Json(res));
                throw new BaseException(com.tencentcloudapi.sms.v20190711.models.SendSmsResponse.toJsonString(res));
            }
        } catch (TencentCloudSDKException e) {
            throw new BaseException(e);
        }
    }

    private String sendSmsAliyun(ServerSms serverSms, String phone) {
        String code = createCode();
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        final String regionId = serverSms.getRegion();
        IClientProfile profile = DefaultProfile.getProfile(regionId, serverSms.getAppKey(),
                serverSms.getAppAccess());
        DefaultProfile.addEndpoint(regionId, "Dysmsapi", "dysmsapi.aliyuncs.com");
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象
        SendSmsRequest smsRequest = new SendSmsRequest();
        smsRequest.setSysMethod(MethodType.POST);
        smsRequest.setPhoneNumbers(phone);
        smsRequest.setSignName(serverSms.getSignature());
        smsRequest.setTemplateCode(serverSms.getTemplate());
        smsRequest.setTemplateParam("{\"code\":" + code + "}");

        try {
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(smsRequest);
            if (sendSmsResponse.getCode() != null && "OK".equals(sendSmsResponse.getCode())) {
                return code;
            } else {
                // {"requestId":"01C0231F-AF35-4AE8-A92B-BC10ACDB90C6","bizId":null,"code":"isv.SMS_TEMPLATE_ILLEGAL",
                // "message":"模板不合法(不存在或被拉黑)"}
                log.warn("阿里云短信发送失败:{}", JsonUtil.object2Json(sendSmsResponse));
                throw new BaseException(JsonUtil.object2Json(sendSmsResponse));
            }
        } catch (ClientException e) {
            throw new BaseException(e);
        }
    }

    private String sendSmsBaidu(ServerSms serverSms, String phone) {
        String code = createCode();
        SmsClientConfiguration config = new SmsClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(serverSms.getAppKey(), serverSms.getAppAccess()));
        config.setEndpoint(serverSms.getRegion());
        SmsClient client = new SmsClient(config);

        SendMessageV3Request request = new SendMessageV3Request();
        request.setMobile(phone);
        request.setSignatureId(serverSms.getSignature());
        request.setTemplate(serverSms.getTemplate());
        Map<String, String> contentVar = new HashMap<>();
        contentVar.put("msg", code);
        //contentVar.put( "minute", "1" );
        request.setContentVar(contentVar);
        try {
            SendMessageV3Response sendSmsResponse = client.sendMessage(request);
            // 解析请求响应 response.isSuccess()为true 表示成功
            if (sendSmsResponse != null && sendSmsResponse.isSuccess()) {
                return code;
            } else {
                log.warn("百度云短信发送失败:{}", JsonUtil.object2Json(sendSmsResponse));
                throw new BaseException(JsonUtil.object2Json(sendSmsResponse));
            }
        } catch (Exception e) {
            throw new BaseException(e);
        }
    }

	/**
	 * 构造请求Body体 Construct the request body
	 * @param sender
	 * @param receiver
	 * @param templateId
	 * @param templateParas
	 * @param statusCallBack
	 * @param signature | 签名名称,使用国内短信通用模板时填写 Signature name, fill in when using the general template for domestic SMS
	 * @return
	 */
	 private static String buildRequestBody(String sender, String receiver, String templateId, String templateParas,
								   String statusCallBack, String signature) {
		if (null == sender || null == receiver || null == templateId || sender.isEmpty() || receiver.isEmpty()
				|| templateId.isEmpty()) {
			return null;
		}
		Map<String, String> map = new HashMap<String, String>();

		map.put("from", sender);
		map.put("to", receiver);
		map.put("templateId", templateId);
		if (null != templateParas && !templateParas.isEmpty()) {
			map.put("templateParas", templateParas);
		}
		if (null != statusCallBack && !statusCallBack.isEmpty()) {
			map.put("statusCallback", statusCallBack);
		}
		if (null != signature && !signature.isEmpty()) {
			map.put("signature", signature);
		}

		StringBuilder sb = new StringBuilder();
		String temp = "";

		for (String s : map.keySet()) {
			try {
				temp = URLEncoder.encode(map.get(s), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sb.append(s).append("=").append(temp).append("&");
		}

		return sb.deleteCharAt(sb.length()-1).toString();
	}

	/**
	 * 构造X-WSSE参数值 Construct X-WSSE parameter value
	 * @param appKey
	 * @param appSecret
	 * @return
	 */
	static String buildWsseHeader(String appKey, String appSecret) {
		if (null == appKey || null == appSecret || appKey.isEmpty() || appSecret.isEmpty()) {
			System.out.println("buildWsseHeader(): appKey or appSecret is null.");
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String time = sdf.format(new Date()); //Created
		String nonce = UUID.randomUUID().toString().replace("-", ""); //Nonce

		MessageDigest md;
		byte[] passwordDigest = null;

		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update((nonce + time + appSecret).getBytes());
			passwordDigest = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		//如果JDK版本是1.8,请加载原生Base64类,并使用如下代码
		String passwordDigestBase64Str = Base64.getEncoder().encodeToString(passwordDigest); //PasswordDigest
		//如果JDK版本低于1.8,请加载三方库提供Base64类,并使用如下代码
		//String passwordDigestBase64Str = Base64.encodeBase64String(passwordDigest); //PasswordDigest
		//若passwordDigestBase64Str中包含换行符,请执行如下代码进行修正
		//passwordDigestBase64Str = passwordDigestBase64Str.replaceAll("[\\s*\t\n\r]", "");
		return String.format(WSSE_HEADER_FORMAT, appKey, passwordDigestBase64Str, nonce, time);
	}

	/*** @throws Exception
	 */
	static void trustAllHttpsCertificates() throws Exception {
		TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {
					public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					}
					public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					}
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
				}
			};
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}


	private String sendSmsHuawei(ServerSms serverSms,String phone){
		String code = createCode();
		String receiver = "+86" + phone;
		String templateParas = "[\""+code+"\"]";

		//请求Body,不携带签名名称时,signature请填null - When requesting Body, if the signature name is not included, please fill in null for signature
		String body = buildRequestBody(serverSms.getSignature(), receiver, serverSms.getTemplate(), templateParas, "", serverSms.getRegion());

		if (null == body || body.isEmpty()) {
			log.error("body is null.");
			return null;
		}

		//请求Headers中的X-WSSE参数值
		String wsseHeader = buildWsseHeader(serverSms.getAppKey(), serverSms.getAppAccess());
		if (null == wsseHeader || wsseHeader.isEmpty()) {
			log.error("wsse header is null.");
			return null;
		}

		Writer out = null;
		BufferedReader in = null;
		StringBuffer result = new StringBuffer();
		HttpsURLConnection connection = null;
		InputStream is = null;

		try {
            trustAllHttpsCertificates();

			URL realUrl = new URL(serverSms.getEndpoint());
			connection = (HttpsURLConnection) realUrl.openConnection();

			connection.setHostnameVerifier((hostname, session) -> true);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(true);
			//请求方法
			connection.setRequestMethod("POST");
			//请求Headers参数
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Authorization", AUTH_HEADER_VALUE);
			connection.setRequestProperty("X-WSSE", wsseHeader);

			connection.connect();
			out = new OutputStreamWriter(connection.getOutputStream());
			out.write(body);
			out.flush();
			out.close();

			int status = connection.getResponseCode();
			if (200 == status) { //200
				is = connection.getInputStream();
			} else { //400/401
				is = connection.getErrorStream();
			}
			in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = "";
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			System.out.println(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != out) {
					out.close();
				}
				if (null != is) {
					is.close();
				}
				if (null != in) {
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}




		return null;
	}

    private String createCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 1; i <= 6; i++) {
            code.append(RandomUtils.randomIntWithMax(1, 9));
        }
        return code.toString();
    }
}
