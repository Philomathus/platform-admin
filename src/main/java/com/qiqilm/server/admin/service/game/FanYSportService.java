package com.qiqilm.server.admin.service.game;

import com.qiqilm.server.admin.cache.GameCacheManager;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.vo.XiaFenResult;
import com.qiqilm.server.admin.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class FanYSportService {
	private final String REGISTER     = "/api/user/register";
	private final String FANY_SPORT   = "fanyaSport";
	private final String LOGIN        = "/api/user/login";
	private final String TRANSFER     = "/api/user/transfer";
	private final String TRANSFER_IN  = "IN";
	private final String TRANSFER_OUT = "OUT";
	private final String BALANCE      = "/api/user/balance";

	@Autowired
	private RestTemplate     restTemplate;
	@Autowired
	private GameCacheManager gameCacheManager;

	private boolean register( GamePlatform gamePlatform, String userId ) {
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.set( "UserName", userId );
		multiValueMap.set( "Password", userId );
		multiValueMap.set( "Currency", "CNY" );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add( "Authorization", gamePlatform.getDes() );
		httpHeaders.add( "Content-Language", "CHN" );
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>( multiValueMap, httpHeaders );
		Map<String, Object>                       resultMap;
		try {
			resultMap = restTemplate.postForObject( gamePlatform.getApiUrl() + REGISTER, httpEntity, Map.class );
		} catch ( Exception e ) {
			log.error( "FanY register ->{} {}", e.getMessage(), userId );
			return false;
		}
		if ( StringUtils.equals( "1", String.valueOf( resultMap.get( "success" ) ) ) ||
				StringUtils.equals( "用户名已经存在", String.valueOf( resultMap.get( "msg" ) ) ) ) {
			gameCacheManager.add( userId, FANY_SPORT );
			return true;
		}
		log.error( "FanY register ->{} {}", userId, JsonUtil.object2Json( resultMap ) );
		return false;
	}

	public boolean transfer( GamePlatform gamePlatform, String userId, BigDecimal changeMoney, String orderId, String type ) {
		MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.set( "UserName", userId );
		multiValueMap.set( "Type", type );
		multiValueMap.set( "Money", changeMoney );
		multiValueMap.set( "ID", orderId );
		multiValueMap.set( "Currency", "CNY" );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add( "Authorization", gamePlatform.getDes() );
		httpHeaders.add( "Content-Language", "CHN" );
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>( multiValueMap, httpHeaders );
		Map<String, Object>                       resultMap;
		try {
			resultMap = restTemplate.postForObject( gamePlatform.getApiUrl() + TRANSFER, httpEntity, Map.class );
		} catch ( Exception e ) {
			log.error( "FanY transfer ->{} {}", e.getMessage(), userId );
			return false;
		}
		if ( StringUtils.equals( "1", String.valueOf( resultMap.get( "success" ) ) ) )
			return true;
		log.error( "FanY transfer ->{} {}", userId, JsonUtil.object2Json( resultMap ) );
		return false;
	}

	public XiaFenResult transfer( GamePlatform gamePlatform, String userId, String orderId, Date date ) {
		XiaFenResult result = new XiaFenResult();
		result.setOk( true );
		BigDecimal balance = queryCoin( gamePlatform, userId, date );
		if ( balance.compareTo( BigDecimal.ZERO ) > 0 ) {
			result.setBackMoney( balance );
			if ( transfer( gamePlatform, userId, balance, orderId, TRANSFER_OUT ) ) {
				return result;
			}
			result.setOk( false );
			return result;
		}
		result.setBackMoney( BigDecimal.ZERO );
		return result;
	}

	public BigDecimal queryCoin( GamePlatform gamePlatform, String userId, Date date ) {
		MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.set( "UserName", userId );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add( "Authorization", gamePlatform.getDes() );
		httpHeaders.add( "Content-Language", "CHN" );
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>( multiValueMap, httpHeaders );
		Map<String, Object>                       resultMap;
		try {
			resultMap = restTemplate.postForObject( gamePlatform.getApiUrl() + BALANCE, httpEntity, Map.class );
		} catch ( Exception e ) {
			log.error( "FanY queryCoin ->{} {}", e.getMessage(), userId );
			return BigDecimal.ZERO;
		}
		if ( StringUtils.equals( "1", String.valueOf( resultMap.get( "success" ) ) ) ) {
			resultMap = ( Map<String, Object> ) resultMap.get( "info" );
			return new BigDecimal( String.valueOf( resultMap.get( "Money" ) ) );
		}
		log.error( "FanY queryCoin ->{} {}", userId, JsonUtil.object2Json( resultMap ) );
		return BigDecimal.ZERO;
	}
}
