package com.qiqilm.server.admin.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum BankCodeMiaoDaoFuType {
    //
    a1(Arrays.asList("邮政储蓄银行", "中国邮政储蓄银行", "中国邮政银行", "中国邮政储蓄", "中国邮政", "邮政银行")),
    a2(Arrays.asList("工商银行", "中国工商银行", "中国工商")),
    a3(Arrays.asList("农业银行", "中国农业银行")),
    a4(Arrays.asList("中国银行")),
    a5(Arrays.asList("建设银行", "中国建设银行")),
    a7(Arrays.asList("交通银行")),
    a8(Arrays.asList("中信银行")),
    a9(Arrays.asList("光大银行", "中国光大银行")),
    a10(Arrays.asList("华夏银行", "中国华夏银行")),
    a11(Arrays.asList("民生银行", "中国民生银行")),
    a12(Arrays.asList("广发银行", "广东发展银行")),
    a13(Arrays.asList("平安银行")),
    a14(Arrays.asList("招商银行", "中国招商银行")),
    a15(Arrays.asList("兴业银行")),
    a16(Arrays.asList("浦发银行", "上海浦东发展银行", "浦东发展银行")),
    a17(Arrays.asList("恒丰银行")),
    a18(Arrays.asList("浙商银行")),
    a19(Arrays.asList("渤海银行")),
    a21(Arrays.asList("东亚银行")),
    a48(Arrays.asList("上海银行")),
    a50(Arrays.asList("北京银行")),
    a56(Arrays.asList("宁波银行")),
    a61(Arrays.asList("汉口银行")),
    a69(Arrays.asList("杭州银行")),
    a70(Arrays.asList("南京银行")),
    a88(Arrays.asList("徽商银行")),
    a109(Arrays.asList("长沙银行")),
    a143(Arrays.asList("晋城银行")),
    a155(Arrays.asList("浙江稠州商业银行")),
    a213(Arrays.asList("上海农商银行", "上海农村商业银行", "上海农商")),
    a219(Arrays.asList("顺德农村商业银行", "顺德农商银行", "顺德农商")),
    a232(Arrays.asList("北京农村商业银行", "北京农商银行", "北京农商")),
    a1358(Arrays.asList("中国银联")),
    ;

    private final List<String> desc;

    BankCodeMiaoDaoFuType(List<String> desc) {
        this.desc = desc;
    }

    public static BankCodeMiaoDaoFuType getCodeByDesc(String desc) {
        for (BankCodeMiaoDaoFuType enumType : BankCodeMiaoDaoFuType.values()) {
            if (enumType.getDesc().contains(desc)) {
                return enumType;
            }
        }
        return null;
    }
}
