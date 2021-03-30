package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberRechargeLog;
import com.qiqilm.server.admin.domain.req.ReqMemberRechargeLog;

import java.util.List;
import java.util.Map;

/**
 * 公司入款信息Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IMemberRechargeLogService {
	/**
	 * 查询公司入款信息
	 *
	 * @param id 公司入款信息ID
	 * @return 公司入款信息
	 */
	public MemberRechargeLog selectMemberRechargeLogById( String id );

	/**
	 * 查询公司入款信息列表
	 *
	 * @param req 公司入款信息
	 * @return 公司入款信息集合
	 */
	public List<MemberRechargeLog> selectMemberRechargeLogList( ReqMemberRechargeLog req );

	public Map listCount( ReqMemberRechargeLog req );

	AjaxResult firstAudit( ReqMemberRechargeLog req );

	AjaxResult finalAudit( ReqMemberRechargeLog req );

	AjaxResult refusedAudit( ReqMemberRechargeLog req );

	AjaxResult recoverAudit( ReqMemberRechargeLog req );
	int checkRechargeLogFail();
}
