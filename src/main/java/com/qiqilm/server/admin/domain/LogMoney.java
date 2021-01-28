package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 log_money
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
public class LogMoney extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 系统编号
     */
    private String id;

    /**
     * 会员编号
     */
    @Excel( name = "会员编号" )
    private String userId;

    /**
     * 账号
     */
    @Excel( name = "账号" )
    private String userName;

    /**
     * 变化类型
     */
    @Excel( name = "变化类型" )
    private Integer type;

    /**
     * 描述
     */
    @Excel( name = "描述" )
    private String des;

    /**
     * 收入
     */
    @Excel( name = "收入" )
    private BigDecimal income;

    /**
     * 支出
     */
    @Excel( name = "支出" )
    private BigDecimal pay;

    /**
     * 余额
     */
    @Excel( name = "余额" )
    private BigDecimal total;

    /**
     * $column.columnComment
     */
    @Excel( name = "余额" )
    private String agent;

    /**
     * 变化前余额
     */
    @Excel( name = "变化前余额" )
    private BigDecimal totalBefore;

    /**
     * 备注信息
     */
    @Excel( name = "备注信息" )
    private String mark;

    /**
     * 备注订单号
     */
    @Excel( name = "备注订单号" )
    private String markorder;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("type", getType())
            .append("des", getDes())
            .append("income", getIncome())
            .append("pay", getPay())
            .append("total", getTotal())
            .append("agent", getAgent())
            .append("createTime", getCreateTime())
            .append("totalBefore", getTotalBefore())
            .append("mark", getMark())
            .append("markorder", getMarkorder())
            .toString();
    }
}
