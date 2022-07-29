package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.dao.WheelPoolDao;
import com.qiqilm.server.admin.domain.WheelPool;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.service.WheelPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * wheel pool Service implementation 执行
 *
 * @author Rajesh
 * @date 2022-07-29
 */

@Service
public class WheelPoolServiceImpl implements WheelPoolService {


    @Autowired
    private WheelPoolDao wheelPoolDao;

    @Override
    public List<WheelPool> selectAllWheelPool(WheelPool wheelPool) {
       List<WheelPool> wheelPoolList =  wheelPoolDao.selectAllWheelPool(wheelPool);
       if(wheelPoolList ==null){
           throw new BusinessException("数据不可用");
       }
        return wheelPoolList;
    }

    @Override
    public WheelPool findWheelPoolById(Long wheelId) {
        WheelPool wheelPoolById = wheelPoolDao.findWheelPoolById(wheelId);
        if(wheelPoolById ==null){
            throw new BusinessException("没有 id 的数据： "+wheelId);
        }
        return wheelPoolById;
    }

    @Override
    public int updateWheelPool(WheelPool wheelPool) {
        return wheelPoolDao.updateWheelPool(wheelPool);
    }
}
