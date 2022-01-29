package com.qiqilm.server.admin.service.impl;

import java.util.List;

import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LotteryHistoryDiceMapper;
import com.qiqilm.server.admin.domain.LotteryHistoryDice;
import com.qiqilm.server.admin.service.ILotteryHistoryDiceService;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2022-01-27
 */
@Service
public class LotteryHistoryDiceServiceImpl implements ILotteryHistoryDiceService {
    @Resource
    private LotteryHistoryDiceMapper lotteryHistoryDiceMapper;
    @Autowired
    private ConfigDomainCacheUtil configDomainCacheUtil;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public LotteryHistoryDice selectLotteryHistoryDiceById(Long id) {
        return lotteryHistoryDiceMapper.selectLotteryHistoryDiceById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param lotteryHistoryDice 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<LotteryHistoryDice> selectLotteryHistoryDiceList(LotteryHistoryDice lotteryHistoryDice) {
        List<LotteryHistoryDice> lotteryHistoryDiceList = lotteryHistoryDiceMapper.selectLotteryHistoryDiceList( lotteryHistoryDice );
        if ( !CollectionUtils.isEmpty( lotteryHistoryDiceList ) ) {
            String domainValue = configDomainCacheUtil.getValue( "domain.oss" );
            for ( LotteryHistoryDice info : lotteryHistoryDiceList ) {
                if ( StringUtils.isNotBlank( info.getHeadImg() ) && !info.getHeadImg().startsWith( "http" ) ) {
                    info.setHeadImg( domainValue + info.getHeadImg() );
                }
            }
        }
        return lotteryHistoryDiceList;
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param lotteryHistoryDice 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertLotteryHistoryDice(LotteryHistoryDice lotteryHistoryDice) {
        return lotteryHistoryDiceMapper.insertLotteryHistoryDice(lotteryHistoryDice);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param lotteryHistoryDice 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateLotteryHistoryDice(LotteryHistoryDice lotteryHistoryDice) {
        return lotteryHistoryDiceMapper.updateLotteryHistoryDice(lotteryHistoryDice);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLotteryHistoryDiceByIds(Long[] ids) {
        return lotteryHistoryDiceMapper.deleteLotteryHistoryDiceByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLotteryHistoryDiceById(Long id) {
        return lotteryHistoryDiceMapper.deleteLotteryHistoryDiceById(id);
    }
}
