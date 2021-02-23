package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LotteryTempMapper;
import com.qiqilm.server.admin.domain.LotteryTemp;
import com.qiqilm.server.admin.service.ILotteryTempService;

/**
 * 彩票即时信息Service业务层处理
 *
 * @author 77tv
 * @date 2021-02-23
 */
@Service
public class LotteryTempServiceImpl implements ILotteryTempService {
    @Autowired
    private LotteryTempMapper lotteryTempMapper;

    /**
     * 查询彩票即时信息列表
     *
     * @param lotteryTemp 彩票即时信息
     * @return 彩票即时信息
     */
    @Override
    public List<LotteryTemp> selectLotteryTempList(LotteryTemp lotteryTemp) {
        return lotteryTempMapper.selectLotteryTempList(lotteryTemp);
    }

}
