package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ServerImMapper;
import com.qiqilm.server.admin.domain.ServerIm;
import com.qiqilm.server.admin.service.IServerImService;

/**
 * IM即时通讯服务配置Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class ServerImServiceImpl implements IServerImService {
    @Autowired
    private ServerImMapper serverImMapper;

    /**
     * 查询IM即时通讯服务配置
     *
     * @param id IM即时通讯服务配置ID
     * @return IM即时通讯服务配置
     */
    @Override
    public ServerIm selectServerImById(Long id) {
        return serverImMapper.selectServerImById(id);
    }

    /**
     * 查询IM即时通讯服务配置列表
     *
     * @param serverIm IM即时通讯服务配置
     * @return IM即时通讯服务配置
     */
    @Override
    public List<ServerIm> selectServerImList(ServerIm serverIm) {
        return serverImMapper.selectServerImList(serverIm);
    }

    /**
     * 新增IM即时通讯服务配置
     *
     * @param serverIm IM即时通讯服务配置
     * @return 结果
     */
    @Override
    public int insertServerIm(ServerIm serverIm) {
        return serverImMapper.insertServerIm(serverIm);
    }

    /**
     * 修改IM即时通讯服务配置
     *
     * @param serverIm IM即时通讯服务配置
     * @return 结果
     */
    @Override
    public int updateServerIm(ServerIm serverIm) {
        return serverImMapper.updateServerIm(serverIm);
    }

    /**
     * 批量删除IM即时通讯服务配置
     *
     * @param ids 需要删除的IM即时通讯服务配置ID
     * @return 结果
     */
    @Override
    public int deleteServerImByIds(Long[] ids) {
        return serverImMapper.deleteServerImByIds(ids);
    }

    /**
     * 删除IM即时通讯服务配置信息
     *
     * @param id IM即时通讯服务配置ID
     * @return 结果
     */
    @Override
    public int deleteServerImById(Long id) {
        return serverImMapper.deleteServerImById(id);
    }
}
