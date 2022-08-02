package com.qiqilm.server.admin.domain;


import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 轮池对象 wheel_pool
 *
 * @author rajesh
 * @date 2022-07-29
 */

@Data
public class WheelPool extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private Long id;

    /** 名称 */
    @Excel(name = "名称")
    private String name;


    /**秒钱 */
    @Excel(name = "秒钱")
    private BigDecimal secMoney;

    /** 金钱限制 */
    @Excel(name = "金钱限制")
    private BigDecimal limitMoney;

    /** 权重 */
    @Excel(name = "权重")
    private Long weight;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("secMoney", getSecMoney())
                .append("limitMoney", getLimitMoney())
                .append("weight", getWeight())
                .toString();
    }
}
