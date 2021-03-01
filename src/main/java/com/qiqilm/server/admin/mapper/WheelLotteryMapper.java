package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.WheelLottery;

/**
 * 转盘彩票Mapper接口
 *
 * @author 77tv
 * @date 2021-03-01
 */
public interface WheelLotteryMapper {
	/**
	 * 查询转盘彩票
	 *
	 * @param id 转盘彩票ID
	 * @return 转盘彩票
	 */
	public WheelLottery selectWheelLotteryById(String id);

	/**
	 * 查询转盘彩票列表
	 *
	 * @param wheelLottery 转盘彩票
	 * @return 转盘彩票集合
	 */
	public List<WheelLottery> selectWheelLotteryList(WheelLottery wheelLottery);

	/**
	 * 新增转盘彩票
	 *
	 * @param wheelLottery 转盘彩票
	 * @return 结果
	 */
	public int insertWheelLottery(WheelLottery wheelLottery);

	/**
	 * 修改转盘彩票
	 *
	 * @param wheelLottery 转盘彩票
	 * @return 结果
	 */
	public int updateWheelLottery(WheelLottery wheelLottery);

	/**
	 * 删除转盘彩票
	 *
	 * @param id 转盘彩票ID
	 * @return 结果
	 */
	public int deleteWheelLotteryById(String id);

	/**
	 * 批量删除转盘彩票
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteWheelLotteryByIds(String[] ids );
}
