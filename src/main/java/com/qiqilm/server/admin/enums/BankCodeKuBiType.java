package com.qiqilm.server.admin.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum BankCodeKuBiType {
    //
    cmb( Arrays.asList( "招商银行" ) ),
    icbc( Arrays.asList( "工商银行", "中国工商银行", "中国工商" ) ),
    ccb( Arrays.asList( "建设银行", "中国建设银行" ) ),
    spdb( Arrays.asList( "上海浦东发展银行", "浦发银行", "上海浦发银行" ) ),
    abc( Arrays.asList( "农业银行", "中国农业银行" ) ),
    cmbc( Arrays.asList( "民生银行", "中国民生银行" ) ),
    cib( Arrays.asList( "兴业银行" ) ),
    comm( Arrays.asList( "交通银行", "中国交通银行" ) ),
    ceb( Arrays.asList( "光大银行", "中国光大银行" ) ),
    boc( Arrays.asList( "中国银行" ) ),
    bccb( Arrays.asList( "北京银行" ) ),
    pingan( Arrays.asList( "平安银行" ) ),
    cgb( Arrays.asList( "广东发展银行", "广发银行" ) ),
    psbc( Arrays.asList( "邮政储蓄银行", "中国邮政储蓄银行", "中国邮政银行", "中国邮政储蓄", "中国邮政", "邮政银行" ) ),
    ecitic( Arrays.asList( "中信银行" ) ),
    hxb( Arrays.asList( "华夏银行" ) ),
    bankofshanghai( Arrays.asList( "上海银行" ) ),
    other( Arrays.asList( "其他银行" ) ),

    ;

    private final List<String> desc;

    BankCodeKuBiType( List<String> desc ) {
        this.desc = desc;
    }

    public static BankCodeKuBiType getCodeByDesc( String desc ) {
        for ( BankCodeKuBiType enumType : BankCodeKuBiType.values() ) {
            if ( enumType.getDesc().contains( desc ) ) {
                return enumType;
            }
        }
        return other;
    }
}
