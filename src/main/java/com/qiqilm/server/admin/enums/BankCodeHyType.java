package com.qiqilm.server.admin.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;


@Getter
public enum BankCodeHyType {

    a1(Arrays.asList("北京农商银行")),
    a2(Arrays.asList("邮政储蓄")),
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

    a101(Arrays.asList("常熟农商银行")),
    a102(Arrays.asList("成都农商银行")),
    a103(Arrays.asList("重庆农村商业银行")),
    a104(Arrays.asList("大连农村商业银行")),
    a105(Arrays.asList("东莞农村商业银行")),
    a106(Arrays.asList("广州农村商业银行")),
    a107(Arrays.asList("海口联合农商银行")),
    a108(Arrays.asList("江南农村商业银行")),
    a109(Arrays.asList("江苏江阴农村商业银行")),
    a110(Arrays.asList("昆山农村商业银行")),
    a111(Arrays.asList("南海农商银行")),
    a112(Arrays.asList("宁夏黄河农村商业银行")),
    a113(Arrays.asList("尧都农商银行村镇银行")),
    a114(Arrays.asList("顺德农商银行")),
    a115(Arrays.asList("天津农商银行")),
    a116(Arrays.asList("吴江农村商业银行")),
    a117(Arrays.asList("武汉农村商业银行")),
    a118(Arrays.asList("张家港农村商业银行")),
    a119(Arrays.asList("江苏省农村信用社联合社")),
    a120(Arrays.asList("安徽省农村信用社")),
    a121(Arrays.asList("福建省农村信用社联合社")),
    a122(Arrays.asList("贵州省农村信用社联合社")),
    a123(Arrays.asList("甘肃省农村信用社")),
    a124(Arrays.asList("广东省农村信用社联合社")),
    a125(Arrays.asList("广西壮族自治区农村信用社联合社")),
    a126(Arrays.asList("湖南省农村信用社")),
    a127(Arrays.asList("河南省农村信用社")),
    a128(Arrays.asList("河北省农村信用社")),
    a129(Arrays.asList("海南省农村信用社")),
    a130(Arrays.asList("黑龙江省农村信用社联合社")),
    a131(Arrays.asList("吉林省农村信用社联合社")),
    a132(Arrays.asList("江西省农村信用社")),
    a133(Arrays.asList("辽宁省农村信用社")),
    a134(Arrays.asList("内蒙古农村信用社联合社")),
    a135(Arrays.asList("青海省农村信用社")),
    a136(Arrays.asList("山西省农村信用社")),
    a137(Arrays.asList("四川省农村信用社联合社")),
    a138(Arrays.asList("山东省农村信用社联合社")),
    a139(Arrays.asList("新疆农村信用社")),
    a140(Arrays.asList("云南省农村信用社")),
    a141(Arrays.asList("浙江省农村信用社联合")),
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
