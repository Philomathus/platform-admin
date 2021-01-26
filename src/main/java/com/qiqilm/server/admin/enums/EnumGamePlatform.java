package com.qiqilm.server.admin.enums;
/**
 * 游戏平台
 */
public enum EnumGamePlatform {

    KY_CHESS(1,"开元棋牌"),
    OG_LIVE(2,"OG视讯"),
    CX_LIVE(3,"77直播"),
    CX_LOTTERY(4,"77彩票"),
    AG_LIVE(5,"AG视讯"),

    ;
    private int type;
    private String name ;

    EnumGamePlatform(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
