package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.WheelPrize;

/**
 * 转盘奖励Mapper接口
 *
 * @author 77tv
 * @date 2021-02-26
 */
public interface WheelPrizeMapper {
	/**
	 * 查询转盘奖励
	 *
	 * @param id 转盘奖励ID
	 * @return 转盘奖励
	 */
	public WheelPrize selectWheelPrizeById(Long id);

	/**
	 * 查询转盘奖励列表
	 *
	 * @param wheelPrize 转盘奖励
	 * @return 转盘奖励集合
	 */
	public List<WheelPrize> selectWheelPrizeList(WheelPrize wheelPrize);

	/**
	 * 新增转盘奖励
	 *
	 * @param wheelPrize 转盘奖励
	 * @return 结果
	 */
	public int insertWheelPrize(WheelPrize wheelPrize);

	/**
	 * 修改转盘奖励
	 *
	 * @param wheelPrize 转盘奖励
	 * @return 结果
	 */
	public int updateWheelPrize(WheelPrize wheelPrize);

	/**
	 * 删除转盘奖励
	 *
	 * @param id 转盘奖励ID
	 * @return 结果
	 */
	public int deleteWheelPrizeById(Long id);

	/**
	 * 批量删除转盘奖励
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteWheelPrizeByIds(Long[] ids );
}
