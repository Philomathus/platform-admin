package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ConfigMoneydesMapper;
import com.qiqilm.server.admin.domain.ConfigMoneydes;
import com.qiqilm.server.admin.service.IConfigMoneydesService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Service
public class ConfigMoneydesServiceImpl implements IConfigMoneydesService {
    @Autowired
    private ConfigMoneydesMapper configMoneydesMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param mdId 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public ConfigMoneydes selectConfigMoneydesById(Long mdId) {
        return configMoneydesMapper.selectConfigMoneydesById(mdId);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param configMoneydes 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ConfigMoneydes> selectConfigMoneydesList(ConfigMoneydes configMoneydes) {
        return configMoneydesMapper.selectConfigMoneydesList(configMoneydes);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param configMoneydes 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertConfigMoneydes(ConfigMoneydes configMoneydes) {
        return configMoneydesMapper.insertConfigMoneydes(configMoneydes);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param configMoneydes 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateConfigMoneydes(ConfigMoneydes configMoneydes) {
        return configMoneydesMapper.updateConfigMoneydes(configMoneydes);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param mdIds 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteConfigMoneydesByIds(Long[] mdIds) {
        return configMoneydesMapper.deleteConfigMoneydesByIds(mdIds);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param mdId 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteConfigMoneydesById(Long mdId) {
        return configMoneydesMapper.deleteConfigMoneydesById(mdId);
    }
}
