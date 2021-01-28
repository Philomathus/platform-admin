package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ServerOssMapper;
import com.qiqilm.server.admin.domain.ServerOss;
import com.qiqilm.server.admin.service.IServerOssService;

/**
 * oss文件存储服务配置Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class ServerOssServiceImpl implements IServerOssService {
    @Autowired
    private ServerOssMapper serverOssMapper;

    /**
     * 查询oss文件存储服务配置
     *
     * @param id oss文件存储服务配置ID
     * @return oss文件存储服务配置
     */
    @Override
    public ServerOss selectServerOssById(Long id) {
        return serverOssMapper.selectServerOssById(id);
    }

    /**
     * 查询oss文件存储服务配置列表
     *
     * @param serverOss oss文件存储服务配置
     * @return oss文件存储服务配置
     */
    @Override
    public List<ServerOss> selectServerOssList(ServerOss serverOss) {
        return serverOssMapper.selectServerOssList(serverOss);
    }

    /**
     * 新增oss文件存储服务配置
     *
     * @param serverOss oss文件存储服务配置
     * @return 结果
     */
    @Override
    public int insertServerOss(ServerOss serverOss) {
        serverOss.setCreateTime(DateUtils.getNowDate());
        return serverOssMapper.insertServerOss(serverOss);
    }

    /**
     * 修改oss文件存储服务配置
     *
     * @param serverOss oss文件存储服务配置
     * @return 结果
     */
    @Override
    public int updateServerOss(ServerOss serverOss) {
        serverOss.setUpdateTime(DateUtils.getNowDate());
        return serverOssMapper.updateServerOss(serverOss);
    }

    /**
     * 批量删除oss文件存储服务配置
     *
     * @param ids 需要删除的oss文件存储服务配置ID
     * @return 结果
     */
    @Override
    public int deleteServerOssByIds(Long[] ids) {
        return serverOssMapper.deleteServerOssByIds(ids);
    }

    /**
     * 删除oss文件存储服务配置信息
     *
     * @param id oss文件存储服务配置ID
     * @return 结果
     */
    @Override
    public int deleteServerOssById(Long id) {
        return serverOssMapper.deleteServerOssById(id);
    }
}
