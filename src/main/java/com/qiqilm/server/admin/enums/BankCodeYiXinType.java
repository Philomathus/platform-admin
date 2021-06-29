package com.qiqilm.server.admin.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum BankCodeYiXinType {

    //
    CDB(Arrays.asList( "国家开发银行" ) ),
    ICBC( Arrays.asList( "工商银行", "中国工商银行", "中国工商" ) ),
    ABC(Arrays.asList( "中国农业银行", "农业银行" ) ),
    BOC(Arrays.asList( "中国银行" ) ),
    CCB(Arrays.asList( "中国建设银行", "建设银行") ),
    PSBC( Arrays.asList( "邮政储蓄银行", "中国邮政储蓄银行", "中国邮政银行", "中国邮政储蓄", "中国邮政", "邮政银行" ) ),
    COMM(Arrays.asList( "交通银行" ) ),
    CMB(Arrays.asList( "招商银行" ) ),
    SPDB(Arrays.asList( "上海浦东发展银行" ) ),
    CIB(Arrays.asList( "兴业银行" ) ),
    HXBANK(Arrays.asList( "华夏银行" ) ),
    GDB(Arrays.asList( "广东发展银行" ) ),
    CMBC(Arrays.asList( "中国民生银行", "民生银行" ) ),
    CITIC(Arrays.asList( "中信银行" ) ),
    CEB(Arrays.asList( "光大银行", "中国光大银行" ) ),
    SPABANK(Arrays.asList( "平安银行" ) ),
    BJBANK(Arrays.asList( "北京银行" ) ),
    SHBANK(Arrays.asList( "上海银行" ) ),
    JSBANK(Arrays.asList( "江苏银行" ) ),
    ;

    private final List<String> desc;

    BankCodeYiXinType( List<String> desc ) {
        this.desc = desc;
    }

    public static BankCodeYiXinType getCodeByDesc( String desc ) {
        for ( BankCodeYiXinType enumType : BankCodeYiXinType.values() ) {
            if ( enumType.getDesc().contains( desc ) ) {
                return enumType;
            }
        }
        return null;
    }
}
