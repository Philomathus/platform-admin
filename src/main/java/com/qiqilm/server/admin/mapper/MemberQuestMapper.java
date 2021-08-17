package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.MemberQuest;

/**
 * 会员任务Mapper接口
 *
 * @author 77tv
 * @date 2021-03-20
 */
public interface MemberQuestMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	MemberQuest selectMemberQuestById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param memberQuest 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	List<MemberQuest> selectMemberQuestList(MemberQuest memberQuest);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param memberQuest 【请填写功能名称】
	 * @return 结果
	 */
	int insertMemberQuest(MemberQuest memberQuest);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param memberQuest 【请填写功能名称】
	 * @return 结果
	 */
	int updateMemberQuest(MemberQuest memberQuest);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	int deleteMemberQuestById(String id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	int deleteMemberQuestByIds(String[] ids );

	/**
	 * 重置每日任务
	 */
	void resetDayTaskStatus();
}