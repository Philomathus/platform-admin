package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
public class MemberGameData extends GameData {
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
    private Integer platformId;

    /** 代理编号 */
    @Excel(name = "代理编号")
    private String agent;

    /** 游戏平台类型 */
    @Excel(name = "游戏平台类型")
    private String platformType;

    /** 0:未洗码1已经洗码 */
    @Excel(name = "0:未洗码1已经洗码")
    private Integer status;

    /** 游戏开始时间 */
    @Excel(name = "游戏开始时间")
    private String gameStartTime;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("gameId", getGameId())
            .append("serverId", getServerId())
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
