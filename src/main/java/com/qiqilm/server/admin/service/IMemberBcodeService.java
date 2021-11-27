package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberBcode;

import java.util.List;

/**
 * 会员打码数据Service接口
 *
 * @author 77tv
 * @date 2021-01-29
 */
public interface IMemberBcodeService {
	/**
	 * 查询会员打码数据
	 *
	 * @param id 会员打码数据ID
	 * @return 会员打码数据
	 */
	public MemberBcode selectMemberBcodeById( String id );

	/**
	 * 查询会员打码数据列表
	 *
	 * @param memberBcode 会员打码数据
	 * @return 会员打码数据集合
	 */
	public List<MemberBcode> selectMemberBcodeList( MemberBcode memberBcode );

	/**
	 * 统计
	 *
	 * @return {@link AjaxResult}
	 */
	AjaxResult getTotalData(MemberBcode memberBcode);

	int updateMemberBcode(MemberBcode memberBcode);

}
