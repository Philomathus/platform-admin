package com.qiqilm.server.admin.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum BankCodeLangYaType {
	//
	ICBC( Arrays.asList( "工商银行", "中国工商银行", "中国工商" ),1 ),
	ABC( Arrays.asList( "农业银行" ),2 ),
	BOC( Arrays.asList( "中国银行" ),3 ),
	CCB( Arrays.asList( "建设银行" ),4 ),
	BOCO( Arrays.asList( "交通银行" ),5 ),
	CTTIC( Arrays.asList( "中信银行" ),6 ),
	CEB( Arrays.asList( "光大银行" ) ,7),
	CIB( Arrays.asList( "兴业银行" ) ,12),
	BCCB( Arrays.asList( "北京银行" ),14 ),
	GDB( Arrays.asList( "广东发展银行", "广发银行" ),9 ),
	SPDB( Arrays.asList( "浦发银行", "上海浦东发展银行", "浦东发展银行" ),13 ),
	PINGANBANK( Arrays.asList( "平安银行" ) ,10),
	HXB( Arrays.asList( "华夏银行" ) ,8),
	NBCB( Arrays.asList( "宁波银行" ) ,45),
	CZB( Arrays.asList( "浙商银行" ),11 );


	private final List<String> desc;

	private final int code;

	BankCodeLangYaType( List<String> desc, int code) {
		this.desc = desc;
		this.code = code;
	}

	public static BankCodeLangYaType getCodeByDesc(String desc ) {
		for ( BankCodeLangYaType enumType : BankCodeLangYaType.values() ) {
			if ( enumType.getDesc().contains( desc ) ) {
				return enumType;
			}
		}
		return null;
	}
}
