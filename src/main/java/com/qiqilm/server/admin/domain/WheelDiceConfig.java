package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 wheel_dice_config
 *
 * @author 77tv
 * @date 2021-09-01
 */
@Data
public class WheelDiceConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 当日存款总额最小值 */
    @Excel(name = "当日存款总额最小值")
    private Long depositTotalMin;

    /** 当日存款总额最大值 */
    @Excel(name = "当日存款总额最大值")
    private Long depositTotalMax;

    /** 抽奖次数 */
    @Excel(name = "抽奖次数")
    private Long lotteryTimes;

    /** 状态(1 启用 0 停用 ) */
    @Excel(name = "状态(1 启用 0 停用 )")
    private String status;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("depositTotalMin", getDepositTotalMin())
            .append("depositTotalMax", getDepositTotalMax())
            .append("lotteryTimes", getLotteryTimes())
            .append("status", getStatus())
            .toString();
    }
}
