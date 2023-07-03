package com.qiqilm.server.admin.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum BankCodeXiaGuType {
    //
    GLB( Arrays.asList( "桂林银行" ) ),
    ICBC( Arrays.asList( "工商银行", "中国工商银行", "中国工商" ) ),
    CMBC( Arrays.asList( "中国民生银行", "民生银行" ) ),
    CCB( Arrays.asList( "建设银行", "中国建设银行" ) ),
    CIB( Arrays.asList( "兴业银行" ) ),
    CMB( Arrays.asList( "招商银行" ) ),
    CEB( Arrays.asList( "中国光大银行", "光大银行" ) ),
    PSBC( Arrays.asList( "邮政储蓄银行", "中国邮政储蓄银行", "中国邮政银行", "中国邮政储蓄", "中国邮政", "邮政银行" ) ),
    BOC( Arrays.asList( "中国银行" ) ),
    PAB( Arrays.asList( "平安银行" ) ),
    ABC( Arrays.asList( "中国农业银行", "农业银行", "中国农行" ) ),
    BOB( Arrays.asList( "北京银行" ) ),
    SPDB( Arrays.asList( "浦发银行", "上海浦东发展银行", "浦东发展银行" ) ),
    NBCB( Arrays.asList( "宁波银行" ) ),
    CITIC( Arrays.asList( "中信银行" ) ),
    HXB( Arrays.asList( "华夏银行" ) ),
    COMM( Arrays.asList( "交通银行" ) ),
    ;

    private final List<String> desc;

    BankCodeXiaGuType( List<String> desc ) {
        this.desc = desc;
    }

    public static BankCodeXiaGuType getCodeByDesc( String desc ) {
        for ( BankCodeXiaGuType enumType : BankCodeXiaGuType.values() ) {
            if ( enumType.getDesc().contains( desc ) ) {
                return enumType;
            }
            for ( String s : enumType.getDesc() ) {
                if ( s.contains( desc ) ) {
                    return enumType;
                }
            }
        }
        return null;
    }
}
