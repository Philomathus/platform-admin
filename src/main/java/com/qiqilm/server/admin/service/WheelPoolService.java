package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.WheelPool;

import java.util.List;

/**
 * wheel pool Service接口
 *
 * @author Rajesh
 * @date 2022-07-29
 */

public interface WheelPoolService {

    List<WheelPool> selectAllWheelPool(WheelPool wheelPool);

    WheelPool findWheelPoolById(Long wheelId);

    int updateWheelPool(WheelPool wheelPool);
}
