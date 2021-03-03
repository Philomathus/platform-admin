package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ConfigWaiterMapper;
import com.qiqilm.server.admin.domain.ConfigWaiter;
import com.qiqilm.server.admin.service.IConfigWaiterService;

/**
 * 客服管理Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-03
 */
@Service
public class ConfigWaiterServiceImpl implements IConfigWaiterService {
    @Autowired
    private ConfigWaiterMapper configWaiterMapper;

    /**
     * 查询客服管理
     *
     * @param id 客服管理ID
     * @return 客服管理
     */
    @Override
    public ConfigWaiter selectConfigWaiterById(String id) {
        return configWaiterMapper.selectConfigWaiterById(id);
    }

    /**
     * 查询客服管理列表
     *
     * @param configWaiter 客服管理
     * @return 客服管理
     */
    @Override
    public List<ConfigWaiter> selectConfigWaiterList(ConfigWaiter configWaiter) {
        return configWaiterMapper.selectConfigWaiterList(configWaiter);
    }

    /**
     * 新增客服管理
     *
     * @param configWaiter 客服管理
     * @return 结果
     */
    @Override
    public int insertConfigWaiter(ConfigWaiter configWaiter) {
        return configWaiterMapper.insertConfigWaiter(configWaiter);
    }

    /**
     * 修改客服管理
     *
     * @param configWaiter 客服管理
     * @return 结果
     */
    @Override
    public int updateConfigWaiter(ConfigWaiter configWaiter) {
        configWaiter.setUpdateTime(DateUtils.getNowDate());
        return configWaiterMapper.updateConfigWaiter(configWaiter);
    }

    /**
     * 批量删除客服管理
     *
     * @param ids 需要删除的客服管理ID
     * @return 结果
     */
    @Override
    public int deleteConfigWaiterByIds(String[] ids) {
        return configWaiterMapper.deleteConfigWaiterByIds(ids);
    }

    /**
     * 删除客服管理信息
     *
     * @param id 客服管理ID
     * @return 结果
     */
    @Override
    public int deleteConfigWaiterById(String id) {
        return configWaiterMapper.deleteConfigWaiterById(id);
    }
}
