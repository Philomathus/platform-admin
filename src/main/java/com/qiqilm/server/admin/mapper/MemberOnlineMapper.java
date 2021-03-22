package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.MemberOnline;

/**
 * 在线会员列表Mapper接口
 *
 * @author 77tv
 * @date 2021-03-22
 */
public interface MemberOnlineMapper {

	/**
	 * 查询在线会员列表列表
	 *
	 * @param memberOnline 在线会员列表
	 * @return 在线会员列表集合
	 */
	public List<MemberOnline> selectMemberOnlineList(MemberOnline memberOnline);

}
