package com.qiqilm.server.admin.service.impl;

import java.util.List;

import com.qiqilm.server.admin.mapper.ConfigVipMapper;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.domain.ConfigVip;
import com.qiqilm.server.admin.service.IConfigVipService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-02-02
 */
@Service
public class ConfigVipServiceImpl implements IConfigVipService {
    @Autowired
    private ConfigVipMapper configVipMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public ConfigVip selectConfigVipById(String id) {
        return configVipMapper.selectConfigVipById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param configVip 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ConfigVip> selectConfigVipList(ConfigVip configVip) {
        return configVipMapper.selectConfigVipList(configVip);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param configVip 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertConfigVip(ConfigVip configVip) {
        configVip.setCreateTime(DateUtils.getNowDate());
        return configVipMapper.insertConfigVip(configVip);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param configVip 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateConfigVip(ConfigVip configVip) {
        configVip.setUpdateTime(DateUtils.getNowDate());
        return configVipMapper.updateConfigVip(configVip);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteConfigVipByIds(String[] ids) {
        return configVipMapper.deleteConfigVipByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteConfigVipById(String id) {
        return configVipMapper.deleteConfigVipById(id);
    }
}