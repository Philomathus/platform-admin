package com.qiqilm.server.admin.enums;

/**
 * 会员行为类型
 */
public enum EnumLiveAction {

	//
	FORBID_SEND_MSG( 10, "禁言" ),
	SUSPEND( 11, "封停" ),
	NO_SUSPEND( 12, "解封" ),
	OSS_UPLOAD( 13, "oss上传图片" ),
	;
	private int    type;
	private String des;

	public int getType() {
		return type;
	}

	public String getDes() {
		return des;
	}

	EnumLiveAction( int type, String des ) {
		this.type = type;
		this.des = des;
	}

}
