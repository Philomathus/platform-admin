package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.MemberActionLogs;

import java.util.List;

/**
 * 会员行为日志Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface MemberActionLogsMapper {
	/**
	 * 查询会员行为日志
	 *
	 * @param id 会员行为日志ID
	 * @return 会员行为日志
	 */
	public MemberActionLogs selectMemberActionLogsById( String id );

	/**
	 * 查询会员行为日志列表
	 *
	 * @param memberActionLogs 会员行为日志
	 * @return 会员行为日志集合
	 */
	public List<MemberActionLogs> selectMemberActionLogsList( MemberActionLogs memberActionLogs );

	/**
	 * 新增会员行为日志
	 *
	 * @param memberActionLogs 会员行为日志
	 * @return 结果
	 */
	public int insertMemberActionLogs( MemberActionLogs memberActionLogs );

	/**
	 * 修改会员行为日志
	 *
	 * @param memberActionLogs 会员行为日志
	 * @return 结果
	 */
	public int updateMemberActionLogs( MemberActionLogs memberActionLogs );

	/**
	 * 删除会员行为日志
	 *
	 * @param id 会员行为日志ID
	 * @return 结果
	 */
	public int deleteMemberActionLogsById( String id );

	/**
	 * 批量删除会员行为日志
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteMemberActionLogsByIds( String[] ids );
}