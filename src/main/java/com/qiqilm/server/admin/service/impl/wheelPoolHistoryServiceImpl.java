package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.dao.WheelPoolHistoryDao;
import com.qiqilm.server.admin.domain.WheelPoolHistory;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.service.WheelPoolHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class wheelPoolHistoryServiceImpl implements WheelPoolHistoryService {

    @Autowired
    private WheelPoolHistoryDao wheelPoolHistoryDao;

    @Override
    public List<WheelPoolHistory> selectAllWheelPoolHistory(WheelPoolHistory wheelPoolHistory) {
        List<WheelPoolHistory> wheelPoolHistoryList = wheelPoolHistoryDao.selectAllWheelPoolHistory(wheelPoolHistory);
        if(wheelPoolHistoryList ==null){
            throw new BusinessException("数据不可用");
        }
        return wheelPoolHistoryList;
    }
}
