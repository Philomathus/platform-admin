package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.dao.WheelPoolDao;
import com.qiqilm.server.admin.domain.WheelPool;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.service.WheelPoolService;
import com.qiqilm.server.admin.utils.RedisUtil;
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

    private static final String WHEEL_POOL_LIST_KEY ="live:wheelPoolList";

    @Autowired
    private WheelPoolDao wheelPoolDao;

    @Autowired
    private RedisUtil redisUtil;


     /**查询获取所有轮池列表 query get all wheel pool list->service implementation layer*/
    @Override
    public List<WheelPool> selectAllWheelPool(WheelPool wheelPool) {
       List<WheelPool> wheelPoolList =  wheelPoolDao.selectAllWheelPool(wheelPool);
       if(wheelPoolList ==null){
           throw new BusinessException("数据不可用");
       }
        return wheelPoolList;
    }

    /**查询通过id获取数据 服务实现层-  query to get data by id -> service implementation layer*/
    @Override
    public WheelPool findWheelPoolById(Long wheelId) {
        WheelPool wheelPoolById = wheelPoolDao.findWheelPoolById(wheelId);
        if(wheelPoolById ==null){
            throw new BusinessException("没有 id 的数据： "+wheelId);
        }
        return wheelPoolById;
    }

    /** 更新轮池 update wheel pool ->service implementation layer*/
    @Override
    public int updateWheelPool(WheelPool wheelPool) {
        int wheelPoolUpdated =  wheelPoolDao.updateWheelPool(wheelPool);
        redisUtil.unlink(WHEEL_POOL_LIST_KEY);
        return wheelPoolUpdated;
    }
}
