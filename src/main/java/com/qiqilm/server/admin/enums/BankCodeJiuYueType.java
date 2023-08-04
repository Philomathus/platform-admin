package com.qiqilm.server.admin.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum BankCodeJiuYueType {
    //
    a101(Arrays.asList("工商银行")),
    a102(Arrays.asList("农业银行")),
    a103(Arrays.asList("中国银行")),
    a104(Arrays.asList("中国建设银行")),
    a105(Arrays.asList("交通银行")),
    a106(Arrays.asList("中信银行")),
    a107(Arrays.asList("中国光大银行")),
    a108(Arrays.asList("华夏银行")),
    a109(Arrays.asList("广发银行")),
    a110(Arrays.asList("平安银行")),
    a111(Arrays.asList("招商银行")),
    a112(Arrays.asList("兴业银行")),
    a113(Arrays.asList("浦发银行")),
    a114(Arrays.asList("北京银行")),
    a115(Arrays.asList("南京银行")),
    a116(Arrays.asList("宁波银行")),
    a117(Arrays.asList("杭州银行")),
    a118(Arrays.asList("上海银行")),
    a119(Arrays.asList("成都银行")),
    a120(Arrays.asList("富滇银行")),
    a121(Arrays.asList("常熟农商银行")),
    a122(Arrays.asList("中国邮政储蓄银行")),
    a123(Arrays.asList("中国民生银行")),
    a124(Arrays.asList("天津银行")),
    a125(Arrays.asList("齐鲁银行")),
    a126(Arrays.asList("河北银行")),
    a127(Arrays.asList("晋城银行")),
    a128(Arrays.asList("包商银行")),
    a129(Arrays.asList("鄂尔多斯银行")),
    a130(Arrays.asList("大连银行")),
    a131(Arrays.asList("锦州银行")),
    a132(Arrays.asList("昆仑银行")),
    a133(Arrays.asList("龙江银行")),
    a134(Arrays.asList("江苏银行")),
    a135(Arrays.asList("浙江稠州商业银行")),
    a136(Arrays.asList("浙江泰隆商业银行")),
    a137(Arrays.asList("浙江民泰商业银行")),
    a138(Arrays.asList("台州银行")),
    a139(Arrays.asList("九江银行")),
    a140(Arrays.asList("南昌银行")),
    a141(Arrays.asList("上饶银行")),
    a142(Arrays.asList("青岛银行")),
    a143(Arrays.asList("潍坊银行")),
    a144(Arrays.asList("威海市商业银行")),
    a145(Arrays.asList("汉口银行")),
    a146(Arrays.asList("长沙银行")),
    a147(Arrays.asList("哈尔滨银行")),
    a148(Arrays.asList("重庆银行")),
    a149(Arrays.asList("贵阳银行")),
    a150(Arrays.asList("兰州银行")),
    a151(Arrays.asList("青海银行")),
    a152(Arrays.asList("乌鲁木齐市商业银行")),
    a153(Arrays.asList("无锡农村商业银行")),
    a154(Arrays.asList("江阴农村商业银行")),
    a155(Arrays.asList("吴江农商行")),
    a156(Arrays.asList("广州农村商业银行")),
    a157(Arrays.asList("顺德农村商业银行")),
    a158(Arrays.asList("成都农村商业银行")),
    a159(Arrays.asList("重庆农村商业银行")),
    a160(Arrays.asList("恒丰银行")),
    a161(Arrays.asList("浙商银行")),
    a162(Arrays.asList("渤海银行")),
    a163(Arrays.asList("徽商银行")),
    a164(Arrays.asList("上海农商银行")),
    a165(Arrays.asList("农村信用社")),
    a166(Arrays.asList("北京农村商业银行")),
    a167(Arrays.asList("尧都农村商业银行")),
    a168(Arrays.asList("鄞州银行")),
    a169(Arrays.asList("福建省农村信用社")),
    a170(Arrays.asList("东亚银行"));

    private final List<String> desc;

    BankCodeJiuYueType( List<String> desc) {
        this.desc = desc;
    }

    public static BankCodeJiuYueType getCodeByDesc( String desc) {
        for ( BankCodeJiuYueType enumType : BankCodeJiuYueType.values()) {
            if (enumType.getDesc().contains(desc)) {
                return enumType;
            }
        }
        return null;
    }
}
