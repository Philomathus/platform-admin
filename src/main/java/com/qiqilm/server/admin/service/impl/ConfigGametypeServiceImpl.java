package com.qiqilm.server.admin.service.impl;

import java.util.List;

import com.qiqilm.server.admin.domain.ConfigGametype;
import com.qiqilm.server.admin.mapper.ConfigGametypeMapper;
import com.qiqilm.server.admin.service.IConfigGametypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class ConfigGametypeServiceImpl implements IConfigGametypeService {
    @Autowired
    private ConfigGametypeMapper configGametypeMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public ConfigGametype selectConfigGametypeById(String id) {
        return configGametypeMapper.selectConfigGametypeById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param configGametype 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ConfigGametype> selectConfigGametypeList(ConfigGametype configGametype) {
        return configGametypeMapper.selectConfigGametypeList(configGametype);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param configGametype 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertConfigGametype(ConfigGametype configGametype) {
        return configGametypeMapper.insertConfigGametype(configGametype);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param configGametype 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateConfigGametype(ConfigGametype configGametype) {
        return configGametypeMapper.updateConfigGametype(configGametype);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteConfigGametypeByIds(String[] ids) {
        return configGametypeMapper.deleteConfigGametypeByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteConfigGametypeById(String id) {
        return configGametypeMapper.deleteConfigGametypeById(id);
    }
}