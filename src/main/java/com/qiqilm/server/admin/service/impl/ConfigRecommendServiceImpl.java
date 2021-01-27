package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ConfigRecommendMapper;
import com.qiqilm.server.admin.domain.ConfigRecommend;
import com.qiqilm.server.admin.service.IConfigRecommendService;

/**
 * 推广设置Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class ConfigRecommendServiceImpl implements IConfigRecommendService {
    @Autowired
    private ConfigRecommendMapper configRecommendMapper;

    /**
     * 查询推广设置
     *
     * @param id 推广设置ID
     * @return 推广设置
     */
    @Override
    public ConfigRecommend selectConfigRecommendById(String id) {
        return configRecommendMapper.selectConfigRecommendById(id);
    }

    /**
     * 查询推广设置列表
     *
     * @param configRecommend 推广设置
     * @return 推广设置
     */
    @Override
    public List<ConfigRecommend> selectConfigRecommendList(ConfigRecommend configRecommend) {
        return configRecommendMapper.selectConfigRecommendList(configRecommend);
    }

    /**
     * 新增推广设置
     *
     * @param configRecommend 推广设置
     * @return 结果
     */
    @Override
    public int insertConfigRecommend(ConfigRecommend configRecommend) {
        return configRecommendMapper.insertConfigRecommend(configRecommend);
    }

    /**
     * 修改推广设置
     *
     * @param configRecommend 推广设置
     * @return 结果
     */
    @Override
    public int updateConfigRecommend(ConfigRecommend configRecommend) {
        return configRecommendMapper.updateConfigRecommend(configRecommend);
    }

    /**
     * 批量删除推广设置
     *
     * @param ids 需要删除的推广设置ID
     * @return 结果
     */
    @Override
    public int deleteConfigRecommendByIds(String[] ids) {
        return configRecommendMapper.deleteConfigRecommendByIds(ids);
    }

    /**
     * 删除推广设置信息
     *
     * @param id 推广设置ID
     * @return 结果
     */
    @Override
    public int deleteConfigRecommendById(String id) {
        return configRecommendMapper.deleteConfigRecommendById(id);
    }
}
