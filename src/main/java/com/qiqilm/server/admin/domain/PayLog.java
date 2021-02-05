package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 pay_log
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
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
    @Excel(name = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private Integer countTotal;//总成功笔数
    private BigDecimal countSuccessMoney;//总成功金额
    private Integer countSuccess;//成功笔数

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
