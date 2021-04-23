package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 live_user_bank
 *
 * @author 77tv
 * @date 2021-04-23
 */
@Data
public class LiveUserBank extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private Long id;

    /** 会员编号 */
    @Excel(name = "会员编号")
    private Long userId;

    /** 真实姓名 */
    @Excel(name = "真实姓名")
    private String realName;

    /** 银行类型id */
    @Excel(name = "银行类型id")
    private Long bankTypeId;

    /** 银行账号 */
    @Excel(name = "银行账号")
    private String bankAccount;

    /** 开户行 */
    @Excel(name = "开户行")
    private String bankAddress;

    /** 卡片类型1=银行卡2=支付宝 */
    @Excel(name = "卡片类型1=银行卡2=支付宝")
    private Long type;

    /** 是否默认 1是，0否 */
    @Excel(name = "是否默认 1是，0否")
    private Integer dv;

    /** $column.columnComment */
    @Excel(name = "是否默认 1是，0否")
    private String realBankAddress;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("realName", getRealName())
            .append("bankTypeId", getBankTypeId())
            .append("bankAccount", getBankAccount())
            .append("bankAddress", getBankAddress())
            .append("createTime", getCreateTime())
            .append("type", getType())
            .append("dv", getDv())
            .append("realBankAddress", getRealBankAddress())
            .toString();
    }
}