package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 代理统计，主要用于代理渠道的统计对象 report_agentcount
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class ReportAgentcount extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private String repId;

    /** 渠道编码 */
    @Excel(name = "渠道编码")
    private String agentcode;

    /** 邀请账号 */
    @Excel(name = "邀请账号")
    private String agentname;

    /** 统计时间 */
    @Excel(name = "统计时间")
    private String agenttime;

    /** 当日新注册人数 */
    @Excel(name = "当日新注册人数")
    private Long newmember;

    /** 总邀请人数 */
    @Excel(name = "总邀请人数")
    private Long totalmember;

    /** 公司入款（首充） */
    @Excel(name = "公司入款", readConverterExp = "首=充")
    private String gsRukuanjine;

    /** 线上入款（首充） */
    @Excel(name = "线上入款", readConverterExp = "首=充")
    private String xsRukuanjine;

    /** 手工入款（首充） */
    @Excel(name = "手工入款", readConverterExp = "首=充")
    private String sgRukuanjine;

    /** 入款总（首充） */
    @Excel(name = "入款总", readConverterExp = "首=充")
    private String totalfristRukuanjine;

    /** 出款金额（首次） */
    @Excel(name = "出款金额", readConverterExp = "首=次")
    private String chukuanjine;

    /** 总入款（当日总） */
    @Excel(name = "总入款", readConverterExp = "当=日总")
    private String totalRukuanjine;

    /** 总出款（当日总） */
    @Excel(name = "总出款", readConverterExp = "当=日总")
    private String totalChukuanjine;

    /** 进入直播间次数 */
    @Excel(name = "进入直播间次数")
    private Long totalEnterlivetimes;

    /** 代理线活跃的安卓用户 */
    @Excel(name = "代理线活跃的安卓用户")
    private Long totalActiveandroid;

    /** 代理线活跃的苹果用户 */
    @Excel(name = "代理线活跃的苹果用户")
    private Long totalActiveios;

    /** 代理线送礼 */
    @Excel(name = "代理线送礼")
    private String totalGiveprop;

    public void setRepId(String repId) {
        this.repId = repId;
    }

    public String getRepId() {
        return repId;
    }
    public void setAgentcode(String agentcode) {
        this.agentcode = agentcode;
    }

    public String getAgentcode() {
        return agentcode;
    }
    public void setAgentname(String agentname) {
        this.agentname = agentname;
    }

    public String getAgentname() {
        return agentname;
    }
    public void setAgenttime(String agenttime) {
        this.agenttime = agenttime;
    }

    public String getAgenttime() {
        return agenttime;
    }
    public void setNewmember(Long newmember) {
        this.newmember = newmember;
    }

    public Long getNewmember() {
        return newmember;
    }
    public void setTotalmember(Long totalmember) {
        this.totalmember = totalmember;
    }

    public Long getTotalmember() {
        return totalmember;
    }
    public void setGsRukuanjine(String gsRukuanjine) {
        this.gsRukuanjine = gsRukuanjine;
    }

    public String getGsRukuanjine() {
        return gsRukuanjine;
    }
    public void setXsRukuanjine(String xsRukuanjine) {
        this.xsRukuanjine = xsRukuanjine;
    }

    public String getXsRukuanjine() {
        return xsRukuanjine;
    }
    public void setSgRukuanjine(String sgRukuanjine) {
        this.sgRukuanjine = sgRukuanjine;
    }

    public String getSgRukuanjine() {
        return sgRukuanjine;
    }
    public void setTotalfristRukuanjine(String totalfristRukuanjine) {
        this.totalfristRukuanjine = totalfristRukuanjine;
    }

    public String getTotalfristRukuanjine() {
        return totalfristRukuanjine;
    }
    public void setChukuanjine(String chukuanjine) {
        this.chukuanjine = chukuanjine;
    }

    public String getChukuanjine() {
        return chukuanjine;
    }
    public void setTotalRukuanjine(String totalRukuanjine) {
        this.totalRukuanjine = totalRukuanjine;
    }

    public String getTotalRukuanjine() {
        return totalRukuanjine;
    }
    public void setTotalChukuanjine(String totalChukuanjine) {
        this.totalChukuanjine = totalChukuanjine;
    }

    public String getTotalChukuanjine() {
        return totalChukuanjine;
    }
    public void setTotalEnterlivetimes(Long totalEnterlivetimes) {
        this.totalEnterlivetimes = totalEnterlivetimes;
    }

    public Long getTotalEnterlivetimes() {
        return totalEnterlivetimes;
    }
    public void setTotalActiveandroid(Long totalActiveandroid) {
        this.totalActiveandroid = totalActiveandroid;
    }

    public Long getTotalActiveandroid() {
        return totalActiveandroid;
    }
    public void setTotalActiveios(Long totalActiveios) {
        this.totalActiveios = totalActiveios;
    }

    public Long getTotalActiveios() {
        return totalActiveios;
    }
    public void setTotalGiveprop(String totalGiveprop) {
        this.totalGiveprop = totalGiveprop;
    }

    public String getTotalGiveprop() {
        return totalGiveprop;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("repId", getRepId())
            .append("agentcode", getAgentcode())
            .append("agentname", getAgentname())
            .append("agenttime", getAgenttime())
            .append("newmember", getNewmember())
            .append("totalmember", getTotalmember())
            .append("gsRukuanjine", getGsRukuanjine())
            .append("xsRukuanjine", getXsRukuanjine())
            .append("sgRukuanjine", getSgRukuanjine())
            .append("totalfristRukuanjine", getTotalfristRukuanjine())
            .append("chukuanjine", getChukuanjine())
            .append("totalRukuanjine", getTotalRukuanjine())
            .append("totalChukuanjine", getTotalChukuanjine())
            .append("totalEnterlivetimes", getTotalEnterlivetimes())
            .append("totalActiveandroid", getTotalActiveandroid())
            .append("totalActiveios", getTotalActiveios())
            .append("totalGiveprop", getTotalGiveprop())
            .toString();
    }
}