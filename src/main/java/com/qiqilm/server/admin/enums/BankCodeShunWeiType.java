package com.qiqilm.server.admin.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum BankCodeShunWeiType {
	//
	ICBC( Arrays.asList( "工商银行" ) ),
	ABC( Arrays.asList( "农业银行" ) ),
	CCB( Arrays.asList( "建设银行" ) ),
	BOC( Arrays.asList( "中国银行" ) ),
	CMB( Arrays.asList( "招商银行" ) ),
	BCM( Arrays.asList( "交通银行" ) ),
	CIB( Arrays.asList( "兴业银行" ) ),
	CMBC( Arrays.asList( "民生银行" ) ),
	CEB( Arrays.asList( "光大银行" ) ),
	PAB( Arrays.asList( "平安银行" ) ),
	CITIC( Arrays.asList( "中信银行" ) ),
	CGB( Arrays.asList( "广发银行" ) ),
	SPDB( Arrays.asList( "浦发银行" ) ),
	PSBC( Arrays.asList( "邮政储蓄银行", "邮政银行" ) ),
	HXB( Arrays.asList( "华夏银行" ) ),
	QTBC(Arrays.asList( "其它银行" )),
	;

	private final List<String> desc;

	BankCodeShunWeiType( List<String> desc ) {
		this.desc = desc;
	}

	public static BankCodeShunWeiType getCodeByDesc( String desc ) {
		for ( BankCodeShunWeiType enumType : BankCodeShunWeiType.values() ) {
			if ( enumType.getDesc().contains( desc ) ) {
				return enumType;
			}
		}
		return null;
	}
}
