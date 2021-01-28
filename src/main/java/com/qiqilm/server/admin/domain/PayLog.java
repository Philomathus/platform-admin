package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 pay_log
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class PayLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 会员编号 */
    @Excel(name = "会员编号")
    private String memberId;

    /** 会员账号 */
    @Excel(name = "会员账号")
    private String memberAccount;

    /** 支付平台编号 */
    @Excel(name = "支付平台编号")
    private String platformId;

    /** 支付平台名称 */
    @Excel(name = "支付平台名称")
    private String platformName;

    /** 支付通道编号 */
    @Excel(name = "支付通道编号")
    private String channelId;

    /** 支付通道名称 */
    @Excel(name = "支付通道名称")
    private String channelName;

    /** 下单金额 */
    @Excel(name = "下单金额")
    private BigDecimal money;

    /** 是否下单成功 1成功 0 失败 */
    @Excel(name = "是否下单成功 1成功 0 失败")
    private Integer success;

    /** 失败原因 */
    @Excel(name = "失败原因")
    private String failReason;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberId() {
        return memberId;
    }
    public void setMemberAccount(String memberAccount) {
        this.memberAccount = memberAccount;
    }

    public String getMemberAccount() {
        return memberAccount;
    }
    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getPlatformId() {
        return platformId;
    }
    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getPlatformName() {
        return platformName;
    }
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelName() {
        return channelName;
    }
    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public BigDecimal getMoney() {
        return money;
    }
    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Integer getSuccess() {
        return success;
    }
    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public String getFailReason() {
        return failReason;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("memberId", getMemberId())
            .append("memberAccount", getMemberAccount())
            .append("platformId", getPlatformId())
            .append("platformName", getPlatformName())
            .append("channelId", getChannelId())
            .append("channelName", getChannelName())
            .append("money", getMoney())
            .append("success", getSuccess())
            .append("failReason", getFailReason())
            .append("createTime", getCreateTime())
            .toString();
    }
}
