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
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public MemberGameDatafix selectMemberGameDatafixById(String id);

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

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param memberGameDatafix 【请填写功能名称】
	 * @return 结果
	 */
	public int updateMemberGameDatafix(MemberGameDatafix memberGameDatafix);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteMemberGameDatafixByIds(String[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteMemberGameDatafixById(String id);
}