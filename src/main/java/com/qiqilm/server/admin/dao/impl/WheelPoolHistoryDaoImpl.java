package com.qiqilm.server.admin.dao.impl;


import com.qiqilm.server.admin.dao.WheelPoolHistoryDao;
import com.qiqilm.server.admin.domain.WheelPoolHistory;
import com.qiqilm.server.admin.mapper.WheelPoolHistoryMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class WheelPoolHistoryDaoImpl implements WheelPoolHistoryDao {

    @Resource
    private WheelPoolHistoryMapper wheelPoolHistoryMapper;

    @Override
    public List<WheelPoolHistory> selectAllWheelPoolHistory(WheelPoolHistory wheelPoolHistory) {
        return wheelPoolHistoryMapper.selectAllWheelPoolHistory(wheelPoolHistory);
    }
}
