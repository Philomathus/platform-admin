package com.qiqilm.server.admin.service.impl;

import java.util.List;

import com.qiqilm.server.admin.domain.ConfigUsdtRecharge;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ConfigUsdtRechargeMapper;

import com.qiqilm.server.admin.service.IConfigUsdtRechargeService;

/**
 * USDT渠道 Service业务层处理
 *
 * @author 77tv
 * @date 2021-09-11
 */
@Service
public class ConfigUsdtRechargeServiceImpl implements IConfigUsdtRechargeService {
    @Autowired
    private ConfigUsdtRechargeMapper configUsdtRechargeMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public ConfigUsdtRecharge selectConfigUsdtRechargeById(String id) {
        return configUsdtRechargeMapper.selectConfigUsdtRechargeById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param configUsdtRecharge 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ConfigUsdtRecharge> selectConfigUsdtRechargeList(ConfigUsdtRecharge configUsdtRecharge) {
        return configUsdtRechargeMapper.selectConfigUsdtRechargeList(configUsdtRecharge);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param configUsdtRecharge 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertConfigUsdtRecharge(ConfigUsdtRecharge configUsdtRecharge) {
        configUsdtRecharge.setCreateTime(DateUtils.getNowDate());
        return configUsdtRechargeMapper.insertConfigUsdtRecharge(configUsdtRecharge);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param configUsdtRecharge 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateConfigUsdtRecharge(ConfigUsdtRecharge configUsdtRecharge) {
        configUsdtRecharge.setUpdateTime(DateUtils.getNowDate());
        return configUsdtRechargeMapper.updateConfigUsdtRecharge(configUsdtRecharge);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteConfigUsdtRechargeByIds(String[] ids) {
        return configUsdtRechargeMapper.deleteConfigUsdtRechargeByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteConfigUsdtRechargeById(String id) {
        return configUsdtRechargeMapper.deleteConfigUsdtRechargeById(id);
    }
}
