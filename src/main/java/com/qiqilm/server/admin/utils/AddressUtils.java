package com.qiqilm.server.admin.utils;


import com.qiqilm.server.admin.constant.AdminConstants;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

/**
 * 获取地址类
 *
 * @author 77tv
 */
@Log4j2
public class AddressUtils {
	// IP地址查询
	public static final String IP_URL  = "http://whois.pconline.com.cn/ipJson.jsp";
	// 未知地址
	public static final String UNKNOWN = "XX XX";

	public static String getRealAddressByIP( String ip ) {
		String address = UNKNOWN;
		// 内网不查询
		if ( UserDataUtil.internalIp( ip ) ) {
			return "内网IP";
		}
		try {
			String rspStr = HttpUtils.sendGet( IP_URL, "ip=" + ip + "&json=true", AdminConstants.GBK );
			if ( StringUtils.isEmpty( rspStr ) ) {
				log.error( "获取地理位置异常 {}", ip );
				return UNKNOWN;
			}
			Map<String, String> resultMap = JsonUtil.json2Map( rspStr );
			String              region    = resultMap.get( "pro" );
			String              city      = resultMap.get( "city" );
			if ( StringUtils.isAllBlank( region, city ) ) {
				return resultMap.get( "addr" );
			}
			return String.format( "%s %s", region, city );
		} catch ( Exception e ) {
			log.error( "获取地理位置异常 {}", ip );
		}
		return address;
	}
}
