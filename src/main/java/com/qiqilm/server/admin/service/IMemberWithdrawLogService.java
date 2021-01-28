package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.MemberWithdrawLog;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IMemberWithdrawLogService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public MemberWithdrawLog selectMemberWithdrawLogById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param memberWithdrawLog 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<MemberWithdrawLog> selectMemberWithdrawLogList(MemberWithdrawLog memberWithdrawLog);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param memberWithdrawLog 【请填写功能名称】
	 * @return 结果
	 */
	public int insertMemberWithdrawLog(MemberWithdrawLog memberWithdrawLog);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param memberWithdrawLog 【请填写功能名称】
	 * @return 结果
	 */
	public int updateMemberWithdrawLog(MemberWithdrawLog memberWithdrawLog);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteMemberWithdrawLogByIds(String[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteMemberWithdrawLogById(String id);
}
