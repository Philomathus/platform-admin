package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ServerLiveMapper;
import com.qiqilm.server.admin.domain.ServerLive;
import com.qiqilm.server.admin.service.IServerLiveService;

/**
 * 直播流服务配置Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class ServerLiveServiceImpl implements IServerLiveService {
    @Autowired
    private ServerLiveMapper serverLiveMapper;

    /**
     * 查询直播流服务配置
     *
     * @param id 直播流服务配置ID
     * @return 直播流服务配置
     */
    @Override
    public ServerLive selectServerLiveById(Long id) {
        return serverLiveMapper.selectServerLiveById(id);
    }

    /**
     * 查询直播流服务配置列表
     *
     * @param serverLive 直播流服务配置
     * @return 直播流服务配置
     */
    @Override
    public List<ServerLive> selectServerLiveList(ServerLive serverLive) {
        return serverLiveMapper.selectServerLiveList(serverLive);
    }

    /**
     * 新增直播流服务配置
     *
     * @param serverLive 直播流服务配置
     * @return 结果
     */
    @Override
    public int insertServerLive(ServerLive serverLive) {
        return serverLiveMapper.insertServerLive(serverLive);
    }

    /**
     * 修改直播流服务配置
     *
     * @param serverLive 直播流服务配置
     * @return 结果
     */
    @Override
    public int updateServerLive(ServerLive serverLive) {
        return serverLiveMapper.updateServerLive(serverLive);
    }

    /**
     * 批量删除直播流服务配置
     *
     * @param ids 需要删除的直播流服务配置ID
     * @return 结果
     */
    @Override
    public int deleteServerLiveByIds(Long[] ids) {
        return serverLiveMapper.deleteServerLiveByIds(ids);
    }

    /**
     * 删除直播流服务配置信息
     *
     * @param id 直播流服务配置ID
     * @return 结果
     */
    @Override
    public int deleteServerLiveById(Long id) {
        return serverLiveMapper.deleteServerLiveById(id);
    }
}
