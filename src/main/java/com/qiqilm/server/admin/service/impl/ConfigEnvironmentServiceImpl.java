package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ConfigEnvironmentMapper;
import com.qiqilm.server.admin.domain.ConfigEnvironment;
import com.qiqilm.server.admin.service.IConfigEnvironmentService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class ConfigEnvironmentServiceImpl implements IConfigEnvironmentService {
    @Autowired
    private ConfigEnvironmentMapper configEnvironmentMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param envCode 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public ConfigEnvironment selectConfigEnvironmentById(String envCode) {
        return configEnvironmentMapper.selectConfigEnvironmentById(envCode);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param configEnvironment 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ConfigEnvironment> selectConfigEnvironmentList(ConfigEnvironment configEnvironment) {
        return configEnvironmentMapper.selectConfigEnvironmentList(configEnvironment);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param configEnvironment 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertConfigEnvironment(ConfigEnvironment configEnvironment) {
        return configEnvironmentMapper.insertConfigEnvironment(configEnvironment);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param configEnvironment 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateConfigEnvironment(ConfigEnvironment configEnvironment) {
        return configEnvironmentMapper.updateConfigEnvironment(configEnvironment);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param envCodes 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteConfigEnvironmentByIds(String[] envCodes) {
        return configEnvironmentMapper.deleteConfigEnvironmentByIds(envCodes);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param envCode 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteConfigEnvironmentById(String envCode) {
        return configEnvironmentMapper.deleteConfigEnvironmentById(envCode);
    }
}
