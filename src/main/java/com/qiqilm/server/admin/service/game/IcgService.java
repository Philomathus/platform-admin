package com.qiqilm.server.admin.service.game;

import com.qiqilm.server.admin.cache.GameCacheManager;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.vo.XiaFenResult;
import com.qiqilm.server.admin.utils.JsonUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
public class IcgService {
	private static final String LOGIN      = "/login";
	private static final String COMMON_URL = "/api/v1/players";
	private static final String TO_ICG     = "/deposit";
	private static final String TO_QIQI    = "/withdraw";
	private static final String GAME       = "/api/v1/games";
	private static final String ICG        = "icg";

	@Resource
	private RestTemplate     restTemplate;
	@Resource
	private GameCacheManager gameCacheManager;

	private String getToken() {
		return gameCacheManager.getICGToken();
	}

	public boolean transfer( GamePlatform gamePlatform, String token, String userId,
							 String orderId, BigDecimal changeMoney, String type ) {
		Map<String, Object> map = new HashMap<>();
		map.put( "transactionId", orderId );
		map.put( "amount", changeMoney.multiply( new BigDecimal( 100 ) ) );
		map.put( "player", userId );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_JSON );
		httpHeaders.add( "Authorization", "Bearer " + token );
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>( map, httpHeaders );
		Map<String, Object>             resultMap  = null;
		try {
			resultMap = restTemplate.postForObject( gamePlatform.getApiUrl() + COMMON_URL + type, httpEntity, Map.class );
		} catch ( Exception e ) {
			log.error( "ICG 充值失败->{}", e.getMessage() );
			return false;
		}
		resultMap = ( Map<String, Object> ) resultMap.get( "data" );
		return org.apache.commons.lang3.StringUtils.equals( userId, String.valueOf( resultMap.get( "username" ) ) );
	}

	public XiaFenResult transfer( GamePlatform gamePlatform, String userId, String orderId ) {
		String       token  = getToken();
		XiaFenResult result = new XiaFenResult();
		result.setOk( true );
		BigDecimal balance = queryCoin( gamePlatform, userId );
		if ( balance.compareTo( BigDecimal.ZERO ) > 0 ) {
			result.setBackMoney( balance );
			if ( transfer( gamePlatform, token, userId, orderId, balance, TO_QIQI ) ) {
				return result;
			}
			result.setOk( false );
			return result;
		}
		result.setBackMoney( BigDecimal.ZERO );
		return result;
	}

	public BigDecimal queryCoin( GamePlatform gamePlatform, String userId ) {
		String token = getToken();
		if ( StringUtils.isEmpty( token ) ) {
			return BigDecimal.ZERO;
		}
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add( "Authorization", "Bearer " + token );
		HttpEntity          request  = new HttpEntity<>( httpHeaders );
		ResponseEntity<Map> response = null;
		try {
			response = restTemplate.exchange( gamePlatform.getApiUrl() + COMMON_URL + "?player=" + userId,
					HttpMethod.GET, request, Map.class );
		} catch ( Exception e ) {
			log.error( "ICG 获取{}余额 失败->{}", userId, e.getMessage() );
			return BigDecimal.ZERO;
		}
		if ( response.getStatusCode() == HttpStatus.OK ) {
			Map                            returnMap = response.getBody();
			ArrayList<Map<String, Object>> list      = ( ArrayList ) returnMap.get( "data" );
			if ( list != null && list.size() > 0 ) {
				returnMap = list.get( 0 );
				BigDecimal coin = new BigDecimal( returnMap.getOrDefault( "balance", "0" ).toString() );
				return coin.divide( new BigDecimal( 100 ), 2, BigDecimal.ROUND_HALF_UP );
			}
		}
		log.warn( "ICG 查询余额 失败->{}", JsonUtil.object2Json( response ) );
		return BigDecimal.ZERO;
	}
}
