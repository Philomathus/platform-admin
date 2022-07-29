package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.WheelPool;
import com.qiqilm.server.admin.domain.WheelPoolHistory;

import java.util.List;

/**
 * 轮池一览Mapper接口
 *
 * @author rajesh
 * @date 2022-07-29
 */
public interface WheelPoolHistoryMapper {

	/**
	 * 查询轮池列表 - wheel pool
	 *
	 * @param wheelPoolHistory 轮池一览 list of wheelPool
	 * @return 轮池列表集合 list of wheelPool
	 */
	 List<WheelPoolHistory> selectAllWheelPoolHistory(WheelPoolHistory wheelPoolHistory);


}
