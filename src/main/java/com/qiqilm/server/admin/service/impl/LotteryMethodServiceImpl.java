package com.qiqilm.server.admin.service.impl;

import java.util.List;

import com.qiqilm.server.admin.mapper.LotteryMethodMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.domain.LotteryMethod;
import com.qiqilm.server.admin.service.ILotteryMethodService;

/**
 * 彩票种类Service业务层处理
 *
 * @author 77tv
 * @date 2021-02-23
 */
@Service
public class LotteryMethodServiceImpl implements ILotteryMethodService {
    @Autowired
    private LotteryMethodMapper lotteryMethodMapper;

    /**
     * 查询彩票种类列表
     *
     * @param lotteryMethod 彩票种类
     * @return 彩票种类
     */
    @Override
    public List<LotteryMethod> selectLotteryMethodList(LotteryMethod lotteryMethod) {
        return lotteryMethodMapper.selectLotteryMethodList(lotteryMethod);
    }
}
