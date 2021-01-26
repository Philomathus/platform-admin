package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 live_guard_config
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class LiveGuardConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 规格(一个月，三个月，六个月，十二个月) */
    @Excel(name = "规格(一个月，三个月，六个月，十二个月)")
    private String specifications;

    /** 守护月数 */
    @Excel(name = "守护月数")
    private Long month;

    /** 价格 */
    @Excel(name = "价格")
    private BigDecimal price;

    /** 1银之守护2.星之守护 */
    @Excel(name = "1银之守护2.星之守护")
    private Long type;

    /** 礼物id */
    @Excel(name = "礼物id")
    private Long propId;

    /** 优惠价格 */
    @Excel(name = "优惠价格")
    private BigDecimal discountPrice;

    /** 赠送天数 */
    @Excel(name = "赠送天数")
    private Long giveday;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getSpecifications() {
        return specifications;
    }
    public void setMonth(Long month) {
        this.month = month;
    }

    public Long getMonth() {
        return month;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }
    public void setType(Long type) {
        this.type = type;
    }

    public Long getType() {
        return type;
    }
    public void setPropId(Long propId) {
        this.propId = propId;
    }

    public Long getPropId() {
        return propId;
    }
    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }
    public void setGiveday(Long giveday) {
        this.giveday = giveday;
    }

    public Long getGiveday() {
        return giveday;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("specifications", getSpecifications())
            .append("month", getMonth())
            .append("price", getPrice())
            .append("type", getType())
            .append("propId", getPropId())
            .append("discountPrice", getDiscountPrice())
            .append("giveday", getGiveday())
            .toString();
    }
}
