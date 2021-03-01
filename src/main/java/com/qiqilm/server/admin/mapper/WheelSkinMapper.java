package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.WheelSkin;

/**
 * 转盘皮肤列Mapper接口
 *
 * @author 77tv
 * @date 2021-02-26
 */
public interface WheelSkinMapper {
	/**
	 * 查询转盘皮肤列
	 *
	 * @param id 转盘皮肤列ID
	 * @return 转盘皮肤列
	 */
	public WheelSkin selectWheelSkinById(Long id);

	/**
	 * 查询转盘皮肤列列表
	 *
	 * @param wheelSkin 转盘皮肤列
	 * @return 转盘皮肤列集合
	 */
	public List<WheelSkin> selectWheelSkinList(WheelSkin wheelSkin);

	/**
	 * 新增转盘皮肤列
	 *
	 * @param wheelSkin 转盘皮肤列
	 * @return 结果
	 */
	public int insertWheelSkin(WheelSkin wheelSkin);

	/**
	 * 修改转盘皮肤列
	 *
	 * @param wheelSkin 转盘皮肤列
	 * @return 结果
	 */
	public int updateWheelSkin(WheelSkin wheelSkin);

	/**
	 * 删除转盘皮肤列
	 *
	 * @param id 转盘皮肤列ID
	 * @return 结果
	 */
	public int deleteWheelSkinById(Long id);

	/**
	 * 批量删除转盘皮肤列
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteWheelSkinByIds(Long[] ids );
}
