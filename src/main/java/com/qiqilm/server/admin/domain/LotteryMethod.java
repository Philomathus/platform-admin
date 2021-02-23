package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 彩票种类对象 lottery_method
 *
 * @author 77tv
 * @date 2021-02-23
 */
@Data
public class LotteryMethod extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private String id;

    /** 所属彩种类型 */
    @Excel(name = "所属彩种类型")
    private String lotteryType;


    /** 排序键（玩法序号） */
    @Excel(name = "排序键", readConverterExp = "玩=法序号")
    private Long order;


    /** 投注名称 */
    @Excel(name = "投注名称")
    private String name;


    /** 排序键（玩法序号） */
    @Excel(name = "排序键", readConverterExp = "玩=法序号")
    private Long ind;


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("lotteryType", getLotteryType())
            .append("order", getOrder())
            .append("name", getName())
            .append("ind", getInd())
            .toString();
    }
}
