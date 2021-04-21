package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 member_card
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class MemberCard extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 姓名 */
    @Excel(name = "姓名")
    private String realName;

    /** 银行名称 */
    @Excel(name = "银行名称")
    private String bankName;

    /** 银行编码 */
    @Excel(name = "银行编码")
    private String bankCode;

    /** 银行账号 */
    @Excel(name = "银行账号")
    private String bankAccount;

    /** 银行地址 */
    @Excel(name = "银行地址")
    private String bankAddress;

    /** 会员编号 */
    @Excel(name = "会员编号")
    private String memberId;

    /** 卡片类型1=银行卡2=支付宝 */
    @Excel(name = "卡片类型1=银行卡2=支付宝")
    private Long type;

    /** 是否默认 */
    @Excel(name = "是否默认")
    private Integer dv;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRealName() {
        return realName;
    }
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankName() {
        return bankName;
    }
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankCode() {
        return bankCode;
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
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberId() {
        return memberId;
    }
    public void setType(Long type) {
        this.type = type;
    }

    public Long getType() {
        return type;
    }
    public void setDv(Integer dv) {
        this.dv = dv;
    }

    public Integer getDv() {
        return dv;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("realName", getRealName())
            .append("bankName", getBankName())
            .append("bankCode", getBankCode())
            .append("bankAccount", getBankAccount())
            .append("bankAddress", getBankAddress())
            .append("memberId", getMemberId())
            .append("createTime", getCreateTime())
            .append("type", getType())
            .append("dv", getDv())
            .toString();
    }
}
