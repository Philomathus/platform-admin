package com.qiqilm.server.admin.payagent;

import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.mapper.MemberWithdrawLogMapper;
import com.qiqilm.server.admin.mapper.PayAgentLogMapper;
import com.qiqilm.server.admin.mapper.PayAgentPlatformMapper;
import com.qiqilm.server.admin.service.IPayAgentService;
import com.qiqilm.server.admin.utils.AuthUtil;
import com.qiqilm.server.admin.utils.JsonUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Set;

@Log4j2
public abstract class AbstractPayAgent implements BasePayAgent {
	@Resource
	protected PayAgentPlatformMapper  payAgentPlatformMapper;
	@Resource
	protected MemberWithdrawLogMapper withdrawLogMapper;
	@Resource
	protected PayAgentLogMapper       payAgentLogMapper;
	@Resource
	protected RestTemplate            restTemplate;
	@Resource
	protected IPayAgentService        payAgentService;
	@Resource
	protected SysConfigCacheUtil      sysConfigCacheUtil;

	public static final String SECRET_PAYAGENT_KEY;

	static {
		try {
			SECRET_PAYAGENT_KEY = AuthUtil.getSecurityKeyStr( "secretkey/payAgentPrivateKey" );
		} catch ( IOException e ) {
			throw new RuntimeException( e );
		}
	}

	protected String assemblyUrl( Map<String, ?> bodyMap ) {
		StringBuilder sb = new StringBuilder();
		bodyMap.forEach( ( k, v ) -> sb.append( k ).append( "=" ).append( v ).append( "&" ) );
		return sb.substring( 0, sb.length() - 1 );
	}

	protected String assemblyUrl2( Map<String, ?> bodyMap ) {
		StringBuilder sb = new StringBuilder();
		bodyMap.forEach( ( k, v ) -> sb.append( v ) );
		return sb.toString();
	}

	protected String assemblyUrl3( Map<String, ?> bodyMap ) {
		StringBuilder sb = new StringBuilder();
		bodyMap.forEach( ( k, v ) -> sb.append( k ).append( "&" ).append( v ) );
		return sb.toString();
	}

	protected boolean checkWhiteIp( String platWhiteIpList, String realIp ) {
		if ( StringUtils.hasText( platWhiteIpList ) ) {
			Set<String> whiteIpSet = Sets.newHashSet( platWhiteIpList.split( "," ) );
			return !whiteIpSet.contains( realIp ) && !"0:0:0:0:0:0:0:1".equals( realIp );
		}
		return false;
	}

	@NotNull
	protected static HttpEntity<MultiValueMap<String, Object>> packageForm( Map<String, Object> params ) {
		MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
		requestMap.setAll( params );

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
		return new HttpEntity<>( requestMap, httpHeaders );
	}

	@NotNull
	protected static HttpEntity<Map<String, Object>> packageJson( Map<String, Object> params ) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_JSON );
		return new HttpEntity<>( params, httpHeaders );
	}

	protected Map<String, Object> sendPostMap( String url, HttpEntity<?> httpEntity, ReqPayAgent reqPayAgent ) {
		Map<String, Object> resultMap = null;
		try {
			resultMap = restTemplate.execute( url, HttpMethod.POST, restTemplate.httpEntityCallback( httpEntity ), response -> {
				InputStream bodyStream = response.getBody();
				String      text;
				try ( Reader reader = new InputStreamReader( bodyStream ) ) {
					text = CharStreams.toString( reader );
				}
				return JsonUtil.json2Map( text );
			} );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
			if ( reqPayAgent != null ) {
				reqPayAgent.setFailReason( e.getMessage() );
			}
		}
		return resultMap;
	}

	protected String sendPostString( String url, HttpEntity<?> httpEntity, ReqPayAgent reqPayAgent ) {
		String resultStr = null;
		try {
			resultStr = restTemplate.execute( url, HttpMethod.POST, restTemplate.httpEntityCallback( httpEntity ), response -> {
				InputStream bodyStream = response.getBody();
				String      text;
				try ( Reader reader = new InputStreamReader( bodyStream ) ) {
					text = CharStreams.toString( reader );
				}
				return text;
			} );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
			if ( reqPayAgent != null ) {
				reqPayAgent.setFailReason( e.getMessage() );
			}
		}
		return resultStr;
	}

	protected String sendGetString( String url, ReqPayAgent reqPayAgent ) {
		String resultStr = null;
		try {
			resultStr = restTemplate.execute( url, HttpMethod.GET, restTemplate.httpEntityCallback( null ), response -> {
				InputStream bodyStream = response.getBody();
				String      text;
				try ( Reader reader = new InputStreamReader( bodyStream ) ) {
					text = CharStreams.toString( reader );
				}
				return text;
			} );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
			if ( reqPayAgent != null ) {
				reqPayAgent.setFailReason( e.getMessage() );
			}
		}
		return resultStr;
	}

	protected Map<String, Object> sendGetMap( String url, ReqPayAgent reqPayAgent ) {
		Map<String, Object> resultMap = null;
		try {
			resultMap = restTemplate.execute( url, HttpMethod.GET, restTemplate.httpEntityCallback( null ), response -> {
				InputStream bodyStream = response.getBody();
				String      text;
				try ( Reader reader = new InputStreamReader( bodyStream ) ) {
					text = CharStreams.toString( reader );
				}
				return JsonUtil.json2Map( text );
			} );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
			if ( reqPayAgent != null ) {
				reqPayAgent.setFailReason( e.getMessage() );
			}
		}
		return resultMap;
	}
}
