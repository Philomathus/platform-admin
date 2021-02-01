package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 pay_channel_new
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Data
public class PayChannelNew extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 通道名称 */
    @Excel(name = "通道名称")
    private String name;

    /** 支付方式编码 */
    @Excel(name = "支付方式编码")
    private String payMethod;

    /** 支付属性  0http 1支付宝sdk 2第三方app */
    @Excel(name = "支付属性  0http 1支付宝sdk 2第三方app")
    private String payAttr;

    /** 失败次数 */
    @Excel(name = "失败次数")
    private Long failNum;

    /** 成功次数 */
    @Excel(name = "成功次数")
    private Long successNum;

    /** 累计成功金额 */
    @Excel(name = "累计成功金额")
    private BigDecimal totalSuccessMoney;

    /** 充值最低 */
    @Excel(name = "充值最低")
    private BigDecimal rechargeMin;

    /** 充值最高 */
    @Excel(name = "充值最高")
    private BigDecimal rechargeMax;

    /** 状态(1启用0停用) */
    @Excel(name = "状态(1启用0停用)")
    private String status;

    /** 是否允许回调(默认1 允许 0不允许) */
    @Excel(name = "是否允许回调(默认1 允许 0不允许)")
    private String isCanCallback;

    /** 排序号 */
    @Excel(name = "排序号")
    private Long indexes;

    /** 开放层级 */
    @Excel(name = "开放层级")
    private Long openLevel;

    /** 支付平台编号 */
    @Excel(name = "支付平台编号")
    private String payPlatformId;

    /** 支付类型编号 */
    @Excel(name = "支付类型编号")
    private String payTypeId;

    /** 优惠比例 */
    @Excel(name = "优惠比例")
    private BigDecimal discountBill;

    /** 快捷金额 */
    @Excel(name = "快捷金额")
    private String quickAmount;

    /** 输入类型 (自定义金额+快捷金额1 仅快捷金额0) */
    @Excel(name = "输入类型 (自定义金额+快捷金额1 仅快捷金额0)")
    private String inputType;

    /** 创建人 */
    @Excel(name = "创建人")
    private String creator;

    /** 修改人 */
    @Excel(name = "修改人")
    private String updator;

    @Excel(name = "支付平台名称")
    private String payPlatformName;

    @Excel(name = "支付类型名称")
    private String payTypeName;

    /** 通道费率 */
    @Excel(name = "通道费率")
    private BigDecimal payRate;

    public String getPayRateStr() {
        if (payRate != null) {
            return payRate.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).toString().concat("%");
        }
        return "";
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("payMethod", getPayMethod())
            .append("payAttr", getPayAttr())
            .append("failNum", getFailNum())
            .append("successNum", getSuccessNum())
            .append("totalSuccessMoney", getTotalSuccessMoney())
            .append("rechargeMin", getRechargeMin())
            .append("rechargeMax", getRechargeMax())
            .append("status", getStatus())
            .append("isCanCallback", getIsCanCallback())
            .append("indexes", getIndexes())
            .append("openLevel", getOpenLevel())
            .append("payPlatformId", getPayPlatformId())
            .append("payTypeId", getPayTypeId())
            .append("discountBill", getDiscountBill())
            .append("quickAmount", getQuickAmount())
            .append("inputType", getInputType())
            .append("remark", getRemark())
            .append("creator", getCreator())
            .append("createTime", getCreateTime())
            .append("updator", getUpdator())
            .append("updateTime", getUpdateTime())
            .append("payRate", getPayRate())
            .toString();
    }
}
