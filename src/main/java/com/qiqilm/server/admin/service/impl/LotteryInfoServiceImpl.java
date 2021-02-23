package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LotteryInfoMapper;
import com.qiqilm.server.admin.domain.LotteryInfo;
import com.qiqilm.server.admin.service.ILotteryInfoService;

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

    /**
     * 查询彩票名称列表
     *
     * @param lotteryInfo 彩票名称
     * @return 彩票名称
     */
    @Override
    public List<LotteryInfo> selectLotteryInfoList(LotteryInfo lotteryInfo) {
        return lotteryInfoMapper.selectLotteryInfoList(lotteryInfo);
    }
}
