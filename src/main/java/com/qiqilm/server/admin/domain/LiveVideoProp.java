package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 送礼物对象 live_video_prop
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
public class LiveVideoProp extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 礼物id */
    @Excel(name = "礼物id")
    private Long propId;

    /** 道具名 */
    @Excel(name = "道具名")
    private String propName;

    /** 积分（from_user_id可获得的积分）合计 */
    @Excel(name = "积分", suffix = "f=rom_user_id可获得的积分")
    private Long totalScore;

    /** 钻石（from_user_id减少的钻石）合计 */
    @Excel(name = "钻石", suffix = "f=rom_user_id减少的钻石")
    private BigDecimal totalDiamonds;

    /** 印票(to_user_id增加的印票）合计;is_red_envelope=1时,为主播获得的：钻石 数量 */
    @Excel(name = "印票(to_user_id增加的印票）合计;is_red_envelope=1时,为主播获得的：钻石 数量")
    private BigDecimal totalTicket;

    /** 平台用户当前余额 */
    @Excel(name = "平台用户当前余额")
    private BigDecimal currentDiamonds;

    /** 主播当前印票 */
    @Excel(name = "主播当前印票")
    private BigDecimal beforeTicket;

    /** 送 */
    @Excel(name = "送")
    private Long fromUserId;

    /** 送礼平台用户ID */
    @Excel(name = "送礼平台用户ID")
    private String pUserId;

    /** 送礼平台用户登录名 */
    @Excel(name = "送礼平台用户登录名")
    private String pUserName;

    /** 收 */
    @Excel(name = "收")
    private Long toUserId;

    /** 日期字段,按日期归档；要不然数据量太大了；不好维护 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "日期字段,按日期归档；要不然数据量太大了；不好维护", width = 30, exportFormat = "yyyy-MM-dd")
    private Date createDate;

    /** 年 */
    @Excel(name = "年")
    private Long createY;

    /** 月 */
    @Excel(name = "月")
    private Long createM;

    /** 日 */
    @Excel(name = "日")
    private Long createD;

    /** 周 */
    @Excel(name = "周")
    private Long createW;

    /** 送的数量 */
    @Excel(name = "送的数量")
    private Long num;

    /** 直播ID */
    @Excel(name = "直播ID")
    private Long videoId;

    /** 群组ID */
    @Excel(name = "群组ID")
    private String groupId;

    /** 1:红包 */
    @Excel(name = "1:红包")
    private Integer isRedEnvelope;

    /** 弹幕内容 */
    @Excel(name = "弹幕内容")
    private String msg;

    /** 消息发送，请求处理的结果，OK表示处理成功，FAIL表示失败。 */
    @Excel(name = "消息发送，请求处理的结果，OK表示处理成功，FAIL表示失败。")
    private String actionstatus;

    /** 消息发送，错误信息 */
    @Excel(name = "消息发送，错误信息")
    private String errorinfo;

    /** 错误码 */
    @Excel(name = "错误码")
    private Long errorcode;

    /** 年月 如:201610 */
    @Excel(name = "年月 如:201610")
    private String createYm;

    /** 判断是否为私信送礼 1表示私信 2表示不是私信 */
    @Excel(name = "判断是否为私信送礼 1表示私信 2表示不是私信")
    private Long isPrivate;

    /** 送礼物人IP */
    @Excel(name = "送礼物人IP")
    private String fromIp;

    /** 双币礼物，0是钻石，1是游戏币 */
    @Excel(name = "双币礼物，0是钻石，1是游戏币")
    private Long isCoin;
    /**
     * 发送开始时间
     */
    private String sendStartTime;

    @JsonIgnore
    private String[] selectDate;
    @JsonIgnore
    private String   startTime;
    @JsonIgnore
    private String   endTime;

    private String countTotal;
    private String testAccountCreateTime;

    /**
     * 发送结束时间
     */
    private String sendEndTime;

    public String getSendStartTime() {
        return sendStartTime;
    }

    public void setSendStartTime(String sendStartTime) {
        this.sendStartTime = sendStartTime;
    }

    public String getSendEndTime() {
        return sendEndTime;
    }

    public void setSendEndTime(String sendEndTime) {
        this.sendEndTime = sendEndTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setPropId(Long propId) {
        this.propId = propId;
    }

    public Long getPropId() {
        return propId;
    }
    public void setPropName(String propName) {
        this.propName = propName;
    }

    public String getPropName() {
        return propName;
    }
    public void setTotalScore(Long totalScore) {
        this.totalScore = totalScore;
    }

    public Long getTotalScore() {
        return totalScore;
    }
    public void setTotalDiamonds(BigDecimal totalDiamonds) {
        this.totalDiamonds = totalDiamonds;
    }

    public BigDecimal getTotalDiamonds() {
        return totalDiamonds;
    }
    public void setTotalTicket(BigDecimal totalTicket) {
        this.totalTicket = totalTicket;
    }

    public BigDecimal getTotalTicket() {
        return totalTicket;
    }
    public void setCurrentDiamonds(BigDecimal currentDiamonds) {
        this.currentDiamonds = currentDiamonds;
    }

    public BigDecimal getCurrentDiamonds() {
        return currentDiamonds;
    }
    public void setBeforeTicket(BigDecimal beforeTicket) {
        this.beforeTicket = beforeTicket;
    }

    public BigDecimal getBeforeTicket() {
        return beforeTicket;
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
    public void setpUserName(String pUserName) {
        this.pUserName = pUserName;
    }

    public String getpUserName() {
        return pUserName;
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
    public void setCreateY(Long createY) {
        this.createY = createY;
    }

    public Long getCreateY() {
        return createY;
    }
    public void setCreateM(Long createM) {
        this.createM = createM;
    }

    public Long getCreateM() {
        return createM;
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
    public void setNum(Long num) {
        this.num = num;
    }

    public Long getNum() {
        return num;
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
    public void setIsRedEnvelope(Integer isRedEnvelope) {
        this.isRedEnvelope = isRedEnvelope;
    }

    public Integer getIsRedEnvelope() {
        return isRedEnvelope;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
    public void setActionstatus(String actionstatus) {
        this.actionstatus = actionstatus;
    }

    public String getActionstatus() {
        return actionstatus;
    }
    public void setErrorinfo(String errorinfo) {
        this.errorinfo = errorinfo;
    }

    public String getErrorinfo() {
        return errorinfo;
    }
    public void setErrorcode(Long errorcode) {
        this.errorcode = errorcode;
    }

    public Long getErrorcode() {
        return errorcode;
    }
    public void setCreateYm(String createYm) {
        this.createYm = createYm;
    }

    public String getCreateYm() {
        return createYm;
    }
    public void setIsPrivate(Long isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Long getIsPrivate() {
        return isPrivate;
    }
    public void setFromIp(String fromIp) {
        this.fromIp = fromIp;
    }

    public String getFromIp() {
        return fromIp;
    }
    public void setIsCoin(Long isCoin) {
        this.isCoin = isCoin;
    }

    public Long getIsCoin() {
        return isCoin;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("propId", getPropId())
            .append("propName", getPropName())
            .append("totalScore", getTotalScore())
            .append("totalDiamonds", getTotalDiamonds())
            .append("totalTicket", getTotalTicket())
            .append("currentDiamonds", getCurrentDiamonds())
            .append("beforeTicket", getBeforeTicket())
            .append("fromUserId", getFromUserId())
            .append("pUserId", getpUserId())
            .append("pUserName", getpUserName())
            .append("toUserId", getToUserId())
            .append("createTime", getCreateTime())
            .append("createDate", getCreateDate())
            .append("createY", getCreateY())
            .append("createM", getCreateM())
            .append("createD", getCreateD())
            .append("createW", getCreateW())
            .append("num", getNum())
            .append("videoId", getVideoId())
            .append("groupId", getGroupId())
            .append("isRedEnvelope", getIsRedEnvelope())
            .append("msg", getMsg())
            .append("actionstatus", getActionstatus())
            .append("errorinfo", getErrorinfo())
            .append("errorcode", getErrorcode())
            .append("createYm", getCreateYm())
            .append("isPrivate", getIsPrivate())
            .append("fromIp", getFromIp())
            .append("isCoin", getIsCoin())
            .toString();
    }
}
