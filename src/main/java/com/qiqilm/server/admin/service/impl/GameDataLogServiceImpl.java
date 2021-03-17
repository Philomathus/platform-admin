package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.GameDataLogMapper;
import com.qiqilm.server.admin.domain.GameDataLog;
import com.qiqilm.server.admin.service.IGameDataLogService;

/**
 * 总代理游戏注单Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-17
 */
@Service
public class GameDataLogServiceImpl implements IGameDataLogService {
    @Autowired
    private GameDataLogMapper gameDataLogMapper;

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
     * @param gameDataLog 总代理游戏注单
     * @return 总代理游戏注单
     */
    @Override
    public List<GameDataLog> selectGameDataLogList(GameDataLog gameDataLog) {
        return gameDataLogMapper.selectGameDataLogList(gameDataLog);
    }

    /**
     * 新增总代理游戏注单
     *
     * @param gameDataLog 总代理游戏注单
     * @return 结果
     */
    @Override
    public int insertGameDataLog(GameDataLog gameDataLog) {
        return gameDataLogMapper.insertGameDataLog(gameDataLog);
    }

    /**
     * 修改总代理游戏注单
     *
     * @param gameDataLog 总代理游戏注单
     * @return 结果
     */
    @Override
    public int updateGameDataLog(GameDataLog gameDataLog) {
        return gameDataLogMapper.updateGameDataLog(gameDataLog);
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

    /**
     * 删除总代理游戏注单信息
     *
     * @param id 总代理游戏注单ID
     * @return 结果
     */
    @Override
    public int deleteGameDataLogById(String id) {
        return gameDataLogMapper.deleteGameDataLogById(id);
    }
}
