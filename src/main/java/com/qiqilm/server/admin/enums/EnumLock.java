package com.qiqilm.server.admin.enums;

/**
 * 锁
 */
public enum EnumLock {

    game("game:"),

    loginUser("loginUserName:"),

    member("member:"),
    adminUser("adminUser:"),
    Anchor("Anchor"),
    payAgent("payAgent:"),

    adminTask("adminTask:");

    private String key;

    EnumLock(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
