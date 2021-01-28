package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 pay_agent_recharge_log
 *
 * @author 77tv
 * @date 2021-01-26
 */
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

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }
    public void setRechargeAcount(String rechargeAcount) {
        this.rechargeAcount = rechargeAcount;
    }

    public String getRechargeAcount() {
        return rechargeAcount;
    }
    public void setRechargeNickName(String rechargeNickName) {
        this.rechargeNickName = rechargeNickName;
    }

    public String getRechargeNickName() {
        return rechargeNickName;
    }
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberId() {
        return memberId;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public BigDecimal getMoney() {
        return money;
    }

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
