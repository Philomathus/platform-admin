package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
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

    /** 彩票类型 */
    private Long id;

    /** 彩票类型名称 */
    @Excel(name = "彩票类型名称")
    private String name;

    /** 开奖说明 */
    @Excel(name = "开奖说明")
    private String des;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("des", getDes())
            .toString();
    }
}
