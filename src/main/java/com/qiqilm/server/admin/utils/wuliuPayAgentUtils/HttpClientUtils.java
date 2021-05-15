//package com.qiqilm.server.admin.utils.wuliuPayAgentUtils;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.RandomAccessFile;
//import java.io.UnsupportedEncodingException;
//import java.net.HttpURLConnection;
//import java.net.Socket;
//import java.net.URL;
//import java.net.URLConnection;
//import java.net.URLEncoder;
//import java.net.UnknownHostException;
//import java.nio.charset.Charset;
//import java.security.KeyManagementException;
//import java.security.KeyStore;
//import java.security.KeyStoreException;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.security.UnrecoverableKeyException;
//import java.security.cert.X509Certificate;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//
//import jodd.http.HttpRequest;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.HttpVersion;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.conn.ClientConnectionManager;
//import org.apache.http.conn.scheme.PlainSocketFactory;
//import org.apache.http.conn.scheme.Scheme;
//import org.apache.http.conn.scheme.SchemeRegistry;
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.conn.ssl.SSLSocketFactory;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
//import org.apache.http.params.BasicHttpParams;
//import org.apache.http.params.CoreConnectionPNames;
//import org.apache.http.params.HttpParams;
//import org.apache.http.params.HttpProtocolParams;
//import org.apache.http.protocol.HTTP;
//import org.apache.http.ssl.SSLContextBuilder;
//import org.apache.http.util.EntityUtils;
//import org.apache.log4j.Logger;
//import org.dom4j.Document;
//import org.dom4j.Element;
//import org.dom4j.io.SAXReader;
//import org.xml.sax.InputSource;
//
//import com.alibaba.fastjson.JSONObject;
//
//import jodd.util.StringPool;
//import jodd.util.StringUtil;
//
//
///**
// * @author rickon
// * @ClassName: HttpClientUtils
// * @date 2018/8/17 6:00
// * @Description:发送http请求工具
// */
//public class HttpClientUtils {
//    protected static Logger logger = Logger.getLogger(HttpClientUtils.class);
//
//    static int socketTimeout = 30000;// 请求超时时间
//    static int connectTimeout = 30000;// 传输超时时间
//
//    //private static HashMap<String, String> result = new HashMap<>();
//
//    public static byte[] httpGet(final String url) {
//        if (url == null || url.length() == 0) {
//            return null;
//        }
//
//        HttpClient httpClient = getNewHttpClient();
//        HttpGet httpGet = new HttpGet(url);
//
//        try {
//            HttpResponse resp = httpClient.execute(httpGet);
//            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//                return null;
//            }
//
//            return EntityUtils.toByteArray(resp.getEntity());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public static HashMap<String, String> doPostByFormByHttps(String url, Map<String, Object> data) {
//        HashMap<String, String> result = new HashMap<>();
//        HttpRequest request = HttpRequest.post(url).trustAllCerts(true);
//        request.timeout(20 * 1000);
//        jodd.http.HttpResponse response = request.form(data).send();
//        try {
//            result.put("data", String.valueOf(response.bodyText()));
//            result.put("status", "200");
//        } catch (Exception e) {
//            logger.info(ExceptionUtils.getStackTrace(e));
//            result.put("data", ExceptionUtils.getMessage(e));
//            result.put("status", "500");
//        }
//        return result;
//    }
//
//    /**
//     * 提交表单到第三方
//     *
//     * @param url  第三方网关
//     * @param data 表单数据
//     * @return
//     */
//    public static HashMap<String, String> doPostByForm(String url, Map<String, Object> data) {
//        HashMap<String, String> result = new HashMap<>();
//        URL netUrl = null;
//        HttpURLConnection con = null;
//        StringBuffer sb = new StringBuffer();
//        String code = "";
//        if (data != null) {
//            for (Entry<String, Object> e : data.entrySet()) {
//                sb.append(e.getKey());
//                sb.append("=");
//                sb.append(e.getValue());
//                sb.append("&");
//            }
//            sb = new StringBuffer(sb.substring(0, sb.length() - 1));
//        }
//        try {
//            netUrl = new URL(url);
//            con = (HttpURLConnection) netUrl.openConnection();
//            con.setReadTimeout(120 * 1000);
//            con.setConnectTimeout(120 * 1000);
//            con.setRequestMethod("POST");
//            con.setDoOutput(true);
//            con.setDoInput(true);
//            con.setUseCaches(false);
//            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
//            osw.write(sb.toString());
//            osw.flush();
//            osw.close();
//        } catch (Exception e) {
//            logger.info(ExceptionUtils.getStackTrace(e));
//        } finally {
//            if (con != null) {
//                con.disconnect();
//            }
//        }
//        // 读取返回内容
//        StringBuffer buffer = new StringBuffer();
//        try {
//            // 一定要有返回值，否则无法把请求发送给server端。
//            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
//            code = String.valueOf(con.getResponseCode());
//            String temp;
//            while ((temp = br.readLine()) != null) {
//                buffer.append(temp);
//                buffer.append("\n");
//            }
//            result.put("data", buffer.toString());
//            result.put("status", code);
//        } catch (Exception e) {
//            logger.info(ExceptionUtils.getStackTrace(e));
//            result.put("data", ExceptionUtils.getMessage(e));
//            result.put("status", "500");
//        }
//        return result;
//    }
//
//    /**
//     * 提交表单到第三方
//     *
//     * @param url  第三方网关
//     * @param data 表单数据
//     * @return
//     */
//    public static HashMap<String, String> doPostByForm(String url, Map<String, Object> data,String userAgent) {
//        HashMap<String, String> result = new HashMap<>();
//        URL netUrl = null;
//        HttpURLConnection con = null;
//        StringBuffer sb = new StringBuffer();
//        String code = "";
//        if (data != null) {
//            for (Entry<String, Object> e : data.entrySet()) {
//                sb.append(e.getKey());
//                sb.append("=");
//                sb.append(e.getValue());
//                sb.append("&");
//            }
//            sb = new StringBuffer(sb.substring(0, sb.length() - 1));
//        }
//        try {
//            netUrl = new URL(url);
//            con = (HttpURLConnection) netUrl.openConnection();
//            con.setReadTimeout(120 * 1000);
//            con.setConnectTimeout(120 * 1000);
//            con.setRequestMethod("POST");
//            con.setDoOutput(true);
//            con.setDoInput(true);
//            con.setUseCaches(false);
//            con.setRequestProperty("User-Agent", userAgent);
//            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
//            osw.write(sb.toString());
//            osw.flush();
//            osw.close();
//        } catch (Exception e) {
//            logger.info(ExceptionUtils.getStackTrace(e));
//        } finally {
//            if (con != null) {
//                con.disconnect();
//            }
//        }
//        // 读取返回内容
//        StringBuffer buffer = new StringBuffer();
//        try {
//            // 一定要有返回值，否则无法把请求发送给server端。
//            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
//            code = String.valueOf(con.getResponseCode());
//            String temp;
//            while ((temp = br.readLine()) != null) {
//                buffer.append(temp);
//                buffer.append("\n");
//            }
//            result.put("data", buffer.toString());
//            result.put("status", code);
//        } catch (Exception e) {
//            logger.info(ExceptionUtils.getStackTrace(e));
//            result.put("data", ExceptionUtils.getMessage(e));
//            result.put("status", "500");
//        }
//        return result;
//    }
//
//
//    /**
//     * 设置请求头为application/json格式的post请求
//     * @param url
//     * @param entity
//     * @return
//     */
//    public static HashMap<String, String> httpPost(String url, String entity) {
//        HashMap<String, String> result = new HashMap<>(16);
//        String code;
//        if (url == null || url.length() == 0) {
//            return null;
//        }
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
//        HttpPost httpPost = new HttpPost(url);
//
//        try {
//            httpPost.setConfig(requestConfig);
//            httpPost.setEntity(new StringEntity(entity, "utf-8"));
//            httpPost.setHeader("Content-type", "application/json");
//            HttpResponse resp = httpClient.execute(httpPost);
//            // 读取返回内容
//            StringBuffer buffer = new StringBuffer();
//            // 一定要有返回值，否则无法把请求发送给server端。
//            BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(), "UTF-8"));
//            code = String.valueOf(resp.getStatusLine().getStatusCode());
//            String temp;
//            while ((temp = br.readLine()) != null) {
//                buffer.append(temp);
//                buffer.append("\n");
//            }
//            result.put("data", buffer.toString());
//            result.put("httpStatus", code);
//        } catch (Exception e) {
//            logger.info(ExceptionUtils.getStackTrace(e));
//            result.put("data", ExceptionUtils.getMessage(e));
//            result.put("httpStatus", "500");
//        }
//        return result;
//    }
//
//    public static byte[] httpsPost(String url, String entity) {
//        if (url == null || url.length() == 0) {
//            return null;
//        }
//        HttpClient httpClient = getNewHttpClient();
//        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
//        HttpPost httpPost = new HttpPost(url);
//
//        try {
//            httpPost.setConfig(requestConfig);
//            httpPost.setEntity(new StringEntity(entity, "utf-8"));
//            httpPost.setHeader("Accept", "application/json");
//            httpPost.setHeader("Content-type", "application/json");
//            HttpResponse resp = httpClient.execute(httpPost);
//            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//                return null;
//            }
//            return EntityUtils.toByteArray(resp.getEntity());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public static byte[] httpsPost(String url, String entity, int outTime) {
//        if (url == null || url.length() == 0) {
//            return null;
//        }
//        HttpClient httpClient = getNewHttpClient();
//        RequestConfig requestConfig;
//        if (outTime != 0) {
//            requestConfig = RequestConfig.custom().setSocketTimeout(outTime * 1000).setConnectTimeout(outTime * 1000).build();
//        } else {
//            requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
//
//        }
//        HttpPost httpPost = new HttpPost(url);
//        try {
//            httpPost.setConfig(requestConfig);
//            httpPost.setEntity(new StringEntity(entity, "utf-8"));
//            httpPost.setHeader("Accept", "application/json");
//            httpPost.setHeader("Content-type", "application/json");
//            HttpResponse resp = httpClient.execute(httpPost);
//            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//                return null;
//            }
//            return EntityUtils.toByteArray(resp.getEntity());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private static class SSLSocketFactoryEx extends SSLSocketFactory {
//
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//
//        public SSLSocketFactoryEx(KeyStore truststore)
//                throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
//            super(truststore);
//
//            TrustManager tm = new X509TrustManager() {
//
//                @Override
//                public X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//
//                @Override
//                public void checkClientTrusted(X509Certificate[] chain, String authType)
//                        throws java.security.cert.CertificateException {
//                }
//
//                @Override
//                public void checkServerTrusted(X509Certificate[] chain, String authType)
//                        throws java.security.cert.CertificateException {
//                }
//            };
//
//            sslContext.init(null, new TrustManager[]{tm}, null);
//        }
//
//        @Override
//        public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
//                throws IOException, UnknownHostException {
//            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
//        }
//
//        @Override
//        public Socket createSocket() throws IOException {
//            return sslContext.getSocketFactory().createSocket();
//        }
//    }
//
//    @SuppressWarnings("deprecation")
//    private static HttpClient getNewHttpClient() {
//        try {
//            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            trustStore.load(null, null);
//
//            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
//            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//            HttpParams params = new BasicHttpParams();
//            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
//
//            SchemeRegistry registry = new SchemeRegistry();
//            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//            registry.register(new Scheme("https", sf, 443));
//
//            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
//
//            return new DefaultHttpClient(ccm, params);
//        } catch (Exception e) {
//            return new DefaultHttpClient();
//        }
//    }
//
//    public static byte[] readFromFile(String fileName, int offset, int len) {
//        if (fileName == null) {
//            return null;
//        }
//
//        File file = new File(fileName);
//        if (!file.exists()) {
//            return null;
//        }
//
//        if (len == -1) {
//            len = (int) file.length();
//        }
//
//        if (offset < 0) {
//            return null;
//        }
//        if (len <= 0) {
//            return null;
//        }
//        if (offset + len > (int) file.length()) {
//            return null;
//        }
//
//        byte[] b = null;
//        try {
//            RandomAccessFile in = new RandomAccessFile(fileName, "r");
//            b = new byte[len];
//            in.seek(offset);
//            in.readFully(b);
//            in.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return b;
//    }
//
//    public static String sha1(String str) {
//        if (str == null || str.length() == 0) {
//            return null;
//        }
//
//        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
//
//        try {
//            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
//            mdTemp.update(str.getBytes());
//
//            byte[] md = mdTemp.digest();
//            int j = md.length;
//            char buf[] = new char[j * 2];
//            int k = 0;
//            for (int i = 0; i < j; i++) {
//                byte byte0 = md[i];
//                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
//                buf[k++] = hexDigits[byte0 & 0xf];
//            }
//            return new String(buf);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public static List<String> stringsToList(final String[] src) {
//        if (src == null || src.length == 0) {
//            return null;
//        }
//        final List<String> result = new ArrayList<String>();
//        for (int i = 0; i < src.length; i++) {
//            result.add(src[i]);
//        }
//        return result;
//    }
//
//    public static byte[] httpPostSSL(String url, String entity, String corpId, String keyPath) {
//        if (url == null || url.length() == 0) {
//            return null;
//        }
//        HttpClient httpClient = getNewHttpClientSSL(corpId, keyPath);
//        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);// 连接时间
//        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);// 数据传输时间
//        HttpPost httpPost = new HttpPost(url);
//
//        try {
//            httpPost.setEntity(new StringEntity(entity, "utf-8"));
//            httpPost.setHeader("Accept", "application/json");
//            httpPost.setHeader("Content-type", "application/json");
//
//            HttpResponse resp = httpClient.execute(httpPost);
//            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//                return null;
//            }
//
//            return EntityUtils.toByteArray(resp.getEntity());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * 证书验证
//     *
//     * @return
//     */
//    @SuppressWarnings("deprecation")
//    private static HttpClient getNewHttpClientSSL(String corpId, String keyPath) {
//        try {
//            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            FileInputStream instream = new FileInputStream(new File(keyPath));
//            // trustStore.load(instream, corpId.toCharArray());
//            try {
//                // 指定PKCS12的密码(商户ID)
//                trustStore.load(instream, corpId.toCharArray());
//            } finally {
//                instream.close();
//            }
//            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
//            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//            HttpParams params = new BasicHttpParams();
//            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
//
//            SchemeRegistry registry = new SchemeRegistry();
//            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//            registry.register(new Scheme("https", sf, 443));
//
//            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
//
//            return new DefaultHttpClient(ccm, params);
//        } catch (Exception e) {
//            return new DefaultHttpClient();
//        }
//    }
//
//
//    /**
//     * 使用SOAP1.1发送消息
//     *
//     * @param postUrl
//     * @param soapXml
//     * @param soapAction
//     * @return
//     */
//    public static String doPostSoap(String postUrl, String soapXml,
//                                    String soapAction, int sTimeout, int cTimeout) {
//        String retStr = "";
//        // 创建HttpClientBuilder
//        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
//        // HttpClient
//        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
//        HttpPost httpPost = new HttpPost(postUrl);
//        //  设置请求和传输超时时间
//        if (socketTimeout != 0 && connectTimeout != 0) {
//            RequestConfig requestConfig = RequestConfig.custom()
//                    .setSocketTimeout(sTimeout * 1000)
//                    .setConnectTimeout(cTimeout * 1000).build();
//            httpPost.setConfig(requestConfig);
//        } else {
//            RequestConfig requestConfig = RequestConfig.custom()
//                    .setSocketTimeout(socketTimeout)
//                    .setConnectTimeout(connectTimeout).build();
//            httpPost.setConfig(requestConfig);
//        }
//        try {
//            httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
//            httpPost.setHeader("SOAPAction", soapAction);
//            StringEntity data = new StringEntity(soapXml, Charset.forName("UTF-8"));
//            httpPost.setEntity(data);
//            CloseableHttpResponse response = closeableHttpClient
//                    .execute(httpPost);
//            HttpEntity httpEntity = response.getEntity();
//            if (httpEntity != null) {
//                // 打印响应内容
//                retStr = EntityUtils.toString(httpEntity, "UTF-8");
////                logger.info("response:" + retStr);
//            }
//            // 释放资源
//            closeableHttpClient.close();
//        } catch (Exception e) {
//            logger.error("exception in doPostSoap1_1", e);
//            logger.info("*****************请求报错Exception：" + ExceptionUtils.getStackTrace(e));
//        }
//        return retStr;
//    }
//
//    /**
//     * post请求
//     *
//     * @param url  请求地址
//     * @param data 请求参数
//     * @return 返回body
//     */
//    public static HashMap<String, String> Jsonpost(String url, String data) {
//        HashMap<String, String> map = new HashMap<>();
//        CloseableHttpClient httpclient = buildHttpClient(url.startsWith("https"));
//        HttpPost httppost = new HttpPost(url);
//        String body = null;
//        try {
//            StringEntity reqEntity = new StringEntity(data, "UTF-8");
//            // 设置类型
//            reqEntity.setContentType("application/json");
//            reqEntity.setContentEncoding("UTF-8");
//            // 设置请求的数据
//            httppost.setEntity(reqEntity);
//            // 执行
//            HttpResponse httpresponse = httpclient.execute(httppost);
//            HttpEntity entity = httpresponse.getEntity();
//            int code = httpresponse.getStatusLine().getStatusCode();
//            body = EntityUtils.toString(entity);
//            map.put("data", body);
//            map.put("status", String.valueOf(code));
//            return map;
//        } catch (IOException e) {
//            e.printStackTrace();
//            map.put("status", "500");
//            map.put("data", ExceptionUtils.getMessage(e));
//        }
//        return map;
//    }
//
//    private static CloseableHttpClient buildHttpClient(boolean isHttps) {
//        if (!isHttps) {
//            return HttpClients.createDefault();
//        } else {
//            return createSSLClient();
//        }
//    }
//
//    private static CloseableHttpClient createSSLClient() {
//        try {
//            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
//                    null, (chain, authType) -> true).build();
//            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
//                    sslContext,
//                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
//        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
//            e.printStackTrace();
//        }
//        return HttpClients.createDefault();
//    }
//
//    public static HashMap<String, String> doPostByJoddByHttps(String url, Map<String, Object> data) {
//        HashMap<String, String> map = new HashMap<>();
//        CloseableHttpClient httpclient = buildHttpClient(url.startsWith("https"));
//        HttpPost httppost = new HttpPost(url);
//        String body = null;
//
//        StringBuffer sb = new StringBuffer();
//        String requestParams = null;
//        //是否对请求参数进行URLEncode编码 默认成不进行
//        Boolean isURLEncode = false;
//        if(data.containsKey("isURLEncode") && data.get("isURLEncode") != null){
//            isURLEncode = (Boolean)data.remove("isURLEncode");
//        }
//        if (data != null) {
//            for (Entry<String, Object> e : data.entrySet()) {
//                sb.append(e.getKey());
//                sb.append("=");
//                if(isURLEncode != null && isURLEncode){
//                    try {
//                        sb.append(URLEncoder.encode(e.getValue().toString(),"UTF-8"));
//                    } catch (UnsupportedEncodingException e1) {
//                        e1.printStackTrace();
//                    }
//                }else{
//                    sb.append(e.getValue());
//                }
//                sb.append("&");
//            }
//            requestParams = sb.substring(0, sb.length() - 1);
//        }
//
//        try {
//            StringEntity reqEntity = new StringEntity(requestParams, "UTF-8");
//            // 设置类型
//            reqEntity.setContentType("application/x-www-form-urlencoded");
//            reqEntity.setContentEncoding("UTF-8");
//            // 设置请求的数据
//            httppost.setEntity(reqEntity);
//            // 执行
//            HttpResponse httpresponse = httpclient.execute(httppost);
//            HttpEntity entity = httpresponse.getEntity();
//            int code = httpresponse.getStatusLine().getStatusCode();
//            body = EntityUtils.toString(entity);
//            if(data.get("isEncoded") != null && (Boolean)data.get("isEncoded")){
//                body = StringUtil.convertCharset(body, StringPool.ISO_8859_1, "UTF-8");
//            }
//            map.put("data", body);
//            map.put("status", String.valueOf(code));
//            return map;
//        } catch (IOException e) {
//            e.printStackTrace();
//            map.put("data", ExceptionUtils.getMessage(e));
//            map.put("httpStatus", "500");
//        }
//        return map;
//    }
//
//    public static boolean isContainChinese(String str) {
//        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
//        Matcher m = p.matcher(str);
//        if (m.find()) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * http参数xml格式请求
//     * @param url
//     * @param map
//     * @return
//     * @throws Exception
//     */
//    public static Map<String, String> httpPostXmlX(String url, Map map) throws Exception {
//        HttpPost httpPost = new HttpPost(url);
//        CloseableHttpResponse resp = null;
//        CloseableHttpClient client = null;
//        StringEntity entityParams;
//        entityParams = new StringEntity(parseXML(map), "utf-8");
//        httpPost.setEntity(entityParams);
//        client = HttpClients.createDefault();
//        resp = client.execute(httpPost);
//        if (resp != null && resp.getEntity() != null) {
//            Map<String, String> resultMap = toMap(EntityUtils.toByteArray(resp.getEntity()), "utf-8");
//            return resultMap;
//        }
//        return null;
//    }
//
//    /**
//     * http xml 格式请求第三方接口 by kelly
//     * @param url
//     * @param map
//     * @return
//     * @throws Exception
//     */
//    public static Map<String, String> httpPostXml(String url, Map map) throws Exception {
//        HashMap<String, String> mapResult = new HashMap<>();
//        HttpPost httpPost = new HttpPost(url);
//        CloseableHttpResponse resp = null;
//        CloseableHttpClient client = null;
//        StringEntity entityParams;
//        String resResult = null;
//        try {
//            entityParams = new StringEntity(parseXML(map), "utf-8");
//            httpPost.setEntity(entityParams);
//            client = HttpClients.createDefault();
//            resp = client.execute(httpPost);
//            int code = resp.getStatusLine().getStatusCode();
//            if (resp != null && resp.getEntity() != null) {
//                resResult = JSONObject.toJSONString(toMap(EntityUtils.toByteArray(resp.getEntity()), "utf-8"));
//            }
//            mapResult.put("data", resResult);
//            mapResult.put("status", String.valueOf(code));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            mapResult.put("status", "500");
//            mapResult.put("data", ExceptionUtils.getMessage(e));
//        }
//        return mapResult;
//    }
//
//    /**
//     * https xml 格式请求第三方接口kelly
//     *
//     * @param map,url
//     * @return
//     */
//    public static Map<String, String> httpSPostXml(String url, Map map) throws Exception {
//        HashMap<String, String> mapResult = new HashMap<>();
//        HttpPost httpPost = new HttpPost(url);
//        CloseableHttpResponse resp = null;
//        CloseableHttpClient client = null;
//        StringEntity entityParams;
//        String resResult = null;
//        try {
//            entityParams = new StringEntity(parseXML(map), "utf-8");
//            httpPost.setEntity(entityParams);
//            client = HttpClients.createDefault();
//            //resp = client.execute(httpPost);
//            resp = (CloseableHttpResponse) HttpUtil.postToServer(parseXML(map), url);
//            int code = resp.getStatusLine().getStatusCode();
//            if (resp != null && resp.getEntity() != null) {
//                resResult = JSONObject.toJSONString(toMap(EntityUtils.toByteArray(resp.getEntity()), "utf-8"));
//            }
//            mapResult.put("data", resResult);
//            mapResult.put("status", String.valueOf(code));
//        } catch (Exception e) {
//            e.printStackTrace();
//            mapResult.put("status", "500");
//            mapResult.put("data", ExceptionUtils.getMessage(e));
//        }
//        return null;
//    }
//
//
//    /**
//     * 转XMLmap
//     *
//     * @param xmlBytes
//     * @param charset
//     * @return
//     * @throws Exception
//     * @author
//     */
//    public static Map<String, String> toMap(byte[] xmlBytes, String charset) throws Exception {
//        SAXReader reader = new SAXReader(false);
//        InputSource source = new InputSource(new ByteArrayInputStream(xmlBytes));
//        source.setEncoding(charset);
//        Document doc = reader.read(source);
//        Map<String, String> params = toMap(doc.getRootElement());
//        return params;
//    }
//
//    /**
//     * 转MAP
//     *
//     * @param element
//     * @return
//     * @author
//     */
//    public static Map<String, String> toMap(Element element) {
//        Map<String, String> rest = new HashMap<String, String>();
//        List<Element> els = element.elements();
//        for (Element el : els) {
//            rest.put(el.getName().toLowerCase(), el.getTextTrim());
//        }
//        return rest;
//    }
//
//    public static String parseXML(Map<String, String> parameters) {
//        StringBuffer sb = new StringBuffer();
//        sb.append("<xml>");
//        Set es = parameters.entrySet();
//        Iterator it = es.iterator();
//        while (it.hasNext()) {
//            Map.Entry entry = (Map.Entry) it.next();
//            String k = (String) entry.getKey();
//            String v = (String) entry.getValue();
//            if (StringUtils.isNotBlank(v)) {
//                sb.append("<" + k + ">" + parameters.get(k) + "</" + k + ">\n");
//            }
//        }
//        sb.append("</xml>");
//        return sb.toString();
//    }
//}
//
