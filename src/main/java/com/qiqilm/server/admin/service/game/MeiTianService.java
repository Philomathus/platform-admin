package com.qiqilm.server.admin.service.game;

import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.vo.XiaFenResult;
import com.qiqilm.server.admin.utils.JsonUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Log4j2
public class MeiTianService {

	private static final String CREATE_USER   = "/services/dg/player/playerCreate2";
	private static final String QUERY_BALANCE = "/services/dg/player/getPlayerBalance/";
	private static final String TO_MEITIAN    = "/services/dg/player/deposit2/";
	private static final String TO_QIQI       = "/services/dg/player/withdraw2/";
	private static final String LOGIN         = "/services/dg/player/playerPlatformUrl/";
	private static final String MEITIAN       = "meiTian";

	@Resource
	private RestTemplate restTemplate;

	public boolean transfer( GamePlatform gamePlatform, String userId,
							 String orderId, BigDecimal changeMoney, String type ) {
		orderId = orderId.replace( "_", "" );
		StringBuilder       stringBuilder = new StringBuilder();
		Map<String, String> rawData       = new LinkedHashMap<>();
		rawData.put( "merchantId", gamePlatform.getAgent() );
		rawData.put( "playerName", userId );
		rawData.put( "extTransId", orderId );
		rawData.put( "coins", changeMoney.setScale( 4, BigDecimal.ROUND_HALF_UP ).toString() );
		String rawDataStr = JsonUtil.object2Json( rawData );
		stringBuilder.append( "/" ).append( gamePlatform.getAgent() )
				.append( "/" ).append( userId )
				.append( "/" ).append( changeMoney.setScale( 2, BigDecimal.ROUND_HALF_UP ) )
				.append( "/" ).append( orderId )
				.append( "/" ).append( DigestUtils.md5Hex( gamePlatform.getMd5() + rawDataStr ) )
				.append( "/" ).append( Base64.getEncoder().encodeToString( rawDataStr.getBytes() ) );
		Map<String, String>             map         = new HashMap<>();
		HttpHeaders                     httpHeaders = new HttpHeaders();
		HttpEntity<Map<String, String>> httpEntity  = new HttpEntity<>( map, httpHeaders );
		Map<String, String>             resultMap   = null;
		try {
			resultMap = restTemplate.postForObject( gamePlatform.getApiUrl() + type + stringBuilder.toString(), httpEntity,
					Map.class );
		} catch ( Exception e ) {
			log.error( "美天 转入接口 失败->{}", e.getMessage() );
			return false;
		}
		if ( !CollectionUtils.isEmpty( resultMap ) ) {
			if ( !org.apache.commons.lang3.StringUtils.equals( "1", resultMap.get( "resultCode" ) ) ) {
				log.warn( "美天 转入接口 失败->{}", JsonUtil.object2Json( resultMap ) );
				return false;
			}
			return true;
		}
		return false;
	}

	public XiaFenResult transfer( GamePlatform gamePlatform, String userId, String orderId ) {
		XiaFenResult result = new XiaFenResult();
		result.setOk( true );
		BigDecimal balance = queryCoin( gamePlatform, userId );
		if ( balance.compareTo( BigDecimal.ZERO ) > 0 ) {
			result.setBackMoney( balance );
			if ( transfer( gamePlatform, userId, orderId, balance, TO_QIQI ) ) {
				return result;
			}
			result.setOk( false );
			return result;
		}
		result.setBackMoney( BigDecimal.ZERO );
		return result;
	}

	public BigDecimal queryCoin( GamePlatform gamePlatform, String userId ) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( "/" ).append( userId )
				.append( "/" ).append( gamePlatform.getAgent() );
		Map<String, String>             map         = new HashMap<>();
		HttpHeaders                     httpHeaders = new HttpHeaders();
		HttpEntity<Map<String, String>> httpEntity  = new HttpEntity<>( map, httpHeaders );
		Map<String, String>             resultMap   = null;
		try {
			resultMap = restTemplate.postForObject( gamePlatform.getApiUrl() + QUERY_BALANCE + stringBuilder.toString(),
					httpEntity, Map.class );
		} catch ( Exception e ) {
			log.error( "美天 查询{}余额 失败->{}", userId, e.getMessage() );
			return BigDecimal.ZERO;
		}
		if ( !CollectionUtils.isEmpty( resultMap ) ) {
			if ( org.apache.commons.lang3.StringUtils.equals( "1", resultMap.get( "resultCode" ) ) ) {
				return new BigDecimal( resultMap.get( "coinBalance" ) ).setScale( 2, BigDecimal.ROUND_FLOOR );
			}
			log.warn( "美天 查询{}余额 失败->{}", userId, JsonUtil.object2Json( resultMap ) );
		}
		return BigDecimal.ZERO;
	}
}
