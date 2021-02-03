package com.qiqilm.server.admin.im;

public class General {
	public static final String TEN_XUN_API = "live.tencentcloudapi.com";
	//IM
	public static final String IM_API      = "https://console.tim.qq.com/v4";

	public static String contenttype = "json";

	public static long randomNum() {
		return ( long ) ( Math.random() * 4294967295L );
	}
}
