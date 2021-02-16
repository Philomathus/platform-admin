package com.qiqilm.server.admin.enums;

import lombok.Getter;

@Getter
public enum BankCodeShunWeiType {
	//
	ICBC( "工商银行" ),
	ABC( "农业银行" ),
	CCB( "建设银行" ),
	BOC( "中国银行" ),
	CMB( "招商银行" ),
	BCM( "交通银行" ),
	CIB( "兴业银行" ),
	CMBC( "民生银行" ),
	CEB( "光大银行" ),
	PAB( "平安银行" ),
	CITIC( "中信银行" ),
	CGB( "广发银行" ),
	SPDB( "浦发银行" ),
	PSBC( "邮政储蓄银行" ),
	HXB( "华夏银行" ),
	;

	private final String desc;

	BankCodeShunWeiType( String desc ) {
		this.desc = desc;
	}

	public static BankCodeShunWeiType getCodeByDesc( String desc ) {
		for ( BankCodeShunWeiType enumType : BankCodeShunWeiType.values() ) {
			if ( enumType.getDesc().equals( desc ) ) {
				return enumType;
			}
		}
		return null;
	}
}
