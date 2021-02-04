package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 用户投资行为对象 lottery_bet
 *
 * @author 77tv
 * @date 2021-02-03
 */
public class LotteryBet extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** id */
    private String id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 下注彩种id */
    @Excel(name = "下注彩种id")
    private Long lotteryId;

    /** 下注期数 */
    @Excel(name = "下注期数")
    private String issue;

    /** 下注选择菜单 */
    @Excel(name = "下注选择菜单")
    private String methodId;

    /** 下注选择 */
    @Excel(name = "下注选择")
    private String betSelect;

    /** 筹码 */
    @Excel(name = "筹码")
    private BigDecimal chip;

    /** 下注时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "下注时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date betTime;

    /** 0= 待开奖 1= 已中奖 2=未中奖 */
    @Excel(name = "0= 待开奖 1= 已中奖 2=未中奖")
    private Long status;

    /** 中奖金额 */
    @Excel(name = "中奖金额")
    private BigDecimal prize;

    /** 投资 */
    @Excel(name = "投资")
    private BigDecimal cost;

    /** 彩票名称 */
    @Excel(name = "彩票名称")
    private String lotteryName;

    /** 平台用户ID */
    @Excel(name = "平台用户ID")
    private String puserId;

    /** 主播ID */
    @Excel(name = "主播ID")
    private Long anchor;

    /** 开奖号码 */
    @Excel(name = "开奖号码")
    private String code;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
    public void setLotteryId(Long lotteryId) {
        this.lotteryId = lotteryId;
    }

    public Long getLotteryId() {
        return lotteryId;
    }
    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getIssue() {
        return issue;
    }
    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }

    public String getMethodId() {
        return methodId;
    }
    public void setBetSelect(String betSelect) {
        this.betSelect = betSelect;
    }

    public String getBetSelect() {
        return betSelect;
    }
    public void setChip(BigDecimal chip) {
        this.chip = chip;
    }

    public BigDecimal getChip() {
        return chip;
    }
    public void setBetTime(Date betTime) {
        this.betTime = betTime;
    }

    public Date getBetTime() {
        return betTime;
    }
    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getStatus() {
        return status;
    }
    public void setPrize(BigDecimal prize) {
        this.prize = prize;
    }

    public BigDecimal getPrize() {
        return prize;
    }
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getCost() {
        return cost;
    }
    public void setLotteryName(String lotteryName) {
        this.lotteryName = lotteryName;
    }

    public String getLotteryName() {
        return lotteryName;
    }
    public void setPuserId(String puserId) {
        this.puserId = puserId;
    }

    public String getPuserId() {
        return puserId;
    }
    public void setAnchor(Long anchor) {
        this.anchor = anchor;
    }

    public Long getAnchor() {
        return anchor;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("userId", getUserId())
                .append("lotteryId", getLotteryId())
                .append("issue", getIssue())
                .append("methodId", getMethodId())
                .append("betSelect", getBetSelect())
                .append("chip", getChip())
                .append("betTime", getBetTime())
                .append("status", getStatus())
                .append("prize", getPrize())
                .append("cost", getCost())
                .append("lotteryName", getLotteryName())
                .append("puserId", getPuserId())
                .append("anchor", getAnchor())
                .append("code", getCode())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
