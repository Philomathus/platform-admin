package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * h5插件对象 h5_plugin
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class H5Plugin extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 1=彩票2=转盘才3=转盘彩票 */
    private Long id;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 1 启用 0 禁用 */
    @Excel(name = "1 启用 0 禁用")
    private Long status;

    /** 内容地址 */
    @Excel(name = "内容地址")
    private String conUrl;

    /** 图标地址 */
    @Excel(name = "图标地址")
    private String iconUrl;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getStatus() {
        return status;
    }
    public void setConUrl(String conUrl) {
        this.conUrl = conUrl;
    }

    public String getConUrl() {
        return conUrl;
    }
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("status", getStatus())
            .append("conUrl", getConUrl())
            .append("iconUrl", getIconUrl())
            .toString();
    }
}
