package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 pay_agent_recharge_record
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class PayAgentRechargeRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 订单号主键 */
    private String orderNo;

    /** 代充账号 */
    @Excel(name = "代充账号")
    private String rechargeAcount;

    /** 代充昵称 */
    @Excel(name = "代充昵称")
    private String rechargeNickName;

    /** 存入(提出)类型 */
    @Excel(name = "存入(提出)类型")
    private String type;

    /** 存入(提出)金额 */
    @Excel(name = "存入(提出)金额")
    private BigDecimal money;

    /** 操作人 */
    @Excel(name = "操作人")
    private String opName;

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
    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public BigDecimal getMoney() {
        return money;
    }
    public void setOpName(String opName) {
        this.opName = opName;
    }

    public String getOpName() {
        return opName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("orderNo", getOrderNo())
            .append("rechargeAcount", getRechargeAcount())
            .append("rechargeNickName", getRechargeNickName())
            .append("type", getType())
            .append("remark", getRemark())
            .append("money", getMoney())
            .append("createTime", getCreateTime())
            .append("opName", getOpName())
            .toString();
    }
}
