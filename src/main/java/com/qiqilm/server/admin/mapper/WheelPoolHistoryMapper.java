package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.WheelPoolHistory;

import java.util.List;
import java.util.Map;

/**
 * 轮池一览Mapper interface接口
 *
 * @author rajesh
 * @date 2022-07-29
 */
public interface WheelPoolHistoryMapper {

	/**
	 * 查询轮池列表 - wheel pool history
	 *
	 * @param wheelPoolHistory 轮池一览 list of wheelPool history
	 * @return 轮池列表集合 list of wheelPool history
	 */
	 List<WheelPoolHistory> selectAllWheelPoolHistory(WheelPoolHistory wheelPoolHistory);


	/**
	 * 统计总行数和总钱轮池 History mapper 实现层
	 * - count total rows and total money wheel pool History mapper implementation layer
	 */

	List<Map<String, Object>> listCount(WheelPoolHistory wheelPoolHistory);
}
