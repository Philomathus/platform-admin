package com.qiqilm.server.admin.enums;

import lombok.Getter;

@Getter
public enum BankCodeHengXinType {

	// 
	ICBC( "工商银行" ),
	CCB( "建设银行" ),
	ABC( "农业银行" ),
	PSBS( "邮政储蓄银行" ),
	BOC( "中国银行" ),
	BOCO( "交通银行" ),
	CMB( "招商银行" ),
	CEB( "光大银行" ),
	CIB( "兴业银行" ),
	CMBC( "民生银行" ),
	BCCB( "北京银行" ),
	CTTIC( "中信银行" ),
	GDB( "广发银行" ),
	SDB( "深圳发展银行" ),
	SPDB( "浦发银行" ),
	PINGANBANK( "平安银行" ),
	HXB( "华夏银行" ),
	SHB( "上海银行" ),
	CBHB( "渤海银行" ),
	HKBEA( "东亚银行" ),
	NBCB( "宁波银行" ),
	CZB( "浙商银行" ),
	NJCB( "南京银行" ),
	HZCB( "杭州银行" ),
	BJRCB( "北京农村商业银行" ),
	SRCB( "上海农商银行" ),
	;

	private final String desc;

	BankCodeHengXinType( String desc ) {
		this.desc = desc;
	}

	public static BankCodeHengXinType getCodeByDesc( String desc ) {
		for ( BankCodeHengXinType enumType : BankCodeHengXinType.values() ) {
			if ( enumType.getDesc().equals( desc ) ) {
				return enumType;
			}
		}
		return null;
	}
}
