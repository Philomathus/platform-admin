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

    @Override
    public List<WheelPool> selectAllWheelPool(WheelPool wheelPool) {
        return wheelPoolMapper.selectAllWheelPool(wheelPool);
    }

    @Override
    public WheelPool findWheelPoolById(Long id) {
        return wheelPoolMapper.WheelPoolFindById(id);
    }

    @Override
    public int updateWheelPool(WheelPool wheelPool) {
        return wheelPoolMapper.updateWheelPool(wheelPool);
    }


}
