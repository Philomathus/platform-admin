package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 pay_agent_recharge_account_log
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class PayAgentRechargeAccountLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 订单号主键 */
    private String orderNo;

    /** 代充人账号 */
    @Excel(name = "代充人账号")
    private String account;

    /** 代充人昵称 */
    @Excel(name = "代充人昵称")
    private String nickName;

    /** 汇款银行卡ID */
    @Excel(name = "汇款银行卡ID")
    private Long bankId;

    /** 汇款金额 */
    @Excel(name = "汇款金额")
    private BigDecimal rechargeMoney;

    /** 实际到账金额 */
    @Excel(name = "实际到账金额")
    private BigDecimal subMoney;

    /** 汇款姓名 */
    @Excel(name = "汇款姓名")
    private String rechargeRealName;

    /** 状态(0已提交1锁定2已拒绝3已存入4存入失败) */
    @Excel(name = "状态(0已提交1锁定2已拒绝3已存入4存入失败)")
    private Long status;

    /** 审核人 */
    @Excel(name = "审核人")
    private String opName;

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }
    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }
    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public Long getBankId() {
        return bankId;
    }
    public void setRechargeMoney(BigDecimal rechargeMoney) {
        this.rechargeMoney = rechargeMoney;
    }

    public BigDecimal getRechargeMoney() {
        return rechargeMoney;
    }
    public void setSubMoney(BigDecimal subMoney) {
        this.subMoney = subMoney;
    }

    public BigDecimal getSubMoney() {
        return subMoney;
    }
    public void setRechargeRealName(String rechargeRealName) {
        this.rechargeRealName = rechargeRealName;
    }

    public String getRechargeRealName() {
        return rechargeRealName;
    }
    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getStatus() {
        return status;
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
            .append("account", getAccount())
            .append("nickName", getNickName())
            .append("bankId", getBankId())
            .append("rechargeMoney", getRechargeMoney())
            .append("subMoney", getSubMoney())
            .append("rechargeRealName", getRechargeRealName())
            .append("remark", getRemark())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("opName", getOpName())
            .toString();
    }
}
