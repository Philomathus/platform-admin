package com.qiqilm.server.admin.utils;

import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 获取用户缓存数据 Created by admin on 2018/2/6.
 */
@Log4j2
public class UserDataUtil {
	/**
	 * 获取客户端请求的真实ip地址
	 *
	 * @param request 请求对象
	 * @return String 客户端请求ip
	 */
	public static String getIp( HttpServletRequest request ) {

		String ip = request.getHeader( "cf-pseudo-ipv4" );
		if ( ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase( ip ) ) {
			ip = request.getHeader( "client-ip" );
		}
		if ( ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase( ip ) ) {
			ip = request.getHeader( "cf-connecting-ip" );
		}
		if ( ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase( ip ) ) {
			ip = request.getHeader( "ali-cdn-real-ip" );
		}
		if ( ip != null && ip.contains( "," ) ) {
			ip = ip.split( "," )[ 0 ];
		}
		if ( StringUtils.isBlank( ip ) ) {
			ip = request.getRemoteAddr();
			log.warn( JsonUtil.object2Json( getRequestInfo( request ) ) );
		}
		return ip;
	}

	//得到所有的消息头
	private static Map<String, String> getRequestInfo( HttpServletRequest request ) {
		Enumeration<String> e         = request.getHeaderNames();
		Map<String, String> resultMap = new HashMap<>();
		while ( e.hasMoreElements() ) {
			String              headerName   = e.nextElement();//透明称
			Enumeration<String> headerValues = request.getHeaders( headerName );
			while ( headerValues.hasMoreElements() ) {
				resultMap.put( headerName, headerValues.nextElement() );
			}
		}
		return resultMap;
	}

	public static boolean internalIp( String ip ) {
		byte[] addr = textToNumericFormatV4( ip );
		return internalIp( addr ) || "127.0.0.1".equals( ip );
	}

	private static boolean internalIp( byte[] addr ) {
		if ( StringUtils.isNull( addr ) || addr.length < 2 ) {
			return true;
		}
		final byte b0 = addr[ 0 ];
		final byte b1 = addr[ 1 ];
		// 10.x.x.x/8
		final byte SECTION_1 = 0x0A;
		// 172.16.x.x/12
		final byte SECTION_2 = ( byte ) 0xAC;
		final byte SECTION_3 = ( byte ) 0x10;
		final byte SECTION_4 = ( byte ) 0x1F;
		// 192.168.x.x/16
		final byte SECTION_5 = ( byte ) 0xC0;
		final byte SECTION_6 = ( byte ) 0xA8;
		switch ( b0 ) {
		case SECTION_1:
			return true;
		case SECTION_2:
			if ( b1 >= SECTION_3 && b1 <= SECTION_4 ) {
				return true;
			}
		case SECTION_5:
			if ( b1 == SECTION_6 ) {
				return true;
			}
		default:
			return false;
		}
	}

	/**
	 * 将IPv4地址转换成字节
	 *
	 * @param text IPv4地址
	 * @return byte 字节
	 */
	public static byte[] textToNumericFormatV4( String text ) {
		if ( text.length() == 0 ) {
			return null;
		}
		byte[]   bytes    = new byte[ 4 ];
		String[] elements = text.split( "\\.", -1 );
		try {
			long l;
			int  i;
			switch ( elements.length ) {
			case 1:
				l = Long.parseLong( elements[ 0 ] );
				if ( ( l < 0L ) || ( l > 4294967295L ) ) {
					return null;
				}
				bytes[ 0 ] = ( byte ) ( int ) ( l >> 24 & 0xFF );
				bytes[ 1 ] = ( byte ) ( int ) ( ( l & 0xFFFFFF ) >> 16 & 0xFF );
				bytes[ 2 ] = ( byte ) ( int ) ( ( l & 0xFFFF ) >> 8 & 0xFF );
				bytes[ 3 ] = ( byte ) ( int ) ( l & 0xFF );
				break;
			case 2:
				l = Integer.parseInt( elements[ 0 ] );
				if ( ( l < 0L ) || ( l > 255L ) ) {
					return null;
				}
				bytes[ 0 ] = ( byte ) ( int ) ( l & 0xFF );
				l = Integer.parseInt( elements[ 1 ] );
				if ( ( l < 0L ) || ( l > 16777215L ) ) {
					return null;
				}
				bytes[ 1 ] = ( byte ) ( int ) ( l >> 16 & 0xFF );
				bytes[ 2 ] = ( byte ) ( int ) ( ( l & 0xFFFF ) >> 8 & 0xFF );
				bytes[ 3 ] = ( byte ) ( int ) ( l & 0xFF );
				break;
			case 3:
				for ( i = 0; i < 2; ++i ) {
					l = Integer.parseInt( elements[ i ] );
					if ( ( l < 0L ) || ( l > 255L ) ) {
						return null;
					}
					bytes[ i ] = ( byte ) ( int ) ( l & 0xFF );
				}
				l = Integer.parseInt( elements[ 2 ] );
				if ( ( l < 0L ) || ( l > 65535L ) ) {
					return null;
				}
				bytes[ 2 ] = ( byte ) ( int ) ( l >> 8 & 0xFF );
				bytes[ 3 ] = ( byte ) ( int ) ( l & 0xFF );
				break;
			case 4:
				for ( i = 0; i < 4; ++i ) {
					l = Integer.parseInt( elements[ i ] );
					if ( ( l < 0L ) || ( l > 255L ) ) {
						return null;
					}
					bytes[ i ] = ( byte ) ( int ) ( l & 0xFF );
				}
				break;
			default:
				return null;
			}
		} catch ( NumberFormatException e ) {
			return null;
		}
		return bytes;
	}

	/**
	 * 注册账号检查
	 */
	public static boolean checkUserName( String username ) {
		Pattern pattern = Pattern.compile( "[0-9a-z.@]" );
		return pattern.matcher( username ).find();
	}

	/**
	 * 判断是否为数字格式不限制位数
	 *
	 * @param o 待校验参数
	 * @return 如果全为数字，返回true；否则，返回false
	 */
	public static boolean isNumber( Object o ) {
		return ( Pattern.compile( "[0-9]*" ) ).matcher( String.valueOf( o ) ).matches();
	}

	public static boolean isSpecial( String o, String words ) {

		String[] arr = words.split( "," );
		o = o.toLowerCase();
		for ( String k : arr ) {
			if ( o.contains( k ) ) {
				return true;
			}
		}
		return false;
	}

	public static boolean isMobile( String mobile ) {
		return Pattern.compile( "^(1[3-9]\\d{9}$)" ).matcher( mobile ).matches();
	}

	/**
	 * 判断字符串中 数字个数
	 *
	 * @return
	 */
	public static int isNumCount( String str ) {
		byte[] array1 = str.getBytes();//将字符串转换为字符数组
		int    count  = 0;
		for ( int i = 0; i < array1.length; i++ ) {
			if ( array1[ i ] >= 48 && array1[ i ] <= 57 ) {//数字的ASCII码为48--57
				count++;
			}
		}
		return count;
	}

	/**
	 * 注册账号检查
	 */
	public static boolean checkABC( String username ) {
		if ( username.length() < 1 ) {
			return false;
		}
		Pattern pattern = Pattern.compile( "[a-z]" );
		return pattern.matcher( username.substring( 0, 1 ) ).find();
	}

}
