package com.qiqilm.server.admin.service.game;


import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.vo.XiaFenResult;
import com.qiqilm.server.admin.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AFBService {
	private final String DEPOSIT  = "deposit";
	private final String WITHDRAW = "withdraw";

	@Autowired
	private RestTemplate restTemplate;

	public boolean transfer( GamePlatform gamePlatform,
							 String userId, BigDecimal changeMoney, String orderId,
							 Date date, String type ) {
		Map<String, Object> map = new HashMap<>();
		map.put( "jsonrpc", "2.0" );
		map.put( "method", type );
		map.put( "id", date.getTime() );
		Map<String, Object> params = new HashMap<>();
		params.put( "token", gamePlatform.getDes() );
		params.put( "amount", changeMoney.multiply( new BigDecimal( 100 ) ) );
		params.put( "userId", userId );
		params.put( "desc", orderId );
		map.put( "params", params );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>( map, httpHeaders );
		Map<String, Object>             resultMap  = null;
		try {
			resultMap = restTemplate.postForObject( gamePlatform.getApiUrl(), httpEntity, Map.class );
		} catch ( Exception e ) {
			log.error( "AFB transfer 失败 ->{}", e.getMessage() );
			return false;
		}
		resultMap = ( Map<String, Object> ) resultMap.get( "error" );
		if ( !CollectionUtils.isEmpty( resultMap ) ) {
			log.error( "AFB {} transfer 失败 ->{}", userId, JsonUtil.object2Json( resultMap ) );
			return false;
		}
		return true;
	}

	public XiaFenResult transfer( GamePlatform gamePlatform, String userId, String orderId, Date date ) {
		XiaFenResult result = new XiaFenResult();
		result.setOk( true );
		BigDecimal balance = queryCoin( gamePlatform, userId, date );
		if ( balance.compareTo( BigDecimal.ZERO ) > 0 ) {
			result.setBackMoney( balance );
			if ( transfer( gamePlatform, userId, balance, orderId, date, WITHDRAW ) ) {
                return result;
            }
			result.setOk( false );
			return result;
		}
		result.setBackMoney( BigDecimal.ZERO );
		return result;
	}

	public BigDecimal queryCoin( GamePlatform gamePlatform, String userId, Date date ) {
		Map<String, Object> map = new HashMap<>();
		map.put( "jsonrpc", "2.0" );
		map.put( "method", "getBalance" );
		map.put( "id", date.getTime() );
		Map<String, Object> params = new HashMap<>();
		params.put( "token", gamePlatform.getDes() );
		params.put( "userId", userId );
		map.put( "params", params );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>( map, httpHeaders );
		Map<String, Object>             resultMap  = null;
		try {
			resultMap = restTemplate.postForObject( gamePlatform.getApiUrl(), httpEntity, Map.class );
		} catch ( Exception e ) {
			log.error( "AFB {} queryCoin 失败 ->{}", userId, e.getMessage() );
			return BigDecimal.ZERO;
		}
		Map<String, Object> result = ( Map<String, Object> ) resultMap.get( "result" );
		if ( !CollectionUtils.isEmpty( result ) ) {
			return new BigDecimal( String.valueOf( result.get( "balance" ) ) ).
					divide( new BigDecimal( 100 ) ).setScale( 2, BigDecimal.ROUND_FLOOR );
		}
		log.error( "AFB {} queryCoin 失败 ->{}", userId, JsonUtil.object2Json( resultMap ) );
		return BigDecimal.ZERO;
	}
}
