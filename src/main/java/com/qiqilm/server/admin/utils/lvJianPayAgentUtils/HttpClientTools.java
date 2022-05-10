package com.qiqilm.server.admin.utils.lvJianPayAgentUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.codec.digest.DigestUtils;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Set;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.io.*;
import java.util.Map.Entry;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import com.alibaba.fastjson.JSON;

/**
 * Http网络请求工具类
 */
public class HttpClientTools {

    public static final Logger logger = LoggerFactory.getLogger(HttpClientTools.class);

    //0的ASCII�?
    private static final int ASCII_0=48;
    //9的ASCII�?
    private static final int ASCII_9=57;
    //A的ASCII�?
    private static final int ASCII_A=65;
    //F的ASCII�?
    private static final int ASCII_F=70;
    //a的ASCII�?
    private static final int ASCII_a=97;
    //f的ASCII�?
    private static final int ASCII_f=102;

    private static final String HASH_MD5 = "MD5";

    /** 签名属性名 sign **/
    private static final String SIGN_KEY = "sign";

    /** 密钥属性名key**/
    private static final String SECRET_KEY = "key";

    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f'};

    /** 字符串MD5加密，结果大写**/
    public static String MD5(String sourceStr) {
        return encode2MD5(sourceStr,StandardCharsets.UTF_8).toUpperCase();
    }
    /** 字符串MD5加密，结果小写**/
    public static String md5(String sourceStr) {
        return encode2MD5(sourceStr,StandardCharsets.UTF_8).toLowerCase();
    }

    public static String CHARTSET_UTF_8 = "UTF-8";
    // 设置连接超时时间，单位毫秒
    private static int connectTimeout = 10000;
    // 设置从connectManager获取Connection超时时间，单位毫秒
    private static int connectionRequestTimeout = 10000;
    // 请求获取数据的超时时间，单位毫秒
    private static int setSocketTimeout = 10000;
    private static final int connTimeOut = 10000;

    private static ConnectionKeepAliveStrategy myStrategy = null;
    static {
        myStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if ((value != null) && ("timeout".equalsIgnoreCase(param))) {
                        return Long.parseLong(value) * 1000L;
                    }
                }
                return connectTimeout;
            }
        };
    }

    private static CloseableHttpClient httpclient = HttpClientBuilder.create().setMaxConnTotal(1000).setMaxConnPerRoute(15).setKeepAliveStrategy(myStrategy).build();

    /**
     * @描述:   基础网络请求类http client post请求
     * @param   url  请求url
     * @param   valuePairs List<BasicNameValuePair>
     * @param   charset 字符集
     * @throws  SocketTimeoutException
     * @throws  Exception
     */
    public static String httpClientSendPost(String url, List<BasicNameValuePair> valuePairs, String charset)throws SocketTimeoutException, Exception {
        CloseableHttpResponse response = null;
        HttpPost httpPost = null;
        String respContent = null;
        try {
            httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout)
                    .setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(setSocketTimeout).build();
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(new UrlEncodedFormEntity(valuePairs, charset));
            response = httpclient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity he = response.getEntity();
                respContent = EntityUtils.toString(he, charset);
            } else {
                throw new SocketTimeoutException("httpClientPost连接异常["+ response.getStatusLine().getStatusCode() +"]");
            }
        } catch (Exception e) {
            logger.error("基础网络请求异常{}", url, e);
        } finally {
            if (response != null) {
                try {
                    response.getEntity().getContent().close();
                    response.close();
                } catch (Exception e) {
                    logger.error("关闭连接异常",e);
                }
            }
            if (httpPost != null) {
                try {
                    httpPost.releaseConnection();
                } catch (Exception e) {
                    logger.error("释放连接异常",e);
                }
            }
        }
        return respContent;
    }
    /**
     * @描述:基础网络请求类http client post请求
     * @param url 请求url
     * @param reqParam json字符串
     * @param charset 字符集
     * @throws SocketTimeoutException
     * @throws Exception
     */
    public static String baseHttpSendPost(String url, String reqParam, String charset) throws SocketTimeoutException, Exception {
        CloseableHttpResponse response = null;
        HttpPost httpPost = null;
        String respContent = null;
        try {
            httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout)
                    .setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(Integer.valueOf(setSocketTimeout)).build();
            httpPost.setConfig(requestConfig);
            StringEntity postParams = new StringEntity(reqParam, charset);
            postParams.setContentEncoding(charset);
            if(StringUtils.isEmpty(charset)){
                charset = CHARTSET_UTF_8;
            }
            postParams.setContentType("application/json;charset="+charset);
            httpPost.setEntity(postParams);
            logger.error("基础网络请求网关url{},入参:{}", url, reqParam);
            response = httpclient.execute(httpPost);
            int code=response.getStatusLine().getStatusCode();
            logger.info("访问code:"+code);
            if (code == 200) {
                HttpEntity he = response.getEntity();
                respContent = EntityUtils.toString(he, charset);
            } else {
                throw new SocketTimeoutException("基础网络链接非200异常{}" + url);
            }
        } catch (ConnectTimeoutException e){
            logger.error("基础网络请求链接超时异常{},参数{}", url, reqParam);
        } catch (SocketTimeoutException e){
            logger.error("基础网络请求发送超时异常{},参数{}", url, reqParam);
        } catch (Exception e) {
            logger.error("基础网络请求异常{},参数{}", url, reqParam, e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    logger.error("关闭response链接异常", e);
                }
            }
            if (httpPost != null) {
                try {
                    httpPost.releaseConnection();
                } catch (Exception e) {
                    logger.error("关闭httpPost链接异常", e);
                }
            }
            logger.info("关闭链接OK");
        }
        return respContent;
    }
    /**
     * @描述:模拟form发送POST方法的请求
     * @描述:请求参数应该是 name1=value1&name2=value2 的形式
     */
    public static String httpSendPostForm(String url, Map<String, String> params,Map<String, String> header) throws IOException {
        HttpURLConnection conn = null;
        StringBuilder result = new StringBuilder();
        try {
            StringBuffer param = new StringBuffer();
            int num = 0;
            for (String key : params.keySet()) {
                if (num == 0) {
                    param.append(key).append("=").append(params.get(key));
                } else {
                    param.append("&").append(key).append("=").append(params.get(key));
                }
                num++;
            }
//            logger.info("添加{}个参数", num);
            URL realUrl = new URL(url);
            conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("charset", "UTF-8");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            for(String key:header.keySet()){
                conn.setRequestProperty(key,header.get(key));
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(0);
            conn.setReadTimeout(0);
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            for (String line = null; (line = br.readLine()) != null;) {
                result.append((new StringBuilder()).append(line));
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("模拟form发送 POST 请求出现异常", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result.toString();
    }
    /**
     * @描述:PostMethod请求 application/x-www-form-urlencoded
     */
    public static  String POSTReturnString(String url, JSONObject jsonObject,String charSet) {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url);
        String result = "";
        try {
//            client.getHttpConnectionManager().getParams().setConnectionTimeout(10000);//            链接超时 10S
//            client.getHttpConnectionManager().getParams().setSoTimeout(10000);//            读取超时 10S
            method.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=" + charSet);
            for (String key :jsonObject.keySet()) {
                method.setParameter(key, jsonObject.getString(key));
            }
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                logger.info("请求响应失败" + statusCode);
            } else {
                result = method.getResponseBodyAsString();
                logger.info("POST请求响应:{}",result);
            }
        } catch (HttpException e) {
            logger.error("POST请求异常",e);
        } catch (IOException e) {
            logger.error("POST请求IO异常",e);
        }finally {
            method.releaseConnection(); // 释放连接
        }
        return result;
    }

    /**
     * @描述: Post发送NameValuePair参数
     * @param Payurl
     * @param param
     * @return
     */

    public static String doPostMethod(String Payurl, NameValuePair[] param) {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(Payurl);
        String result = "";
        try {
            client.getHttpConnectionManager().getParams().setConnectionTimeout(10000);//            链接超时 10S
            client.getHttpConnectionManager().getParams().setSoTimeout(10000);//            读取超时 10S
            method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            method.setRequestBody(param);
            logger.info("doPostMethod请求入参:{}", JSON.toJSONString(param));
            int status = client.executeMethod(method);
            if (status == HttpStatus.SC_OK) {
                result = method.getResponseBodyAsString();
                logger.info("doPostMethod返回参数:{}",result);
            } else {
                logger.info("请求无响应!");
                throw new RuntimeException("请求无响应");
            }
        } catch (Exception ie) {
            logger.error("doPostMethod请求异常!",ie);
        } finally {
            method.releaseConnection(); // 释放连接
        }
        return result;
    }



    //基础网络请求二次包装 CHARTSET_UTF_8
    public static String baseHttpSendPost(String url, JSONObject reqParam) throws Exception {
        return baseHttpSendPost(url, reqParam, CHARTSET_UTF_8);
    }
    //基础网络请求二次包装 CHARTSET_UTF_8
    public static String baseHttpSendPost(String url, String jsonStr) throws Exception {
        return baseHttpSendPost(url, jsonStr, CHARTSET_UTF_8);
    }
    //基础网络请求二次包装 CHARTSET_UTF_8
    public static String baseHttpSendPost(String url, Map<String, String> reqMap) throws Exception {
        return baseHttpSendPost(url, reqMap.toString(), CHARTSET_UTF_8);
    }
    //基础网络请求二次包装 json
    public static String baseHttpSendPost(String url, JSONObject reqParam, String charset) throws Exception {
        return baseHttpSendPost(url,reqParam.toString(),charset);
    }

    public static String httpSendPostForm(String url,Map<String,String> params) throws IOException {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type","application/x-www-form-urlencoded");
//        header.put("Content-Type","application/json;charset=utf-8");
        return httpSendPostForm(url,params,header);
    }


    /**
     * @描述: post发送 Map<String, String> 转 NameValuePair参数
     */
    public static String doPostMethodWithUrlEncode(String postUrl,Map<String, String> parm, String charset) throws Exception {
        try {
            return doPostMethod(postUrl,generatNameValuePair(parm,charset,true));
        } catch (Exception e) {
            logger.error("doPostMethod组装参数异常!",e);
            return "";
        }
    }
    //MAP类型数组转换成NameValuePair类型
    private static NameValuePair[] generatNameValuePair(Map<String, String> properties, String charset, boolean urlEncode) throws Exception {
        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String value = urlEncode? URLEncoder.encode(entry.getValue(), charset):entry.getValue();
            nameValuePair[i++] = new NameValuePair(entry.getKey(),value);
        }
        return nameValuePair;
    }
    /**
     * @描述:发送 Map<String, String> 转 BasicNameValuePair数据 CHARTSET_UTF_8
     */
    public static String sendBasicNameValueData(String url,Map<String, String> pamrs) throws Exception{
        return httpClientSendPost(url, getBasicNameValuePair(pamrs),CHARTSET_UTF_8);
    }
    //MAP类型数组转换成BasicNameValuePair类型
    private static List<BasicNameValuePair> getBasicNameValuePair( Map<String, String> properties) throws Exception {
        List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            list.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
        }
        return list;
    }

    /**
     * 向指定URL发送GET方法的请求
     * @param url 发送请求的URL
     * @param param  请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("发送GET请求出现异常",e);
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    public static String getBase64ByUrl(String imageUrl) throws Exception {
        // new一个URL对象
        URL url = new URL(imageUrl);
        // 打开链接
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 设置请求方式为"GET"
        conn.setRequestMethod("GET");
        // 超时响应时间为5秒
        conn.setConnectTimeout(3 * 1000);
        // 通过输入流获取图片数据
        InputStream inStream = conn.getInputStream();
        // 得到图片的二进制数据，以二进制封装得到数据，具有通用性
        byte[] data = readInputStream(inStream);
        return encode(data);
    }

    private static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        // 创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        // 每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        // 使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            // 用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        // 关闭输入流
        inStream.close();
        // 把outStream里的数据写入内存
        return outStream.toByteArray();
    }

    public static String sendPost(String url, String param) {
        return sendPost(url,param,"");
    }

    public static String sendPost(String url, String param,String contentType) {
        return sendPost(url,param,contentType,new HashMap<>());
    }
    /**
     * 向指定 URL 发送POST方法的请求
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param,String contentType,Map<String,String> header) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            //打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            //设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Charsert", "UTF-8");
            if(StringUtils.isNotEmpty(contentType)){
                conn.setRequestProperty("Content-Type",contentType);
            }
            if(null != header && !header.isEmpty()){
                for(String headerKey:header.keySet()){
                    conn.setRequestProperty(headerKey,header.get(headerKey));
                }
            }
            //发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * @描述:请求&拼接字符串转为JSONObject格式（通常get请求参数转为json）
     */
    public static JSONObject StringToJson(String str){
        JSONObject notifyJson = new JSONObject();
        String[] param = str.split("&");
        for (String content : param) {
            if (content.indexOf("=") > 0) {
                String key = content.substring(0, content.indexOf("="));
                String value = content.substring(content.indexOf("=") + 1);
                notifyJson.put(StringUtils.deleteWhitespace(key), StringUtils.deleteWhitespace(value).replace("\"", ""));
            }else if (content.indexOf(":") > 0) {
                String key = content.substring(0, content.indexOf(":"));
                String value = content.substring(content.indexOf(":") + 1);
                notifyJson.put(StringUtils.deleteWhitespace(key),StringUtils.deleteWhitespace(value).replace("\"", ""));
            }
        }
        return notifyJson;
    }

    public static String sendUrlGet(String url, String param) throws IOException {
        Map<String, String> header = new HashMap<>();
        header.put("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        return sendUrlGet(url,param,header);
    }

    /**
     * 向指定URL发送GET方法的请求
     */
    public static String sendUrlGet(String url, String param,Map<String, String> header) throws IOException {
        BufferedReader in = null;
        try {
            String result = "";
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            for(String key:header.keySet()){
                connection.setRequestProperty(key,header.get(key));
            }
            connection.connect();
            //获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            for (String key : map.keySet()) {
                System.out.println(key+"="+map.get(key));
            }
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (Exception e) {
            logger.error("发送GET请求出现异常",e);
            return null;
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }


    /**
     * @param url String 发送地址
     * @param nameValue String Map 发送数据
     * @return String 返回类型 FAIL 处理失败， EXCEPTION 系统异常 ，其他的为response结果
     * @Title: httpPost
     * @Description: HTTP POST 发送方式
     */
    public static String httpPost (String url, Map<String, String> nameValue) {
        // 构造HttpClient的实例
        HttpClient httpClient = new HttpClient ();
        PostMethod postMethod = new PostMethod (url);
        // 超时时间
        if (connTimeOut != 0) {
            httpClient.getHttpConnectionManager ().getParams ().setConnectionTimeout (connTimeOut);
        }
        if (connTimeOut != 0) {
            httpClient.getHttpConnectionManager ().getParams ().setSoTimeout (connTimeOut);
        }
        // 填入各个表单域的值
        if (null != nameValue) {
            Set<String> keys = nameValue.keySet ();
            for (String key : keys) {
                postMethod.setParameter (key, nameValue.get (key));
            }
        }
        // 设置字符编码
        postMethod.getParams ().setParameter (HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        try {
            // 执行postMethod
            int statusCode = httpClient.executeMethod (postMethod);
            // HttpClient对于要求接受后继服务的请求，象POST和PUT等不能自动处理转发
            // 301或者302
            if (statusCode == org.apache.commons.httpclient.HttpStatus.SC_OK) {
                byte[] responseBody = postMethod.getResponseBody ();
                return new String (responseBody, StandardCharsets.UTF_8);
            } else if (statusCode == org.apache.commons.httpclient.HttpStatus.SC_MOVED_PERMANENTLY || statusCode == org.apache.commons.httpclient.HttpStatus.SC_MOVED_TEMPORARILY) {
                // 需要自己处理转发
                // 从头中取出转向的地址
                Header locationHeader = postMethod.getResponseHeader("location");
                /*if (locationHeader != null) {
                    String location = locationHeader.getValue ();
                    if (count == 0) {// 只运行转发向一次，防止死循环
                        count++;
                        return httpPost (location, nameValue, charSet, connTimeOut, soTimeOut);
                    } else {
                        return "FAIL";
                    }
                } else {
                    return "FAIL";
                }*/
            }
        } catch (HttpException e) {
            e.printStackTrace ();
        } catch (IOException e) {
            e.printStackTrace ();
        } finally {
            postMethod.releaseConnection ();
        }
        return null;
    }

    /**
     * 文件读取缓冲区大小
     */
    private static final int CACHE_SIZE = 1024;

    /**
     * <p>
     * BASE64字符串解码为二进制数据
     * </p>
     *
     * @param base64
     * @return
     * @throws Exception
     */
    public static byte[] decode(String base64) throws Exception {
        return Base64.decode(base64.getBytes());
    }

    /**
     * <p>
     * 二进制数据编码为BASE64字符串
     * </p>
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    public static String encode(byte[] bytes) throws Exception {
        return new String(Base64.encode(bytes));
    }

    /**
     * <p>
     * 将文件编码为BASE64字符串
     * </p>
     * <p>
     * 大文件慎用，可能会导致内存溢出
     * </p>
     *
     * @param filePath
     *            文件绝对路径
     * @return
     * @throws Exception
     */
    public static String encodeFile(String filePath) throws Exception {
        byte[] bytes = fileToByte(filePath);
        return encode(bytes);
    }

    /**
     * <p>
     * BASE64字符串转回文件
     * </p>
     *
     * @param filePath
     *            文件绝对路径
     * @param base64
     *            编码字符串
     * @throws Exception
     */
    public static void decodeToFile(String filePath, String base64)
            throws Exception {
        byte[] bytes = decode(base64);
        byteArrayToFile(bytes, filePath);
    }

    /**
     * <p>
     * 文件转换为二进制数组
     * </p>
     *
     * @param filePath
     *            文件路径
     * @return
     * @throws Exception
     */
    public static byte[] fileToByte(String filePath) throws Exception {
        byte[] data = new byte[0];
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead = 0;
            while ((nRead = in.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }
            out.close();
            in.close();
            data = out.toByteArray();
        }
        return data;
    }

    /**
     * <p>
     * 二进制数据写文件
     * </p>
     *
     * @param bytes
     *            二进制数据
     * @param filePath
     *            文件生成目录
     */
    public static void byteArrayToFile(byte[] bytes, String filePath)
            throws Exception {
        InputStream in = new ByteArrayInputStream(bytes);
        File destFile = new File(filePath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        destFile.createNewFile();
        OutputStream out = new FileOutputStream(destFile);
        byte[] cache = new byte[CACHE_SIZE];
        int nRead = 0;
        while ((nRead = in.read(cache)) != -1) {
            out.write(cache, 0, nRead);
            out.flush();
        }
        out.close();
        in.close();
    }
    public static void main(String[] args) {
        String str = "{\"s\":\"WAP\",\"n\":\"京东官网\",\"id\":\"https://m.jd.com\"}";
        try {
            System.out.println(java.net.URLEncoder.encode(encode(str.getBytes("GBK")),"GBK"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected static String bytesToString(byte[] encrytpByte) {
        String result = "";
        for (Byte bytes : encrytpByte) {
            result += bytes.toString() + " ";
        }
        return result;
    }

    protected static byte[] splitToString(String tmp) {
        String[] strArr = tmp.split(" ");
        int len = strArr.length;
        System.out.println("len=" + len);
        byte[] clone = new byte[len];
        for (int i = 0; i < len; i++) {
            clone[i] = Byte.parseByte(strArr[i]);
        }
        return clone;
    }

    public static String stringToHexString(String strPart) {
        String hexString = "";
        for (int i = 0; i < strPart.length(); i++) {
            int ch = (int) strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString = hexString + strHex;
        }
        return hexString;
    }

    private static String hexString = "0123456789ABCDEF";

    /*
     * 将字符串编码成16进制数字,适用于所有字符（包括中文）
     */
    public static String encode(String str) {
        // 根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        // 将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    /*
     * 将16进制数字解码成字符串,适用于所有字符（包括中文）
     */
    public static String hexToString(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                bytes.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2) {
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
                    .indexOf(bytes.charAt(i + 1))));
        }
        return new String(baos.toByteArray());
    }

    private static byte uniteBytes(byte src0, byte src1) {
        byte byte1 = Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();
        byte1 = (byte) (byte1 << 4);
        byte byte2 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
        byte ret = (byte) (byte1 | byte2);
        return ret;
    }

    public static byte[] hexString2Bytes(String src) {
        byte[] ret = new byte[6];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < 6; ++i) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    /**
     * MD5加base64加密
     * @param parameters
     * @param key
     * @return
     */
    public static String sign64(SortedMap<String, Object> parameters, String key) {
        List<String> sb = new ArrayList<>();
        Set<Entry<String, Object>> es = parameters.entrySet();
        for (Entry<String, Object> entry : es) {
            String k = entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"signMethod".equals(k) && !"signature".equals(k)) {
                sb.add(k + "=" + v);
            }
        }
        byte[] digest = null;
        String str = StringUtils.join(sb, "&") + key;
        System.out.println(str);
        try {
            byte[] data = (str).getBytes(StandardCharsets.UTF_8);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(data);
            digest = messageDigest.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(org.apache.commons.codec.binary.Base64.encodeBase64(digest));
    }

    /**
     * SortedMap转成String
     * @param parameters
     * @return
     */
    public static String sortedToString(SortedMap<String, Object> parameters) {
        List<String> sb = new ArrayList<>();
        Set<Entry<String, Object>> es = parameters.entrySet();
        for (Entry<String, Object> entry : es) {
            String k = entry.getKey();
            Object v = entry.getValue();
            sb.add(k + "=" + v);
        }
        return StringUtils.join(sb, "&");
    }

    /**
     * @描述：按照ascii码排序根据MD5加密
     */
    public static String md5ascii(Map<String,String> sourceObj,String md5Key) {
        try {
            String text = getSignStr(sourceObj,md5Key);
            String sign = md5(text);
            return sign;
        } catch (Exception e) {
            System.out.println("根据字母排序验签异常");
            e.printStackTrace();
            return "";
        }
    }

    public static String getSignStr(Map<String,String> sourceObj,String md5Key) {
        try {
            return getSignStr(sourceObj) + "&key=" + md5Key;
        } catch (Exception e) {
            System.out.println("根据字母排序验签异常");
            e.printStackTrace();
            return "";
        }
    }

    /**
     * @描述:拼接签名串
     */
    public static String getSignStr(JSONObject jsonData){
        String signStr = "";
        try {
            SortedMap<String, Object> sortedMap = new TreeMap<String, Object>();
            for (Object key : jsonData.keySet()) {
                sortedMap.put(key.toString(), jsonData.get(key));
            }
            return getSignStr(sortedMap);
        } catch (Exception e) {
            System.out.println("根据字母排序验签异常");
            e.printStackTrace();
            return signStr;
        }
    }
    public static String getSignStr(Map<String,String> map){
        String signStr = "";
        try {
            SortedMap<String, Object> sortedMap = new TreeMap<String, Object>();
            for (String key : map.keySet()) {
                sortedMap.put(key, map.get(key));
            }
            return getSignStr(sortedMap);
        } catch (Exception e) {
            System.out.println("根据字母排序验签异常");
            e.printStackTrace();
            return signStr;
        }
    }

    public static String getSignStr(SortedMap<String, Object> sortedMap){
        String signStr = "";
        try {
            StringBuilder buffer = new StringBuilder();
            for (String key : sortedMap.keySet()){
                if(!sortedMap.containsKey(key) || sortedMap.get(key) == null || "".equals(sortedMap.get(key))){
                    continue;
                }
                if("sign".equalsIgnoreCase(key) || "signature".equalsIgnoreCase(key) || "signData".equalsIgnoreCase(key)){
                    continue;
                }
                if("amount".equalsIgnoreCase(key)){
                    buffer.append(key).append("=").append(sortedMap.get(key).toString()).append("&");
                    continue;
                }
                buffer.append(key).append("=").append(sortedMap.get(key).toString()).append("&");
            }
            signStr = buffer.toString();
            return signStr.substring(0,signStr.length()-1);
        } catch (Exception e) {
            System.out.println("根据字母排序验签异常");
            e.printStackTrace();
            return signStr;
        }
    }

    /**
     * @描述:参数签名 不包含参数名 参数直接拼接
     */
    public static String getRequestSign (JSONObject jsonPrams, String signKey) throws Exception {
        SortedMap<String, String> sortedMap = new TreeMap<String, String>();
        for (String key : jsonPrams.keySet()) {
            sortedMap.put(key, jsonPrams.getString(key));
        }
        StringBuilder builder = new StringBuilder();
        for (String key : sortedMap.keySet()){
            builder.append(sortedMap.get(key));
        }
        builder.append(signKey);
        String signStr = builder.toString();
        System.out.println("signStr = " + signStr);
        return md5(signStr);
    }

    /**
     * 字符串MD5加密
     * @param sourceStr 加密原始串
     * @param charset 字符集
     * @return String 结果小写
     */
    private static String encode2MD5(String sourceStr, Charset charset) {
        try {
            if(StringUtils.isEmpty(sourceStr)){
                return "";
            }
            byte[] btInput = null;
            if(null == charset){
                btInput = sourceStr.getBytes();
            }else{
                btInput = sourceStr.getBytes(charset);
            }
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance(HASH_MD5);
            // 使用指定的字节更新摘要 获得密文
            byte[] md = mdInst.digest(btInput);
            // 把密文转换成十六进制的字符串形式
            return toHexString(md);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取字符串MD5
     */
    public static String encoding(String text) {
        if( text==null ){
            return null;
        }
        try {
            return encoding( text.getBytes(StandardCharsets.UTF_8) );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取字节数组MD5
     */
    public static String encoding(byte[] bs) {
        String encodingStr = null;
        try {
            MessageDigest mdTemp = MessageDigest.getInstance(HASH_MD5);
            mdTemp.update(bs);
            return toHexString( mdTemp.digest() );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodingStr;
    }
    /**
     * 获取文件内容MD5
     */
    public static String encodingFile(String filePath) throws IOException {
        InputStream fis=null;
        try{
            fis = new FileInputStream(filePath);
            return encoding(fis);
        }catch( Exception ee){
            return null;
        }finally{
            if(fis!=null ){
                fis.close();
            }
        }
    }

    /**
     * 获取输入流MD5
     */
    public static String encoding(InputStream fis) throws Exception{
        byte[] buffer = new byte[1024];
        MessageDigest md5 = MessageDigest.getInstance(HASH_MD5);
        int numRead = 0;
        while ((numRead = fis.read(buffer)) > 0) {
            md5.update(buffer, 0, numRead);
        }
        return toHexString(md5.digest());
    }

    /**
     * 判断是否是合法的MD5
     * @param md5Str
     * @return
     */
    public static boolean validate(String md5Str){
        if(md5Str==null || md5Str.length()!=32 ){
            return false;
        }
        byte[] by = md5Str.getBytes();
        for (byte b : by) {
            int asciiValue = (int) b;
            if (asciiValue < ASCII_0
                    || (asciiValue > ASCII_9 && asciiValue < ASCII_A)
                    || (asciiValue > ASCII_F && asciiValue < ASCII_a)
                    || asciiValue > ASCII_f) {
                return false;
            }
        }
        return true;
    }

    /**
     * 转换为用16进制字符表示的MD5
     * @param b byte[]
     * @return 大写的MD5
     */
    private static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte b1 : b) {
            sb.append(HEX_CHAR[(b1 & 0xf0) >>> 4]);
            sb.append(HEX_CHAR[b1 & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * 计算签名
     * @param jsonObj 要参与签名的json数据
     * @param md5Key  密钥
     * @return 签名
     */
    public static String getSign(JSONObject jsonObj, String md5Key) {
        if (jsonObj == null || jsonObj.isEmpty()) {
            return null;
        }
        String str2Sign = buildParam4Sign(jsonObj, SIGN_KEY, md5Key);
        System.err.println("str2Sign:"+str2Sign);
        return DigestUtils.md5Hex(str2Sign).toUpperCase();
    }
    /**
     * 拼接用于签名的参数
     * @param jsonObj
     * @return
     */
    private static String buildParam4Sign(JSONObject jsonObj, String signKey, String md5Key) {
        Set<String> keySet = jsonObj.keySet();
        StringBuilder param = new StringBuilder(20 * keySet.size());
        String[] keys = keySet.toArray(new String[0]);
        Arrays.sort(keys, String.CASE_INSENSITIVE_ORDER);
        for (String key : keys) {
            // 排除sign
            if (signKey.equals(key)) {
                continue;
            }
            Object value = jsonObj.get(key);
            // 排除值为null的情况
            if (value != null) {
                param.append(key).append("=").append(value).append("&");
            }
        }
        param.append(SECRET_KEY).append("=").append(md5Key);
        return param.toString();
    }
}

