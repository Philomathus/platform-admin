package com.qiqilm.server.admin.enums;

/**
 * 会员行为类型
 */
public enum EnumAction {
    gm(-1,"人工入款"),
    /** 登录相关 */
    register(0,"注册"),
    login(1,"登录"),
    logout(2,"登出"),
    password(3,"修改密码"),
    login_fail(4,"登录失败"),
    reg_fail(5,"注册失败"),


    /** 游戏平台相关 */
    shangfen(10,"上分"),
    xiafen(11,"下分"),





    /** 资金相关 */
    bindcard(50,"绑定银行卡"),
    cardcancel(51,"解除银行卡"),
    charge_agent(52,"代理充值"),
    charge_card(53,"银行卡充值"),
    withdraw(54,"提现申请"),
    codeclean(55,"主动洗码"),




    ;
    private int type ;
    private String des;

    public int getType() {
        return type;
    }

    public String getDes() {
        return des;
    }

    EnumAction(int type, String des) {
        this.type = type;
        this.des = des;
    }

}
