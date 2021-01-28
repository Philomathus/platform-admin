package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 佣金领取日志对象 log_commission
 *
 * @author 77tv
 * @date 2021-01-27
 */
public class LogCommission extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 会员ID */
    @Excel(name = "会员ID")
    private String memberId;

    /** 佣金 */
    @Excel(name = "佣金")
    private BigDecimal commission;

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
    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("memberId", getMemberId())
            .append("commission", getCommission())
            .append("createTime", getCreateTime())
            .toString();
    }
}
