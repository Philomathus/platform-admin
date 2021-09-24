package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.WheelDiceConfig;
import com.qiqilm.server.admin.mapper.WheelDiceConfigMapper;
import com.qiqilm.server.admin.service.IWheelDiceConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-09-01
 */
@Service
public class WheelDiceConfigServiceImpl implements IWheelDiceConfigService {
    @Resource
    private WheelDiceConfigMapper wheelDiceConfigMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public WheelDiceConfig selectWheelDiceConfigById(Long id) {
        return wheelDiceConfigMapper.selectWheelDiceConfigById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param wheelDiceConfig 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<WheelDiceConfig> selectWheelDiceConfigList(WheelDiceConfig wheelDiceConfig) {
        return wheelDiceConfigMapper.selectWheelDiceConfigList(wheelDiceConfig);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param wheelDiceConfig 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertWheelDiceConfig(WheelDiceConfig wheelDiceConfig) {
        return wheelDiceConfigMapper.insertWheelDiceConfig(wheelDiceConfig);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param wheelDiceConfig 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateWheelDiceConfig(WheelDiceConfig wheelDiceConfig) {
        return wheelDiceConfigMapper.updateWheelDiceConfig(wheelDiceConfig);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteWheelDiceConfigByIds(Long[] ids) {
        return wheelDiceConfigMapper.deleteWheelDiceConfigByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteWheelDiceConfigById(Long id) {
        return wheelDiceConfigMapper.deleteWheelDiceConfigById(id);
    }
}
