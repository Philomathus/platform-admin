package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * //付费直播记录对象 live_pay_log
 *
 * @author 77tv
 * @date 2021-02-03
 */
public class LivePayLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 观看时间（from_user_id累计观看时间）合计 */
    @Excel(name = "观看时间", readConverterExp = "f=rom_user_id累计观看时间")
    private Long totalTime;

    /** 主播获得的印票 */
    @Excel(name = "主播获得的印票")
    private BigDecimal totalTicket;

    /** 钻石（from_user_id减少的钻石）合计 */
    @Excel(name = "钻石", readConverterExp = "f=rom_user_id减少的钻石")
    private Long totalDiamonds;

    /** 观众 */
    @Excel(name = "观众")
    private Long fromUserId;

    /** 平台用户ID */
    @Excel(name = "平台用户ID")
    private String pUserId;

    /** 平台用户昵称 */
    @Excel(name = "平台用户昵称")
    private String fromUserName;

    /** 主播 */
    @Excel(name = "主播")
    private Long toUserId;

    /** 日期字段,按日期归档；要不然数据量太大了；不好维护 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "日期字段,按日期归档；要不然数据量太大了；不好维护", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    /** 年月 如:201610 */
    @Excel(name = "年月 如:201610")
    private String createYm;

    /** 日 */
    @Excel(name = "日")
    private Long createD;

    /** 周 */
    @Excel(name = "周")
    private Long createW;

    /** 收取费用（钻石/分钟） */
    @Excel(name = "收取费用", readConverterExp = "钻=石/分钟")
    private Long liveFee;

    /** 直播间开始收费时间 */
    @Excel(name = "直播间开始收费时间")
    private Long livePayTime;

    /** 直播间开始收费 日期字段 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "直播间开始收费 日期字段", width = 30, dateFormat = "yyyy-MM-dd")
    private Date livePayDate;

    /** 直播ID */
    @Excel(name = "直播ID")
    private Long videoId;

    /** 群组ID */
    @Excel(name = "群组ID")
    private String groupId;

    /** 最后一次扣款时间 */
    @Excel(name = "最后一次扣款时间")
    private Long payTimeEnd;

    /** 下次扣款时间 */
    @Excel(name = "下次扣款时间")
    private Long payTimeNext;

    /** 提档后开始收费时间 */
    @Excel(name = "提档后开始收费时间")
    private Long liveIsMentionTime;

    /** 提档前扣费合计 */
    @Excel(name = "提档前扣费合计")
    private Long liveIsMentionPay;

    /** 直播类型 0 按时收费 1按场收费 */
    @Excel(name = "直播类型 0 按时收费 1按场收费")
    private Integer livePayType;

    /** 新付费直播的ID , 用于异常终止直播间付费，主播新开的主播ID  */
    @Excel(name = "新付费直播的ID , 用于异常终止直播间付费，主播新开的主播ID ")
    private Long newRoomId;

    /** 积分（from_user_id可获得的积分）合计 */
    @Excel(name = "积分", readConverterExp = "f=rom_user_id可获得的积分")
    private Long totalScore;

    /** 观众（from_user_id）获得积分的转换比例 */
    @Excel(name = "观众", readConverterExp = "f=rom_user_id")
    private BigDecimal uesddiamondsToScore;

    /** 主播（to_user_id）获得的印票转换比例 */
    @Excel(name = "主播", readConverterExp = "t=o_user_id")
    private BigDecimal ticketToRate;

    /** 是否为公屏收费记录 0 否； 1 是； */
    @Excel(name = "是否为公屏收费记录 0 否； 1 是；")
    private Integer payType;

    /** $column.columnComment */
    @Excel(name = "是否为公屏收费记录 0 否； 1 是；")
    private boolean isHistory;

    /** 按时收费是否注单 */
    @Excel(name = "按时收费是否注单")
    private Integer isNote;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setTotalTime(Long totalTime) {
        this.totalTime = totalTime;
    }

    public Long getTotalTime() {
        return totalTime;
    }
    public void setTotalTicket(BigDecimal totalTicket) {
        this.totalTicket = totalTicket;
    }

    public BigDecimal getTotalTicket() {
        return totalTicket;
    }
    public void setTotalDiamonds(Long totalDiamonds) {
        this.totalDiamonds = totalDiamonds;
    }

    public Long getTotalDiamonds() {
        return totalDiamonds;
    }
    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getFromUserId() {
        return fromUserId;
    }
    public void setpUserId(String pUserId) {
        this.pUserId = pUserId;
    }

    public String getpUserId() {
        return pUserId;
    }
    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromUserName() {
        return fromUserName;
    }
    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateYm(String createYm) {
        this.createYm = createYm;
    }

    public String getCreateYm() {
        return createYm;
    }
    public void setCreateD(Long createD) {
        this.createD = createD;
    }

    public Long getCreateD() {
        return createD;
    }
    public void setCreateW(Long createW) {
        this.createW = createW;
    }

    public Long getCreateW() {
        return createW;
    }
    public void setLiveFee(Long liveFee) {
        this.liveFee = liveFee;
    }

    public Long getLiveFee() {
        return liveFee;
    }
    public void setLivePayTime(Long livePayTime) {
        this.livePayTime = livePayTime;
    }

    public Long getLivePayTime() {
        return livePayTime;
    }
    public void setLivePayDate(Date livePayDate) {
        this.livePayDate = livePayDate;
    }

    public Date getLivePayDate() {
        return livePayDate;
    }
    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getVideoId() {
        return videoId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }
    public void setPayTimeEnd(Long payTimeEnd) {
        this.payTimeEnd = payTimeEnd;
    }

    public Long getPayTimeEnd() {
        return payTimeEnd;
    }
    public void setPayTimeNext(Long payTimeNext) {
        this.payTimeNext = payTimeNext;
    }

    public Long getPayTimeNext() {
        return payTimeNext;
    }
    public void setLiveIsMentionTime(Long liveIsMentionTime) {
        this.liveIsMentionTime = liveIsMentionTime;
    }

    public Long getLiveIsMentionTime() {
        return liveIsMentionTime;
    }
    public void setLiveIsMentionPay(Long liveIsMentionPay) {
        this.liveIsMentionPay = liveIsMentionPay;
    }

    public Long getLiveIsMentionPay() {
        return liveIsMentionPay;
    }
    public void setLivePayType(Integer livePayType) {
        this.livePayType = livePayType;
    }

    public Integer getLivePayType() {
        return livePayType;
    }
    public void setNewRoomId(Long newRoomId) {
        this.newRoomId = newRoomId;
    }

    public Long getNewRoomId() {
        return newRoomId;
    }
    public void setTotalScore(Long totalScore) {
        this.totalScore = totalScore;
    }

    public Long getTotalScore() {
        return totalScore;
    }
    public void setUesddiamondsToScore(BigDecimal uesddiamondsToScore) {
        this.uesddiamondsToScore = uesddiamondsToScore;
    }

    public BigDecimal getUesddiamondsToScore() {
        return uesddiamondsToScore;
    }
    public void setTicketToRate(BigDecimal ticketToRate) {
        this.ticketToRate = ticketToRate;
    }

    public BigDecimal getTicketToRate() {
        return ticketToRate;
    }
    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getPayType() {
        return payType;
    }
    public void setIsHistory(boolean isHistory) {
        this.isHistory = isHistory;
    }

    public boolean getIsHistory() {
        return isHistory;
    }
    public void setIsNote(Integer isNote) {
        this.isNote = isNote;
    }

    public Integer getIsNote() {
        return isNote;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("totalTime", getTotalTime())
            .append("totalTicket", getTotalTicket())
            .append("totalDiamonds", getTotalDiamonds())
            .append("fromUserId", getFromUserId())
            .append("pUserId", getpUserId())
            .append("fromUserName", getFromUserName())
            .append("toUserId", getToUserId())
            .append("createTime", getCreateTime())
            .append("createDate", getCreateDate())
            .append("createYm", getCreateYm())
            .append("createD", getCreateD())
            .append("createW", getCreateW())
            .append("liveFee", getLiveFee())
            .append("livePayTime", getLivePayTime())
            .append("livePayDate", getLivePayDate())
            .append("videoId", getVideoId())
            .append("groupId", getGroupId())
            .append("payTimeEnd", getPayTimeEnd())
            .append("payTimeNext", getPayTimeNext())
            .append("liveIsMentionTime", getLiveIsMentionTime())
            .append("liveIsMentionPay", getLiveIsMentionPay())
            .append("livePayType", getLivePayType())
            .append("newRoomId", getNewRoomId())
            .append("totalScore", getTotalScore())
            .append("uesddiamondsToScore", getUesddiamondsToScore())
            .append("ticketToRate", getTicketToRate())
            .append("payType", getPayType())
            .append("isHistory", getIsHistory())
            .append("isNote", getIsNote())
            .toString();
    }
}
