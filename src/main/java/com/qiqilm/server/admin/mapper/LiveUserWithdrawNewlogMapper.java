package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveUserWithdrawNewlog;

/**
 * 主播提现管理Mapper接口
 *
 * @author 77tv
 * @date 2021-03-23
 */
public interface LiveUserWithdrawNewlogMapper {
	/**
	 * 查询主播提现管理
	 *
	 * @param id 主播提现管理ID
	 * @return 主播提现管理
	 */
	public LiveUserWithdrawNewlog selectLiveUserWithdrawNewlogById(String id);

	/**
	 * 查询主播提现管理列表
	 *
	 * @param liveUserWithdrawNewlog 主播提现管理
	 * @return 主播提现管理集合
	 */
	public List<LiveUserWithdrawNewlog> selectLiveUserWithdrawNewlogList(LiveUserWithdrawNewlog liveUserWithdrawNewlog);

	/**
	 * 新增主播提现管理
	 *
	 * @param liveUserWithdrawNewlog 主播提现管理
	 * @return 结果
	 */
	public int insertLiveUserWithdrawNewlog(LiveUserWithdrawNewlog liveUserWithdrawNewlog);

	/**
	 * 修改主播提现管理
	 *
	 * @param liveUserWithdrawNewlog 主播提现管理
	 * @return 结果
	 */
	public int updateLiveUserWithdrawNewlog(LiveUserWithdrawNewlog liveUserWithdrawNewlog);

	/**
	 * 删除主播提现管理
	 *
	 * @param id 主播提现管理ID
	 * @return 结果
	 */
	public int deleteLiveUserWithdrawNewlogById(String id);

	/**
	 * 批量删除主播提现管理
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLiveUserWithdrawNewlogByIds(String[] ids );
}
