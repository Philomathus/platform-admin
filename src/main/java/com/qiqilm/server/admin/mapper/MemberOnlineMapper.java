package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.MemberOnline;
import com.qiqilm.server.admin.domain.req.ReqMemberOnline;
import com.qiqilm.server.admin.domain.rsp.RspMemberOnline;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
	List<MemberOnline> selectMemberOnlineList(MemberOnline memberOnline);
	List<MemberOnline> selectMemberOnlineListTwo(MemberOnline memberOnline);

	MemberOnline selectMemberOnlineListCountTotal(MemberOnline memberOnline);
	MemberOnline selectMemberOnlineListCountTotalTwo(MemberOnline memberOnline);

	RspMemberOnline sumCount(@Param("req") ReqMemberOnline reqMemberOnline);
	RspMemberOnline sumCountTwo(@Param("req") ReqMemberOnline reqMemberOnline);

	void cutTableOnline(@Param("tableNode") String tableNode);

	void dropTableOnline(@Param("tableNode") String tableNode);

}
