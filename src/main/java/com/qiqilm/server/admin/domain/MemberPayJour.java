package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 member_pay_jour
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class MemberPayJour extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 会员编号 */
    @Excel(name = "会员编号")
    private String memberId;

    /** 支付平台编号 */
    @Excel(name = "支付平台编号")
    private String platformId;

    /** 支付通道编码 */
    @Excel(name = "支付通道编码")
    private String channelId;

    /** 支付方式 */
    @Excel(name = "支付方式")
    private String paymentMethod;

    /** 本系统订单号 */
    @Excel(name = "本系统订单号")
    private String orderNo;

    /** 上游订单号 */
    @Excel(name = "上游订单号")
    private String tradeSn;

    /** 请求金额 */
    @Excel(name = "请求金额")
    private BigDecimal money;

    /** 实际到账金额 */
    @Excel(name = "实际到账金额")
    private BigDecimal subMoney;

    /** 支付接口的支付地址 */
    @Excel(name = "支付接口的支付地址")
    private String paymentCode;

    /** 支付成功时间(上游回调时间) */
    @Excel(name = "支付成功时间(上游回调时间)")
    private String paymentTime;

    /** 商户下单时间 */
    @Excel(name = "商户下单时间")
    private String payTime;

    /** 状态(1 成功0失败 -1待确认) */
    @Excel(name = "状态(1 成功0失败 -1待确认)")
    private String status;

    /** 是否是人工补单 */
    @Excel(name = "是否是人工补单")
    private Integer isPatchOrder;

    /** 省 */
    @Excel(name = "省")
    private String province;

    /** 市 */
    @Excel(name = "市")
    private String city;

    /** 区 */
    @Excel(name = "区")
    private String area;

    /** 通道手续费 */
    @Excel(name = "通道手续费")
    private BigDecimal platformRate;

    /** 近期通道成功率 */
    @Excel(name = "近期通道成功率")
    private BigDecimal currentSuccessRate;

    /** 补单操作员 */
    @Excel(name = "补单操作员")
    private String manWork;

    /** 账号 */
    @Excel(name = "账号")
    private String userName;

    /** 是否首次1是0否 */
    @Excel(name = "是否首次1是0否")
    private Long first;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberId() {
        return memberId;
    }
    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getPlatformId() {
        return platformId;
    }
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }
    public void setTradeSn(String tradeSn) {
        this.tradeSn = tradeSn;
    }

    public String getTradeSn() {
        return tradeSn;
    }
    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public BigDecimal getMoney() {
        return money;
    }
    public void setSubMoney(BigDecimal subMoney) {
        this.subMoney = subMoney;
    }

    public BigDecimal getSubMoney() {
        return subMoney;
    }
    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public String getPaymentCode() {
        return paymentCode;
    }
    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getPaymentTime() {
        return paymentTime;
    }
    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getPayTime() {
        return payTime;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
    public void setIsPatchOrder(Integer isPatchOrder) {
        this.isPatchOrder = isPatchOrder;
    }

    public Integer getIsPatchOrder() {
        return isPatchOrder;
    }
    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvince() {
        return province;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }
    public void setArea(String area) {
        this.area = area;
    }

    public String getArea() {
        return area;
    }
    public void setPlatformRate(BigDecimal platformRate) {
        this.platformRate = platformRate;
    }

    public BigDecimal getPlatformRate() {
        return platformRate;
    }
    public void setCurrentSuccessRate(BigDecimal currentSuccessRate) {
        this.currentSuccessRate = currentSuccessRate;
    }

    public BigDecimal getCurrentSuccessRate() {
        return currentSuccessRate;
    }
    public void setManWork(String manWork) {
        this.manWork = manWork;
    }

    public String getManWork() {
        return manWork;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
    public void setFirst(Long first) {
        this.first = first;
    }

    public Long getFirst() {
        return first;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("memberId", getMemberId())
            .append("platformId", getPlatformId())
            .append("channelId", getChannelId())
            .append("paymentMethod", getPaymentMethod())
            .append("orderNo", getOrderNo())
            .append("tradeSn", getTradeSn())
            .append("money", getMoney())
            .append("subMoney", getSubMoney())
            .append("paymentCode", getPaymentCode())
            .append("paymentTime", getPaymentTime())
            .append("payTime", getPayTime())
            .append("status", getStatus())
            .append("isPatchOrder", getIsPatchOrder())
            .append("remark", getRemark())
            .append("province", getProvince())
            .append("city", getCity())
            .append("area", getArea())
            .append("platformRate", getPlatformRate())
            .append("currentSuccessRate", getCurrentSuccessRate())
            .append("manWork", getManWork())
            .append("userName", getUserName())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("first", getFirst())
            .toString();
    }
}
