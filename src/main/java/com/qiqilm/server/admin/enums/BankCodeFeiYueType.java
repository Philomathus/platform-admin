package com.qiqilm.server.admin.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum BankCodeFeiYueType {
	//
	a286( Arrays.asList( "浙商银行" ) ),
	a292( Arrays.asList( "建设银行" ) ),
	a296( Arrays.asList( "农业银行", "中国农业银行" ) ),
	a290( Arrays.asList( "工商银行", "中国工商银行", "中国工商" ) ),
	a298( Arrays.asList( "中国银行" ) ),
	a277( Arrays.asList( "招商银行", "中国招商银行" ) ),
	a299( Arrays.asList( "邮政储蓄银行", "中国邮政储蓄银行", "中国邮政银行", "中国邮政储蓄", "中国邮政", "邮政银行" ) ),
	a188( Arrays.asList( "平安银行" ) ),
	a136( Arrays.asList( "交通银行" ) ),
	a69( Arrays.asList( "广发银行" ) ),
	a294( Arrays.asList( "民生银行" ) ),
	a112( Arrays.asList( "华夏银行" ) ),
	a256( Arrays.asList( "兴业银行" ) ),
	a204( Arrays.asList( "浦发银行", "上海浦东发展银行", "浦东发展银行" ) ),
	a291( Arrays.asList( "光大银行" ) ),
	a301( Arrays.asList( "中信银行" ) ),
	;

	private final List<String> desc;

	BankCodeFeiYueType( List<String> desc ) {
		this.desc = desc;
	}

	public static BankCodeFeiYueType getCodeByDesc( String desc ) {
		for ( BankCodeFeiYueType enumType : BankCodeFeiYueType.values() ) {
			if ( enumType.getDesc().contains( desc ) ) {
				return enumType;
			}
		}
		return null;
	}
}
