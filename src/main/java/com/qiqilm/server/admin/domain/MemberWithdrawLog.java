package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 member_withdraw_log
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class MemberWithdrawLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 会员编号 */
    @Excel(name = "会员编号")
    private String memberId;

    /** 提现金额 */
    @Excel(name = "提现金额")
    private BigDecimal withdrawMoney;

    /** 银行编码 */
    @Excel(name = "银行编码")
    private String bankCode;

    /** 提现银行 */
    @Excel(name = "提现银行")
    private String bankName;

    /** 提现账号 */
    @Excel(name = "提现账号")
    private String bankAccount;

    /** 开户地 */
    @Excel(name = "开户地")
    private String bankAddress;

    /** 收款人 */
    @Excel(name = "收款人")
    private String bankUserName;

    /** 状态(0申请中1锁定2审核不通过3人工入款成功 4代付中5代付失败6代付成功) */
    @Excel(name = "状态(0申请中1锁定2审核不通过3人工入款成功 4代付中5代付失败6代付成功)")
    private Long status;

    /** 提现类型(1提现到银行卡 2代付下单) */
    @Excel(name = "提现类型(1提现到银行卡 2代付下单)")
    private Long type;

    /** 操作人 */
    @Excel(name = "操作人")
    private String opName;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderNo;

    /** 账号 */
    @Excel(name = "账号")
    private String account;

    /** 是否首次1是0否 */
    @Excel(name = "是否首次1是0否")
    private Long first;

    /** 入款出款比 */
    @Excel(name = "入款出款比")
    private BigDecimal rechargeWithdrawRate;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberId() {
        return memberId;
    }
    public void setWithdrawMoney(BigDecimal withdrawMoney) {
        this.withdrawMoney = withdrawMoney;
    }

    public BigDecimal getWithdrawMoney() {
        return withdrawMoney;
    }
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankCode() {
        return bankCode;
    }
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankName() {
        return bankName;
    }
    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankAccount() {
        return bankAccount;
    }
    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public String getBankAddress() {
        return bankAddress;
    }
    public void setBankUserName(String bankUserName) {
        this.bankUserName = bankUserName;
    }

    public String getBankUserName() {
        return bankUserName;
    }
    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getStatus() {
        return status;
    }
    public void setType(Long type) {
        this.type = type;
    }

    public Long getType() {
        return type;
    }
    public void setOpName(String opName) {
        this.opName = opName;
    }

    public String getOpName() {
        return opName;
    }
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
    public void setFirst(Long first) {
        this.first = first;
    }

    public Long getFirst() {
        return first;
    }
    public void setRechargeWithdrawRate(BigDecimal rechargeWithdrawRate) {
        this.rechargeWithdrawRate = rechargeWithdrawRate;
    }

    public BigDecimal getRechargeWithdrawRate() {
        return rechargeWithdrawRate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("memberId", getMemberId())
            .append("withdrawMoney", getWithdrawMoney())
            .append("bankCode", getBankCode())
            .append("bankName", getBankName())
            .append("bankAccount", getBankAccount())
            .append("bankAddress", getBankAddress())
            .append("bankUserName", getBankUserName())
            .append("status", getStatus())
            .append("type", getType())
            .append("createTime", getCreateTime())
            .append("opName", getOpName())
            .append("updateTime", getUpdateTime())
            .append("orderNo", getOrderNo())
            .append("remark", getRemark())
            .append("account", getAccount())
            .append("first", getFirst())
            .append("rechargeWithdrawRate", getRechargeWithdrawRate())
            .toString();
    }
}
