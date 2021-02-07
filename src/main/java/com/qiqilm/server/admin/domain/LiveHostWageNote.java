package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Formatter;

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
    private boolean hasChildren = true;

    /** 直播开始时间 */
    @Excel(name = "直播开始时间")
    private String startTime;
    private BigDecimal settlementRate;
    private String[] selectDate = new String[2];

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

    private BigDecimal allCpCost;

    private BigDecimal allPrize;
    private int alltime;
    private String shijian;
    @Excel(name = "直播总时长（小时）")
    private String alltimeDes;
    @Excel(name = "族长直播总结算印票")
    private String allticket;
    @Excel(name = "统计日期")
    private String timedata;
    private String familyName;
    @Excel(name = "族长昵称")
    private String familyNickName;
    private String nickName;
    @Excel(name = "族长直播结算印票")
    private BigDecimal allticketRes;
    public String getAlltimeDes() {
        if(!StringUtils.isEmpty(alltime)){
            double df = alltime;
            return new Formatter().format("%.2f", df/3600).toString();
        }
        return "";
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public BigDecimal getSettlementRate() {
        return settlementRate;
    }

    public void setSettlementRate(BigDecimal settlementRate) {
        this.settlementRate = settlementRate;
    }

    public String[] getSelectDate() {
        return selectDate;
    }

    public void setSelectDate(String[] selectDate) {
        this.selectDate = selectDate;
    }

    public int getAlltime() {
        return alltime;
    }

    public void setAlltime(int alltime) {
        this.alltime = alltime;
    }

    public String getShijian() {
        return shijian;
    }

    public void setShijian(String shijian) {
        this.shijian = shijian;
    }

    public String getFamilyName() {
        if(StringUtils.isEmpty(familyName)){
            return "直播家族散户(未入家族)";
        }
        return familyName;
    }
    public void setAlltimeDes(String alltimeDes) {
        this.alltimeDes = alltimeDes;
    }

    public String getAllticket() {
        return allticket;
    }

    public void setAllticket(String allticket) {
        this.allticket = allticket;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFamilyNickName() {
        return familyNickName;
    }

    public void setFamilyNickName(String familyNickName) {
        this.familyNickName = familyNickName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public BigDecimal getAllticketRes() {
        return allticketRes;
    }

    public void setAllticketRes(BigDecimal allticketRes) {
        this.allticketRes = allticketRes;
    }

    public BigDecimal getAllCpCost() {
        return allCpCost;
    }

    public void setAllCpCost(BigDecimal allCpCost) {
        this.allCpCost = allCpCost;
    }

    public BigDecimal getAllPrize() {
        return allPrize;
    }

    public void setAllPrize(BigDecimal allPrize) {
        this.allPrize = allPrize;
    }

    public String getTimedata() {
        return timedata;
    }

    public void setTimedata(String timedata) {
        this.timedata = timedata;
    }

    private String createTimes;

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

    public String getCreateTimes() {
        return createTimes;
    }

    public void setCreateTimes( String createTimes ) {
        this.createTimes = createTimes;
    }
}
