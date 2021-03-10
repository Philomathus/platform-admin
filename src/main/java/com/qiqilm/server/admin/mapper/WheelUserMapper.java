package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.WheelUser;

/**
 * 转盘用户Mapper接口
 *
 * @author 77tv
 * @date 2021-03-08
 */
public interface WheelUserMapper {
	/**
	 * 查询转盘用户
	 *
	 * @param id 转盘用户ID
	 * @return 转盘用户
	 */
	public WheelUser selectWheelUserById(String id);

	/**
	 * 查询转盘用户列表
	 *
	 * @param wheelUser 转盘用户
	 * @return 转盘用户集合
	 */
	public List<WheelUser> selectWheelUserList(WheelUser wheelUser);

	/**
	 * 新增转盘用户
	 *
	 * @param wheelUser 转盘用户
	 * @return 结果
	 */
	public int insertWheelUser(WheelUser wheelUser);

	/**
	 * 修改转盘用户
	 *
	 * @param wheelUser 转盘用户
	 * @return 结果
	 */
	public int updateWheelUser(WheelUser wheelUser);

	/**
	 * 删除转盘用户
	 *
	 * @param id 转盘用户ID
	 * @return 结果
	 */
	public int deleteWheelUserById(String id);

	/**
	 * 批量删除转盘用户
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteWheelUserByIds(String[] ids );
}
