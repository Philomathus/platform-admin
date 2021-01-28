package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 贡献榜对象 report_anchorhot_day
 *
 * @author 77tv
 * @date 2021-01-28
 */
public class ReportAnchorhotDay extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** id */
    @Excel(name = "id")
    private String repId;

    /** 主播ID */
    @Excel(name = "主播ID")
    private Integer anchorid;

    /** 主播昵称 */
    @Excel(name = "主播昵称")
    private String nickname;

    /** 每日热度 */
    @Excel(name = "每日热度")
    private BigDecimal dayTicket;

    /** 热度排名 */
    @Excel(name = "热度排名")
    private Integer sort;

    /** 距离上一次差值 */
    @Excel(name = "距离上一次差值")
    private BigDecimal dayTicketUpdiff;

    /** 日榜 */
    @Excel(name = "日榜")
    private String repdate;

    /** 第几天 */
    @Excel(name = "第几天")
    private String num;

    /** 更新日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date reptime;

    /** 主播头像 */
    @Excel(name = "主播头像")
    private String headImage;

    public void setRepId(String repId) {
        this.repId = repId;
    }

    public String getRepId() {
        return repId;
    }
    public void setAnchorid(Integer anchorid) {
        this.anchorid = anchorid;
    }

    public Integer getAnchorid() {
        return anchorid;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
    public void setDayTicket(BigDecimal dayTicket) {
        this.dayTicket = dayTicket;
    }

    public BigDecimal getDayTicket() {
        return dayTicket;
    }
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getSort() {
        return sort;
    }
    public void setDayTicketUpdiff(BigDecimal dayTicketUpdiff) {
        this.dayTicketUpdiff = dayTicketUpdiff;
    }

    public BigDecimal getDayTicketUpdiff() {
        return dayTicketUpdiff;
    }
    public void setRepdate(String repdate) {
        this.repdate = repdate;
    }

    public String getRepdate() {
        return repdate;
    }
    public void setNum(String num) {
        this.num = num;
    }

    public String getNum() {
        return num;
    }
    public void setReptime(Date reptime) {
        this.reptime = reptime;
    }

    public Date getReptime() {
        return reptime;
    }
    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getHeadImage() {
        return headImage;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("repId", getRepId())
            .append("anchorid", getAnchorid())
            .append("nickname", getNickname())
            .append("dayTicket", getDayTicket())
            .append("sort", getSort())
            .append("dayTicketUpdiff", getDayTicketUpdiff())
            .append("repdate", getRepdate())
            .append("num", getNum())
            .append("reptime", getReptime())
            .append("headImage", getHeadImage())
            .toString();
    }
}
