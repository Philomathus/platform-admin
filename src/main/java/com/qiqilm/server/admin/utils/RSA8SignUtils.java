package com.qiqilm.server.admin.utils;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA 签名验签工具类
 *
 * @author xietansheng
 */
public class RSA8SignUtils {

	/** 秘钥对算法名称 */
	private static final String ALGORITHM = "RSA";

	/** 签名算法 */
	private static final String SIGNATURE_ALGORITHM = "Sha1WithRSA";

	/**
	 * 私钥签名（数据）: 用私钥对指定字节数组数据进行签名, 返回签名信息
	 */
	public static String sign( String source, String privatekey ) throws Exception {
		byte[] decoded = Base64.decodeBase64( privatekey );
		RSAPrivateKey priKey =
				( RSAPrivateKey ) KeyFactory.getInstance( ALGORITHM ).generatePrivate( new PKCS8EncodedKeySpec( decoded ) );

		Signature sign = Signature.getInstance( SIGNATURE_ALGORITHM );

		sign.initSign( priKey );
		sign.update( source.getBytes( StandardCharsets.UTF_8 ) );
		return Base64.encodeBase64String( sign.sign() );
	}

	/**
	 * 公钥验签（数据）: 用公钥校验指定数据的签名是否来自对应的私钥
	 */
	public static boolean verify( String source, String signInfo, String publickey ) throws Exception {

		byte[] decoded = Base64.decodeBase64( publickey );
		RSAPublicKey pubKey =
				( RSAPublicKey ) KeyFactory.getInstance( ALGORITHM ).generatePublic( new X509EncodedKeySpec( decoded ) );

		Signature sign = Signature.getInstance( SIGNATURE_ALGORITHM );

		sign.initVerify( pubKey );
		sign.update( source.getBytes( StandardCharsets.UTF_8 ) );
		return sign.verify( Base64.decodeBase64( signInfo ) );
	}
}
