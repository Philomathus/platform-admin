package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.LotteryBet0;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 总代理游戏注单Service接口
 *
 * @author 77tv
 * @date 2021-03-17
 */
public interface IGameDataLogService {

    public void beatGameCodeAgent( Map<Integer, String> platformType, Map<Integer, BigDecimal> beatRateMap,
                                   String start, String end, String account, Long platformId );

    public void beatLotteryCode( String platformTypeId, BigDecimal beatRate, String start, String end );


    public void beatLiveProp( String platformTypeId, BigDecimal beatRate, long start, long end );


    void beatLotteryCode2( String platformTypeId, BigDecimal beatRate, LotteryBet0 lotteryBet0 );
}
