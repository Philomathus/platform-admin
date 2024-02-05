package com.qiqilm.server.admin.utils;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.enums.EnumGamePlatform;
import com.qiqilm.server.admin.exception.BaseException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


/**
 * 前端数据处理类
 *
 * @author temdy
 * @Date 2016-08-26
 */
@Log4j2
@Component
public class PostData {
    private static RestTemplate restTemplate;

    @PostConstruct
    void init() {
        restTemplate = SpringUtils.getBean( RestTemplate.class );
    }

    /**
     * POST提交数据方法
     *
     * @param postUrl POST提交URL
     * @param entity  参数集合
     *
     * @return String
     * @author temdy
     * @Date 2016-08-26
     */
    public static String post( String postUrl, Map<String, String> entity ) {
        String obj = null;
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            // httpHeaders.setContentType( MediaType.APPLICATION_JSON );
            HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>( entity, httpHeaders );

            obj = restTemplate.execute( postUrl, HttpMethod.POST, restTemplate.httpEntityCallback( httpEntity ), response -> {
                log.info( response.getRawStatusCode() );
                InputStream bodyStream = response.getBody();
                String      text;
                try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                    text = CharStreams.toString( reader );
                }
                return text;
            } );

            log.info( obj );
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        return obj;
    }

    /**
     * POST提交数据方法
     *
     * @param postUrl POST提交URL
     *
     * @return String
     * @author temdy
     * @Date 2016-08-26
     */
    public static String get( String postUrl ) {
        String     obj    = null;
        try {
            HttpClient client = new HttpClient();
            GetMethod  method = new GetMethod( postUrl );
            client.executeMethod( method );
            client.getHttpConnectionManager().getParams().setConnectionTimeout( 8000 );
            client.getHttpConnectionManager().getParams().setSoTimeout( 8000 );
            obj = method.getResponseBodyAsString();

            /*HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setAccept( new ArrayList<>() );
            httpHeaders.setConnection( new ArrayList<>() );
            HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>( null, httpHeaders );
            obj = restTemplate.execute( postUrl, HttpMethod.GET, restTemplate.httpEntityCallback( httpEntity ), response -> {
                InputStream bodyStream = response.getBody();
                String      text;
                try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                    text = CharStreams.toString( reader );
                }
                return text;
            } );*/
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        return obj;
    }

    /**
     * 进入游戏接口
     *
     * @param agent    代理帐号
     * @param account  帐号
     * @param money    余额
     * @param orderId  订单号
     * @param dk       DES密钥
     * @param mk       MD5密钥
     * @param apiUrl   api接口URL
     * @param ip       请求IP地址
     * @param lineCode lineCode
     *
     * @return 结果
     * @throws Exception
     */
    public static String game( String agent, String account, String money, String orderId, String dk, String mk, String apiUrl,
                               String ip, String lineCode, String kindId ) throws Exception {
        String time = System.currentTimeMillis() + "";
        String params =
                "s=0&account=" + account + "&money=" + money + "&orderid=" + orderId + "&ip=" + ip + "&lineCode=" + lineCode
                        + "&KindID=" + kindId;
        String param = Encrypt.AESEncrypt( params, dk );
        String key   = DigestUtils.md5Hex( agent + time + mk );
        String postUrl = apiUrl.concat( "?agent=" ).concat( agent ).concat( "&timestamp=" ).concat( time ).concat( "&param=" )
                               .concat( param ).concat( "&key=" ).concat( key );
        log.info( "提交" + postUrl );
        return get( postUrl );
    }


    /**
     * 查询可下分余额
     *
     * @param agent   代理帐号
     * @param account 帐号
     * @param des     DES密钥
     * @param md5     MD5密钥
     * @param apiUrl  API接口
     *
     * @throws Exception
     */
    public static String getBalance( String agent, String account, String des, String md5, String apiUrl ) throws Exception {
        String time   = System.currentTimeMillis() + "";
        String params = "s=1&account=" + account;
        String param  = Encrypt.AESEncrypt( params, des );
        String key    = DigestUtils.md5Hex( agent + time + md5 );
        String postUrl = apiUrl.concat( "?agent=" ).concat( agent ).concat( "&timestamp=" ).concat( time ).concat( "&param=" )
                               .concat( param ).concat( "&key=" ).concat( key );
        log.info( "请求URL：" + postUrl );
        return get( postUrl );
    }

    /**
     * 上分接口
     *
     * @param agent   代理帐号
     * @param account 帐号
     * @param money   余额
     * @param orderid 订单号
     * @param des     DES密钥
     * @param md5     MD5密钥
     * @param apiUrl  API接口
     *
     * @throws Exception
     */
    public static String sf( String agent, String account, String money, String orderid, String des, String md5,
                             String apiUrl ) throws Exception {
        String time   = System.currentTimeMillis() + "";
        String params = "s=2&account=" + account + "&money=" + money + "&orderid=" + orderid;
        String param  = Encrypt.AESEncrypt( params, des );
        String key    = DigestUtils.md5Hex( agent + time + md5 );
        String postUrl = apiUrl.concat( "?agent=" ).concat( agent ).concat( "&timestamp=" ).concat( time ).concat( "&param=" )
                               .concat( param ).concat( "&key=" ).concat( key );
        log.info( "请求URL：" + postUrl );
        return get( postUrl );
    }


    /**
     * 下分接口
     *
     * @param agent   代理帐号
     * @param account 帐号
     * @param money   余额
     * @param orderid 订单号
     * @param des     DES密钥
     * @param md5     MD5密钥
     * @param apiUrl  API接口
     *
     * @throws Exception
     */
    public static String xiafen( String agent, String account, String money, String orderid, String des, String md5,
                                 String apiUrl ) throws Exception {
        String time   = System.currentTimeMillis() + "";
        String params = "s=3&account=" + account + "&money=" + money + "&orderid=" + orderid;
        String param  = Encrypt.AESEncrypt( params, des );
        String key    = DigestUtils.md5Hex( agent + time + md5 );
        String postUrl = apiUrl.concat( "?agent=" ).concat( agent ).concat( "&timestamp=" ).concat( time ).concat( "&param=" )
                               .concat( param ).concat( "&key=" ).concat( key );
        log.info( "请求URL：" + postUrl );
        return get( postUrl );
    }


    /**
     * 订单查询
     *
     * @param agent   代理帐号
     * @param orderid 订单号
     * @param des     DES密钥
     * @param md5     MD5密钥
     * @param apiUrl  API接口
     *
     * @throws Exception
     */
    public static String getOrder( String agent, String orderid, String des, String md5, String apiUrl ) throws Exception {
        String time   = System.currentTimeMillis() + "";
        String params = "s=4&orderid=" + orderid;
        String param  = Encrypt.AESEncrypt( params, des );
        String key    = DigestUtils.md5Hex( agent + time + md5 );
        String postUrl = apiUrl.concat( "?agent=" ).concat( agent ).concat( "&timestamp=" ).concat( time ).concat( "&param=" )
                               .concat( param ).concat( "&key=" ).concat( key );
        log.info( "请求URL：" + postUrl );
        return get( postUrl );
    }

    /**
     * 玩家是否在线查询
     *
     * @param agent   代理帐号
     * @param account 帐号
     * @param des     DES密钥
     * @param md5     MD5密钥
     * @param apiUrl  API接口
     *
     * @throws Exception
     */
    public static String getState( String agent, String account, String des, String md5, String apiUrl ) throws Exception {
        String time   = System.currentTimeMillis() + "";
        String params = "s=5&account=" + account;
        String param  = Encrypt.AESEncrypt( params, des );
        String key    = DigestUtils.md5Hex( agent + time + md5 );
        String postUrl = apiUrl.concat( "?agent=" ).concat( agent ).concat( "&timestamp=" ).concat( time ).concat( "&param=" )
                               .concat( param ).concat( "&key=" ).concat( key );
        log.info( "请求URL：" + postUrl );
        return get( postUrl );
    }

    /**
     * 获取游戏结果数据接口
     *
     * @param agent     代理帐号
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param dk        DES密钥
     * @param mk        MD5密钥
     * @param apiUrl    api接口URL
     *
     * @return 结果
     * @throws Exception
     */
    public static String getRecord( String agent, String startTime, String endTime, String dk, String mk, String apiUrl ) throws Exception {
        String time   = System.currentTimeMillis() + "";
        String params = "s=6&startTime=" + startTime + "&endTime=" + endTime;
        String param  = Encrypt.AESEncrypt( params, dk );
        String key    = DigestUtils.md5Hex( agent + time + mk );
        String postUrl = apiUrl.concat( "?agent=" ).concat( agent ).concat( "&timestamp=" ).concat( time ).concat( "&param=" )
                               .concat( param ).concat( "&key=" ).concat( key );
        log.info( "提交" + postUrl );
        return get( postUrl );
    }

    /**
     * 查询玩家总分
     *
     * @param agent   代理帐号
     * @param account 帐号
     * @param des     DES密钥
     * @param md5     MD5密钥
     * @param apiUrl  API接口
     *
     * @throws Exception
     */
    public static String getAllBalance( String agent, String account, String des, String md5, String apiUrl ) throws Exception {
        String time   = System.currentTimeMillis() + "";
        String params = "s=7&account=" + account;
        String param  = Encrypt.AESEncrypt( params, des );
        String key    = DigestUtils.md5Hex( agent + time + md5 );
        String postUrl = apiUrl.concat( "?agent=" ).concat( agent ).concat( "&timestamp=" ).concat( time ).concat( "&param=" )
                               .concat( param ).concat( "&key=" ).concat( key );
        log.info( "请求URL：" + postUrl );
        return get( postUrl );
    }

    public static String getKYBalance( String agent, String account, String kindID, String recordID, String DESKey, String md5,
                                       String apiUrl ) throws Exception {
        String time   = System.currentTimeMillis() + "";
        String params = "s=19&kindID=" + kindID + "&recordID=" + recordID + "&account=" + account;
        String param  = Encrypt.AESEncrypt( params, DESKey );
        String key    = DigestUtils.md5Hex( agent + time + md5 );
        String postUrl = apiUrl.concat( "?agent=" ).concat( agent ).concat( "&timestamp=" ).concat( time ).concat( "&param=" )
                               .concat( param ).concat( "&key=" ).concat( key );
        log.info( "请求URL：" + postUrl );
        return get( postUrl );
    }


    /**
     * 踢玩家下线
     *
     * @param agent   代理帐号
     * @param account 帐号
     * @param des     DES密钥
     * @param md5     MD5密钥
     * @param apiUrl  API接口
     *
     * @throws Exception
     */
    public static String kick( String agent, String account, String des, String md5, String apiUrl ) throws Exception {
        String time   = System.currentTimeMillis() + "";
        String params = "s=8&account=" + account;
        String param  = Encrypt.AESEncrypt( params, des );
        String key    = DigestUtils.md5Hex( agent + time + md5 );
        String postUrl = apiUrl.concat( "?agent=" ).concat( agent ).concat( "&timestamp=" ).concat( time ).concat( "&param=" )
                               .concat( param ).concat( "&key=" ).concat( key );
        log.info( "请求URL：" + postUrl );
        return get( postUrl );
    }

    /**
     * 产生上下分订单号
     *
     * @param agent   代理帐号
     * @param account 帐号
     */
    public static String createOrderId( String agent, String account, Integer platform_id, Date date ) {
        if ( EnumGamePlatform.AG_LIVE.getType() == platform_id || EnumGamePlatform.FANY_SPORT.getType() == platform_id ) {
            return agent.concat( account.split( "_" )[ 1 ] ).concat( String.valueOf( System.currentTimeMillis() / 1000 ) );
        }
        if ( EnumGamePlatform.BBIN_LIVE.getType() == platform_id || EnumGamePlatform.BBIN_SPORT.getType() == platform_id
                || EnumGamePlatform.BBIN_FISH.getType() == platform_id
                || EnumGamePlatform.BBIN_DIANZI.getType() == platform_id ) {
            return account.split( "_" )[ 1 ].concat( String.valueOf( System.currentTimeMillis() / 1000 ) );
        }
        return agent.concat( DateFormatUtils.formate( date, "yyyyMMddHHmmssSSS" ) ).concat( account );

    }

    /**
     * 用于注册账号及检查
     *
     * @throws Exception
     */
    public static String CheckOrCreateGameAccout( String account, GamePlatform gamePlatform ) throws Exception {
        String url         = gamePlatform.getApiUrl();
        String encrypt_key = gamePlatform.getDes();
        String params = ( "cagent=" + gamePlatform.getAgent() + "/\\\\\\\\/method=lg/\\\\\\\\/loginname=" + account
                + "/\\\\\\\\/actype=1/\\\\\\\\/password=" + account + "/\\\\\\\\/cur=CNY/\\\\\\\\/oddtype=A" );
        String targetParams = DESSecretUtil.jdkBase64String( DESSecretUtil.desEncrypt( params, encrypt_key ) );
        targetParams = targetParams.replace( "\n", "" );
        targetParams = targetParams.replace( "\r", "" );
        String key     = DigestUtils.md5Hex( targetParams + gamePlatform.getMd5() );
        String postUrl = url.concat( "doBusiness.do?params=" ).concat( targetParams ).concat( "&key=" ).concat( key );
        return getAGResult( postUrl );
    }

    /**
     * @throws Exception
     */
    public static String AGtogame( String account, GamePlatform gamePlatform, String orderId, String kindId ) throws Exception {
        String encrypt_key = gamePlatform.getDes();
        String params = ( "cagent=" + gamePlatform.getAgent() + "/\\\\\\\\/loginname=" + account + "/\\\\\\\\/actype=1"
                + "/\\\\\\\\/password=" + account + "/\\\\\\\\/cur=CNY/\\\\\\\\/oddtype=A"
                + "/\\\\\\\\/dm=https://77.tv?AGGameQuit/\\\\\\\\/sid=" + gamePlatform.getAgent() + orderId + "/\\\\\\\\/lang"
                + "=1" + "/\\\\\\\\/gameType=" + kindId );
        String targetParams = DESSecretUtil.jdkBase64String( DESSecretUtil.desEncrypt( params, encrypt_key ) );
        targetParams = targetParams.replace( "\n", "" );
        targetParams = targetParams.replace( "\r", "" );
        String key = DigestUtils.md5Hex( targetParams + gamePlatform.getMd5() );
        String postUrl = "https://gci.zhk365.com/".concat( "forwardGame.do?params=" ).concat( targetParams ).concat( "&key=" )
                                                  .concat( key );
        return postUrl;
    }

    public static String PrepareTransferCredit( String account, GamePlatform gamePlatform, BigDecimal changeMoney,
                                                String orderId, String type ) throws Exception {
        String url         = gamePlatform.getApiUrl();
        String encrypt_key = gamePlatform.getDes();
        String params = ( "cagent=" + gamePlatform.getAgent() + "/\\\\\\\\/loginname=" + account + "/\\\\\\\\/actype=1"
                + "/\\\\\\\\/password=" + account + "/\\\\\\\\/cur=CNY" + "/\\\\\\\\/method=tc/\\\\\\\\/billno=" + orderId
                + "/\\\\\\\\/type=" + type + "/\\\\\\\\/credit=" + changeMoney + "" );
        String targetParams = DESSecretUtil.jdkBase64String( DESSecretUtil.desEncrypt( params, encrypt_key ) );
        targetParams = targetParams.replace( "\n", "" );
        targetParams = targetParams.replace( "\r", "" );
        String key     = DigestUtils.md5Hex( targetParams + gamePlatform.getMd5() );
        String postUrl = url.concat( "doBusiness.do?params=" ).concat( targetParams ).concat( "&key=" ).concat( key );
        return getAGResult( postUrl );
    }

    /**
     * 确认转账
     *
     * @throws Exception
     */
    public static String confirmMoney( String account, GamePlatform gamePlatform, BigDecimal changeMoney, String orderId,
                                       String type ) throws Exception {
        String url         = gamePlatform.getApiUrl();
        String encrypt_key = gamePlatform.getDes();
        String params = ( "cagent=" + gamePlatform.getAgent() + "/\\\\\\\\/loginname=" + account + "/\\\\\\\\/actype=1"
                + "/\\\\\\\\/password=" + account + "/\\\\\\\\/cur=CNY/\\\\\\\\/flag=1" + "/\\\\\\\\/method=tcc/\\\\\\\\/billno="
                + orderId + "/\\\\\\\\/type=" + type + "/\\\\\\\\/credit=" + changeMoney );
        String targetParams = DESSecretUtil.jdkBase64String( DESSecretUtil.desEncrypt( params, encrypt_key ) );
        targetParams = targetParams.replace( "\n", "" );
        targetParams = targetParams.replace( "\r", "" );
        String key     = DigestUtils.md5Hex( targetParams + gamePlatform.getMd5() );
        String postUrl = url.concat( "doBusiness.do?params=" ).concat( targetParams ).concat( "&key=" ).concat( key );
        return getAGResult( postUrl );
    }

    /**
     * 查询订单状态
     *
     * @throws Exception
     */
    public static String queryOrderStatus( String account, GamePlatform gamePlatform, BigDecimal changeMoney, String orderId ) throws Exception {
        String url         = gamePlatform.getApiUrl();
        String encrypt_key = gamePlatform.getDes();
        String params = ( "cagent=" + gamePlatform.getAgent() + "/\\\\\\\\/cur=CNY/\\\\\\\\/actype=1"
                + "/\\\\\\\\/method=qos/\\\\\\\\/billno=" + orderId );
        String targetParams = DESSecretUtil.jdkBase64String( DESSecretUtil.desEncrypt( params, encrypt_key ) );
        targetParams = targetParams.replace( "\n", "" );
        targetParams = targetParams.replace( "\r", "" );
        String key     = DigestUtils.md5Hex( targetParams + gamePlatform.getMd5() );
        String postUrl = url.concat( "doBusiness.do?params=" ).concat( targetParams ).concat( "&key=" ).concat( key );
        return getAGResult( postUrl );
    }

    /**
     * 查询余额
     *
     * @throws Exception
     */
    public static String GetBalance( String account, GamePlatform gamePlatform, String orderId ) throws Exception {
        String url         = gamePlatform.getApiUrl();
        String encrypt_key = gamePlatform.getDes();
        String params = ( "cagent=" + gamePlatform.getAgent() + "/\\\\\\\\/cur=CNY/\\\\\\\\/actype=1/\\\\\\\\/loginname="
                + account + "/\\\\\\\\/actype=1" + "/\\\\\\\\/method=gb/\\\\\\\\/password=" + account );
        String targetParams = DESSecretUtil.jdkBase64String( DESSecretUtil.desEncrypt( params, encrypt_key ) );
        targetParams = targetParams.replace( "\n", "" );
        targetParams = targetParams.replace( "\r", "" );
        String key     = DigestUtils.md5Hex( targetParams + gamePlatform.getMd5() );
        String postUrl = url.concat( "doBusiness.do?params=" ).concat( targetParams ).concat( "&key=" ).concat( key );
        return getAGResult( postUrl );
    }

    /**
     * POST提交数据方法
     *
     * @param postUrl POST提交URL
     *
     * @return String
     * @author temdy
     * @Date 2016-08-26
     */
    public static String getAGResult( String postUrl ) {
        String obj = null;
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set( "User-Agent", "WEB_LIB_GI_GY9_AGIN" );
            HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>( null, httpHeaders );

            obj = restTemplate.execute( postUrl, HttpMethod.POST, restTemplate.httpEntityCallback( httpEntity ), response -> {
                if ( response.getRawStatusCode() != 200 ) {
                    throw new BaseException( "AG接口请求异常" );
                }
                InputStream bodyStream = response.getBody();
                String      text;
                try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                    text = CharStreams.toString( reader );
                }
                return text;
            } );
        } catch ( Exception e ) {
            throw new BaseException( e );
        }
        return obj;
    }

    /**
     * 获取MG token
     */
    public static String getMGToken( GamePlatform gamePlatform ) {
        String                        gameUrl = "https://sts-powerasia.k2net.io/connect/token";
        HttpHeaders                   headers = new HttpHeaders();
        MultiValueMap<String, String> params  = new LinkedMultiValueMap<>();
        params.add( "client_id", gamePlatform.getAgent() );
        params.add( "client_secret", gamePlatform.getMd5() );
        params.add( "grant_type", "client_credentials" );
        HttpMethod method = HttpMethod.POST;
        // 以表单的方式提交
        headers.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>( params, headers );

        ResponseEntity<Map> responseGameResult = restTemplate.exchange( gameUrl, method, requestEntity, Map.class );
        Map                 result             = responseGameResult.getBody();
        log.info( result.get( "access_token" ) );
        return result.get( "access_token" ).toString();
    }

    /**
     * 获取MG游戏地址
     */
    public static String beginGame( String userId, String token, GamePlatform gamePlatform, String kindId ) {
        String url = gamePlatform.getApiUrl() + gamePlatform.getAgent() + "/players/" + userId + "/sessions?agentCode="
                + gamePlatform.getAgent() + "&playerId=" + userId;
        HttpHeaders                   headers = new HttpHeaders();
        MultiValueMap<String, String> params  = new LinkedMultiValueMap<>();
        params.add( "contentCode", kindId );
        params.add( "langCode", "zh-CN" );
        params.add( "platform", "Mobile" );

        HttpMethod method = HttpMethod.POST;
        headers.add( "Authorization", "Bearer " + token );
        // 以表单的方式提交
        headers.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>( params, headers );

        ResponseEntity<Map> responseGameResult = restTemplate.exchange( url, method, requestEntity, Map.class );
        Map                 result             = responseGameResult.getBody();
        log.info( result.get( "gameURL" ) );
        return result.get( "gameURL" ).toString();
    }

    public static void playersMg( String token, String userId, GamePlatform gamePlatform ) {
        String url = gamePlatform.getApiUrl() + gamePlatform.getAgent() + "/players?agentCode=" + gamePlatform.getAgent();
        HttpHeaders                   headers = new HttpHeaders();
        MultiValueMap<String, String> params  = new LinkedMultiValueMap<>();
        params.add( "playerId", userId );

        HttpMethod method = HttpMethod.POST;
        headers.add( "Authorization", "Bearer " + token );
        // 以表单的方式提交
        headers.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>( params, headers );

        ResponseEntity<Map> responseGameResult = restTemplate.exchange( url, method, requestEntity, Map.class );
        if ( "200 OK".equals( responseGameResult.getStatusCode().toString() ) ) {
            Map result = responseGameResult.getBody();
            log.info( result.get( "gameURL" ) );
        }
    }

    //MG用户转账
    public static boolean WalletTransactions( String token, String userId, BigDecimal changeMoney, String type,
                                              GamePlatform gamePlatform ) {
        String url = gamePlatform.getApiUrl() + gamePlatform.getAgent() + "/WalletTransactions" + "?agentCode="
                + gamePlatform.getAgent();
        HttpHeaders                   headers = new HttpHeaders();
        MultiValueMap<String, String> params  = new LinkedMultiValueMap<>();
        params.add( "playerId", userId );
        params.add( "type", type );
        params.add( "amount", changeMoney.toString() );
        HttpMethod method = HttpMethod.POST;
        headers.add( "Authorization", "Bearer " + token );
        // 以表单的方式提交
        headers.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>( params, headers );

        ResponseEntity<Map> responseGameResult = restTemplate.exchange( url, method, requestEntity, Map.class );

        if ( responseGameResult.getStatusCodeValue() == 201 ) {
            Map result = responseGameResult.getBody();
            //			log.info(result);
            //throw new BaseException( "MG转账失败" );
            return result.get( "status" ).equals( "Succeeded" );
        } else {
            //throw new BaseException( "MG转账失败" );
            return false;
        }
    }

    public static BigDecimal getMGBalance( String token, String userId, GamePlatform gamePlatform ) {
        BigDecimal backMoney = BigDecimal.ZERO;
        String url = gamePlatform.getApiUrl() + gamePlatform.getAgent() + "/players/" + userId + "?properties=balance";
        HttpHeaders                   headers = new HttpHeaders();
        MultiValueMap<String, String> params  = new LinkedMultiValueMap<>();
        HttpMethod                    method  = HttpMethod.GET;
        headers.add( "Authorization", "Bearer " + token );
        // 以表单的方式提交
        headers.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>( params, headers );

        ResponseEntity<Map> responseGameResult = restTemplate.exchange( url, method, requestEntity, Map.class );
        if ( responseGameResult.getStatusCodeValue() == 200 ) {
            Map result    = responseGameResult.getBody();
            Map resultMap = ( Map ) result.get( "balance" );
            backMoney = new BigDecimal( resultMap.get( "total" ).toString() );
        }
        return backMoney;
    }

    public static void main( String[] args ) {
        String url = "http://127.0.0.1:42002/test/test";
        RestTemplate restTemp = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept( new ArrayList<>() );
        httpHeaders.setConnection( new ArrayList<>() );
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>( null, httpHeaders );
        String obj = restTemp.execute( url, HttpMethod.GET, restTemp.httpEntityCallback( httpEntity ), response -> {
            InputStream bodyStream = response.getBody();
            String      text;
            try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                text = CharStreams.toString( reader );
            }
            return text;
        } );
        System.out.println(obj);
        System.out.println(get( url ));
    }
}
