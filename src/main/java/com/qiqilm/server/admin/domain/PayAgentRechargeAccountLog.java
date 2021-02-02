package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 pay_agent_recharge_account_log
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
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
    private String status;

    /** 审核人 */
    @Excel(name = "审核人")
    private String opName;

    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date createTime;

    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date updateTime;



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
