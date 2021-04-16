package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 config_bank
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
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
    private String status;

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
    @Excel(name = "开放层级最小")
    private Long openLevel;
    @Excel(name = "开放层级最大")
    private Integer openLevelMax;


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
            .append("openLevelMax", getOpenLevelMax())
            .toString();
    }
}
