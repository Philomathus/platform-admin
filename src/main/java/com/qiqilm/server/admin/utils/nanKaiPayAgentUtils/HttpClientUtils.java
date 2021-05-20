package com.qiqilm.server.admin.utils.nanKaiPayAgentUtils;

import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.qiqilm.server.admin.utils.JsonUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import com.alibaba.fastjson.JSONObject;

import java.security.cert.CertificateException;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;


/*
 * 测试类(比较完善)
 * 测试Https接口 post
 *https接口的调用与http有些不同
 *  * apache官网可以下载最新的jar包和demo
 * http://hc.apache.org/downloads.cgi
 */
public class HttpClientUtils extends DefaultHttpClient {

    private static String inputCharset = "UTF-8";


    public HttpClientUtils() throws Exception {
        super();
        SSLContext ctx = SSLContext.getInstance("TLS");
        X509TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        ctx.init(null, new TrustManager[]{tm}, null);
        SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        ClientConnectionManager ccm = this.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", 443, ssf));
    }


    //secret为MD5密钥,pubKey为公钥,priKey为私钥
    public static String doPost(String url, String merchantNo, Map<String, Object> data, String secret, String pubKey, String priKey) {
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try {
            httpClient = new HttpClientUtils();
            httpPost = new HttpPost(url);
            //设置参数

            String sign = SignUtils.getSign(data, secret);
            String bt_cipher = SecurityUtils.encrypt(JsonUtil.object2Json(data), pubKey);
            JSONObject postData = new JSONObject();
            postData.put("merId", MD5Util.encode(merchantNo));
            postData.put("signData", sign);
            postData.put("encryptData", bt_cipher);

            StringEntity entity = new StringEntity(postData.toString(), inputCharset);
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, inputCharset);
                }
            }
            if (result != null) {
                return SecurityUtils.decrypt(result, priKey);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }


    public static String doPost(String url, Map<String, String> map, String charset) {
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try {
            httpClient = new HttpClientUtils();
            httpPost = new HttpPost(url);
            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> elem = (Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }


    public List<NameValuePair> genEncryptData(String data) throws Exception {
        if (data == null) {
            return null;
        }
        List<NameValuePair> formparams = new ArrayList();
        formparams.add(new BasicNameValuePair("encryptData", data));
        return formparams;
    }


}
