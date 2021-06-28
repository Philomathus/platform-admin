package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.MemberWithdrawLogShunWei;
import com.qiqilm.server.admin.domain.req.ReqMemberWithdrawLog;
import com.qiqilm.server.admin.domain.rsp.RspMemberInfo;

import java.util.List;

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
	public MemberWithdrawLog selectMemberWithdrawLogById( String id );

	/**
	 * 查询会员提现信息列表
	 *
	 * @param memberWithdrawLog 会员提现信息
	 * @return 会员提现信息集合
	 */
	public List<MemberWithdrawLog> selectMemberWithdrawLogList( MemberWithdrawLog memberWithdrawLog );

	public List<MemberWithdrawLogShunWei> selectMemberWithdrawLogShunWeiList(ReqMemberWithdrawLog req );

	AjaxResult refused( ReqMemberWithdrawLog req );

	AjaxResult refuseds( ReqMemberWithdrawLog req );

	AjaxResult back( ReqMemberWithdrawLog req );

	AjaxResult queryStatus( ReqMemberWithdrawLog req );

	AjaxResult lock( ReqMemberWithdrawLog req );

	AjaxResult locks( ReqMemberWithdrawLog req );

	AjaxResult unlock( ReqMemberWithdrawLog req );

	AjaxResult artificial( ReqMemberWithdrawLog req );

	AjaxResult abnormalWithdrawal( ReqMemberWithdrawLog req );

	AjaxResult manualWithdrawal( ReqMemberWithdrawLog req );

	AjaxResult withdrawReport(String id );

	AjaxResult getTotal( MemberWithdrawLog memberWithdrawLog );

	List<MemberWithdrawLog> getWithdrawLogList();
}
