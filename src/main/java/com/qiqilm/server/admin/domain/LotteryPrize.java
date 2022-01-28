package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 lottery_prize
 *
 * @author 77tv
 * @date 2022-01-27
 */
@Data
public class LotteryPrize extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private Long id;

    /** 名称 */
    @Excel(name = "名称")
    private String prizeName;

    /** 奖励 */
    @Excel(name = "奖励")
    private String prize;

    /** 奖品权重 */
    @Excel(name = "奖品权重")
    private Long prizeWeight;

    /** 活动类型 */
    @Excel(name = "活动类型")
    private Long type;

    /** 奖品（剩余）数量 */
    @Excel(name = "奖品")
    private Long prizeAmount;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("prizeName", getPrizeName())
            .append("prize", getPrize())
            .append("prizeWeight", getPrizeWeight())
            .append("type", getType())
            .append("prizeAmount", getPrizeAmount())
            .toString();
    }
}
