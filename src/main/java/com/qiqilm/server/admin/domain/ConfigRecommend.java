package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 推广设置对象 config_recommend
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class ConfigRecommend extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 级别(1 一级 2 二级) */
    @Excel(name = "级别(1 一级 2 二级)")
    private Long level;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 比例 */
    @Excel(name = "比例")
    private BigDecimal bill;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setLevel(Long level) {
        this.level = level;
    }

    public Long getLevel() {
        return level;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setBill(BigDecimal bill) {
        this.bill = bill;
    }

    public BigDecimal getBill() {
        return bill;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("level", getLevel())
            .append("name", getName())
            .append("bill", getBill())
            .toString();
    }
}
