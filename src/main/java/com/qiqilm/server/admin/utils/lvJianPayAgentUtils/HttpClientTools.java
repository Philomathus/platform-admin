package com.qiqilm.server.admin.utils.lvJianPayAgentUtils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Http网络请求工具类
 */
@Log4j2
public class HttpClientTools {

    private static final String HASH_MD5 = "MD5";

    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f'};

    /** 字符串MD5加密，结果大写**/
    public static String MD5(String sourceStr) {
        return encode2MD5(sourceStr,StandardCharsets.UTF_8).toUpperCase();
    }
    /** 字符串MD5加密，结果小写**/
    public static String md5(String sourceStr) {
        return encode2MD5(sourceStr,StandardCharsets.UTF_8).toLowerCase();
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
            log.error( e.getMessage(), e );
            log.info("模拟form发送 POST 请求出现异常", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result.toString();
    }

    public static String httpSendPostForm(String url,Map<String,String> params) throws IOException {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type","application/x-www-form-urlencoded");
//        header.put("Content-Type","application/json;charset=utf-8");
        return httpSendPostForm(url,params,header);
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

    public static String getSignStr(Map<String,String> map){
        String signStr = "";
        try {
            SortedMap<String, Object> sortedMap = new TreeMap<>();
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

}

