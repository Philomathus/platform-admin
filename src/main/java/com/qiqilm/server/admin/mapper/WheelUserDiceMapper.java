package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.WheelUserDice;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-09-02
 */
public interface WheelUserDiceMapper {
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

	public int updateWheelUserDiceTimes(WheelUserDice wheelUserDice);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteWheelUserDiceById(String id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteWheelUserDiceByIds(String[] ids );
}
