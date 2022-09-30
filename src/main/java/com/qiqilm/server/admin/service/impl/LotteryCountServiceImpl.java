package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.LotteryCount;
import com.qiqilm.server.admin.mapper.LotteryCountMapper;
import com.qiqilm.server.admin.service.LotteryCountService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LotteryCountServiceImpl implements LotteryCountService {

    @Resource
    private LotteryCountMapper lotteryCountMapper;

    @Override
    public List<LotteryCount> selectAllLotteryCount(LotteryCount lotteryCount) {
        return lotteryCountMapper.selectAllLotteryCount(lotteryCount);
    }
}
