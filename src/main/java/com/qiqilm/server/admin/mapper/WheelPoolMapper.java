package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.WheelPool;

import java.util.List;

/**
 * 轮池一览Mapper接口
 *
 * @author rajesh
 * @date 2022-07-29
 */
public interface WheelPoolMapper {

	/**
	 * 查询轮池列表 - wheel pool
	 *
	 * @param wheelPool 轮池一览 list of wheelPool
	 * @return 轮池列表集合 list of wheelPool
	 */
	 List<WheelPool> selectAllWheelPool(WheelPool wheelPool);


	/**
	 * 查询轮池列表 - wheel pool
	 *
	 * @param id fine by id -> wheelPool
	 * @return find wheelPool by id
	 */
	WheelPool WheelPoolFindById(Long id);

	/**
	 * 修改wheelpool列表 , update list of wheelPool
	 *
	 * @param wheelPool wheelPool
	 * @return 结果
	 */
	 int updateWheelPool(WheelPool wheelPool);


}
