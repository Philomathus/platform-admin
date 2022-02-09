package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 派送彩金暂存表对象 member_money
 *
 * @author 77tv
 * @date 2022-02-09
 */
@Data
public class MemberMoney extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 会员id */
    @Excel(name = "会员id")
    private String memberId;

    /** 派送金额 */
    @Excel(name = "派送金额")
    private BigDecimal money;

    private String moneydes;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("memberId", getMemberId())
            .append("money", getMoney())
            .toString();
    }
}
