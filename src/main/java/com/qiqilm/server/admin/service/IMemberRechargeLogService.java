package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.MemberRechargeLog;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IMemberRechargeLogService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public MemberRechargeLog selectMemberRechargeLogById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param memberRechargeLog 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<MemberRechargeLog> selectMemberRechargeLogList(MemberRechargeLog memberRechargeLog);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param memberRechargeLog 【请填写功能名称】
	 * @return 结果
	 */
	public int insertMemberRechargeLog(MemberRechargeLog memberRechargeLog);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param memberRechargeLog 【请填写功能名称】
	 * @return 结果
	 */
	public int updateMemberRechargeLog(MemberRechargeLog memberRechargeLog);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteMemberRechargeLogByIds(String[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteMemberRechargeLogById(String id);
}
