package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 pay_agent_log
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class PayAgentLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 提现订单号 */
    @Excel(name = "提现订单号")
    private String withdrawOrderNo;

    /** 三方代付订单号 */
    @Excel(name = "三方代付订单号")
    private String payAgentOrderNo;

    /** 三方代付平台ID */
    @Excel(name = "三方代付平台ID")
    private String payAgentPlatId;

    /** 三方代付平台名称 */
    @Excel(name = "三方代付平台名称")
    private String payAgentPlatName;

    /** 会员ID */
    @Excel(name = "会员ID")
    private String memberId;

    /** 会员账号 */
    @Excel(name = "会员账号")
    private String memberAccount;

    /** 提现金额 */
    @Excel(name = "提现金额")
    private BigDecimal withdrawMoney;

    /** 回调时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "回调时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date callbackTime;

    /** 回调状态 0 回调中 1 成功 2失败 */
    @Excel(name = "回调状态 0 回调中 1 成功 2失败")
    private Long callbackStatus;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setWithdrawOrderNo(String withdrawOrderNo) {
        this.withdrawOrderNo = withdrawOrderNo;
    }

    public String getWithdrawOrderNo() {
        return withdrawOrderNo;
    }
    public void setPayAgentOrderNo(String payAgentOrderNo) {
        this.payAgentOrderNo = payAgentOrderNo;
    }

    public String getPayAgentOrderNo() {
        return payAgentOrderNo;
    }
    public void setPayAgentPlatId(String payAgentPlatId) {
        this.payAgentPlatId = payAgentPlatId;
    }

    public String getPayAgentPlatId() {
        return payAgentPlatId;
    }
    public void setPayAgentPlatName(String payAgentPlatName) {
        this.payAgentPlatName = payAgentPlatName;
    }

    public String getPayAgentPlatName() {
        return payAgentPlatName;
    }
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberId() {
        return memberId;
    }
    public void setMemberAccount(String memberAccount) {
        this.memberAccount = memberAccount;
    }

    public String getMemberAccount() {
        return memberAccount;
    }
    public void setWithdrawMoney(BigDecimal withdrawMoney) {
        this.withdrawMoney = withdrawMoney;
    }

    public BigDecimal getWithdrawMoney() {
        return withdrawMoney;
    }
    public void setCallbackTime(Date callbackTime) {
        this.callbackTime = callbackTime;
    }

    public Date getCallbackTime() {
        return callbackTime;
    }
    public void setCallbackStatus(Long callbackStatus) {
        this.callbackStatus = callbackStatus;
    }

    public Long getCallbackStatus() {
        return callbackStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("withdrawOrderNo", getWithdrawOrderNo())
            .append("payAgentOrderNo", getPayAgentOrderNo())
            .append("payAgentPlatId", getPayAgentPlatId())
            .append("payAgentPlatName", getPayAgentPlatName())
            .append("memberId", getMemberId())
            .append("memberAccount", getMemberAccount())
            .append("withdrawMoney", getWithdrawMoney())
            .append("createTime", getCreateTime())
            .append("callbackTime", getCallbackTime())
            .append("callbackStatus", getCallbackStatus())
            .toString();
    }
}
