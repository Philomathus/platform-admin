package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 中秋博饼对象 wheel_dice
 *
 * @author 77tv
 * @date 2021-09-03
 */
@Data
public class WheelDice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private Long id;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 奖励 */
    @Excel(name = "奖励")
    private String prize;

    /** 权重 */
    @Excel(name = "权重")
    private Long weight;

    /** 排序 */
    @Excel(name = "排序")
    private Long odr;

    /** 类型(1返现打码,2坐骑) */
    @Excel(name = "类型(1返现打码,2坐骑)")
    private Long type;

    /** 坐骑天数或是打码倍数 */
    @Excel(name = "坐骑天数或是打码倍数")
    private String des;

    /** 奖励对应的图示 */
    @Excel(name = "奖励对应的图示")
    private String diceValue;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("prize", getPrize())
                .append("weight", getWeight())
                .append("odr", getOdr())
                .append("type", getType())
                .append("des", getDes())
                .append("diceValue", getDiceValue())
                .toString();
    }
}
