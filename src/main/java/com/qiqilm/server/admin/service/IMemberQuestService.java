package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.MemberQuest;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-03-20
 */
public interface IMemberQuestService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public MemberQuest selectMemberQuestById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param memberQuest 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<MemberQuest> selectMemberQuestList(MemberQuest memberQuest);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param memberQuest 【请填写功能名称】
	 * @return 结果
	 */
	public int insertMemberQuest(MemberQuest memberQuest);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param memberQuest 【请填写功能名称】
	 * @return 结果
	 */
	public int updateMemberQuest(MemberQuest memberQuest);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteMemberQuestByIds(String[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteMemberQuestById(String id);
}