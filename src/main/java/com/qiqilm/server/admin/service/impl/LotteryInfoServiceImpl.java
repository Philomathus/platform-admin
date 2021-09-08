package com.qiqilm.server.admin.service.impl;

import java.util.List;

import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.domain.ActivityInfo;
import com.qiqilm.server.admin.domain.LotteryPrizepool;
import com.qiqilm.server.admin.domain.MemberBcode;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LotteryInfoMapper;
import com.qiqilm.server.admin.domain.LotteryInfo;
import com.qiqilm.server.admin.service.ILotteryInfoService;
import org.springframework.util.CollectionUtils;

/**
 * 彩票名称Service业务层处理
 *
 * @author 77tv
 * @date 2021-02-23
 */
@Service
public class LotteryInfoServiceImpl implements ILotteryInfoService {
    @Autowired
    private LotteryInfoMapper lotteryInfoMapper;

    @Autowired
    private ConfigDomainCacheUtil configDomainCacheUtil;

    /**
     * 查询彩票名称数据
     *
     * @param id 彩票名称数据ID
     * @return 彩票名称数据
     */
    @Override
    public LotteryInfo selectLotteryInfoListById(String id ) {
        return lotteryInfoMapper.selectLotteryInfoListById( id );
    }

    /**
     * 查询彩票名称列表
     *
     * @param lotteryInfo 彩票名称
     * @return 彩票名称
     */
    @Override
    public List<LotteryInfo> selectLotteryInfoList(LotteryInfo lotteryInfo) {
        List<LotteryInfo> lotteryInfos = lotteryInfoMapper.selectLotteryInfoList(lotteryInfo);
        if (!CollectionUtils.isEmpty(lotteryInfos)) {
            String domainValue = configDomainCacheUtil.getValue("domain.oss");
            for (LotteryInfo info : lotteryInfos) {
                if (StringUtils.isNotBlank(info.getIcon()) && !info.getIcon().startsWith("http")) {
                    info.setIcon(domainValue + info.getIcon());
                }
            }
        }
        return lotteryInfos;
    }

    /**
     * 修改彩票名称
     *
     * @param lotteryInfo 彩票名称
     * @return 结果
     */
    @Override
    public int updateLotteryInfo(LotteryInfo lotteryInfo) {
        return lotteryInfoMapper.updateLotteryInfo(lotteryInfo);
    }
}
