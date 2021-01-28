package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 config_bank
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class ConfigBank extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 银行编码 */
    @Excel(name = "银行编码")
    private String code;

    /** 银行名称 */
    @Excel(name = "银行名称")
    private String name;

    /** 图标 */
    @Excel(name = "图标")
    private String icon;

    /** 银行官网地址 */
    @Excel(name = "银行官网地址")
    private String url;

    /** 排序 */
    @Excel(name = "排序")
    private Long indexs;

    /** 银行账号 */
    @Excel(name = "银行账号")
    private String bankAccount;

    /** 状态(1启用0停用) */
    @Excel(name = "状态(1启用0停用)")
    private Long status;

    /** 开户人姓名 */
    @Excel(name = "开户人姓名")
    private String accountName;

    /** 开户地址 */
    @Excel(name = "开户地址")
    private String bankAddress;

    /** 优惠比例 */
    @Excel(name = "优惠比例")
    private BigDecimal discountBill;

    /** 开放层级 */
    @Excel(name = "开放层级")
    private Long openLevel;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
    public void setIndexs(Long indexs) {
        this.indexs = indexs;
    }

    public Long getIndexs() {
        return indexs;
    }
    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankAccount() {
        return bankAccount;
    }
    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getStatus() {
        return status;
    }
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountName() {
        return accountName;
    }
    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public String getBankAddress() {
        return bankAddress;
    }
    public void setDiscountBill(BigDecimal discountBill) {
        this.discountBill = discountBill;
    }

    public BigDecimal getDiscountBill() {
        return discountBill;
    }
    public void setOpenLevel(Long openLevel) {
        this.openLevel = openLevel;
    }

    public Long getOpenLevel() {
        return openLevel;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("icon", getIcon())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("url", getUrl())
            .append("indexs", getIndexs())
            .append("bankAccount", getBankAccount())
            .append("remark", getRemark())
            .append("status", getStatus())
            .append("accountName", getAccountName())
            .append("bankAddress", getBankAddress())
            .append("discountBill", getDiscountBill())
            .append("openLevel", getOpenLevel())
            .toString();
    }
}
