package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 开奖规则说明对象 lottery_rule
 *
 * @author 77tv
 * @date 2021-02-26
 */
@Data
public class LotteryRule extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 彩票类型主键 */
    private Long id;

    /** 彩票类型名称 */
    @Excel(name = "彩票类型名称")
    private String name;

    /** 彩票类型id */
    @Excel(name = "彩票类型id")
    private String kind;

    /** 排序号 */
    @Excel(name = "排序号")
    private Integer ind;

    /** 开奖说明 */
    @Excel(name = "开奖说明")
    private String des;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("kind", getKind())
            .append("ind", getInd())
            .append("des", getDes())
            .toString();
    }
}
