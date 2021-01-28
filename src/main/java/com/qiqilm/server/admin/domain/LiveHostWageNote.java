package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 live_host_wage_note
 *
 * @author 77tv
 * @date 2021-01-27
 */
public class LiveHostWageNote extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 家族长ID */
    @Excel(name = "家族长ID")
    private Long familyId;

    /** 主播ID */
    @Excel(name = "主播ID")
    private Long hostId;

    /** 直播开始时间 */
    @Excel(name = "直播开始时间")
    private String startTime;

    /** 直播结束时间 */
    @Excel(name = "直播结束时间")
    private String endTime;

    /** 直播时长（秒） */
    @Excel(name = "直播时长", readConverterExp = "秒=")
    private Long liveTimeSec;

    /** 主播直播结算印票 */
    @Excel(name = "主播直播结算印票")
    private BigDecimal ticket;

    /** 历史印票总数 */
    @Excel(name = "历史印票总数")
    private BigDecimal beforeTotalTicket;

    /** 彩票投注 */
    @Excel(name = "彩票投注")
    private BigDecimal cpCost;

    /** 彩票派奖 */
    @Excel(name = "彩票派奖")
    private BigDecimal cpPrize;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setFamilyId(Long familyId) {
        this.familyId = familyId;
    }

    public Long getFamilyId() {
        return familyId;
    }
    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }

    public Long getHostId() {
        return hostId;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartTime() {
        return startTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndTime() {
        return endTime;
    }
    public void setLiveTimeSec(Long liveTimeSec) {
        this.liveTimeSec = liveTimeSec;
    }

    public Long getLiveTimeSec() {
        return liveTimeSec;
    }
    public void setTicket(BigDecimal ticket) {
        this.ticket = ticket;
    }

    public BigDecimal getTicket() {
        return ticket;
    }
    public void setBeforeTotalTicket(BigDecimal beforeTotalTicket) {
        this.beforeTotalTicket = beforeTotalTicket;
    }

    public BigDecimal getBeforeTotalTicket() {
        return beforeTotalTicket;
    }
    public void setCpCost(BigDecimal cpCost) {
        this.cpCost = cpCost;
    }

    public BigDecimal getCpCost() {
        return cpCost;
    }
    public void setCpPrize(BigDecimal cpPrize) {
        this.cpPrize = cpPrize;
    }

    public BigDecimal getCpPrize() {
        return cpPrize;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("familyId", getFamilyId())
            .append("hostId", getHostId())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("liveTimeSec", getLiveTimeSec())
            .append("ticket", getTicket())
            .append("beforeTotalTicket", getBeforeTotalTicket())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .append("cpCost", getCpCost())
            .append("cpPrize", getCpPrize())
            .toString();
    }
}
