package com.qiqilm.server.admin.service.impl;

import java.util.List;

import com.qiqilm.server.admin.domain.GameInfo;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.rsp.RspGameInfo;
import com.qiqilm.server.admin.mapper.GameInfoMapper;
import com.qiqilm.server.admin.mapper.GamePlatformMapper;
import com.qiqilm.server.admin.service.IGameInfoService;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class GameInfoServiceImpl implements IGameInfoService {
    @Autowired
    private GameInfoMapper gameInfoMapper;
    @Autowired
    private GamePlatformMapper gamePlatformMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public GameInfo selectGameInfoById(String id) {
        return gameInfoMapper.selectGameInfoById(id);
    }

    @Override
    public Integer updateStatus(GameInfo gameInfo) {
        return  gameInfoMapper.updateStatus(gameInfo);

    }

    @Override
    public int changeIsWh(GameInfo gameInfo) {
        return  gameInfoMapper.changeIsWh(gameInfo);

    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param gameInfo 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<RspGameInfo> selectGameInfoList(GameInfo gameInfo) {
        return gameInfoMapper.selectGameInfoList(gameInfo);
    }

    @Override
    public List<RspGameInfo> getGameListInfo() {
        return gamePlatformMapper.getGameListInfo();
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param gameInfo 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertGameInfo(GameInfo gameInfo) {
        return gameInfoMapper.insertGameInfo(gameInfo);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param gameInfo 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateGameInfo(GameInfo gameInfo) {
        return gameInfoMapper.updateGameInfo(gameInfo);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteGameInfoByIds(String id) {
        return gameInfoMapper.deleteGameInfoByIds(id);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteGameInfoById(String id) {
        return gameInfoMapper.deleteGameInfoById(id);
    }
}