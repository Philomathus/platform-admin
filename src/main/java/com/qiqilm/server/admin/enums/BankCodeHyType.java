package com.qiqilm.server.admin.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;


@Getter
public enum BankCodeHyType {

    a1(Arrays.asList("北京农商银行")),
    a2(Arrays.asList("邮政储蓄银行", "中国邮政储蓄银行", "中国邮政银行", "中国邮政储蓄", "中国邮政", "邮政银行", "邮政储蓄")),
    a3(Arrays.asList("农业银行")),
    a4(Arrays.asList("广发银行")),
    a5(Arrays.asList("成都银行")),
    a6(Arrays.asList("杭州银行")),
    a7(Arrays.asList("浦发银行")),
    a8(Arrays.asList("光大银行")),
    a9(Arrays.asList("上海农商行")),
    a10(Arrays.asList("中国银行")),
    a11(Arrays.asList("建设银行")),
    a12(Arrays.asList("民生银行")),
    a13(Arrays.asList("华夏银行")),
    a14(Arrays.asList("平安银行")),
    a15(Arrays.asList("宁波银行")),
    a16(Arrays.asList("交通银行")),
    a17(Arrays.asList("招商银行")),
    a18(Arrays.asList("上海银行")),
    a19(Arrays.asList("中信银行")),
    a20(Arrays.asList("工商银行")),
    a22(Arrays.asList("兴业银行")),
    a23(Arrays.asList("北京银行")),
    a24(Arrays.asList("烟台银行")),
    a25(Arrays.asList("青岛银行")),

    a101(Arrays.asList("常熟农商银行","常熟农商","常熟农村商业银行")),
    a102(Arrays.asList("成都农商银行","成都农商","成都农村商业银行")),
    a103(Arrays.asList("重庆农村商业银行","重庆农商银行","重庆农商")),
    a104(Arrays.asList("大连农村商业银行","大连农商银行","大连农商")),
    a105(Arrays.asList("东莞农村商业银行","东莞农商银行","东莞农商")),
    a106(Arrays.asList("广州农村商业银行","广州农商银行","广州农商")),
    a107(Arrays.asList("海口联合农商银行","海口农商银行","海口农商")),
    a108(Arrays.asList("江南农村商业银行","江南农商银行","江南农商")),
    a109(Arrays.asList("江苏江阴农村商业银行","江苏江阴农商银行","江苏江阴农商")),
    a110(Arrays.asList("昆山农村商业银行","昆山农商银行","昆山农商")),
    a111(Arrays.asList("南海农商银行")),
    a112(Arrays.asList("宁夏黄河农村商业银行","宁夏黄河农商银行","宁夏黄河农商")),
    a113(Arrays.asList("尧都农商银行村镇银行")),
    a114(Arrays.asList("顺德农商银行")),
    a115(Arrays.asList("天津农商银行")),
    a116(Arrays.asList("吴江农村商业银行")),
    a117(Arrays.asList("武汉农村商业银行")),
    a118(Arrays.asList("张家港农村商业银行")),
    a119(Arrays.asList("江苏省农村信用社联合社", "江苏省农村信用社", "江苏农信", "江苏农村信用社银行", "江苏农村信用社", "江苏省农村信用社银行")),
    a120(Arrays.asList("安徽省农村信用社", "安徽省农村信用社", "安徽农信", "安徽农村信用社银行", "安徽农村信用社", "安徽省农村信用社银行")),
    a121(Arrays.asList("福建省农村信用社联合社", "福建省农村信用社", "福建农信", "福建农村信用社银行", "福建农村信用社", "福建省农村信用社银行")),
    a122(Arrays.asList("贵州省农村信用社联合社", "贵州省农村信用社", "贵州农信", "贵州农村信用社银行", "贵州农村信用社", "贵州省农村信用社银行")),
    a123(Arrays.asList("甘肃省农村信用社", "甘肃省农村信用社", "甘肃农信", "甘肃农村信用社银行", "甘肃农村信用社", "甘肃省农村信用社银行")),
    a124(Arrays.asList("广东省农村信用社联合社", "广东省农村信用社", "广东农信", "广东农村信用社银行", "广东农村信用社", "广东省农村信用社银行")),
    a125(Arrays.asList("广西壮族自治区农村信用社联合社", "广西省农村信用社", "广西农信", "广西农村信用社银行", "广西农村信用社", "广西省农村信用社银行")),
    a126(Arrays.asList("湖南省农村信用社", "湖南省农村信用社", "湖南农信", "湖南农村信用社银行", "湖南农村信用社", "湖南省农村信用社银行")),
    a127(Arrays.asList("河南省农村信用社", "河南省农村信用社", "河南农信", "河南农村信用社银行", "河南农村信用社", "河南省农村信用社银行")),
    a128(Arrays.asList("河北省农村信用社", "河北省农村信用社", "河北农信", "河北农村信用社银行", "河北农村信用社", "河北省农村信用社银行")),
    a129(Arrays.asList("海南省农村信用社", "海南省农村信用社", "海南农信", "海南农村信用社银行", "海南农村信用社", "海南省农村信用社银行")),
    a130(Arrays.asList("黑龙江省农村信用社联合社", "黑龙江省农村信用社", "黑龙江农信", "黑龙江农村信用社银行", "黑龙江农村信用社", "黑龙江省农村信用社银行")),
    a131(Arrays.asList("吉林省农村信用社联合社", "吉林省农村信用社", "吉林农信", "吉林农村信用社银行", "吉林农村信用社", "吉林省农村信用社银行")),
    a132(Arrays.asList("江西省农村信用社", "江西省农村信用社", "江西农信", "江西农村信用社银行", "江西农村信用社", "江西省农村信用社银行")),
    a133(Arrays.asList("辽宁省农村信用社", "辽宁省农村信用社", "辽宁农信", "辽宁农村信用社银行", "辽宁农村信用社", "辽宁省农村信用社银行")),
    a134(Arrays.asList("内蒙古农村信用社联合社", "内蒙古省农村信用社", "内蒙古农信", "内蒙古农村信用社银行", "内蒙古农村信用社", "内蒙古省农村信用社银行")),
    a135(Arrays.asList("青海省农村信用社", "青海省农村信用社", "青海农信", "青海农村信用社银行", "青海农村信用社", "青海省农村信用社银行")),
    a136(Arrays.asList("山西省农村信用社", "山西省农村信用社", "山西农信", "山西农村信用社银行", "山西农村信用社", "山西省农村信用社银行")),
    a137(Arrays.asList("四川省农村信用社联合社", "四川省农村信用社", "四川农信", "四川农村信用社银行", "四川农村信用社", "四川省农村信用社银行")),
    a138(Arrays.asList("山东省农村信用社联合社", "山东省农村信用社", "山东农信", "山东农村信用社银行", "山东农村信用社", "山东省农村信用社银行")),
    a139(Arrays.asList("新疆农村信用社", "新疆省农村信用社", "新疆农信", "新疆农村信用社银行", "新疆农村信用社", "新疆省农村信用社银行")),
    a140(Arrays.asList("云南省农村信用社", "云南省农村信用社", "云南农信", "云南农村信用社银行", "云南农村信用社", "云南省农村信用社银行")),
    a141(Arrays.asList("浙江省农村信用社联合", "浙江省农村信用社", "浙江农信", "浙江农村信用社银行", "浙江农村信用社", "浙江省农村信用社银行")),
    a142(Arrays.asList("台州银行"));

    private final List<String> desc;

    BankCodeHyType(List<String> desc) {
        this.desc = desc;
    }

    public static BankCodeHyType getCodeByDesc( String desc ) {
        for ( BankCodeHyType enumType : BankCodeHyType.values() ) {
            if ( enumType.getDesc().contains( desc ) ) {
                return enumType;
            }
        }
        return null;
    }

}
