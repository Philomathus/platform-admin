package com.qiqilm.server.admin.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum BankCodeYuZhouType {

	ABC( Arrays.asList( "农业银行", "中国农业银行" ) ),
	BJBANK( Arrays.asList( "北京银行" ) ),
    BEAI( Arrays.asList( "东亚银行" ) ),
    BOC( Arrays.asList( "中国银行" ) ),
    BCOM( Arrays.asList( "交通银行" ) ),
	CBHB( Arrays.asList( "渤海银行" ) ),
    BOS( Arrays.asList( "上海银行" ) ),
	CCB( Arrays.asList( "建设银行", "中国建设银行" ) ),
	CEB( Arrays.asList( "光大银行" ) ),
	CIB( Arrays.asList( "兴业银行" ) ),
	CMB( Arrays.asList( "招商银行", "中国招商银行" ) ),
	CMBC( Arrays.asList( "民生银行", "中国民生银行" ) ),
    CZSB( Arrays.asList( "浙商银行" ) ),
	CITIC( Arrays.asList( "中信银行" ) ),
	GDB( Arrays.asList( "广东发展银行", "广发银行" ) ),
	HSCB( Arrays.asList( "徽商银行" ) ),
	HXB( Arrays.asList( "华夏银行" ) ),
	HZCB( Arrays.asList( "杭州银行" ) ),
	ICBC( Arrays.asList( "工商银行", "中国工商银行", "中国工商" ) ),
	NBCB( Arrays.asList( "宁波银行" ) ),
	NJCB( Arrays.asList( "南京银行" ) ),
	PABC( Arrays.asList( "平安银行" ) ),
	PSBC( Arrays.asList( "邮政储蓄银行", "中国邮政储蓄银行", "中国邮政银行", "中国邮政储蓄", "中国邮政", "邮政银行" ) ),
	SDB( Arrays.asList( "深圳发展银行" ) ),
	SPDB( Arrays.asList( "浦发银行", "上海浦东发展银行", "浦东发展银行" ) ),
	BJRCB( Arrays.asList( "北京农商行","北京农村商业银行" ) ),
	SRCB( Arrays.asList( "上海农商银行" ) ),
	CCQTGB( Arrays.asList( "重庆三峡银行" )),
	CSCB( Arrays.asList( "长沙银行" ));

	private final List<String> desc;

	BankCodeYuZhouType(List<String> desc ) {
		this.desc = desc;
	}

	public static BankCodeYuZhouType getCodeByDesc(String desc ) {
		for ( BankCodeYuZhouType enumType : BankCodeYuZhouType.values() ) {
			if ( enumType.getDesc().contains( desc ) ) {
				return enumType;
			}
		}
		return null;
	}
}
