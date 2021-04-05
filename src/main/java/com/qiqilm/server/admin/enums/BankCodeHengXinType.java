package com.qiqilm.server.admin.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum BankCodeHengXinType {

	// 
	ICBC( Arrays.asList( "工商银行" ) ),
	CCB( Arrays.asList( "建设银行" ) ),
	ABC( Arrays.asList( "农业银行" ) ),
	PSBS( Arrays.asList( "邮政储蓄银行", "邮政银行" ) ),
	BOC( Arrays.asList( "中国银行" ) ),
	BOCO( Arrays.asList( "交通银行" ) ),
	CMB( Arrays.asList( "招商银行" ) ),
	CEB( Arrays.asList( "光大银行" ) ),
	CIB( Arrays.asList( "兴业银行" ) ),
	CMBC( Arrays.asList( "民生银行" ) ),
	BCCB( Arrays.asList( "北京银行" ) ),
	CTTIC( Arrays.asList( "中信银行" ) ),
	GDB( Arrays.asList( "广发银行" ) ),
	SDB( Arrays.asList( "深圳发展银行" ) ),
	SPDB( Arrays.asList( "浦发银行" ) ),
	PINGANBANK( Arrays.asList( "平安银行" ) ),
	HXB( Arrays.asList( "华夏银行" ) ),
	SHB( Arrays.asList( "上海银行" ) ),
	CBHB( Arrays.asList( "渤海银行" ) ),
	HKBEA( Arrays.asList( "东亚银行" ) ),
	NBCB( Arrays.asList( "宁波银行" ) ),
	CZB( Arrays.asList( "浙商银行" ) ),
	NJCB( Arrays.asList( "南京银行" ) ),
	HZCB( Arrays.asList( "杭州银行" ) ),
	BJRCB( Arrays.asList( "北京农村商业银行" ) ),
	SRCB( Arrays.asList( "上海农商银行" ) ),
	;

	private final List<String> desc;

	BankCodeHengXinType( List<String> desc ) {
		this.desc = desc;
	}

	public static BankCodeHengXinType getCodeByDesc( String desc ) {
		for ( BankCodeHengXinType enumType : BankCodeHengXinType.values() ) {
			if ( enumType.getDesc().contains( desc ) ) {
				return enumType;
			}
		}
		return null;
	}
}
