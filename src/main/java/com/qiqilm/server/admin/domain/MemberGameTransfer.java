package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 【请填写功能名称】对象 member_game_transfer
 *
 * @author 77tv
 * @date 2021-08-05
 */
@Data
public class MemberGameTransfer extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private String id;

    /** 玩家ID */
    private String userId;

    /** 代理ID */
    private String agent;

    /** 平台ID */
    private String platformId;

    /** 交易ID */
    private String transferId;

    /** 产品类型 */
    private String product;

    /** 交易状态 */
    private String transferState;

    /** 交易类型 */
    private String transferType;

    /** 交易金额 */
    private BigDecimal transferAmount;

    /** 交易时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date transferTime;

    /** 时间数组 **/
    @JsonIgnore
    private String[] selectDate;

    private String   startTime;
    private String   endTime;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("userId", getUserId())
                .append("agent", getAgent())
                .append("platformId", getPlatformId())
                .append("transferId", getTransferId())
                .append("product", getProduct())
                .append("transferState", getTransferState())
                .append("transferType", getTransferType())
                .append("transferAmount", getTransferAmount())
                .append("transferTime", getTransferTime())
                .toString();
    }
}