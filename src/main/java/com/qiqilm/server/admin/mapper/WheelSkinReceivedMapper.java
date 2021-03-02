package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.WheelSkinReceived;
import com.qiqilm.server.admin.domain.dto.WheelSkinReceivedExcel;

import java.util.List;
import java.util.Map;

/**
 * 转盘皮肤领取Mapper接口
 *
 * @author 77tv
 * @date 2021-02-24
 */
public interface WheelSkinReceivedMapper {
	/**
	 * 查询转盘皮肤领取
	 *
	 * @param id 转盘皮肤领取ID
	 * @return 转盘皮肤领取
	 */
	public WheelSkinReceived selectWheelSkinReceivedById(Long id);

	/**
	 * 查询转盘皮肤领取列表
	 *
	 * @param wheelSkinReceived 转盘皮肤领取
	 * @return 转盘皮肤领取集合
	 */
	public List<WheelSkinReceived> selectWheelSkinReceivedList(WheelSkinReceived wheelSkinReceived);

	public List<WheelSkinReceivedExcel> selectWheelSkinReceivedList2(WheelSkinReceived wheelSkinReceived);

	/**
	 * 新增转盘皮肤领取
	 *
	 * @param wheelSkinReceived 转盘皮肤领取
	 * @return 结果
	 */
	public int insertWheelSkinReceived(WheelSkinReceived wheelSkinReceived);

	/**
	 * 修改转盘皮肤领取
	 *
	 * @param wheelSkinReceived 转盘皮肤领取
	 * @return 结果
	 */
	public int updateWheelSkinReceived(WheelSkinReceived wheelSkinReceived);

	/**
	 * 删除转盘皮肤领取
	 *
	 * @param id 转盘皮肤领取ID
	 * @return 结果
	 */
	public int deleteWheelSkinReceivedById(Long id);

	/**
	 * 批量删除转盘皮肤领取
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteWheelSkinReceivedByIds(Long[] ids );

    public Map getTotal(WheelSkinReceived wheelSkinReceived);
}
