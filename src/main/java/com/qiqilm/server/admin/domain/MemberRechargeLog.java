package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 会员充值记录对象 member_recharge_log
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class MemberRechargeLog extends BaseEntity {
    private static final Long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 会员编号 */
    @Excel(name = "会员编号")
    private String memberId;

    /** 会员账号 */
    @Excel(name = "会员账号")
    private String userName;

    /** 充值金额 */
    @Excel(name = "充值金额")
    private BigDecimal rechargeMoney;

    /** 银行名称 */
    @Excel(name = "银行名称")
    private String bankName;

    /** 银行账号 */
    @Excel(name = "银行账号")
    private String bankAccount;

    private String[] selectDate;

    /** 状态(0已提交1初级审核通过2审核不通过3终极审核通过4入库失败) */
    @Excel(name = "状态")
    private Integer status;

    /** 操作人 */
    @Excel(name = "操作人")
    private String opName;

    /** 开户地址 */
    @Excel(name = "开户地址")
    private String bankAddress;

    /** 充值类型(1线下，10线上) */
    @Excel(name = "充值类型")
    private Integer type;

    /** 存款人姓名 */
    @Excel(name = "存款人姓名")
    private String rechargeUserName;

    /** 收款人 */
    @Excel(name = "收款人")
    private String bankUserName;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderNo;

    /** 优惠比例 */
    @Excel(name = "优惠比例")
    private BigDecimal discountBill;

    /** 是否首次1是0否 */
    @Excel(name = "是否首次")
    private Integer first;
    private String startDate;
    private String endDate;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String[] getSelectDate() {
        return selectDate;
    }

    public void setSelectDate(String[] selectDate) {
        this.selectDate = selectDate;
    }

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
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
    public void setRechargeMoney(BigDecimal rechargeMoney) {
        this.rechargeMoney = rechargeMoney;
    }

    public BigDecimal getRechargeMoney() {
        return rechargeMoney;
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
    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
    public void setOpName(String opName) {
        this.opName = opName;
    }

    public String getOpName() {
        return opName;
    }
    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public String getBankAddress() {
        return bankAddress;
    }
    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
    public void setRechargeUserName(String rechargeUserName) {
        this.rechargeUserName = rechargeUserName;
    }

    public String getRechargeUserName() {
        return rechargeUserName;
    }
    public void setBankUserName(String bankUserName) {
        this.bankUserName = bankUserName;
    }

    public String getBankUserName() {
        return bankUserName;
    }
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }
    public void setDiscountBill(BigDecimal discountBill) {
        this.discountBill = discountBill;
    }

    public BigDecimal getDiscountBill() {
        return discountBill;
    }
    public void setFirst(Integer first) {
        this.first = first;
    }

    public Integer getFirst() {
        return first;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("memberId", getMemberId())
            .append("userName", getUserName())
            .append("rechargeMoney", getRechargeMoney())
            .append("bankName", getBankName())
            .append("bankAccount", getBankAccount())
            .append("status", getStatus())
            .append("remark", getRemark())
            .append("opName", getOpName())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("bankAddress", getBankAddress())
            .append("type", getType())
            .append("rechargeUserName", getRechargeUserName())
            .append("bankUserName", getBankUserName())
            .append("orderNo", getOrderNo())
            .append("discountBill", getDiscountBill())
            .append("first", getFirst())
            .toString();
    }
}
