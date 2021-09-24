package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 pay_usdt_recharge
 *
 * @author 77tv
 * @date 2021-09-14
 */
@Data
public class PayUsdtRecharge extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private Long id;

    /** 会员编号 */
    @Excel(name = "会员编号")
    private String memberId;

    /** 会员账号 */
    @Excel(name = "会员账号")
    private String userName;

    /** 渠道名称 */
    @Excel(name = "渠道名称")
    private String channelName;

    /** 充值U数量 */
    @Excel(name = "充值U数量")
    private Long rechargeNumber;

    /** 充值金额 */
    @Excel(name = "充值金额")
    private BigDecimal rechargeMoney;

    /** 状态(0已提交1通过2驳回) */
    @Excel(name = "状态(0已提交1通过2驳回)")
    private String status;

    /** 优惠比例 */
    @Excel(name = "优惠比例")
    private BigDecimal discountBill;

    /** 链名称 */
    @Excel(name = "链名称")
    private String chainName;

    /** 充值地址 */
    @Excel(name = "充值地址")
    private String rechargeAddress;

    /** 交易id */
    @Excel(name = "交易id")
    private String transactionId;

    /** 操作人 */
    @Excel(name = "操作人")
    private String opName;

    @JsonIgnore
    private String[] selectDate;
    @JsonIgnore
    private String   selectStartDate;
    @JsonIgnore
    private String   selectEndDate;

    private Integer googleAuthCode;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("memberId", getMemberId())
            .append("userName", getUserName())
            .append("channelName", getChannelName())
            .append("rechargeNumber", getRechargeNumber())
            .append("rechargeMoney", getRechargeMoney())
            .append("status", getStatus())
            .append("remark", getRemark())
            .append("discountBill", getDiscountBill())
            .append("chainName", getChainName())
            .append("rechargeAddress", getRechargeAddress())
            .append("transactionId", getTransactionId())
            .append("opName", getOpName())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
