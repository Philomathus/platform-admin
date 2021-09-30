package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 代充银行提示语对象 pay_agent_recharge_reminder
 *
 * @author 77tv
 * @date 2021-09-25
 */
@Data
public class PayAgentRechargeReminder extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 充值提示类型(card：银行卡) */
    @Excel(name = "充值提示类型(card：银行卡)")
    private String type;

    /** 内容 */
    @Excel(name = "内容")
    private String content;

    /** 状态(1开启 0关闭) */
    @Excel(name = "状态(1开启 0关闭)")
    private String status;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("type", getType())
            .append("content", getContent())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("status", getStatus())
            .toString();
    }
}
