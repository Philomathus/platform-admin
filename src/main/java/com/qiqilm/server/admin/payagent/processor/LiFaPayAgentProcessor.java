package com.qiqilm.server.admin.payagent.processor;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.JsonUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

@Repository( value = ConstantsPayAgent.LIFA + "PayAgentProcessor" )
@Log4j2
public class LiFaPayAgentProcessor extends AbstractPayAgent {
    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        SortedMap<String, Object> bodyMap = new TreeMap<>();
        bodyMap.put( "mchId", payAgentPlatform.getMerId() );
        bodyMap.put( "appId", payAgentPlatform.getHeaderKey() );
        bodyMap.put( "mchAgentpayOrderNo", withdrawLog.getOrderNo() );
        bodyMap.put( "amount", withdrawLog.getWithdrawMoney().multiply( new BigDecimal( 100 ) )
                                          .setScale( 0, RoundingMode.HALF_UP ) );
        bodyMap.put( "currency", "CNY" );

        //账户信息（”账户名|账户|开户行|开户网点”字符串转16进制的AES加密）
        String content       =
                withdrawLog.getBankUserName().trim() + "|" + withdrawLog.getBankAccount().trim() + "|" + withdrawLog.getBankName()
                                                                                                                    .trim() + "|"
                        + "深圳市北京路支行";
        String clearUserInfo = encrypt( content, payAgentPlatform.getSignPublicKey() );
        bodyMap.put( "clearUserInfo", clearUserInfo );
        bodyMap.put( "notifyUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + payAgentPlatform.getSignPublicKey();
        String sign    = DigestUtils.md5Hex( tempStr ).toUpperCase();
        bodyMap.put( "sign", sign );
        bodyMap.put( "params", JsonUtil.object2Json( bodyMap ) );

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll( bodyMap );
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity( requestMap, httpHeaders );

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute( payAgentPlatform.getPayOrderAddr(), HttpMethod.POST,
                    restTemplate.httpEntityCallback( httpEntity ), response -> {
                InputStream bodyStream = response.getBody();
                String      text;
                try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                    text = CharStreams.toString( reader );
                }
                return JsonUtil.json2Map( text );
            } );
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
            reqPayAgent.setFailReason( "利发代付下单报错原因:" + e );
        }
        log.info( payAgentPlatform.getName()
                + "下单结果{},订单号:{}", JsonUtil.object2Json( resultMap ), withdrawLog.getOrderNo() );
        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            String retCode = resultMap.getOrDefault( "retCode", "" ).toString();
            if ( "SUCCESS".equals( retCode ) ) {
                log.info( "利发代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                reqPayAgent.setFailReason( resultMap.getOrDefault( "message", "" ).toString() );

                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
        }
        log.warn( "利发代付订单提交失败 - result:{}", JsonUtil.object2Json( resultMap ) );
        return false;
    }

    /**
     * AES加密字符串
     *
     * @param content  需要被加密的字符串
     * @param password 加密需要的密码(秘钥)
     *
     * @return 密文
     */
    public static String encrypt( String content, String password ) {
        try {
            KeyGenerator kgen         = KeyGenerator.getInstance( "AES" );
            SecureRandom secureRandom = SecureRandom.getInstance( "SHA1PRNG" );
            secureRandom.setSeed( password.getBytes( "UTF-8" ) );
            kgen.init( 128, secureRandom );
            SecretKey     secretKey    = kgen.generateKey();
            byte[]        enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key          = new SecretKeySpec( enCodeFormat, "AES" );
            Cipher        cipher       = Cipher.getInstance( "AES" );
            byte[]        byteContent  = content.getBytes( "UTF-8" );
            cipher.init( Cipher.ENCRYPT_MODE, key );
            byte[] result = cipher.doFinal( byteContent );
            return parseByte2HexStr( result );
        } catch ( Exception e ) {

        }
        return null;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     */
    public static String parseByte2HexStr( byte buf[] ) {
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < buf.length; i++ ) {
            String hex = Integer.toHexString( buf[ i ] & 0xFF );
            if ( hex.length() == 1 ) {
                hex = '0' + hex;
            }
            sb.append( hex.toUpperCase() );
        }
        return sb.toString();
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        String                    sign    = requestMap.remove( "sign" ).toString();
        String                    status  = requestMap.getOrDefault( "status", "" ).toString();
        SortedMap<String, Object> bodyMap = new TreeMap<>( requestMap );

        //回调时md5加密的太长了报错,不加密的MD5密钥放在公钥字段
        String tempStr = this.assemblyUrl( bodyMap ) + "&key=" + payAgentPlatform.getSignPublicKey();
        String signStr = DigestUtils.md5Hex( tempStr );

        log.info( "利发代付回调签名字符串:" + sign + "_" + signStr );
        if ( sign.equalsIgnoreCase( signStr ) ) {
            String mchAgentpayOrderNo = ( String ) requestMap.get( "mchAgentpayOrderNo" );

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( mchAgentpayOrderNo );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", mchAgentpayOrderNo );
                return "fail";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", mchAgentpayOrderNo );
                return "success";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( mchAgentpayOrderNo );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, "2".equals( status ) );
            return "success";
        }
        return "fail";
    }

    @Override
    public Map<String, Object> reverseCheckOrderPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap,
                                                     String realIp ) throws Exception {
        return null;
    }

    @Override
    public String queryOrderPay( PayAgentLog payAgentLog ) throws Exception {
        MemberWithdrawLog   withdrawLog      = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
        PayAgentPlatform    payAgentPlatform =
                payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
        Map<String, Object> paramsMap        = new TreeMap<>();
        paramsMap.put( "mchId", payAgentPlatform.getMerId() );
        paramsMap.put( "mchAgentpayOrderNo", withdrawLog.getOrderNo() );

        String tempStr = this.assemblyUrl( paramsMap ) + "&key=" + payAgentPlatform.getSignPublicKey();
        String sign    = DigestUtils.md5Hex( tempStr ).toUpperCase();
        paramsMap.put( "sign", sign );
        paramsMap.put( "params", JsonUtil.object2Json( paramsMap ) );

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.setAll( paramsMap );
        log.warn( JsonUtil.object2Json( requestMap ) );
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity( requestMap, httpHeaders );

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute( payAgentPlatform.getPayOrderQueryAddr(), HttpMethod.POST,
                    restTemplate.httpEntityCallback( httpEntity ), response -> {
                InputStream bodyStream = response.getBody();
                String      text;
                try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                    text = CharStreams.toString( reader );
                }
                return JsonUtil.json2Map( text );
            } );
            log.info( "利发代付查询结果- result:{}", JsonUtil.object2Json( resultMap ) );
            if ( !CollectionUtils.isEmpty( resultMap ) ) {
                String retCode = resultMap.getOrDefault( "retCode", "" ).toString();
                if ( "SUCCESS".equals( retCode ) ) {
                    int statusType = Integer.parseInt( resultMap.getOrDefault( "status", "" ).toString() );
                    if ( statusType == 2 || statusType == 3 ) {
                        // status 4代付中 5代付失败 6代付成功
                        // statusType  0-待处理,1-处理中,2-成功,3-失败)
                        int status = 4;
                        if ( statusType == 2 ) {
                            status = 6;
                        } else {
                            status = 5;
                        }
                        payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, statusType );
                    }
                }
                return resultMap.getOrDefault( "msg", "" ).toString();
            }
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        return "利发代付查询失败";
    }
}
