package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 会员提现信息对象 member_withdraw_log
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
public class MemberWithdrawLog extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private String[] downLoadDate = new String[2];
    /**
     * 系统编号
     */
    private String id;

    /**
     * 会员编号
     */
    @Excel(name = "会员编号", orderNum = "1")
    private String memberId;

    /**
     * 提现金额
     */
    @Excel(name = "提现金额", orderNum = "4")
    private BigDecimal withdrawMoney;

    /**
     * 提现银行
     */
    @Excel(name = "提现银行", orderNum = "3")
    private String bankName;

    /**
     * 提现账号
     */
    @Excel(name = "提现账号", orderNum = "6")
    private String bankAccount;

    /**
     * 开户地
     */
    @Excel(name = "开户地", orderNum = "5")
    private String bankAddress;

    /**
     * 收款人
     */
    @Excel(name = "收款人", orderNum = "2")
    private String bankUserName;

    /**
     * 状态(0申请中1锁定2审核不通过3人工入款成功 4代付中5代付失败6代付成功)
     */
    private Integer status;

    /**
     * 会员状态是否为套利号
     */
    private Integer memberStatus;

    @Excel(name = "状态", orderNum = "7")
    private String statusDes;

    /**
     * 提现类型(1提现到银行卡 2代付下单)
     */
    private Integer type;

    /**
     * 操作人
     */
    @Excel(name = "操作人", orderNum = "8")
    private String opName;

    /**
     * 订单号
     */
    @Excel(name = "订单号", orderNum = "9")
    private String orderNo;

    /**
     * 账号
     */
    @Excel(name = "会员名", orderNum = "10")
    private String account;

    @Excel(name = "备注", orderNum = "11")
    private String remark;

    /**
     * 是否首次1是0否
     */
    @Excel(name = "是否首次", orderNum = "12")
    private Integer first;

    /**
     * 入款出款比
     */
    @Excel(name = "入款出款比", orderNum = "13")
    private BigDecimal rechargeWithdrawRate;

    /**
     * 公司入款成功次数
     */
    @Excel(name = "公司入款成功次数", orderNum = "14")
    private BigDecimal bankCharge;

    /**
     * 银行卡省/市
     */
    @Excel(name = "银行卡省/市", orderNum = "15")
    private String realBankAddress;

    /**
     * 银行卡黑名单1是0否
     */
    @Excel(name = "银行卡黑名单", orderNum = "16")
    private String cardBlack = "0";

    private String SearchCardBlack;
    private String province;
    private String city;

    @JsonIgnore
    private String bankCode;
    @JsonIgnore
    private Integer priceMin;
    @JsonIgnore
    private Integer priceMax;
    @JsonIgnore
    private String[] searchTime;
    @JsonIgnore
    private String startTime;
    @JsonIgnore
    private String endTime;

    @JsonIgnore
    private String payAgentOrderNo;

    public String getStatusDes() {
        if (status != null) {
            switch (status) {
                case 0:
                    return "入账中";
                case 1:
                    return "初级审核通过";
                case 2:
                    return "审核不通过";
                case 3:
                    return "终极审核通过";
                case 4:
                    return "代付中";
                case 5:
                    return "代付失败";
                case 6:
                    return "代付成功";
                case 7:
                    return "出款异常";
                case 8:
                    return "人工代付中";
                default:
            }
        }
        return "";
    }

    public String getStartTime() {
        if (searchTime != null && searchTime.length > 0) {
            return searchTime[0];
        }
        return null;
    }

    public String getEndTime() {
        if (searchTime != null && searchTime.length > 0) {
            return searchTime[1];
        }
        return null;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("memberId", getMemberId())
                .append("withdrawMoney", getWithdrawMoney())
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
