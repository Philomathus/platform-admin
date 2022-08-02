package com.qiqilm.server.admin.dao.impl;

import com.qiqilm.server.admin.dao.WheelPoolDao;
import com.qiqilm.server.admin.domain.WheelPool;
import com.qiqilm.server.admin.mapper.WheelPoolMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * wheel pool dao implementation 执行
 *
 * @author Rajesh
 * @date 2022-07-29
 */

@Component
public class WheelPoolDaoImpl implements WheelPoolDao {

    @Resource
    private WheelPoolMapper wheelPoolMapper;

    /**查询获取所有轮池列表 query get all wheel pool list->data access layer implementation layer*/
    @Override
    public List<WheelPool> selectAllWheelPool(WheelPool wheelPool) {
        return wheelPoolMapper.selectAllWheelPool(wheelPool);
    }

    /**查询通过id获取数据 服务实现层-  query to get data by id -> data access layer implementation layer*/
    @Override
    public WheelPool findWheelPoolById(Long id) {
        return wheelPoolMapper.WheelPoolFindById(id);
    }

    /** 更新轮池 update wheel pool -> data access layer implementation layer*/
    @Override
    public int updateWheelPool(WheelPool wheelPool) {
        return wheelPoolMapper.updateWheelPool(wheelPool);
    }


}
