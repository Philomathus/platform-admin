package com.qiqilm.server.admin.enums;

/**
 * 游戏平台
 */
public enum EnumGamePlatform {
    //
    KY_CHESS(1, "开元棋牌"),
    OG_LIVE(2, "OG视讯"),
    OG_NEW(41, "新OG电子"),
    CX_LIVE(3, "77直播"),
    CX_LOTTERY(4, "77彩票"),
    AG_LIVE(5, "AG视讯"),
    MG_LIVE(6, "MG电子"),
    NG_LIVE(7, "UPG电子"),
    BBIN_SPORT(9, "BBIN体育"),
    BBIN_DIANZI(10, "BBIN电子"),
    BBIN_FISH(11, "BBIN捕鱼"),
    SHABA_SPORT(12, "沙巴体育"),
    ICG_DIANZI(13, "ICG电子"),
    MEITIAN_CHESS(14, "美天棋牌"),
    KAIXUAN_CHESS(15, "凯旋棋牌"),
    LEG_CHESS(16, "乐游棋牌"),
    NEWWORLD_CHESS(17, "新世界棋牌"),
    AFB(18, "AFB电子"),
    FANY_SPORT(19, "泛亚电竞"),
    BG_LIVE(21, "BG视讯"),
    BG_FISH(22, "BG捕鱼"),
    BG_DIANZI(23, "BG电游"),
    PRAGMATIC_PLAY(20, "PragmaticPlay"),
    BBIN_LIVE(8, "BBIN视讯"),
    KY_CHESS_NEW(50, "开元棋牌(新)"),
    KAIXUAN_CHESS_NEW(51, "凯旋棋牌(新)"),
    ;
    private int type;
    private String name;

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

    public static EnumGamePlatform getByType(int type) {
        for (EnumGamePlatform value : EnumGamePlatform.values()) {
            if (value.getType() == type) {
                return value;
            }
        }
        return null;
    }
}
