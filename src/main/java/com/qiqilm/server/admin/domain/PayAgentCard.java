package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 代充人银行卡列表对象 pay_agent_card
 *
 * @author 77tv
 * @date 2021-09-24
 */
@Data
public class PayAgentCard extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 代充人id */
    @Excel(name = "代充人id")
    private Long agentId;

    /** 代充账号 */
    @Excel(name = "代充账号")
    private String account;

    /** 代充昵称 */
    @Excel(name = "代充昵称")
    private String nickName;

    /** 收款银行 */
    @Excel(name = "收款银行")
    private String bankName;

    /** 收款账号 */
    @Excel(name = "收款账号")
    private String bankAccount;

    /** 开户地点 */
    @Excel(name = "开户地点")
    private String openSite;

    /** 收款名称 */
    @Excel(name = "收款名称")
    private String accountName;

    /** 操作人 */
    @Excel(name = "操作人")
    private String operator;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "操作时间", width = 30, databaseFormat = "yyyy-MM-dd HH:mm:ss")
    private String operatorTime;

    /** 状态(0启用 1禁用) */
    @Excel(name = "状态(0启用 1禁用)")
    private String status;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("agentId", getAgentId())
            .append("account", getAccount())
            .append("nickName", getNickName())
            .append("bankName", getBankName())
            .append("bankAccount", getBankAccount())
            .append("openSite", getOpenSite())
            .append("accountName", getAccountName())
            .append("createTime", getCreateTime())
            .append("createBy", getUpdateTime())
            .append("operator", getOperator())
            .append("operatorTime", getOperatorTime())
            .append("status", getStatus())
            .toString();
    }
}
