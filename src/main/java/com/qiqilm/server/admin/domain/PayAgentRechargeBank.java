package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 pay_agent_recharge_bank
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
public class PayAgentRechargeBank extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 收款账号名称 */
    @Excel(name = "收款账号名称")
    private String name;

    /** 收款账号 */
    @Excel(name = "收款账号")
    private String bankAccount;

    /** 银行名称 */
    @Excel(name = "银行名称")
    private String bankName;

    /** 银行图标 */
    @Excel(name = "银行图标")
    private String icon;

    /** 收款人 */
    @Excel(name = "收款人")
    private String accountName;

    /** 状态(1启用0停用) */
    @Excel(name = "状态(1启用0停用)")
    private String status;

    /** 创建人 */
    @Excel(name = "创建人")
    private String creator;

    /** 修改人 */
    @Excel(name = "修改人")
    private String updator;



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("bankAccount", getBankAccount())
            .append("bankName", getBankName())
            .append("icon", getIcon())
            .append("accountName", getAccountName())
            .append("status", getStatus())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .append("creator", getCreator())
            .append("updateTime", getUpdateTime())
            .append("updator", getUpdator())
            .toString();
    }
}
