package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 config_gametype
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class ConfigGametype extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private String id;

    /** 平台id */
    @Excel(name = "平台id")
    private String platformId;

    /** 平台名称 */
    @Excel(name = "平台名称")
    private String platformName;

    /** 子平台id */
    @Excel(name = "子平台id")
    private String sonPlatformId;

    /** 子平台名称 */
    @Excel(name = "子平台名称")
    private String sonPlatformName;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getPlatformId() {
        return platformId;
    }
    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getPlatformName() {
        return platformName;
    }
    public void setSonPlatformId(String sonPlatformId) {
        this.sonPlatformId = sonPlatformId;
    }

    public String getSonPlatformId() {
        return sonPlatformId;
    }
    public void setSonPlatformName(String sonPlatformName) {
        this.sonPlatformName = sonPlatformName;
    }

    public String getSonPlatformName() {
        return sonPlatformName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("platformId", getPlatformId())
            .append("platformName", getPlatformName())
            .append("sonPlatformId", getSonPlatformId())
            .append("sonPlatformName", getSonPlatformName())
            .toString();
    }
}