package com.qiqilm.server.admin.utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class AesUtil {


	// 加密模式ECB  填充方式PKCS5Padding
	private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";


	//data加密
	public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128);    //密钥长度 128
		Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));

		return cipher.doFinal(content.getBytes("utf-8"));
	}

	//data解密
	public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128);
		Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
		byte[] decryptBytes = cipher.doFinal(encryptBytes);

		return new String(decryptBytes,"utf-8");
	}

	//字节加密
	public static String base64Encode(byte[] bytes){
		return Base64.encodeBase64String(bytes);
	}

	//字节解密
	public static byte[] base64Decode(String base64Code) throws Exception{
		return Base64Utils.decodeFromString( base64Code);
	}
	//字符串加密
	public static String aesEncrypt(String content, String encryptKey) throws Exception {
		return base64Encode(aesEncryptToBytes(content, encryptKey));
	}

	//字符串解密
	public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
		return aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
	}
}
