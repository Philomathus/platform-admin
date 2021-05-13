package com.qiqilm.server.admin.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * SHA256withRSA
 *
 * @author 单红宇
 * @date 2019年7月18日
 *
 */
public class RSAUtils {

    private static final Logger logger = LoggerFactory.getLogger(RSAUtils.class);


    // MAX_DECRYPT_BLOCK应等于密钥长度/8（1byte=8bit），所以当密钥位数为2048时，最大解密长度应为256.
    // 128 对应 1024，256对应2048
    private static final int KEYSIZE = 2048;

    // RSA最大加密明文大小
    private static final int MAX_ENCRYPT_BLOCK = 117;

    // RSA最大解密密文大小
    private static final int MAX_DECRYPT_BLOCK = 256;

    // 不仅可以使用DSA算法，同样也可以使用RSA算法做数字签名
    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    public static final String DEFAULT_SEED = "$%^*%^()(ED47d784sde78"; // 默认种子

    /**
     * 目前固定公钥、私钥，有需求再改动
     */
    public static String PUBLIC_KEY_LZ = "***************";
    public static String PRIVATE_KEY_LZ = "*******************************************=";

    public static final String PUBLIC_KEY = "PublicKey";
    public static final String PRIVATE_KEY = "PrivateKey";

    /**
     *
     * 生成密钥
     *
     * @param seed 种子
     *
     * @return 密钥对象
     * @throws Exception
     *
     */

    public static Map<String, Key> initKey(String seed) throws Exception {
        logger.info("生成密钥");
        KeyPairGenerator keygen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom secureRandom = new SecureRandom();
        // 如果指定seed，那么secureRandom结果是一样的，所以生成的公私钥也永远不会变
        secureRandom.setSeed(seed.getBytes());
        // Modulus size must range from 512 to 1024 and be a multiple of 64
        keygen.initialize(KEYSIZE, secureRandom);
        KeyPair keys = keygen.genKeyPair();
        PrivateKey privateKey = keys.getPrivate();
        PublicKey publicKey = keys.getPublic();
        Map<String, Key> map = new HashMap<>(2);
        map.put(PUBLIC_KEY, publicKey);
        map.put(PRIVATE_KEY, privateKey);
        return map;
    }

    /**
     *
     * 生成默认密钥
     *
     *
     * @return 密钥对象
     * @throws Exception
     *
     */

    public static Map<String, Key> initKey() throws Exception {
        return initKey(DEFAULT_SEED);
    }

    /**
     *
     * 取得私钥
     *
     * @param keyMap
     *
     * @return
     * @throws Exception
     *
     */
    public static String getPrivateKey(Map<String, Key> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
//        return encryptBASE64(key.getEncoded()); // base64加密私钥
        return base64ToStr(key.getEncoded()); // base64加密私钥
    }

    private static String base64ToStr(byte[] encoded) {
        return javax.xml.bind.DatatypeConverter.printBase64Binary(encoded);
    }

    /**
     *
     * 取得公钥
     *
     * @param keyMap
     *
     * @return
     * @throws Exception
     *
     */
    public static String getPublicKey(Map<String, Key> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
//        return encryptBASE64(key.getEncoded()); // base64加密公钥
        return base64ToStr(key.getEncoded()); // base64加密公钥
    }

    /**
     *
     * 用私钥对信息进行数字签名
     *
     * @param data       加密数据
     *
     * @param privateKey 私钥-base64加密的
     *
     * @return
     *
     * @throws Exception
     *
     */
    public static String signByPrivateKey(byte[] data, String privateKey) throws Exception {
        logger.info("用私钥对信息进行数字签名");
        byte[] keyBytes = decryptBASE64(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey priKey = factory.generatePrivate(keySpec);// 生成私钥
        // 用私钥对信息进行数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(priKey);
        signature.update(data);
        return encryptBASE64(signature.sign());

    }

    /**
     *
     * BASE64Encoder 加密
     *
     * @param data 要加密的数据
     *
     * @return 加密后的字符串
     *
     */
    private static String encryptBASE64(byte[] data) {
//      BASE64Encoder encoder = new BASE64Encoder();
//      String encode = encoder.encode(data);
//      return encode;
        return new String(Base64.encodeBase64(data));
    }

    private static byte[] decryptBASE64(String data) {
        // BASE64Decoder 每76个字符换行
//      BASE64Decoder decoder = new BASE64Decoder();
//      byte[] buffer = decoder.decodeBuffer(data);
//      return buffer;
        // codec 的 Base64 不换行
        return Base64.decodeBase64(data);
    }

    public static boolean verifyByPublicKey(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = decryptBASE64(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pubKey);
        signature.update(data);
        return signature.verify(decryptBASE64(sign)); // 验证签名
    }

    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws Exception                    加密过程中的异常信息
     */
    public static String encryptByPublicKey(String str, String publicKey)
            throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        // base64编码的公钥
        byte[] keyBytes = decryptBASE64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(keyBytes));
        // RSA加密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        System.out.println(publicKey);
        logger.info("provider: {}" , cipher.getProvider().getClass().getName());
        byte[] data = str.getBytes("UTF-8");
        // 加密时超过117字节就报错。为此采用分段加密的办法来加密
        byte[] enBytes = null;
        for (int i = 0; i < data.length; i += MAX_ENCRYPT_BLOCK) {
            // 注意要使用2的倍数，否则会出现加密后的内容再解密时为乱码
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + MAX_ENCRYPT_BLOCK));
            enBytes = ArrayUtils.addAll(enBytes, doFinal);
        }
        System.out.println(enBytes.length);
        String outStr = encryptBASE64(enBytes);
        return outStr;
    }

    /**
     * RSA私钥加密
     *
     * @param str       加密字符串
     * @param privateKey 公钥
     * @return 密文
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws Exception                    加密过程中的异常信息
     */
    public static String encryptByPrivateKey(String str, String privateKey)
            throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        // base64编码的公钥
        byte[] keyBytes = decryptBASE64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        // RSA加密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, priKey);

        byte[] data = str.getBytes("UTF-8");
        // 加密时超过117字节就报错。为此采用分段加密的办法来加密
        byte[] enBytes = null;
        for (int i = 0; i < data.length; i += MAX_ENCRYPT_BLOCK) {
            // 注意要使用2的倍数，否则会出现加密后的内容再解密时为乱码
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + MAX_ENCRYPT_BLOCK));
            enBytes = ArrayUtils.addAll(enBytes, doFinal);
        }
        String outStr = encryptBASE64(enBytes);
        return outStr;
    }

    /**
     * 读取公钥
     *
     * @param publicKeyPath
     * @return
     */
    public static PublicKey readPublic(String publicKeyPath) {
        if (publicKeyPath != null) {
            try (FileInputStream bais = new FileInputStream(publicKeyPath)) {
                CertificateFactory certificatefactory = CertificateFactory.getInstance("X.509");
                X509Certificate cert = (X509Certificate) certificatefactory.generateCertificate(bais);
                return cert.getPublicKey();
            } catch (CertificateException e) {
                logger.error(e.getMessage(), e);
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 读取私钥
     *
     * @param
     * @return
     */
    public static PrivateKey readPrivate(String privateKeyPath, String privateKeyPwd) {
        if (privateKeyPath == null || privateKeyPwd == null) {
            return null;
        }
        try (InputStream stream = new FileInputStream(new File(privateKeyPath));) {
            // 获取JKS 服务器私有证书的私钥，取得标准的JKS的 KeyStore实例
            KeyStore store = KeyStore.getInstance("JKS");// JKS，二进制格式，同时包含证书和私钥，一般有密码保护；PKCS12，二进制格式，同时包含证书和私钥，一般有密码保护。
            // jks文件密码，根据实际情况修改
            store.load(stream, privateKeyPwd.toCharArray());
            // 获取jks证书别名
            Enumeration<String> en = store.aliases();
            String pName = null;
            while (en.hasMoreElements()) {
                String n = (String) en.nextElement();
                if (store.isKeyEntry(n)) {
                    pName = n;
                }
            }
            // 获取证书的私钥
            PrivateKey key = (PrivateKey) store.getKey(pName, privateKeyPwd.toCharArray());
            return key;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * RSA私钥解密
     *
     * @param encryStr   加密字符串
     * @param privateKey 私钥
     * @return 铭文
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws Exception                 解密过程中的异常信息
     */
    public static String decryptByPrivateKey(String encryStr, String privateKey)
            throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        // base64编码的私钥
        byte[] decoded = decryptBASE64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
        // RSA解密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        System.out.println(privateKey);
        logger.info("provider: {}" , cipher.getProvider().getClass().getName());
        // 64位解码加密后的字符串
        byte[] data = decryptBASE64(encryStr);
        System.out.println(data.length);
        // 解密时超过128字节报错。为此采用分段解密的办法来解密
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i += MAX_DECRYPT_BLOCK) {
            System.out.println(i);
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + MAX_DECRYPT_BLOCK));
            sb.append(new String(doFinal));
        }
        return sb.toString();
    }

    /**
     * RSA公钥解密
     *
     * @param encryStr   加密字符串
     * @return 铭文
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws Exception                 解密过程中的异常信息
     */
    public static String decryptByPublicKey(String encryStr, String publicKey)
            throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException {
        // base64编码的私钥
        byte[] decoded = decryptBASE64(publicKey);
        RSAPublicKey priKey = (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(decoded));
        // RSA解密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, priKey);

        // 64位解码加密后的字符串
        byte[] data = decryptBASE64(encryStr);
        // 解密时超过128字节报错。为此采用分段解密的办法来解密
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i += MAX_DECRYPT_BLOCK) {
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + MAX_DECRYPT_BLOCK));
            sb.append(new String(doFinal));
        }

        return sb.toString();
    }

    /**
     * 加密
     * @param key
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws IOException
     */
    public static String testEncrypt(String key,String data) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException{
        byte[] decode = Base64.decodeBase64(key);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(decode);
        KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey generatePrivate = kf.generatePrivate(pkcs8EncodedKeySpec);
        Cipher ci = Cipher.getInstance(KEY_ALGORITHM);
        ci.init(Cipher.ENCRYPT_MODE, generatePrivate);

        byte[] bytes = data.getBytes();
        int inputLen = bytes.length;
        int offLen = 0;//偏移量
        int i = 0;
        ByteArrayOutputStream bops = new ByteArrayOutputStream();
        while(inputLen - offLen > 0){
            byte [] cache;
            if(inputLen - offLen > 117){
                cache = ci.doFinal(bytes, offLen,117);
            }else{
                cache = ci.doFinal(bytes, offLen,inputLen - offLen);
            }
            bops.write(cache);
            i++;
            offLen = 117 * i;
        }
        bops.close();
        byte[] encryptedData = bops.toByteArray();
        String encodeToString = Base64.encodeBase64String(encryptedData);
        return encodeToString;
    }




    /**
     * 解密
     * @param key
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws IOException
     */
    public static String testDecrypt(String key,String data) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, IOException{
        byte[] decode = Base64.decodeBase64(key);
//      PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(decode); //java底层 RSA公钥只支持X509EncodedKeySpec这种格式
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(decode);
        KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey generatePublic = kf.generatePublic(x509EncodedKeySpec);
        Cipher ci = Cipher.getInstance(KEY_ALGORITHM);
        ci.init(Cipher.DECRYPT_MODE,generatePublic);

        int inputLen = data.getBytes().length;
        byte[] bytes = data.getBytes();
        int offLen = 0;
        int i = 0;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while(inputLen - offLen > 0){
            byte[] cache;
            if(inputLen - offLen > 128){
                cache = ci.doFinal(bytes,offLen,128);
            }else{
                cache = ci.doFinal(bytes,offLen,inputLen - offLen);
            }
            byteArrayOutputStream.write(cache);
            i++;
            offLen = 128 * i;

        }
        byteArrayOutputStream.close();
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return new String(byteArray);
    }

    /**
     * main方法测试 第一种用法：公钥加密，私钥解密。---用于加解密 第二种用法：私钥签名，公钥验签。---用于签名
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
//        String ss = "hello";
//        byte[] data = ss.getBytes();
//        Map<String, Key> keyMap = initKey();// 构建密钥
//        PublicKey publicKey = (PublicKey) keyMap.get(PUBLIC_KEY);
//        PrivateKey privateKey = (PrivateKey) keyMap.get(PRIVATE_KEY);
//        logger.info("私钥format：{}", privateKey.getFormat());
//        logger.info("公钥format：{}", publicKey.getFormat());
//        System.out.println(privateKey.getEncoded());
//        logger.info("私钥string：{}", getPrivateKey(keyMap));
//        logger.info("公钥string：{}", getPublicKey(keyMap));
//        // 产生签名
//        String sign = signByPrivateKey(data, getPrivateKey(keyMap));
//        logger.info("签名sign={}", sign);
//        // 验证签名
//        boolean verify1 = verifyByPublicKey(ss.getBytes(), getPublicKey(keyMap), sign);
//        logger.info("经验证数据和签名匹配：{} ", verify1);
//        boolean verify = verifyByPublicKey(data, getPublicKey(keyMap), sign);
//        logger.error("经验证数据和签名匹配：{} ", verify);
//        // logger.info("数字签名为"+sign);
//        String s = "单红宇测试，e8986ae53e76e7514ebc7e8a42e81e6cea5b6280fb5d3259d5f0a46f9f6e090c";
//        String encrypt = encryptByPrivateKey(s, PRIVATE_KEY_LZ);;
//        System.out.println("固定私钥加密结果为---："+encrypt);
//        String decrypt = decryptByPublicKey(encrypt, PUBLIC_KEY_LZ);
//        System.out.println("固定公钥解密结果为---："+decrypt);
//        String encryStr = encryptByPublicKey(s, getPublicKey(keyMap));
//        logger.info("字符串 {} 的公钥加密结果为：{}", s, encryStr);
//        String decryStr = decryptByPrivateKey(encryStr, getPrivateKey(keyMap));
//        logger.info("私钥解密结果为：{}", decryStr);
////        logger.info("========================================================================================");
////        String s2 = "单红宇测试222，e8986ae53e76e7514ebc7e8a42e81e6cea5b6280fb5d3259d5f0a46f9f6e090c";
////        String encryStr2 = encryptByPrivateKey(s, getPrivateKey(keyMap));
////        logger.info("字符串 {} 的私钥加密结果为：{}", s2, encryStr2);
////        String decryStr2 = decryptByPublicKey(encryStr2, getPublicKey(keyMap));
////        logger.info("公钥解密结果为：{}", decryStr2);



        String str="cardName=李克兰&cardNo=6226632704541825&cardType=光大银行&cbUrl=https://77.wiki/pay-agent-app/pay-agent/callBack/qianBao&mchNo=210430221548&mchOrderNo=TX2021050319201952530&orderAmount=123";
        System.out.println("原始字符串"+str);
        String privateKey="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCryaNriZBt867M\n" +
                "WUnT/b/8Kjh7pqUnzpm0wSazksSl7ZzhLC468rTkXVeT7HtXJkIZr+5ReO7Zhtym\n" +
                "o2swVyUfWH6D2YjyurELjqpH9zZfTXH6qRa/C7f+6U6tQx0XvFJ3LTaiiaKwr1FL\n" +
                "GVS6tZtEXx1QK0mqhL83f0iVR/dNHnOBYx4NYv+DcSgCzlr/62MmaUr6RBGk8NsM\n" +
                "bka9IO2PizFo8L4EaPb0UJ/HqsrlGeo+VbSOMFJn4gayZhKYsusm+Yq6KHemoAjW\n" +
                "pGIbVhTnEratlZH+Hl8P06E01Un97AGXPesrwK0Ez1pYTJfskvjsL9f8I1atE8ix\n" +
                "sruJXsMBAgMBAAECggEAJKG3xlusP7YB5TN5CcqHYOdD6iIr/6QPpLq3XP2ED3nu\n" +
                "xRldtlZA+p1o6nM4Deprl/yrDhaCoEIg1Svt1H4QvXFqcQU3LcNfs341k2kE4Ces\n" +
                "kW3zGsQN9T6c91djYcdXsnAiwWAmIBUSebgqGDJXrKm3n3vLlp/+AoCS4rpY4smt\n" +
                "DiBrF773iwSVTyLx+vPVDzFXLx29h/MRL+rE3+a/q3CrL5PY9vJTDFWl/HqcfYoG\n" +
                "dWldCgUOeySt7PN1ldG5qrExHL8Ryr/h8awbvPjE0DJVugOlp6qN9OsTE1puswRJ\n" +
                "d0YqGvsuoGJ+5XTDfKj+ob5PJyDZwAK1d0qnUxiGHQKBgQDYhn5o03IxN83kDVVl\n" +
                "2N6+voY07riQXkCZbDAuq/+YA6tiz+QAf06R5tnj4mRGyAC5hSLCjqi0q82OnT0V\n" +
                "V2Lh4GkDUkg+sxKkppbFpbOSlVqzL2PPwxxn8RyhBFA+CWEq3xRwws+JN3KDzexV\n" +
                "wxNlwwD66Od1cRx2d5PRsF/g5wKBgQDLGy4bF5L97HPxZyI3vexiHGhmlTWUZ/+u\n" +
                "uxRxFQn3u/jkTPkFDqHu0DsaA2qorKs036vD8El9O/CLnesePdOSawVUruSgmbAl\n" +
                "o3CiEEBYNK5vPEvQRmXc0wp1qGLdEfZEeR3k2wrK9zyAOZkVT5LSeWHYfM2M2AMq\n" +
                "HBOLdRb31wKBgQC7D76n+30dLlPvIOHVYn5Vli5GDrNe7j6rDX+24punjgwECwy0\n" +
                "rUeI30MBELaHPAT8Qhwcr7jqlb655fg+tsdT01eGKhEGMhj6LH33dUdlbSj1wwxd\n" +
                "Evg4iGKubRaR8fKDYkS7gmJf7oo9eqsiEK1ybf0ryAknOjhX1MjdEJ5UowKBgCnA\n" +
                "TJoFHpJUKQFW0hHjWNRSQqPTyE/ZkRW6H1mfmYzzdxZ3PDlt6JvO8ldGnIZP+GHG\n" +
                "GOkOXA0CZVBOBicRToyqs03VJepdrXgPQPgPnyqCqdjz7CHEVuwt9dh55NjM+HLa\n" +
                "8uo4nN9cM3OoY2+eC/B4Q0TdYuOxA/WEEAB68V3BAoGAB3eo7f3TDvWGxjQC0XCY\n" +
                "EAs32zII1XfZ8XgkGCnqJWMBmn78J2TVrNLjAx/Ni3k3q81hOu2VyoR/jxSLtcU8\n" +
                "0Hpih8IitxACyzEFDYUr1QRA832O/fLujiyWINrr0I0v3EtR4cbJdNmsW6o6R8mO\n" +
                "fbzTRCkx7AZ7ArkGHwRFaSU=";
        String s1 = encryptByPrivateKey(str, privateKey);
        System.out.println("私钥加密"+s1);
        String publicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq8mja4mQbfOuzFlJ0/2/\n" +
                "/Co4e6alJ86ZtMEms5LEpe2c4SwuOvK05F1Xk+x7VyZCGa/uUXju2YbcpqNrMFcl\n" +
                "H1h+g9mI8rqxC46qR/c2X01x+qkWvwu3/ulOrUMdF7xSdy02oomisK9RSxlUurWb\n" +
                "RF8dUCtJqoS/N39IlUf3TR5zgWMeDWL/g3EoAs5a/+tjJmlK+kQRpPDbDG5GvSDt\n" +
                "j4sxaPC+BGj29FCfx6rK5RnqPlW0jjBSZ+IGsmYSmLLrJvmKuih3pqAI1qRiG1YU\n" +
                "5xK2rZWR/h5fD9OhNNVJ/ewBlz3rK8CtBM9aWEyX7JL47C/X/CNWrRPIsbK7iV7D\n" +
                "AQIDAQAB";
        String s = decryptByPublicKey(s1, publicKey);
        System.out.println("公钥解密"+s);

    }

}
