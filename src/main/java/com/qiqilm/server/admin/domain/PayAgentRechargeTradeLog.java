package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 pay_agent_recharge_trade_log
 *
 * @author 77tv
 * @date 2021-02-01
 */
@Data
public class PayAgentRechargeTradeLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 订单主键 */
    private String orderNo;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 代充账号 */
    @Excel(name = "代充账号")
    private String account;

    /** 代充昵称 */
    @Excel(name = "代充昵称")
    private String nickName;

    /** 收入（支出） */
    @Excel(name = "收入", readConverterExp = "支=出")
    private BigDecimal income;

    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date createTime;


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("orderNo", getOrderNo())
            .append("name", getName())
            .append("account", getAccount())
            .append("nickName", getNickName())
            .append("income", getIncome())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}
