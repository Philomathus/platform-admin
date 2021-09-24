package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.WheelHistoryDice;

/**
 * 博饼中奖记录Mapper接口
 *
 * @author 77tv
 * @date 2021-09-02
 */
public interface WheelHistoryDiceMapper {
	/**
	 * 查询博饼中奖记录
	 *
	 * @param id 博饼中奖记录ID
	 * @return 博饼中奖记录
	 */
	public WheelHistoryDice selectWheelHistoryDiceById(Long id);

	/**
	 * 查询博饼中奖记录列表
	 *
	 * @param wheelHistoryDice 博饼中奖记录
	 * @return 博饼中奖记录集合
	 */
	public List<WheelHistoryDice> selectWheelHistoryDiceList(WheelHistoryDice wheelHistoryDice);

	/**
	 * 新增博饼中奖记录
	 *
	 * @param wheelHistoryDice 博饼中奖记录
	 * @return 结果
	 */
	public int insertWheelHistoryDice(WheelHistoryDice wheelHistoryDice);

	/**
	 * 修改博饼中奖记录
	 *
	 * @param wheelHistoryDice 博饼中奖记录
	 * @return 结果
	 */
	public int updateWheelHistoryDice(WheelHistoryDice wheelHistoryDice);

	/**
	 * 删除博饼中奖记录
	 *
	 * @param id 博饼中奖记录ID
	 * @return 结果
	 */
	public int deleteWheelHistoryDiceById(Long id);

	/**
	 * 批量删除博饼中奖记录
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteWheelHistoryDiceByIds(Long[] ids );
}
