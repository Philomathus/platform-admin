package com.qiqilm.server.admin.enums;

/**
 * 锁
 */
public enum EnumLock {

    game("game:"),

    member("member:"),
    adminUser("adminUser:");

    private String key;

    EnumLock(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
