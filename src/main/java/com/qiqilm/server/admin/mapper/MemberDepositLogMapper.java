package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.MemberDepositLog;

/**
 * 人工加分日志Mapper接口
 *
 * @author 77tv
 * @date 2021-07-29
 */
public interface MemberDepositLogMapper {
	/**
	 * 查询人工加分日志
	 *
	 * @param id 人工加分日志ID
	 * @return 人工加分日志
	 */
	public MemberDepositLog selectMemberDepositLogById(Long id);

	/**
	 * 查询人工加分日志列表
	 *
	 * @param memberDepositLog 人工加分日志
	 * @return 人工加分日志集合
	 */
	public List<MemberDepositLog> selectMemberDepositLogList(MemberDepositLog memberDepositLog);

	/**
	 * 新增人工加分日志
	 *
	 * @param memberDepositLog 人工加分日志
	 * @return 结果
	 */
	public int insertMemberDepositLog(MemberDepositLog memberDepositLog);

	/**
	 * 修改人工加分日志
	 *
	 * @param memberDepositLog 人工加分日志
	 * @return 结果
	 */
	public int updateMemberDepositLog(MemberDepositLog memberDepositLog);

	/**
	 * 删除人工加分日志
	 *
	 * @param id 人工加分日志ID
	 * @return 结果
	 */
	public int deleteMemberDepositLogById(Long id);

	/**
	 * 批量删除人工加分日志
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteMemberDepositLogByIds(Long[] ids );
}
