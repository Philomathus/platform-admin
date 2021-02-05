package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 会员注单数据对象 member_game_data
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Data
public class MemberGameData extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 本地ID */
    private String id;

    /** 游戏局号 */
    @Excel(name = "游戏局号")
    private String gameId;

    /** 账号 */
    @Excel(name = "账号")
    private String account;

    /** 游戏id */
    @Excel(name = "游戏id")
    private String kindId;

    /** 有效下注 */
    @Excel(name = "有效下注")
    private String cellScore;

    /** 总下注 */
    @Excel(name = "总下注")
    private String allBet;

    /** 盈利 */
    @Excel(name = "盈利")
    private String profit;

    /** 抽水 */
    @Excel(name = "抽水")
    private String revenue;

    /** 游戏结束时间 */
    @Excel(name = "游戏结束时间")
    private String gameEndTime;

    /** 本地平台id */
    @Excel(name = "本地平台id")
    private Long platformId;

    /** 代理编号 */
    @Excel(name = "代理编号")
    private String agent;

    /** 游戏平台类型 */
    @Excel(name = "游戏平台类型")
    private String platformType;

    /** 0:未洗码1已经洗码 */
    @Excel(name = "0:未洗码1已经洗码")
    private Long status;

    /** 游戏开始时间 */
    @Excel(name = "游戏开始时间")
    private String gameStartTime;
    /**
     * 选择日期
     */
    private String[] selectDate;

    private String startTime;
    private String endTime;

    private String platformName;
    private String sonPlatformName;

    public String[] getSelectDate() {
        return selectDate;
    }

    public void setSelectDate(String[] selectDate) {
        this.selectDate = selectDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }
    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }
    public void setKindId(String kindId) {
        this.kindId = kindId;
    }

    public String getKindId() {
        return kindId;
    }
    public void setCellScore(String cellScore) {
        this.cellScore = cellScore;
    }

    public String getCellScore() {
        return cellScore;
    }
    public void setAllBet(String allBet) {
        this.allBet = allBet;
    }

    public String getAllBet() {
        return allBet;
    }
    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getProfit() {
        return profit;
    }
    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public String getRevenue() {
        return revenue;
    }
    public void setGameEndTime(String gameEndTime) {
        this.gameEndTime = gameEndTime;
    }

    public String getGameEndTime() {
        return gameEndTime;
    }
    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public Long getPlatformId() {
        return platformId;
    }
    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getAgent() {
        return agent;
    }
    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public String getPlatformType() {
        return platformType;
    }
    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getStatus() {
        return status;
    }
    public void setGameStartTime(String gameStartTime) {
        this.gameStartTime = gameStartTime;
    }

    public String getGameStartTime() {
        return gameStartTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("gameId", getGameId())
            .append("account", getAccount())
            .append("kindId", getKindId())
            .append("cellScore", getCellScore())
            .append("allBet", getAllBet())
            .append("profit", getProfit())
            .append("revenue", getRevenue())
            .append("gameEndTime", getGameEndTime())
            .append("platformId", getPlatformId())
            .append("agent", getAgent())
            .append("platformType", getPlatformType())
            .append("status", getStatus())
            .append("gameStartTime", getGameStartTime())
            .toString();
    }
}
