package com.qiqilm.server.admin.payagent.processor;

import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Repository( value = ConstantsPayAgent.QUANFU + "PayAgentProcessor" )
@Log4j2
public class QuanFuPayAgentProcessor extends AbstractPayAgent {

    @Override
    public boolean orderPay( MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put( "agtId", payAgentPlatform.getMerId() );
        dataMap.put( "tranCode", "2101" );
        dataMap.put( "orderId", withdrawLog.getOrderNo() );
        dataMap.put( "tranDate", DateFormatUtils.formate( new Date(), "yyyyMMdd" ) );
        dataMap.put( "nonceStr", RANDOM( 16, "0" ) );
        dataMap.put( "txnAmt", withdrawLog.getWithdrawMoney().multiply( BigDecimal.valueOf( 100 ) )
                                          .setScale( 0, BigDecimal.ROUND_HALF_UP ) );
        dataMap.put( "accountNo", withdrawLog.getBankAccount() );
        dataMap.put( "bankName", STR2HEX( withdrawLog.getBankName() ) );
        dataMap.put( "accountName", STR2HEX( withdrawLog.getBankUserName() ) );
        dataMap.put( "cnaps", "308290003298" );
        dataMap.put( "accountType", "1" );
        dataMap.put( "notifyUrl", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + payAgentPlatform.getCode() );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
        String sign    = encryption( tempStr ).toUpperCase();
        sign = RSACoder.signSha256Rsa( sign, payAgentPlatform.getSignPrivateKey() );

        Map<String, Object> hdata = new HashMap<>();
        hdata.put( "sign", sign );

        Map<String, Object> map = new HashMap<>();
        map.put( "REQ_BODY", dataMap );
        map.put( "REQ_HEAD", hdata );

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.postForObject( payAgentPlatform.getPayOrderAddr(), map, Map.class );
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        log.info( payAgentPlatform.getName() + "下单结果 - result:{}", JsonUtil.object2Json( resultMap ) );

        if ( !CollectionUtils.isEmpty( resultMap ) ) {
            Map<String, Object> resMap = ( Map<String, Object> ) resultMap.getOrDefault( "REP_BODY", "" );
            if ( "000000".equals( resMap.getOrDefault( "rspcode", "" ).toString() ) ) {
                log.info( payAgentPlatform.getName() + "订单提交成功 - listResult:{}", JsonUtil.object2Json( resultMap ) );
                return true;
            } else {
                String msg = resMap.getOrDefault( "submsg", "" ).toString();
                reqPayAgent.setFailReason( HEX2STR( msg ) );
                payAgentService.callBackOrder( withdrawLog, payAgentPlatform );
            }
        }
        log.warn( payAgentPlatform.getName() + "订单提交失败 - result:{}", JsonUtil.object2Json( resultMap ) );
        return false;
    }

    @Override
    public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {
        Map<String, Object> signMap = ( Map<String, Object> ) requestMap.getOrDefault( "REP_HEAD", "" );
        String              sign    = signMap.getOrDefault( "sign", "" ).toString();

        Map<String, Object> dataMap = ( Map<String, Object> ) requestMap.getOrDefault( "REP_BODY", "" );
        Map<String, Object> treeMap = new TreeMap<>( dataMap );
        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String  tempStr = this.assemblyUrl( treeMap ) + "&key=" + signMd5;
        String  rspSign = encryption( tempStr ).toUpperCase();
        boolean flag    = RSACoder.verifySha256Rsa( rspSign, payAgentPlatform.getSignPublicKey(), sign );

        log.info( payAgentPlatform.getName() + "回调签名验签:" + flag );
        if ( flag ) {
            String order_num = dataMap.getOrDefault( "orderId", "" ).toString();
            String status    = dataMap.getOrDefault( "orderState", "" ).toString();

            MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( order_num );
            if ( withdrawLog == null ) {
                log.error( "提现相关记录丢失 - merOrderNo:{}", order_num );
                return "FAIL";
            }
            if ( withdrawLog.getStatus() == 2 ) {
                log.error( "订单已拒绝，无需回调 - merOrderNo:{}", order_num );
                return "SUCCESS";
            }
            if ( withdrawLog.getStatus() == 6 ) {
                log.error( "已有代付记录 - merOrderNo:{}", order_num );
                return "SUCCESS";
            }
            PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( order_num );
            payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, "01".equals( status ) );
            log.info( payAgentPlatform.getName() + "订单号:{},回调状态:{},", order_num, "01".equals( status ) ? "成功" : "失败" );
            return "SUCCESS";
        }
        return "FAIL";
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
        Map<String, Object> dataMap          = new TreeMap<>();
        dataMap.put( "agtId", payAgentPlatform.getMerId() );
        dataMap.put( "tranCode", "2102" );
        dataMap.put( "orderId", withdrawLog.getOrderNo() );
        dataMap.put( "tranDate", DateFormatUtils.formate( new Date(), "yyyyMMdd" ) );
        dataMap.put( "nonceStr", RANDOM( 16, "0" ) );

        String signMd5 = RSACoder.decryptByPrivateKey( payAgentPlatform.getSignMd5(), SECRET_PAYAGENT_KEY );
        String tempStr = this.assemblyUrl( dataMap ) + "&key=" + signMd5;
        String sign    = encryption( tempStr ).toUpperCase();
        sign = RSACoder.signSha256Rsa( sign, payAgentPlatform.getSignPrivateKey() );

        Map<String, Object> hdata = new HashMap<>();
        hdata.put( "sign", sign );

        Map<String, Object> map = new HashMap<>();
        map.put( "REQ_BODY", dataMap );
        map.put( "REQ_HEAD", hdata );

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.postForObject( payAgentPlatform.getPayOrderQueryAddr(), map, Map.class );
            log.warn( payAgentPlatform.getName() + "查询结果 - result:{}", JsonUtil.object2Json( resultMap ) );

            if ( !CollectionUtils.isEmpty( resultMap ) ) {
                //  status
                //  4代付中 5代付失败 6代付成功
                int status = 4;

                //  statusCode
                //  0000交易成功 T000未处理 T101 查无此交易 T006清算中 T010交易失败 T011交易结果未知
                String              statusCode = null;
                Map<String, Object> resMap     = ( Map<String, Object> ) resultMap.getOrDefault( "REP_BODY", "" );
                String              code       = resMap.getOrDefault( "rspcode", "" ).toString();
                if ( !"000000".equals( code ) ) {
                    statusCode = "T010";
                }

                statusCode = resMap.getOrDefault( "subcode", "" ).toString();

                if ( "0000".equals( statusCode ) || "T101".equals( statusCode ) || "T010".equals( statusCode ) ) {
                    if ( "0000".equals( statusCode ) ) {
                        status = 6;
                    } else {
                        status = 5;
                    }
                    payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, status );
                }
                String msg = resMap.getOrDefault( "submsg", "" ).toString();
                return resMap.getOrDefault( HEX2STR( msg ), "" ).toString();
            }

        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        return payAgentPlatform.getName() + "查询失败,订单号:" + withdrawLog.getOrderNo();
    }

    /**
     * @param plainText 明文
     *
     * @return 32位密文
     */
    public static String encryption( String plainText ) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance( "MD5" );
            md.update( plainText.getBytes() );
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer( "" );
            for ( int offset = 0; offset < b.length; offset++ ) {
                i = b[ offset ];
                if ( i < 0 ) {
                    i += 256;
                }
                if ( i < 16 ) {
                    buf.append( "0" );
                }
                buf.append( Integer.toHexString( i ) );
            }

            re_md5 = buf.toString();

        } catch ( NoSuchAlgorithmException e ) {
            e.printStackTrace();
        }
        return re_md5;
    }

    /**
     * <p>
     * 将普通字符串转换成十六进制字符串
     * </p>
     * STR2HEX(123456)返回结果为313233343536 <br>
     *
     * @param args [0] : 普通字符串
     *
     * @return 十六进制字符串 @
     */
    public static String STR2HEX( String args ) {
        if ( StringUtils.isEmpty( args ) ) {
            throw new RuntimeException( "HEX2STR" );
        }
        return new String( Hex.encodeHex( args.getBytes() ) );
    }

    /**
     * <p>
     * 将十六进制字符串转换成普通字符串
     * </p>
     * HEX2STR(313233343536)返回结果为123456 <br>
     *
     * @param args [0] : 十六进制字符串
     *
     * @return 转换后的字符串 @
     */
    public static String HEX2STR( String args ) {
        if ( StringUtils.isEmpty( args ) ) {
            throw new RuntimeException( "HEX2STR" );
        }
        try {
            return new String( Hex.decodeHex( args.toCharArray() ) );
        } catch ( DecoderException e ) {
            throw new RuntimeException( e.getMessage() );
        }
    }

    /**
     * 取随机字符串 <br>
     *
     * @param args1 构造指定长度的随机字符串
     * @param args2 指明是否包含字母，0-包含字母,数字和字母混合,默认是2 1-不包含数字,只有字母 2－不包含字母,只有数字
     *
     * @return @
     */
    public static String RANDOM( int args1, String args2 ) {
        if ( StringUtils.isEmpty( args2 ) ) {
            throw new RuntimeException( "RANDOM" );
        }
        int len = args1;
        args2 = StringUtils.trim( args2 );

        if ( StringUtils.equals( args2, "0" ) ) {
            return RandomStringUtils.randomAlphanumeric( len );
        } else if ( StringUtils.equals( args2, "1" ) ) {
            return RandomStringUtils.randomAlphabetic( len );
        } else {
            return RandomStringUtils.randomNumeric( len );
        }

    }

}
