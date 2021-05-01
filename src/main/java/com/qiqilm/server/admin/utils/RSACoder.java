package com.qiqilm.server.admin.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * 经典的数字签名算法RSA 数字签名
 *
 * @author kongqz
 */
public class RSACoder {
    //数字签名，密钥算法
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 数字签名 签名/验证算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * RSA密钥长度，RSA算法的默认密钥长度是1024 密钥长度必须是64的倍数，在512到65536位之间
     */
    private static final int KEY_SIZE = 1024;
    //公钥
    private static final String PUBLIC_KEY = "RSAPublicKey";

    //私钥
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    private static final int enSegmentSize = 117;//加密长度

    private static final int deSegmentSize = 128;//解密长度

    /**
     * 签名
     *
     * @param signSrc       待签名数据
     * @param platPublicKey 密钥
     * @return byte[] 数字签名
     */
    public static byte[] signByPublicKey(String signSrc, String platPublicKey) throws Exception {
        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //初始化公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(java.util.Base64.getDecoder().decode(platPublicKey));
        //产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

        //数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipherDoFinal(cipher, signSrc.getBytes("UTF-8"), 117); //分段加密
    }

    /**
     * 公钥加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return 加密后hex16进制字符串
     * @throws Exception
     */
    public static String encryptByPublicKeyHex(String data, String key) throws Exception {

        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //初始化公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(java.util.Base64.getDecoder().decode(key));
        //产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

        //数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return new BigInteger(1, cipher.doFinal(data.getBytes(StandardCharsets.UTF_8))).toString(16);
    }

    /**
     * 公钥加密
     *
     * @param data 待加密数据
     * @param key  密钥
     */
    public static String encryptByPublicKey(String data, String key) throws Exception {

        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //初始化公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(java.util.Base64.getDecoder().decode(key));
        //产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

        //数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] resultBytes = cipherDoFinal(cipher, data.getBytes(StandardCharsets.UTF_8), enSegmentSize); //分段加密
        return java.util.Base64.getEncoder().encodeToString(resultBytes);
    }

    /**
     * 私钥解密
     *
     * @param data 待解密数据
     * @param key  密钥
     */
    public static String decryptByPrivateKey(String data, String key) throws Exception {
        //取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(java.util.Base64.getDecoder().decode(key));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        //数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decBytes = cipherDoFinal(cipher, java.util.Base64.getDecoder().decode(data), deSegmentSize); //分段加密;
        return new String(decBytes, StandardCharsets.UTF_8);
    }

    /**
     * 分段加解密
     */
    private static byte[] cipherDoFinal(Cipher cipher, byte[] srcBytes, int segmentSize)
            throws IllegalBlockSizeException, BadPaddingException, IOException {
        if (segmentSize <= 0) {
            throw new RuntimeException("分段大小必须大于0");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int inputLen = srcBytes.length;
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > segmentSize) {
                cache = cipher.doFinal(srcBytes, offSet, segmentSize);
            } else {
                cache = cipher.doFinal(srcBytes, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * segmentSize;
        }
        return out.toByteArray();
    }

    /**
     * 初始化密钥对
     *
     * @return Map 甲方密钥的Map
     */
    public static Map<String, Object> initKey() throws Exception {
        //实例化密钥生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        //初始化密钥生成器
        keyPairGenerator.initialize(KEY_SIZE);
        //生成密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //甲方公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        //甲方私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //将密钥存储在map中
        Map<String, Object> keyMap = new HashMap<String, Object>();
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;

    }


    /**
     * 签名
     *
     * @param data       待签名数据
     * @param privateKey 密钥
     * @return byte[] 数字签名
     */
    public static byte[] sign(byte[] data, byte[] privateKey) throws Exception {

        //取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //生成私钥
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        //实例化Signature
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        //初始化Signature
        signature.initSign(priKey);
        //更新
        signature.update(data);
        return signature.sign();
    }

    /**
     * 校验数字签名
     *
     * @param data      待校验数据
     * @param publicKey 公钥
     * @param sign      数字签名
     * @return boolean 校验成功返回true，失败返回false
     */
    public static boolean verifySha256Rsa(String data, String publicKey, String sign) throws Exception {
        try {
            //实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            //初始化公钥
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(java.util.Base64.getDecoder().decode(publicKey));
            //产生公钥
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

            Signature signVer = Signature.getInstance("SHA256withRSA");
            signVer.initVerify(pubKey);
            signVer.update(data.getBytes(StandardCharsets.UTF_8));
            return signVer.verify(java.util.Base64.getDecoder().decode(sign));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 校验数字签名
     *
     * @param data      待校验数据
     * @param publicKey 公钥
     * @param sign      数字签名
     * @return boolean 校验成功返回true，失败返回false
     */
    public static boolean verify(byte[] data, byte[] publicKey, byte[] sign) throws Exception {
        //转换公钥材料
        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //初始化公钥
        //密钥材料转换
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
        //产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        //实例化Signature
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        //初始化Signature
        signature.initVerify(pubKey);
        //更新
        signature.update(data);
        //验证
        return signature.verify(sign);
    }

    /**
     * 私钥解密
     **/
    public static String decryptByPrivateKeyShunWei(String data, String key) {
        try {
            Base64 base64 = new Base64();
            byte[] decryptStr = decryptByPrivateKeyShunWei(base64.decode(data), base64.decode(key));
            return new String(decryptStr, "UTF-8");
        } catch (IOException e) {
        }
        return null;
    }

    private static byte[] decryptByPrivateKeyShunWei(byte[] data, byte[] key) {
        try {
            // 取得私钥
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            // 生成私钥
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            // 数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            int blockSize = cipher.getOutputSize(data.length);
            return doFinal(data, cipher, blockSize);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return null;
    }

    private static byte[] doFinal(byte[] decryptData, Cipher cipher, int blockSize)
            throws IllegalBlockSizeException, BadPaddingException, IOException {
        int offSet = 0;
        byte[] cache;
        int i = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (decryptData.length - offSet > 0) {
            if (decryptData.length - offSet > blockSize) {
                cache = cipher.doFinal(decryptData, offSet, blockSize);
            } else {
                cache = cipher.doFinal(decryptData, offSet, decryptData.length - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * blockSize;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //初始化密钥
        //生成密钥对
        Map<String, Object> keyMap = RSACoder.initKey();
        String merchant_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCUujRem7Hy9ll3ue3" +
                "+OgCQa3NPeX897rYbYKNKMXjS7bif1M+1OKSoD5HsBADkXWST78ok1RV8f/OtNrEFXxKhcr8uPs2RpheKgjPrBIFxTn54oUmAxARXem1k5KO" +
                "+uo" +
                "+cQtpRJsk+YMDssHdy6MSFHHdtlGaJPKy9k+3ApjxWAQIDAQAB";
        //公钥
        //byte[] publicKey=RSACoder.getPublicKey(keyMap);
        byte[] publicKey = Base64.decodeBase64(merchant_public_key);
        //私钥
        //byte[] privateKey=RSACoder.getPrivateKey(keyMap);
        String merchant_private_key = "MIICdgIBAdANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANX5GMeHwd" +
                "+ZrpZQ9aKKaD2rrXDXVlh53fOcu5prqQ+ZZ6BzB+0nrARU+Y8pmZdsUxMZ1mYLcDTRbRXh8idkjFUhIfrFrwUKBpu" +
                "/G5HLZ2k4w03BS1pYz9CYKwyKrQ0cTfVot0k61EQZ8WlYNyaGD96pGUwwDNIgaUspSVutSKnvAgMBAAECgYAuMgJEia20nZRQxZfSiLkqn1bppsPwhBcEElF6bEXqpT73J/6NF+SOZt4eJ2gOhgeFdy2PiGaoMJKxh79k+9ND5LHnkASjOtp5Cqa3/CSHCQxEaFNOabYBZUpm4XqomxeJn5lMv/a9qNNY28Gx3g0dRRpRn0g+c7OPEicqOHjyyQJBAPERjAHN2ZIIRhzHgRhIBEm07M0w6wo+tDn6LEluQbCrU2sV02VblP88RfkIToxafjByAfX+cQlnN8zq2u56DNMCQQDjOeplpZWmByEyqWrOXJUD1vghEeZJguM+FEkknLbxikQ2IxLE46OyBT4FabB7kDnNZCIV1grAFqIxgJDITYz1AkANaUp+tzMJeshbxYWbEjaa2yPpbnVFBqQELbTVCPtCluV3KamvE99AK9xAtIOaL1ah31XYl6U2PrXOAqrXZZbdAkEAoBeH/AHEA+v2CdmvdKFqJABrZfFUjOp47J4iQndftaIzGOlxKeMwzBZBclLaktQ0xW8NTNE3Vcscj0ADwfxRmQJAImSU9H4TGrDFcdGpmd6M+4+SsyUBf35keX/DWZkBqlc9o4lDraC1WSfshEvM6GOzkyd2hJc38OrPqllprI7OeQ==";
        byte[] privateKey = Base64.decodeBase64(merchant_private_key);
        System.out.println("公钥：/n" + Base64.encodeBase64String(publicKey));
        System.out.println("私钥：/n" + Base64.encodeBase64String(privateKey));

        System.out.println("================密钥对构造完毕，开始进行加密数据的传输=============");
        String str = "RSA数字签名算法";
        System.out.println("原文:" + str);
        //甲方进行数据的加密
        byte[] sign = RSACoder.sign(str.getBytes(), privateKey);
        System.out.println("产生签名：" + Base64.encodeBase64String(sign));
        //验证签名
        boolean status = RSACoder.verify(str.getBytes(), publicKey, sign);
        System.out.println("状态：" + status + "/n/n");


    }
}