package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ServerSmsMapper;
import com.qiqilm.server.admin.domain.ServerSms;
import com.qiqilm.server.admin.service.IServerSmsService;

/**
 * SMS短信服务配置Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class ServerSmsServiceImpl implements IServerSmsService {
    @Autowired
    private ServerSmsMapper serverSmsMapper;

    /**
     * 查询SMS短信服务配置
     *
     * @param id SMS短信服务配置ID
     * @return SMS短信服务配置
     */
    @Override
    public ServerSms selectServerSmsById(Long id) {
        return serverSmsMapper.selectServerSmsById(id);
    }

    /**
     * 查询SMS短信服务配置列表
     *
     * @param serverSms SMS短信服务配置
     * @return SMS短信服务配置
     */
    @Override
    public List<ServerSms> selectServerSmsList(ServerSms serverSms) {
        return serverSmsMapper.selectServerSmsList(serverSms);
    }

    /**
     * 新增SMS短信服务配置
     *
     * @param serverSms SMS短信服务配置
     * @return 结果
     */
    @Override
    public int insertServerSms(ServerSms serverSms) {
        return serverSmsMapper.insertServerSms(serverSms);
    }

    /**
     * 修改SMS短信服务配置
     *
     * @param serverSms SMS短信服务配置
     * @return 结果
     */
    @Override
    public int updateServerSms(ServerSms serverSms) {
        return serverSmsMapper.updateServerSms(serverSms);
    }

    /**
     * 批量删除SMS短信服务配置
     *
     * @param ids 需要删除的SMS短信服务配置ID
     * @return 结果
     */
    @Override
    public int deleteServerSmsByIds(Long[] ids) {
        return serverSmsMapper.deleteServerSmsByIds(ids);
    }

    /**
     * 删除SMS短信服务配置信息
     *
     * @param id SMS短信服务配置ID
     * @return 结果
     */
    @Override
    public int deleteServerSmsById(Long id) {
        return serverSmsMapper.deleteServerSmsById(id);
    }
}
