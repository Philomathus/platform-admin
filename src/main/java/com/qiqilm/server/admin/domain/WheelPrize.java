package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 转盘奖励对象 wheel_prize
 *
 * @author 77tv
 * @date 2021-02-26
 */
@Data
public class WheelPrize extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private Long id;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 奖励 */
    @Excel(name = "奖励")
    private Long prize;

    /** 权重 */
    @Excel(name = "权重")
    private Long weight;

    /** 排序 */
    @Excel(name = "排序")
    private Long odr;

    /** wheel_type为0是抽奖转盘,1是皮肤转盘 */
    @Excel(name = "wheel_type为0是抽奖转盘,1是皮肤转盘")
    private Long wheelType;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("prize", getPrize())
            .append("weight", getWeight())
            .append("odr", getOdr())
            .append("wheelType", getWheelType())
            .toString();
    }
}
