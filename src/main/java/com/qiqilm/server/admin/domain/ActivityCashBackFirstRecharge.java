package com.qiqilm.server.admin.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

@Data
public class ActivityCashBackFirstRecharge {
    /** 主键id */
    private Long id;

    /** 当日存款总额最小值 */
    private Long depositTotalMin;

    /** 当日存款总额最大值 */
    private Long depositTotalMax;

    /** 反现金 */
    private BigDecimal rebate;

    /** 状态(1 启用 0 停用 ) */
    private String status;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("depositTotalMin", getDepositTotalMin())
            .append("depositTotalMax", getDepositTotalMax())
            .append("rebate", getRebate())
            .append("status", getStatus())
            .toString();
    }
}
