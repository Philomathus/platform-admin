package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LotteryBet0Mapper;
import com.qiqilm.server.admin.domain.LotteryBet0;
import com.qiqilm.server.admin.service.ILotteryBet0Service;

/**
 * 用户投资行为Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-03
 */
@Service
public class LotteryBet0ServiceImpl implements ILotteryBet0Service {
    @Autowired
    private LotteryBet0Mapper lotteryBet0Mapper;


    /**
     * 查询用户投资行为列表
     *
     * @param lotteryBet0 用户投资行为
     * @return 用户投资行为
     */
    @Override
    public List<LotteryBet0> selectLotteryBet0List(LotteryBet0 lotteryBet0) {
        String tableLast;
        if( StringUtils.isNotBlank( lotteryBet0.getPuserId() ) ){
            tableLast = lotteryBet0.getPuserId().substring( lotteryBet0.getPuserId().length() -1 );
        }else{
            tableLast = "_view";
        }
        lotteryBet0.setTableLast( tableLast );
        return lotteryBet0Mapper.selectLotteryBet0List(lotteryBet0);
    }

}
