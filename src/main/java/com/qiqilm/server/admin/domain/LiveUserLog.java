package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * //帐户资金变动日志对象 live_user_log
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class LiveUserLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 日志信息 */
    @Excel(name = "日志信息")
    private String logInfo;

    /** 日志时间 */
    @Excel(name = "日志时间")
    private Long logTime;

    /** 日志管理员ID */
    @Excel(name = "日志管理员ID")
    private Long logAdminId;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal money;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 平台用户ID */
    @Excel(name = "平台用户ID")
    private String pUserId;

    /** //类型 0表示充值 1表示提现 2赠送道具 3 兑换印票  4 分享获得印票 5 登录赠送积分 6 观看付费直播 7 游戏 */
    @Excel(name = "//类型 0表示充值 1表示提现 2赠送道具 3 兑换印票  4 分享获得印票 5 登录赠送积分 6 观看付费直播 7 游戏")
    private Integer type;

    /** 道具ID号 */
    @Excel(name = "道具ID号")
    private Long propId;

    /** 积分 */
    @Excel(name = "积分")
    private Long score;

    /** 信用值 */
    @Excel(name = "信用值")
    private Long point;

    /** 主播ID */
    @Excel(name = "主播ID")
    private Long podcastId;

    /** 钻石数 */
    @Excel(name = "钻石数")
    private BigDecimal diamonds;

    /** 票数 */
    @Excel(name = "票数")
    private BigDecimal ticket;

    /** 直播间ID */
    @Excel(name = "直播间ID")
    private Long videoId;

    /** 公会贡献成员ID */
    @Excel(name = "公会贡献成员ID")
    private Long contributionId;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }

    public String getLogInfo() {
        return logInfo;
    }
    public void setLogTime(Long logTime) {
        this.logTime = logTime;
    }

    public Long getLogTime() {
        return logTime;
    }
    public void setLogAdminId(Long logAdminId) {
        this.logAdminId = logAdminId;
    }

    public Long getLogAdminId() {
        return logAdminId;
    }
    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public BigDecimal getMoney() {
        return money;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
    public void setpUserId(String pUserId) {
        this.pUserId = pUserId;
    }

    public String getpUserId() {
        return pUserId;
    }
    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
    public void setPropId(Long propId) {
        this.propId = propId;
    }

    public Long getPropId() {
        return propId;
    }
    public void setScore(Long score) {
        this.score = score;
    }

    public Long getScore() {
        return score;
    }
    public void setPoint(Long point) {
        this.point = point;
    }

    public Long getPoint() {
        return point;
    }
    public void setPodcastId(Long podcastId) {
        this.podcastId = podcastId;
    }

    public Long getPodcastId() {
        return podcastId;
    }
    public void setDiamonds(BigDecimal diamonds) {
        this.diamonds = diamonds;
    }

    public BigDecimal getDiamonds() {
        return diamonds;
    }
    public void setTicket(BigDecimal ticket) {
        this.ticket = ticket;
    }

    public BigDecimal getTicket() {
        return ticket;
    }
    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getVideoId() {
        return videoId;
    }
    public void setContributionId(Long contributionId) {
        this.contributionId = contributionId;
    }

    public Long getContributionId() {
        return contributionId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("logInfo", getLogInfo())
            .append("logTime", getLogTime())
            .append("logAdminId", getLogAdminId())
            .append("money", getMoney())
            .append("userId", getUserId())
            .append("pUserId", getpUserId())
            .append("type", getType())
            .append("propId", getPropId())
            .append("score", getScore())
            .append("point", getPoint())
            .append("podcastId", getPodcastId())
            .append("diamonds", getDiamonds())
            .append("ticket", getTicket())
            .append("videoId", getVideoId())
            .append("contributionId", getContributionId())
            .toString();
    }
}
