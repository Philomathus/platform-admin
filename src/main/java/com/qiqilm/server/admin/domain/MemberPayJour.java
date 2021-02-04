package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 线上充值信息对象 member_pay_jour
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
public class MemberPayJour extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 会员编号 */
    private String memberId;

    /** 支付平台编号 */
    private String platformId;

    /** 支付通道编码 */
    private String channelId;

    /** 支付方式 */
    private String paymentMethod;

    /** 本系统订单号 */
    private String orderNo;

    /** 上游订单号 */
    private String tradeSn;

    /** 请求金额 */
    private BigDecimal money;

    /** 实际到账金额 */
    private BigDecimal subMoney;

    /** 支付接口的支付地址 */
    private String paymentCode;

    /** 支付成功时间(上游回调时间) */
    private String paymentTime;

    /** 商户下单时间 */
    private String payTime;

    /** 状态(1 成功0失败 -1待确认) */
    private String status;

    /** 是否是人工补单 */
    private Integer isPatchOrder;

    /** 通道手续费 */
    private BigDecimal platformRate;

    /** 近期通道成功率 */
    private BigDecimal currentSuccessRate;

    /** 补单操作员 */
    private String manWork;

    /** 账号 */
    private String userName;

    /** 是否首次1是0否 */
    private Long first;

    private String createTimes;

    private String updateTimes;

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
