package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.WheelUserDice;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-09-02
 */
public interface IWheelUserDiceService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public WheelUserDice selectWheelUserDiceById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param wheelUserDice 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<WheelUserDice> selectWheelUserDiceList(WheelUserDice wheelUserDice);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param wheelUserDice 【请填写功能名称】
	 * @return 结果
	 */
	public int insertWheelUserDice(WheelUserDice wheelUserDice);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param wheelUserDice 【请填写功能名称】
	 * @return 结果
	 */
	public int updateWheelUserDice(WheelUserDice wheelUserDice);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteWheelUserDiceByIds(String[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteWheelUserDiceById(String id);
}
