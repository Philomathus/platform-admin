package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.MemberGameDatafix;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-29
 */
public interface IMemberGameDatafixService {

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param memberGameDatafix 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<MemberGameDatafix> selectMemberGameDatafixList(MemberGameDatafix memberGameDatafix);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param memberGameDatafix 【请填写功能名称】
	 * @return 结果
	 */
	public int insertMemberGameDatafix(MemberGameDatafix memberGameDatafix);

}