package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 总代理游戏注单对象 game_data_log
 *
 * @author 77tv
 * @date 2021-03-17
 */
@Data
public class GameDataLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 本地ID */
    private String id;

    /** 游戏局号 */
    @Excel(name = "游戏局号")
    private String gameId;

    /** 崇轩代理号 */
    @Excel(name = "崇轩代理号")
    private String cxAgent;

    /** 账号 */
    @Excel(name = "账号")
    private String account;

    /** 房间号 */
    @Excel(name = "房间号")
    private String serverId;

    /** 游戏id */
    @Excel(name = "游戏id")
    private String kindId;

    /** 桌号 */
    @Excel(name = "桌号")
    private String tableId;

    /** 椅子id */
    @Excel(name = "椅子id")
    private String chairId;

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

    /** 游戏开始时间 */
    @Excel(name = "游戏开始时间")
    private String gameStartTime;

    /** 游戏结束时间 */
    @Excel(name = "游戏结束时间")
    private String gameEndTime;

    /** 本地平台id */
    @Excel(name = "本地平台id")
    private Long platformId;

    /** 代理编号 */
    @Excel(name = "代理编号")
    private String agent;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("gameId", getGameId())
            .append("cxAgent", getCxAgent())
            .append("account", getAccount())
            .append("serverId", getServerId())
            .append("kindId", getKindId())
            .append("tableId", getTableId())
            .append("chairId", getChairId())
            .append("cellScore", getCellScore())
            .append("allBet", getAllBet())
            .append("profit", getProfit())
            .append("revenue", getRevenue())
            .append("gameStartTime", getGameStartTime())
            .append("gameEndTime", getGameEndTime())
            .append("platformId", getPlatformId())
            .append("agent", getAgent())
            .toString();
    }
}
