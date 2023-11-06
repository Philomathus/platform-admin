package com.qiqilm.server.admin.service.game;

import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.vo.XiaFenResult;
import com.qiqilm.server.admin.utils.Encrypt;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.PostData;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Service
@Log4j2
public class NewWorldService {

    public boolean transfer( GamePlatform gamePlatform, String userId, BigDecimal changeMoney, String orderId, Date date ) {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder param         = new StringBuilder();
        String        agent         = gamePlatform.getAgent();
        long          timestamp     = date.getTime();
        param
                .append( "s=3&account=" )
                .append( userId )
                .append( "&money=" )
                .append( changeMoney )
                .append( "&orderid=" )
                .append( orderId );
        String paramStr;
        try {
            paramStr = Encrypt.AESEncrypt( param.toString(), gamePlatform.getDes() );
        } catch ( Exception e ) {
            log.error( "newWorld 加密失败->{}", e.getMessage() );
            return false;
        }
        String md5 = gamePlatform.getMd5();
        md5 = DigestUtils.md5Hex( agent + timestamp + md5 );
        stringBuilder
                .append( "?agent=" )
                .append( agent )
                .append( "&timestamp=" )
                .append( timestamp )
                .append( "&param=" )
                .append( paramStr )
                .append( "&key=" )
                .append( md5 );
        String              result    = PostData.get( gamePlatform.getApiUrl() + stringBuilder );
        Map<String, Object> resultMap = JsonUtil.json2Map( result );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            resultMap = ( Map<String, Object> ) resultMap.get( "d" );
            if ( StringUtils.equals( "0", String.valueOf( resultMap.get( "code" ) ) ) ) {
                return true;
            }
            log.warn( "newWorld 下分 失败->{}", JsonUtil.object2Json( resultMap ) );
        }
        return false;
    }

    public XiaFenResult transfer( GamePlatform gamePlatform, String userId, String orderId, Date date ) {
        XiaFenResult result = new XiaFenResult();
        result.setOk( true );
        BigDecimal balance = queryCoin( gamePlatform, userId, date );
        if ( balance.compareTo( BigDecimal.ZERO ) > 0 ) {
            result.setBackMoney( balance );
            if ( transfer( gamePlatform, userId, balance, orderId, date ) ) {
                return result;
            }
            result.setOk( false );
            return result;
        }
        result.setBackMoney( BigDecimal.ZERO );
        return result;
    }

    public BigDecimal queryCoin( GamePlatform gamePlatform, String userId, Date date ) {
        StringBuilder stringBuilder = new StringBuilder( gamePlatform.getApiUrl() );
        StringBuilder param         = new StringBuilder();
        String        agent         = gamePlatform.getAgent();
        long          timestamp     = date.getTime();
        param.append( "method=1&gamePlayer=" ).append( userId );
        String paramStr;
        try {
            paramStr = Encrypt.AESEncrypt( param.toString(), gamePlatform.getDes() );
        } catch ( Exception e ) {
            log.error( "newWorld 加密失败->{}", e.getMessage() );
            return BigDecimal.ZERO;
        }
        String md5 = gamePlatform.getMd5();
        md5 = DigestUtils.md5Hex( agent + timestamp + md5 );
        stringBuilder
                .append( "?channel=" )
                .append( agent )
                .append( "&mTime=" )
                .append( timestamp )
                .append( "&paramerter=" )
                .append( paramStr )
                .append( "&key=" )
                .append( md5 );
        String result = PostData.get( stringBuilder.toString() );
        log.warn( stringBuilder + " ::: " + result );
        Map<String, Object> resultMap = JsonUtil.json2Map( result );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            resultMap = ( Map<String, Object> ) resultMap.get( "dataStr" );
            if ( org.apache.commons.lang3.StringUtils.equals( "0", String.valueOf( resultMap.get( "code" ) ) ) ) {
                return new BigDecimal( String.valueOf( resultMap.get( "score" ) ) ).setScale( 0, BigDecimal.ROUND_DOWN );
            }
            log.warn( "newWorld 余额 失败->{}", JsonUtil.object2Json( resultMap ) );
        }
        return BigDecimal.ZERO;
    }
}
