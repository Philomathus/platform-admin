package com.qiqilm.server.admin.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum BankCodeLPType {

	//
	ICBC( Arrays.asList( "工商银行", "中国工商银行", "中国工商" ) ),
	CCB( Arrays.asList( "建设银行" ) ),
	ABC( Arrays.asList( "农业银行" ) ),
	PSBS( Arrays.asList( "邮政储蓄银行", "中国邮政储蓄银行", "中国邮政银行", "中国邮政储蓄", "中国邮政", "邮政银行" ) ),
	BOC( Arrays.asList( "中国银行" ) ),
	CEB( Arrays.asList( "中国光大银行" ) ),
	CMBC( Arrays.asList( "中国民生银行" ) ),
	CMB	( Arrays.asList( "招商银行" ) ),
	BOCOM( Arrays.asList( "交通银行" ) ),
	CNCB( Arrays.asList( "中信银行" ) ),
	SPDB( Arrays.asList( "浦发银行" ) ),
	GDB( Arrays.asList( "广发银行" ) ),
	HXB( Arrays.asList( "华夏银行" ) ),
	CIB( Arrays.asList( "兴业银行" ) ),
	PAB( Arrays.asList( "平安银行" ) ),
	BCCB( Arrays.asList( "北京银行" ) ),
	NJB( Arrays.asList( "南京银行" ) ),
	HZB( Arrays.asList( "杭州银行" ) ),
	NBB( Arrays.asList( "宁波银行" ) ),
    ;

	private final List<String> desc;

	BankCodeLPType( List<String> desc ) {
		this.desc = desc;
	}

	public static BankCodeLPType getCodeByDesc( String desc ) {
		for ( BankCodeLPType enumType : BankCodeLPType.values() ) {
			if ( enumType.getDesc().contains( desc ) ) {
				return enumType;
			}
		}
		return null;
	}
}
