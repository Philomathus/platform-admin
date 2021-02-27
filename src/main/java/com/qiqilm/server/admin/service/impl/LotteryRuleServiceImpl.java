package com.qiqilm.server.admin.service.impl;

import java.util.List;

import com.qiqilm.server.admin.domain.ActivityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LotteryRuleMapper;
import com.qiqilm.server.admin.domain.LotteryRule;
import com.qiqilm.server.admin.service.ILotteryRuleService;

/**
 * 开奖规则说明Service业务层处理
 *
 * @author 77tv
 * @date 2021-02-26
 */
@Service
public class LotteryRuleServiceImpl implements ILotteryRuleService {
    @Autowired
    private LotteryRuleMapper lotteryRuleMapper;

    /**
     * 查询开奖规则说明
     *
     * @param id 开奖规则说明ID
     * @return 开奖规则说明
     */
    @Override
    public LotteryRule selectLotteryRuleById(Long id) {
        return lotteryRuleMapper.selectLotteryRuleById(id);
    }

    /**
     * 查询开奖规则说明列表
     *
     * @param lotteryRule 开奖规则说明
     * @return 开奖规则说明
     */
    @Override
    public List<LotteryRule> selectLotteryRuleList(LotteryRule lotteryRule) {
        return lotteryRuleMapper.selectLotteryRuleList(lotteryRule);
    }

    /**
     * 新增开奖规则说明
     *
     * @param lotteryRule 开奖规则说明
     * @return 结果
     */
    @Override
    public int insertLotteryRule(LotteryRule lotteryRule) {
        return lotteryRuleMapper.insertLotteryRule(lotteryRule);
    }

    /**
     * 修改开奖规则说明
     *
     * @param lotteryRule 开奖规则说明
     * @return 结果
     */
    @Override
    public int updateLotteryRule(LotteryRule lotteryRule) {
        return lotteryRuleMapper.updateLotteryRule(lotteryRule);
    }

    /**
     * 批量删除开奖规则说明
     *
     * @param ids 需要删除的开奖规则说明ID
     * @return 结果
     */
    @Override
    public int deleteLotteryRuleByIds(Long[] ids) {
        return lotteryRuleMapper.deleteLotteryRuleByIds(ids);
    }

    /**
     * 删除开奖规则说明信息
     *
     * @param id 开奖规则说明ID
     * @return 结果
     */
    @Override
    public int deleteLotteryRuleById(Long id) {
        return lotteryRuleMapper.deleteLotteryRuleById(id);
    }

    @Override
    public List<LotteryRule> selectLotteryRuleType() {
        return lotteryRuleMapper.selectLotteryRuleType();
    }
}
