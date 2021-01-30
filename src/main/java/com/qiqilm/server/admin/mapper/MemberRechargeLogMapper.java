package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.MemberRechargeLog;

import java.util.List;
import java.util.Map;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface MemberRechargeLogMapper {
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
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteMemberRechargeLogById(String id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteMemberRechargeLogByIds(String[] ids );

    public Map listCount(MemberRechargeLog memberRechargeLog);
}
