package com.qiqilm.server.admin.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LotteryDiceConfigMapper;
import com.qiqilm.server.admin.domain.LotteryDiceConfig;
import com.qiqilm.server.admin.service.ILotteryDiceConfigService;

import javax.annotation.Resource;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2022-01-27
 */
@Service
public class LotteryDiceConfigServiceImpl implements ILotteryDiceConfigService {
    @Resource
    private LotteryDiceConfigMapper lotteryDiceConfigMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public LotteryDiceConfig selectLotteryDiceConfigById(Long id) {
        return lotteryDiceConfigMapper.selectLotteryDiceConfigById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param lotteryDiceConfig 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<LotteryDiceConfig> selectLotteryDiceConfigList(LotteryDiceConfig lotteryDiceConfig) {
        return lotteryDiceConfigMapper.selectLotteryDiceConfigList(lotteryDiceConfig);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param lotteryDiceConfig 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertLotteryDiceConfig(LotteryDiceConfig lotteryDiceConfig) {
        return lotteryDiceConfigMapper.insertLotteryDiceConfig(lotteryDiceConfig);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param lotteryDiceConfig 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateLotteryDiceConfig(LotteryDiceConfig lotteryDiceConfig) {
        return lotteryDiceConfigMapper.updateLotteryDiceConfig(lotteryDiceConfig);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLotteryDiceConfigByIds(Long[] ids) {
        return lotteryDiceConfigMapper.deleteLotteryDiceConfigByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLotteryDiceConfigById(Long id) {
        return lotteryDiceConfigMapper.deleteLotteryDiceConfigById(id);
    }
}
