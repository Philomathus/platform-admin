package com.qiqilm.server.admin.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qiqilm.server.admin.domain.MemberGameData;
import com.qiqilm.server.admin.mapper.MemberGameDataMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.GameDataLogMapper;
import com.qiqilm.server.admin.domain.GameDataLog;
import com.qiqilm.server.admin.service.IGameDataLogService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 总代理游戏注单Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-17
 */
@Service
public class GameDataLogServiceImpl implements IGameDataLogService {
    @Resource
    private GameDataLogMapper gameDataLogMapper;

    @Resource
    private MemberGameDataMapper memberGameDataMapper;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    /**
     * 查询总代理游戏注单
     *
     * @param id 总代理游戏注单ID
     * @return 总代理游戏注单
     */
    @Override
    public GameDataLog selectGameDataLogById(String id) {
        return gameDataLogMapper.selectGameDataLogById(id);
    }

    /**
     * 查询总代理游戏注单列表
     *
     * @return 总代理游戏注单
     */
    @Override
    public List<GameDataLog> selectGameDataLogList(String cxAgent,String start, String end,String account, String platformId) {
        return gameDataLogMapper.selectGameDataLogList(cxAgent,start,end,account,platformId);
    }

    @Override
    @Transactional
    public void beatCode(Map<Integer,String> platformType,String cxAgent, String start, String end, String account, String platformId) {
        Map<Integer,List<MemberGameData>> dataMap = new HashMap<>();
        for(GameDataLog og: gameDataLogMapper.selectGameDataLogList(cxAgent,start,end,account,platformId)){
            if ( memberGameDataMapper.findExist(og.getAccount().substring(og.getAccount().length()-1),og.getId()) != null ) {
                continue;
            }

            MemberGameData gameDataLog = new MemberGameData();
            gameDataLog.setId( og.getId() );
            gameDataLog.setGameId( og.getGameId());
            gameDataLog.setAccount( og.getAccount());
            gameDataLog.setKindId( og.getKindId() );
            gameDataLog.setCellScore( og.getCellScore() );
            gameDataLog.setAllBet( og.getAllBet() );
            gameDataLog.setProfit( og.getProfit() );
            gameDataLog.setGameStartTime(og.getGameStartTime());
            gameDataLog.setGameEndTime( og.getGameEndTime());
            gameDataLog.setAgent( og.getAgent() );
            gameDataLog.setStatus( 0 );
            gameDataLog.setPlatformType( platformType.get(og.getPlatformId()));
            gameDataLog.setPlatformId( og.getPlatformId());
            gameDataLog.setRevenue(og.getRevenue());
            dataMap.putIfAbsent(og.getPlatformId(),new ArrayList<>());
            dataMap.get(og.getPlatformId()).add(gameDataLog);
        }

        SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession( ExecutorType.BATCH, false );

    }


    /**
     * 批量删除总代理游戏注单
     *
     * @param ids 需要删除的总代理游戏注单ID
     * @return 结果
     */
    @Override
    public int deleteGameDataLogByIds(String[] ids) {
        return gameDataLogMapper.deleteGameDataLogByIds(ids);
    }

}
