package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.WheelHistory;

import java.util.List;

/**
 * 转盘中奖历史Mapper接口
 *
 * @author 77tv
 * @date 2021-03-05
 */
public interface WheelHistoryMapper {
	/**
	 * 查询转盘中奖历史
	 *
	 * @param id 转盘中奖历史ID
	 * @return 转盘中奖历史
	 */
	public WheelHistory selectWheelHistoryById(Long id);

	/**
	 * 查询转盘中奖历史列表
	 *
	 * @param wheelHistory 转盘中奖历史
	 * @return 转盘中奖历史集合
	 */
	public List<WheelHistory> selectWheelHistoryList(WheelHistory wheelHistory);

	/**
	 * 新增转盘中奖历史
	 *
	 * @param wheelHistory 转盘中奖历史
	 * @return 结果
	 */
	public int insertWheelHistory(WheelHistory wheelHistory);

	/**
	 * 修改转盘中奖历史
	 *
	 * @param wheelHistory 转盘中奖历史
	 * @return 结果
	 */
	public int updateWheelHistory(WheelHistory wheelHistory);

	/**
	 * 删除转盘中奖历史
	 *
	 * @param id 转盘中奖历史ID
	 * @return 结果
	 */
	public int deleteWheelHistoryById(Long id);

	/**
	 * 批量删除转盘中奖历史
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteWheelHistoryByIds(Long[] ids );
}
