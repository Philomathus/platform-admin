package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 report_income_day
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class ReportIncomeDay extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** id */
    @Excel(name = "id")
    private String reppayId;

    /** 收款金额 */
    @Excel(name = "收款金额")
    private BigDecimal money;

    /** 线下或者线上 */
    @Excel(name = "线下或者线上")
    private String type;

    /** 收款平台 */
    @Excel(name = "收款平台")
    private String payplam;

    /** 收款通道 */
    @Excel(name = "收款通道")
    private String paychancl;

    /** 收款商户 */
    @Excel(name = "收款商户")
    private String paycard;

    /** 时间 */
    @Excel(name = "时间")
    private String paydate;

    public void setReppayId(String reppayId) {
        this.reppayId = reppayId;
    }

    public String getReppayId() {
        return reppayId;
    }
    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public BigDecimal getMoney() {
        return money;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    public void setPayplam(String payplam) {
        this.payplam = payplam;
    }

    public String getPayplam() {
        return payplam;
    }
    public void setPaychancl(String paychancl) {
        this.paychancl = paychancl;
    }

    public String getPaychancl() {
        return paychancl;
    }
    public void setPaycard(String paycard) {
        this.paycard = paycard;
    }

    public String getPaycard() {
        return paycard;
    }
    public void setPaydate(String paydate) {
        this.paydate = paydate;
    }

    public String getPaydate() {
        return paydate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("reppayId", getReppayId())
            .append("money", getMoney())
            .append("type", getType())
            .append("payplam", getPayplam())
            .append("paychancl", getPaychancl())
            .append("paycard", getPaycard())
            .append("paydate", getPaydate())
            .toString();
    }
}