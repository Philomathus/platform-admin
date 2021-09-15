package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 config_usdt_recharge
 *
 * @author 77tv
 * @date 2021-09-11
 */
@Data
public class ConfigUsdtRecharge extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private Long id;

    /** 渠道名称 */
    @Excel(name = "渠道名称")
    private String channelName;

    /** 链名称 */
    @Excel(name = "链名称")
    private String chainName;

    /** 充值地址 */
    @Excel(name = "充值地址")
    private String rechargeAddress;

    /** 优惠比例 */
    @Excel(name = "优惠比例")
    private BigDecimal discountBill;

    /** usdt汇率 */
    @Excel(name = "usdt汇率")
    private BigDecimal exchangeRate;

    /** 排序 */
    @Excel(name = "排序")
    private Long indexs;

    /** 钱包二维码 */
    @Excel(name = "钱包二维码")
    private String icon;

    /** 状态(1启用0停用) */
    @Excel(name = "状态(1启用0停用)")
    private Long status;

    /** 开放层级最小 */
    @Excel(name = "开放层级最小")
    private Long openLevel;

    /** 开放层级最大 */
    @Excel(name = "开放层级最大")
    private Long openLevelMax;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("channelName", getChannelName())
            .append("chainName", getChainName())
            .append("rechargeAddress", getRechargeAddress())
            .append("discountBill", getDiscountBill())
            .append("exchangeRate", getExchangeRate())
            .append("indexs", getIndexs())
            .append("icon", getIcon())
            .append("status", getStatus())
            .append("openLevel", getOpenLevel())
            .append("openLevelMax", getOpenLevelMax())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
