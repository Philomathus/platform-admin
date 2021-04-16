package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 礼物列对象 live_mount
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class LiveMount extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 免费领取VIP(-1只能买) */
    @Excel(name = "免费领取VIP(-1只能买)")
    private Long gvip;

    /** 坐骑名 */
    @Excel(name = "坐骑名")
    private String name;

    /** 0:禁用;1:启用;默认启用 */
    @Excel(name = "0:禁用;1:启用;默认启用")
    private String status;

    /** PC端图标 */
    @Excel(name = "PC端图标")
    private String iconUrl;

    /** svga动画路径 */
    @Excel(name = "svga动画路径")
    private String svgUrl;

    /** 价格 */
    @Excel(name = "价格")
    private BigDecimal price;

    /** 折扣价格 */
    @Excel(name = "折扣价格")
    private BigDecimal disPrice;

    /** 有效天数 */
    @Excel(name = "有效天数")
    private Long vday;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setGvip(Long gvip) {
        this.gvip = gvip;
    }

    public Long getGvip() {
        return gvip;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }
    public void setSvgUrl(String svgUrl) {
        this.svgUrl = svgUrl;
    }

    public String getSvgUrl() {
        return svgUrl;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }
    public void setDisPrice(BigDecimal disPrice) {
        this.disPrice = disPrice;
    }

    public BigDecimal getDisPrice() {
        return disPrice;
    }
    public void setVday(Long vday) {
        this.vday = vday;
    }

    public Long getVday() {
        return vday;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("gvip", getGvip())
            .append("name", getName())
            .append("status", getStatus())
            .append("iconUrl", getIconUrl())
            .append("svgUrl", getSvgUrl())
            .append("price", getPrice())
            .append("disPrice", getDisPrice())
            .append("vday", getVday())
            .toString();
    }
}
