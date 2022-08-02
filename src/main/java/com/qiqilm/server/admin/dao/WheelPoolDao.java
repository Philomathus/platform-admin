package com.qiqilm.server.admin.dao;


import com.qiqilm.server.admin.domain.WheelPool;

import java.util.List;

/**
 * wheel pool dao接口
 *
 * @author Rajesh
 * @date 2022-07-29
 */


public interface WheelPoolDao {

    List<WheelPool> selectAllWheelPool(WheelPool wheelPool);

    WheelPool findWheelPoolById(Long id);

    int updateWheelPool(WheelPool wheelPool);
}
