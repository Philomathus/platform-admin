package com.qiqilm.server.admin.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.qiqilm.server.admin.domain.GameDataLog;
import com.qiqilm.server.admin.domain.LiveNote;
import com.qiqilm.server.admin.domain.LotteryBet0;

/**
 * 总代理游戏注单Service接口
 *
 * @author 77tv
 * @date 2021-03-17
 */
public interface IGameDataLogService {
    /**
     * 查询总代理游戏注单
     *
     * @param id 总代理游戏注单ID
     * @return 总代理游戏注单
     */
    public GameDataLog selectGameDataLogById(String id);

    /**
     * 查询总代理游戏注单列表
     *
     * @return 总代理游戏注单集合
     */
    public List<GameDataLog> selectGameDataLogList(String cxAgent, String start, String end, String account, String platformId);


    /**
     * 游戏打码
     */
    @Deprecated
    public void beatGameCode(Map<Integer, String> platformType, Map<Integer, BigDecimal> beatRateMap, String cxAgent, String start, String end, String account, String platformId);

    public void beatGameCodeAgent(String dTime, Map<Integer, String> platformType, Map<Integer, BigDecimal> beatRateMap, String cxAgent, String start, String end, String account, String platformId);

    public void beatLotteryCode(String platformTypeId, BigDecimal beatRate, String start, String end);


    public void beatLiveProp(String platformTypeId, BigDecimal beatRate, long start, long end);

    /**
     * 批量删除总代理游戏注单
     *
     * @param ids 需要删除的总代理游戏注单ID
     * @return 结果
     */
    public int deleteGameDataLogByIds(String[] ids);

    void beatLotteryCode2(String platformTypeId, BigDecimal beatRate, LotteryBet0 lotteryBet0);


}
