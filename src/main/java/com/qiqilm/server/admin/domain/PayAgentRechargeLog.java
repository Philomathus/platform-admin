package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 pay_agent_recharge_log
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
public class PayAgentRechargeLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 订单号主键 */
    private String orderNo;

    /** 代充账号 */
    @Excel(name = "代充账号")
    private String rechargeAcount;

    /** 代充昵称 */
    @Excel(name = "代充昵称")
    private String rechargeNickName;

    /** 会员ID */
    @Excel(name = "会员ID")
    private String memberId;

    /** 会员账号 */
    @Excel(name = "会员账号")
    private String userName;

    /** 上分金额 */
    @Excel(name = "上分金额")
    private BigDecimal money;

    private Integer countNumber;
    private BigDecimal countMoney;
    @JsonIgnore
    private String[] selectDate;
    private String   selectStartDate;
    @JsonIgnore
    private String   selectEndDate;
    @JsonIgnore
    private String   searchValue;



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("orderNo", getOrderNo())
            .append("rechargeAcount", getRechargeAcount())
            .append("rechargeNickName", getRechargeNickName())
            .append("memberId", getMemberId())
            .append("userName", getUserName())
            .append("money", getMoney())
            .append("createTime", getCreateTime())
            .toString();
    }
}
