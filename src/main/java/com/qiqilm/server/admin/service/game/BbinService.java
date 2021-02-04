package com.qiqilm.server.admin.service.game;

import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.vo.XiaFenResult;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.UuidUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Service
@Log4j2
public class BbinService {

	private final String createSession      = "CreateSession?";
	private final String transfer           = "Transfer?";
	private final String query              = "CheckUsrBalance?";
	private final String createSession_key8 = "0P2McG5jG";
	private final String transfer_key8      = "un5zaZl";
	private final String joinGame_key8      = "7G7kc84PxB";
	private final String query_key8         = "5HdRJ";
	private final String transfer_in        = "IN";
	private final String transfer_out       = "OUT";

	@Resource
	private RestTemplate restTemplate;

	public XiaFenResult transferOUT( GamePlatform gamePlatform, String userId, String orderId ) {
		XiaFenResult result = new XiaFenResult();
		result.setOk( true );
		userId = userId.replace( "_", "BBIN" );
		BigDecimal changeMoney = queryCoin( gamePlatform, userId );
		if ( changeMoney.compareTo( BigDecimal.ZERO ) > 0 ) {
			result.setBackMoney( changeMoney );
			if ( transfer( gamePlatform, userId, orderId, changeMoney, transfer_out ) ) {
				return result;
			}
			result.setOk( false );
			return result;
		}
		result.setBackMoney( BigDecimal.ZERO );
		return result;
	}

	public BigDecimal queryCoin( GamePlatform gamePlatform, String userId ) {
		StringBuilder builder = new StringBuilder();
		String        random  = UuidUtil.getRandomUuid();
		String        a       = random.substring( 0, 2 ).replace( "-", "a" );
		String        c       = random.substring( random.length() - 3, random.length() - 1 ).replace( "-", "a" );
		String        md5     = gamePlatform.getDes() + userId + query_key8 + convertTime( new Date() );
		md5 = DigestUtils.md5Hex( md5 );
		builder.append( "website=" ).append( gamePlatform.getDes() )
				.append( "&username=" ).append( userId )
				.append( "&uppername=" ).append( gamePlatform.getMd5() )
				.append( "&key=" ).append( a ).append( md5 ).append( c );
		String resultStr = restTemplate.getForObject( gamePlatform.getApiUrl() + query + builder.toString(),
				String.class );
		Map<String, Object> resultMap = JsonUtil.json2Map( resultStr );
		boolean             result    = ( boolean ) resultMap.get( "result" );
		if ( !result ) {
			log.error( "BBIN 查询余额 ->" + resultStr );
			return BigDecimal.ZERO;
		}
		resultMap = ( ( List<Map<String, Object>> ) resultMap.get( "data" ) ).get( 0 );
		return new BigDecimal( resultMap.get( "Balance" ).toString() );
	}

	public boolean transfer( GamePlatform gamePlatform, String userId, String orderId, BigDecimal changeMoney,
							 String transferType ) {
		StringBuilder builder = new StringBuilder();
		String        random  = UuidUtil.getRandomUuid();
		String        a       = random.substring( 0, 9 ).replace( "-", "a" );
		String        c       = random.substring( random.length() - 2, random.length() - 1 ).replace( "-", "a" );
		String        md5     = gamePlatform.getDes() + userId + orderId + transfer_key8 + convertTime( new Date() );
		md5 = DigestUtils.md5Hex( md5 );
		builder.append( "website=" ).append( gamePlatform.getDes() )
				.append( "&username=" ).append( userId )
				.append( "&uppername=" ).append( gamePlatform.getMd5() )
				.append( "&remitno=" ).append( orderId )
				.append( "&action=" ).append( transferType )
				.append( "&remit=" ).append( changeMoney )
				.append( "&key=" ).append( a ).append( md5 ).append( c );
		String resultStr = restTemplate.getForObject( gamePlatform.getApiUrl() + transfer + builder.toString(),
				String.class );
		Map<String, Object> resultMap = JsonUtil.json2Map( resultStr );
		boolean             result    = ( boolean ) resultMap.get( "result" );
		if ( !result ) {
			log.error( "BBIN转账->" + resultStr );
			return false;
		}
		resultMap = ( Map<String, Object> ) resultMap.get( "data" );
		if ( !"11100".equals( resultMap.get( "Code" ) ) ) {
			log.error( "BBIN转账->" + resultStr );
			return false;
		}
		log.warn( "BBIN转账->{}:{}:BBIN:{}", userId, transferType, changeMoney );
		return true;
	}

	public String convertTime( Date date ) {
		SimpleDateFormat sdf8 = new SimpleDateFormat( "yyyyMMdd" );
		sdf8.setTimeZone( TimeZone.getTimeZone( "America/Caracas" ) );
		return sdf8.format( date );
	}
}
