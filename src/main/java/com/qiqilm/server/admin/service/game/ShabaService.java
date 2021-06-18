package com.qiqilm.server.admin.service.game;

import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.vo.XiaFenResult;
import com.qiqilm.server.admin.utils.JsonUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

@Service
@Log4j2
public class ShabaService {

	private final String CREATE_USER    = "/CreateMember/";
	private final String TRANSFER       = "/FundTransfer/";
	private final String CHECK_TRANSFER = "/CheckFundTransfer/";
	private final String LOGIN          = "/Login/";
	private final String BALANCE        = "/CheckUserBalance/";
	private final int    CURRERCY       = 13;
	private final int    ODDSTYPE       = 2;
	private final int    TOSHABA        = 1;
	private final int    TOQIQI         = 0;
	private final int    MAX_COIN       = 99999;
	private final int    MIN_COIN       = 1;
	private final int    SPORTS_BOOK    = 1;

	@Resource
	private RestTemplate restTemplate;

	public boolean transfer( GamePlatform gamePlatform, String userId, String orderId, BigDecimal changeMoney, int type ) {
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add( "vendor_id", gamePlatform.getAgent() );
		map.add( "Vendor_Member_ID", userId );
		map.add( "vendor_trans_id", orderId );
		map.add( "amount", changeMoney );
		map.add( "currency", CURRERCY );
		map.add( "direction", type );
		map.add( "wallet_id", SPORTS_BOOK );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>( map, httpHeaders );
		Map<String, Object>                       resultMap  = null;
		try {
			resultMap = restTemplate.postForObject( gamePlatform.getApiUrl() + TRANSFER, httpEntity, Map.class );
		} catch ( Exception e ) {
			log.error( "shaba 转账失败 ->{}", e.getMessage() );
			return false;
		}
		int error_code = ( int ) resultMap.get( "error_code" );
		if ( error_code == 0 ) {
			return true;
		} else if ( error_code == 2 ) {
			try {
				Thread.sleep( 5000 );
			} catch ( InterruptedException e ) {
				log.warn( "shaba 线程休眠打断" );
			}
			return confirmTransfer( gamePlatform, orderId );
		}
		log.warn( "shaba 转账失败->{}", JsonUtil.object2Json( resultMap ) );
		return false;
	}

	public XiaFenResult transfer( GamePlatform gamePlatform, String userId, String orderId ) {
		XiaFenResult result = new XiaFenResult();
		result.setOk( true );
		BigDecimal balance = queryCoin( gamePlatform, userId );
		if ( balance.compareTo( BigDecimal.ZERO ) > 0 ) {
			result.setBackMoney( balance );
			if ( transfer( gamePlatform, userId, orderId, balance, TOQIQI ) ) {
				return result;
			}
			result.setOk( false );
			return result;
		}
		result.setBackMoney( BigDecimal.ZERO );
		return result;
	}

	public BigDecimal queryCoin( GamePlatform gamePlatform, String userId ) {
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add( "vendor_id", gamePlatform.getAgent() );
		map.add( "vendor_member_ids", userId );
		map.add( "wallet_id", SPORTS_BOOK );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>( map, httpHeaders );
		Map<String, Object>                       resultMap  = null;
		try {
			resultMap = restTemplate.postForObject( gamePlatform.getApiUrl() + BALANCE, httpEntity, Map.class );
		} catch ( Exception e ) {
			log.error( "shaba 查询余额 ->{}", e.getMessage() );
			return BigDecimal.ZERO;
		}
		int error_code = ( int ) resultMap.get( "error_code" );
		if ( error_code == 0 ) {
			ArrayList list = ( ArrayList ) resultMap.get( "Data" );
			resultMap = ( Map<String, Object> ) list.get( 0 );
			return resultMap.get( "balance" ) == null ? BigDecimal.ZERO : new BigDecimal( String.valueOf( resultMap.get(
					"balance" ) ) );
		}
		log.warn( "shaba 查询余额 失败->{}", JsonUtil.object2Json( resultMap ) );
		return BigDecimal.ZERO;
	}

	private boolean confirmTransfer( GamePlatform gamePlatform, String orderId ) {
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add( "vendor_id", gamePlatform.getAgent() );
		map.add( "vendor_trans_id", orderId );
		map.add( "wallet_id", SPORTS_BOOK );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>( map, httpHeaders );
		Map<String, Object>                       resultMap  = null;
		try {
			resultMap = restTemplate.postForObject( gamePlatform.getApiUrl() + CHECK_TRANSFER, httpEntity, Map.class );
		} catch ( Exception e ) {
			log.error( "shaba 确认转账失败 ->{}", e.getMessage() );
			return false;
		}
		int error_code = ( int ) resultMap.get( "error_code" );
		if ( error_code == 0 )
			return true;
		log.warn( "shaba 确认转账失败->{}", JsonUtil.object2Json( resultMap ) );
		return false;
	}

}
