package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 pay_agent_recharge_record
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
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

    @Excel(name = "操作时间")
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss" )
    public Date createTime;

    private String depositTotal;
    private String proposedTotal;
    private Integer googleAuthCode;


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
