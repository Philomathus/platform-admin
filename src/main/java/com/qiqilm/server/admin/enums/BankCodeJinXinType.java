package com.qiqilm.server.admin.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum BankCodeJinXinType {
    ALIPAY(Arrays.asList( "支付宝" ) ),
    CMB(Arrays.asList( "招商银行" ) ),
    ICBC( Arrays.asList( "工商银行", "中国工商银行", "中国工商" ) ),
    COMM(Arrays.asList( "交通银行" ) ),
    CCB(Arrays.asList( "中国建设银行", "建设银行") ),
    ABC(Arrays.asList( "中国农业银行", "农业银行" ) ),
    CEB(Arrays.asList( "光大银行", "中国光大银行" ) ),
    CMBC(Arrays.asList( "中国民生银行", "民生银行" ) ),
    SPABANK(Arrays.asList( "平安银行" ) ),
    SPDB(Arrays.asList( "上海浦东发展银行", "浦东发展银行", "浦发银行" ) ),
    CIB(Arrays.asList( "兴业银行" ) ),
    BOC(Arrays.asList( "中国银行" ) ),
    CITIC(Arrays.asList( "中信银行" ) ),
    BJBANK(Arrays.asList( "北京银行" ) ),
    NJCB(Arrays.asList( "南京银行" ) ),
    GDB(Arrays.asList( "广东发展银行", "广发银行" ) ),
    HXBANK(Arrays.asList( "华夏银行" ) ),
    PSBC( Arrays.asList( "邮政储蓄银行", "中国邮政储蓄银行", "中国邮政银行", "中国邮政储蓄", "中国邮政", "邮政银行" ) ),
    SDB(Arrays.asList( "深圳发展银行" ) ),
    SHBANK(Arrays.asList( "上海银行" ) ),
    HSB(Arrays.asList( "徽商银行" ) ),
    ZSB(Arrays.asList( "浙商银行" ) ),
    HZBC(Arrays.asList( "杭州银行" ) ),
    NBBC(Arrays.asList( "宁波银行" ) ),
    XMB(Arrays.asList( "厦门银行" ) ),
    BOD(Arrays.asList( "东莞银行" ) ),
    CSCB(Arrays.asList( "长沙银行" ) ),
    JSBANK(Arrays.asList( "江苏银行" ) ),
    HSBC(Arrays.asList( "汇丰银行" ) ),
    ;

    private final List<String> desc;

    BankCodeJinXinType( List<String> desc ) {
        this.desc = desc;
    }

    public static BankCodeJinXinType getCodeByDesc( String desc ) {
        for ( BankCodeJinXinType enumType : BankCodeJinXinType.values() ) {
            if ( enumType.getDesc().contains( desc ) ) {
                return enumType;
            }
        }
        return null;
    }
}
