package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间对象 report_plam_com
 *
 * @author 77tv
 * @date 2021-01-25
 */
public class ReportPlamCom extends BaseEntity {
    private static final long serialVersionUID = 1L;


    private String repId;


    private String classOne;


    private String classOnename;


    private String classTwo;


    @Excel(name = "名称")
    private String classTwoname;

    @Excel(name = "类型")
    private String type;

    /** 对应子类报表的统计值 */
    @Excel(name = "金额")
    private BigDecimal tValue;

    /** $column.columnComment */
    @Excel(name = "报表时间")
    private String reporttime;

    public void setRepId(String repId) {
        this.repId = repId;
    }

    public String getRepId() {
        return repId;
    }
    public void setClassOne(String classOne) {
        this.classOne = classOne;
    }

    public String getClassOne() {
        return classOne;
    }
    public void setClassOnename(String classOnename) {
        this.classOnename = classOnename;
    }

    public String getClassOnename() {
        return classOnename;
    }
    public void setClassTwo(String classTwo) {
        this.classTwo = classTwo;
    }

    public String getClassTwo() {
        return classTwo;
    }
    public void setClassTwoname(String classTwoname) {
        this.classTwoname = classTwoname;
    }

    public String getClassTwoname() {
        return classTwoname;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    public void settValue(BigDecimal tValue) {
        this.tValue = tValue;
    }

    public BigDecimal gettValue() {
        return tValue;
    }
    public void setReporttime(String reporttime) {
        this.reporttime = reporttime;
    }

    public String getReporttime() {
        return reporttime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("repId", getRepId())
                .append("classOne", getClassOne())
                .append("classOnename", getClassOnename())
                .append("classTwo", getClassTwo())
                .append("classTwoname", getClassTwoname())
                .append("type", getType())
                .append("tValue", gettValue())
                .append("reporttime", getReporttime())
                .toString();
    }
}