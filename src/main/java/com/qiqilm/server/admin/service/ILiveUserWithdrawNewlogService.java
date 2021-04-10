package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveUserWithdrawNewlog;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.req.ReqMemberWithdrawLog;

/**
 * 主播提现管理Service接口
 *
 * @author 77tv
 * @date 2021-03-23
 */
public interface ILiveUserWithdrawNewlogService {
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
	 * 批量删除主播提现管理
	 *
	 * @param ids 需要删除的主播提现管理ID
	 * @return 结果
	 */
	public int deleteLiveUserWithdrawNewlogByIds(String[] ids );

	/**
	 * 删除主播提现管理信息
	 *
	 * @param id 主播提现管理ID
	 * @return 结果
	 */
	public int deleteLiveUserWithdrawNewlogById(String id);

	//解锁
	AjaxResult unlock(LiveUserWithdrawNewlog req );
	//拒绝
	AjaxResult refused( LiveUserWithdrawNewlog req );
	//出款
	AjaxResult artificial( LiveUserWithdrawNewlog req );
	//恢复状态
	AjaxResult recoverAudit( LiveUserWithdrawNewlog req );
	//审核
	AjaxResult finalAudit( LiveUserWithdrawNewlog req );

	AjaxResult getTotal( LiveUserWithdrawNewlog req );

	AjaxResult withdrawSucc( LiveUserWithdrawNewlog req );

	AjaxResult withdrawRefused( LiveUserWithdrawNewlog req );

	AjaxResult updateOrder( LiveUserWithdrawNewlog req );

}
