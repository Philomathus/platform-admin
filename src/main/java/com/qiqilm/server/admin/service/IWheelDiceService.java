package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.WheelDice;

/**
 * 中秋博饼Service接口
 *
 * @author 77tv
 * @date 2021-09-02
 */
public interface IWheelDiceService {
	/**
	 * 查询中秋博饼
	 *
	 * @param id 中秋博饼ID
	 * @return 中秋博饼
	 */
	public WheelDice selectWheelDiceById(Long id);

	/**
	 * 查询中秋博饼列表
	 *
	 * @param wheelDice 中秋博饼
	 * @return 中秋博饼集合
	 */
	public List<WheelDice> selectWheelDiceList(WheelDice wheelDice);

	/**
	 * 新增中秋博饼
	 *
	 * @param wheelDice 中秋博饼
	 * @return 结果
	 */
	public int insertWheelDice(WheelDice wheelDice);

	/**
	 * 修改中秋博饼
	 *
	 * @param wheelDice 中秋博饼
	 * @return 结果
	 */
	public int updateWheelDice(WheelDice wheelDice);

	/**
	 * 批量删除中秋博饼
	 *
	 * @param ids 需要删除的中秋博饼ID
	 * @return 结果
	 */
	public int deleteWheelDiceByIds(Long[] ids );

	/**
	 * 删除中秋博饼信息
	 *
	 * @param id 中秋博饼ID
	 * @return 结果
	 */
	public int deleteWheelDiceById(Long id);
}
