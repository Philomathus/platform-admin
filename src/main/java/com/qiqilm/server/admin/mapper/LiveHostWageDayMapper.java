package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveHostWageDay;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-03-29
 */
public interface LiveHostWageDayMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public LiveHostWageDay selectLiveHostWageDayById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param liveHostWageDay 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<LiveHostWageDay> selectLiveHostWageDayList(LiveHostWageDay liveHostWageDay);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param liveHostWageDay 【请填写功能名称】
	 * @return 结果
	 */
	public int insertLiveHostWageDay(LiveHostWageDay liveHostWageDay);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param liveHostWageDay 【请填写功能名称】
	 * @return 结果
	 */
	public int updateLiveHostWageDay(LiveHostWageDay liveHostWageDay);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteLiveHostWageDayById(String id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLiveHostWageDayByIds(String[] ids );
}