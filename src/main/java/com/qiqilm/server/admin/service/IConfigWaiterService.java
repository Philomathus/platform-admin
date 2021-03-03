package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.ConfigWaiter;

/**
 * 客服管理Service接口
 *
 * @author 77tv
 * @date 2021-03-03
 */
public interface IConfigWaiterService {
	/**
	 * 查询客服管理
	 *
	 * @param id 客服管理ID
	 * @return 客服管理
	 */
	public ConfigWaiter selectConfigWaiterById(String id);

	/**
	 * 查询客服管理列表
	 *
	 * @param configWaiter 客服管理
	 * @return 客服管理集合
	 */
	public List<ConfigWaiter> selectConfigWaiterList(ConfigWaiter configWaiter);

	/**
	 * 新增客服管理
	 *
	 * @param configWaiter 客服管理
	 * @return 结果
	 */
	public int insertConfigWaiter(ConfigWaiter configWaiter);

	/**
	 * 修改客服管理
	 *
	 * @param configWaiter 客服管理
	 * @return 结果
	 */
	public int updateConfigWaiter(ConfigWaiter configWaiter);

	/**
	 * 批量删除客服管理
	 *
	 * @param ids 需要删除的客服管理ID
	 * @return 结果
	 */
	public int deleteConfigWaiterByIds(String[] ids );

	/**
	 * 删除客服管理信息
	 *
	 * @param id 客服管理ID
	 * @return 结果
	 */
	public int deleteConfigWaiterById(String id);
}
