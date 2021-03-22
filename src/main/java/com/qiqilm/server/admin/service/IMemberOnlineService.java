package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberOnline;

/**
 * 在线会员列表Service接口
 *
 * @author 77tv
 * @date 2021-03-22
 */
public interface IMemberOnlineService {
	/**
	 * 查询在线会员列表列表
	 *
	 * @param memberOnline 在线会员列表
	 * @return 在线会员列表集合
	 */
	public List<MemberOnline> selectMemberOnlineList(MemberOnline memberOnline);

	MemberOnline selectMemberOnlineListCountTotal(MemberOnline memberOnline);

}
