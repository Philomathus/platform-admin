package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.vo.TokenResult;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * 第三方接入缓存
 */
@Component
public class ThirdPMCacheManager {
	@Autowired
	private RedisUtil    redisUtil;
	@Autowired
	private RestTemplate restTemplate;

	/**
	 * 獲取OG视讯登录信息
	 *
	 * @return
	 */
	public TokenResult pullOgToken( String account, String gameUrl, String xoter, String xkey ) {
		/**取得进入存取凭证(每30分钟会失效)*/
		HttpHeaders headers = new HttpHeaders();
		headers.set( "X-Operator", xoter );
		headers.set( "x-key", xkey );
		String     url    = gameUrl + "/token";
		HttpMethod method = HttpMethod.GET;
		// 以表单的方式提交
		headers.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
		//将请求头部和参数合成一个请求
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>( headers );

		ResponseEntity<TokenResult> responseToken = restTemplate.exchange( url, method, requestEntity, TokenResult.class );
		TokenResult                 tokenResult   = responseToken.getBody();
		if ( !tokenResult.getStatus().equals( "success" ) ) {
			throw new BusinessException( "获取OG視訊安全凭证失败" );
		}
		redisUtil.strSet( Constants.PLATFORM_TOKEN + "og:" + account, tokenResult.getData().getToken(),
				Duration.ofSeconds( 1200 ) );
		return tokenResult;

	}

	/**
	 * 獲取OG视讯登录信息
	 *
	 * @return
	 */
	public String getOgToken( String account ) {
		return redisUtil.strGet( Constants.PLATFORM_TOKEN + "og:" + account );
	}
}
