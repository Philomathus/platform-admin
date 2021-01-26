package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额对象 report_moneyinfo
 *
 * @author 77tv
 * @date 2021-01-25
 */
public class ReportMoneyinfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private String repId;

    /** 报表时间 */
    @Excel(name = "报表时间")
    private String reptime;

    /** 公司入款人数 */
    @Excel(name = "公司入款人数")
    private Long gsRukuanrenshu;

    /** 公司入款金额 */
    @Excel(name = "公司入款金额")
    private BigDecimal gsRukuanjine;

    /** 线上入款人数 */
    @Excel(name = "线上入款人数")
    private Long xsRukunanrenshu;

    /** 线上入款金额 */
    @Excel(name = "线上入款金额")
    private BigDecimal xsRukunanjine;

    /** 人工入款人数 */
    @Excel(name = "人工入款人数")
    private Long rgRukunanrenshu;

    /** 人工入款金额 */
    @Excel(name = "人工入款金额")
    private BigDecimal rgRukunanjine;

    /** 平台优惠人数 */
    @Excel(name = "平台优惠人数")
    private Long palmYouhuirenshu;

    /** 平台优惠金额 */
    @Excel(name = "平台优惠金额")
    private BigDecimal palmYouhuijine;

    /** 入款总人数 */
    @Excel(name = "入款总人数")
    private Long totalRukuanrenshu;

    /** 入款总金额 */
    @Excel(name = "入款总金额")
    private BigDecimal totalRukuanjine;

    /** 公司出款总人数 */
    @Excel(name = "公司出款总人数")
    private Long totalChukuanrenshu;

    /** 公司出款总金额 */
    @Excel(name = "公司出款总金额")
    private BigDecimal totalChukuanjine;

    /** 预估会员剩余积分 */
    @Excel(name = "预估会员剩余积分")
    private BigDecimal totalAccount;

    /** 每日平台盈利 */
    @Excel(name = "每日平台盈利")
    private BigDecimal totalProfile;

    /** 每日送礼金额 */
    @Excel(name = "每日送礼金额")
    private BigDecimal totalGiveprop;

    /** 合计剩余偏差 */
    @Excel(name = "合计剩余偏差")
    private BigDecimal totalLast;

    public void setRepId(String repId) {
        this.repId = repId;
    }

    public String getRepId() {
        return repId;
    }
    public void setReptime(String reptime) {
        this.reptime = reptime;
    }

    public String getReptime() {
        return reptime;
    }
    public void setGsRukuanrenshu(Long gsRukuanrenshu) {
        this.gsRukuanrenshu = gsRukuanrenshu;
    }

    public Long getGsRukuanrenshu() {
        return gsRukuanrenshu;
    }
    public void setGsRukuanjine(BigDecimal gsRukuanjine) {
        this.gsRukuanjine = gsRukuanjine;
    }

    public BigDecimal getGsRukuanjine() {
        return gsRukuanjine;
    }
    public void setXsRukunanrenshu(Long xsRukunanrenshu) {
        this.xsRukunanrenshu = xsRukunanrenshu;
    }

    public Long getXsRukunanrenshu() {
        return xsRukunanrenshu;
    }
    public void setXsRukunanjine(BigDecimal xsRukunanjine) {
        this.xsRukunanjine = xsRukunanjine;
    }

    public BigDecimal getXsRukunanjine() {
        return xsRukunanjine;
    }
    public void setRgRukunanrenshu(Long rgRukunanrenshu) {
        this.rgRukunanrenshu = rgRukunanrenshu;
    }

    public Long getRgRukunanrenshu() {
        return rgRukunanrenshu;
    }
    public void setRgRukunanjine(BigDecimal rgRukunanjine) {
        this.rgRukunanjine = rgRukunanjine;
    }

    public BigDecimal getRgRukunanjine() {
        return rgRukunanjine;
    }
    public void setPalmYouhuirenshu(Long palmYouhuirenshu) {
        this.palmYouhuirenshu = palmYouhuirenshu;
    }

    public Long getPalmYouhuirenshu() {
        return palmYouhuirenshu;
    }
    public void setPalmYouhuijine(BigDecimal palmYouhuijine) {
        this.palmYouhuijine = palmYouhuijine;
    }

    public BigDecimal getPalmYouhuijine() {
        return palmYouhuijine;
    }
    public void setTotalRukuanrenshu(Long totalRukuanrenshu) {
        this.totalRukuanrenshu = totalRukuanrenshu;
    }

    public Long getTotalRukuanrenshu() {
        return totalRukuanrenshu;
    }
    public void setTotalRukuanjine(BigDecimal totalRukuanjine) {
        this.totalRukuanjine = totalRukuanjine;
    }

    public BigDecimal getTotalRukuanjine() {
        return totalRukuanjine;
    }
    public void setTotalChukuanrenshu(Long totalChukuanrenshu) {
        this.totalChukuanrenshu = totalChukuanrenshu;
    }

    public Long getTotalChukuanrenshu() {
        return totalChukuanrenshu;
    }
    public void setTotalChukuanjine(BigDecimal totalChukuanjine) {
        this.totalChukuanjine = totalChukuanjine;
    }

    public BigDecimal getTotalChukuanjine() {
        return totalChukuanjine;
    }
    public void setTotalAccount(BigDecimal totalAccount) {
        this.totalAccount = totalAccount;
    }

    public BigDecimal getTotalAccount() {
        return totalAccount;
    }
    public void setTotalProfile(BigDecimal totalProfile) {
        this.totalProfile = totalProfile;
    }

    public BigDecimal getTotalProfile() {
        return totalProfile;
    }
    public void setTotalGiveprop(BigDecimal totalGiveprop) {
        this.totalGiveprop = totalGiveprop;
    }

    public BigDecimal getTotalGiveprop() {
        return totalGiveprop;
    }
    public void setTotalLast(BigDecimal totalLast) {
        this.totalLast = totalLast;
    }

    public BigDecimal getTotalLast() {
        return totalLast;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("repId", getRepId())
                .append("reptime", getReptime())
                .append("gsRukuanrenshu", getGsRukuanrenshu())
                .append("gsRukuanjine", getGsRukuanjine())
                .append("xsRukunanrenshu", getXsRukunanrenshu())
                .append("xsRukunanjine", getXsRukunanjine())
                .append("rgRukunanrenshu", getRgRukunanrenshu())
                .append("rgRukunanjine", getRgRukunanjine())
                .append("palmYouhuirenshu", getPalmYouhuirenshu())
                .append("palmYouhuijine", getPalmYouhuijine())
                .append("totalRukuanrenshu", getTotalRukuanrenshu())
                .append("totalRukuanjine", getTotalRukuanjine())
                .append("totalChukuanrenshu", getTotalChukuanrenshu())
                .append("totalChukuanjine", getTotalChukuanjine())
                .append("totalAccount", getTotalAccount())
                .append("totalProfile", getTotalProfile())
                .append("totalGiveprop", getTotalGiveprop())
                .append("totalLast", getTotalLast())
                .toString();
    }
}