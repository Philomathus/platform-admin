package com.qiqilm.server.admin.core.vo.req;

import com.qiqilm.server.admin.core.vo.BaseEntity;

public class reqMemberGameData extends BaseEntity {

    private String platform_name;

    private String son_platform_name;

    private String pubStartDate;

    public String getPlatform_name() {
        return platform_name;
    }

    public void setPlatform_name(String platform_name) {
        this.platform_name = platform_name;
    }

    public String getSon_platform_name() {
        return son_platform_name;
    }

    public void setSon_platform_name(String son_platform_name) {
        this.son_platform_name = son_platform_name;
    }

    public String getPubStartDate() {
        return pubStartDate;
    }

    public void setPubStartDate(String pubStartDate) {
        this.pubStartDate = pubStartDate;
    }

    public String getPubEndDate() {
        return pubEndDate;
    }

    public void setPubEndDate(String pubEndDate) {
        this.pubEndDate = pubEndDate;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    private String pubEndDate;

    private String account;
}
