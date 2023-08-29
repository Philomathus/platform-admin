package com.qiqilm.server.admin.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 游戏注单对象 game_data_record
 *
 * @author MengJun
 */
@Data
@EqualsAndHashCode( callSuper = false )
public class GameDataRecord {
    private String id;
    /**
     * 平台代理
     */
    private String agent;
    /**
     * 游戏代理
     */
    private String gameAgent;
    /**
     * 游戏平台ID
     */
    private Long   platformId;
    /**
     * 游戏注单ID
     */
    private String gameId;
    /**
     * 游戏局号
     */
    private String gameRound;
    /**
     * 会员ID
     */
    private String account;
    /**
     * 游戏码
     */
    private String kindId;
    /**
     * 桌号
     */
    private String tableId;
    /**
     * 椅子id
     */
    private String chairId;
    /**
     * 总下注
     */
    private String allBet;
    /**
     * 有效下注
     */
    private String cellScore;
    /**
     * 盈利
     */
    private String profit;
    /**
     * 抽水
     */
    private String revenue;
    /**
     * 游戏开始时间
     */
    private String gameStartTime;
    /**
     * 游戏结束时间
     */
    private String gameEndTime;
}