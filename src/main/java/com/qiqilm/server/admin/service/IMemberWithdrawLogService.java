package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.req.ReqMemberWithdrawLog;

/**
 * 会员提现信息Service接口
 *
 * @author 77tv
 * @date 2021-01-30
 */
public interface IMemberWithdrawLogService {
	/**
	 * 查询会员提现信息
	 *
	 * @param id 会员提现信息ID
	 * @return 会员提现信息
	 */
	public MemberWithdrawLog selectMemberWithdrawLogById(String id);

	/**
	 * 查询会员提现信息列表
	 *
	 * @param memberWithdrawLog 会员提现信息
	 * @return 会员提现信息集合
	 */
	public List<MemberWithdrawLog> selectMemberWithdrawLogList(MemberWithdrawLog memberWithdrawLog);

	AjaxResult refused( ReqMemberWithdrawLog req );

	AjaxResult lock( ReqMemberWithdrawLog req );

	AjaxResult unlock( ReqMemberWithdrawLog req );

	AjaxResult artificial( ReqMemberWithdrawLog req );
}
