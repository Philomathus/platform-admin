package com.qiqilm.server.admin.utils;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 通用http工具封装
 *
 * @author 77tv
 */
public class HttpHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger( HttpHelper.class );

	public static String getBodyString( ServletRequest request ) {
		StringBuilder  sb     = new StringBuilder();
		BufferedReader reader = null;
		try ( InputStream inputStream = request.getInputStream() ) {
			reader = new BufferedReader( new InputStreamReader( inputStream, StandardCharsets.UTF_8 ) );
			String line = "";
			while ( ( line = reader.readLine() ) != null ) {
				sb.append( line );
			}
		} catch ( IOException e ) {
			LOGGER.warn( "getBodyString出现问题！" );
		} finally {
			if ( reader != null ) {
				try {
					reader.close();
				} catch ( IOException e ) {
					LOGGER.error( ExceptionUtils.getMessage( e ) );
				}
			}
		}
		return sb.toString();
	}

	public static boolean isConnServerByHttp( String serverUrl ) {
		boolean           connFlag = false;
		URL               url;
		HttpURLConnection conn     = null;
		try {
			url = new URL( serverUrl );
			conn = ( HttpURLConnection ) url.openConnection();
			conn.setConnectTimeout( 5 * 1000 );
			if ( conn.getResponseCode() == 200 ) {
				connFlag = true;
			}
		} catch ( IOException e ) {
			LOGGER.error( e.getMessage() );
		} finally {
			if ( conn != null ) {
				conn.disconnect();
			}
		}
		return connFlag;
	}
}
