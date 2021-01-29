package com.qiqilm.server.admin.utils;

// 使用旧版本 base64 编解码实现增强兼容性

import org.springframework.util.Base64Utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;

public class TLSSigAPIv2 {


	private static String hmacsha256( String timSdkappid, String timSdkKey, String identifier, long currTime, long expire,
									  String base64Userbuf ) {
		String contentToBeSigned = "TLS.identifier:" + identifier + "\n"
				+ "TLS.sdkappid:" + timSdkappid + "\n"
				+ "TLS.time:" + currTime + "\n"
				+ "TLS.expire:" + expire + "\n";
		if ( null != base64Userbuf ) {
			contentToBeSigned += "TLS.userbuf:" + base64Userbuf + "\n";
		}
		try {
			byte[]        byteKey = timSdkKey.getBytes( StandardCharsets.UTF_8 );
			Mac           hmac    = Mac.getInstance( "HmacSHA256" );
			SecretKeySpec keySpec = new SecretKeySpec( byteKey, "HmacSHA256" );
			hmac.init( keySpec );
			byte[] byteSig = hmac.doFinal( contentToBeSigned.getBytes( StandardCharsets.UTF_8 ) );
			return ( Base64Utils.encodeToString( byteSig ) ).replaceAll( "\\s*", "" );
		} catch ( NoSuchAlgorithmException | InvalidKeyException e ) {
			return "";
		}
	}

	private static String genSig( String timSdkappid, String timSdkKey, String identifier, long expire, byte[] userbuf ) {

		long currTime = System.currentTimeMillis() / 1000;

		Map<String, Object> sigDoc = new HashMap<>();
		sigDoc.put( "TLS.ver", "2.0" );
		sigDoc.put( "TLS.identifier", identifier );
		sigDoc.put( "TLS.sdkappid", timSdkappid );
		sigDoc.put( "TLS.expire", expire );
		sigDoc.put( "TLS.time", currTime );

		String base64UserBuf = null;
		if ( null != userbuf ) {
			base64UserBuf = Base64Utils.encodeToString( userbuf );
			sigDoc.put( "TLS.userbuf", base64UserBuf );
		}
		String sig = hmacsha256( timSdkappid, timSdkKey, identifier, currTime, expire, base64UserBuf );
		if ( sig.length() == 0 ) {
			return "";
		}
		sigDoc.put( "TLS.sig", sig );
		Deflater compressor = new Deflater();
		compressor.setInput( JsonUtil.object2Json( sigDoc ).getBytes( StandardCharsets.UTF_8 ) );
		compressor.finish();
		byte[] compressedBytes       = new byte[ 2048 ];
		int    compressedBytesLength = compressor.deflate( compressedBytes );
		compressor.end();
		return ( new String( Base64Utils.encodeUrlSafe( Arrays.copyOfRange( compressedBytes,
				0, compressedBytesLength ) ) ) ).replaceAll( "\\s*", "" );
	}

	public static String genSig( String timSdkappid, String timSdkKey, String identifier, long expire ) {
		return genSig( timSdkappid, timSdkKey, identifier, expire, null );
	}

}