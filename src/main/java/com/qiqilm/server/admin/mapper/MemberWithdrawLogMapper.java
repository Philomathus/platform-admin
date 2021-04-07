package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.MemberWithdrawLogShunWei;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 会员提现信息Mapper接口
 *
 * @author 77tv
 * @date 2021-01-31
 */
public interface MemberWithdrawLogMapper {
	/**
	 * 查询会员提现信息
	 *
	 * @param id 会员提现信息ID
	 * @return 会员提现信息
	 */
	public MemberWithdrawLog selectMemberWithdrawLogById( String id );

	/**
	 * 查询会员提现信息列表
	 *
	 * @param memberWithdrawLog 会员提现信息
	 * @return 会员提现信息集合
	 */
	public List<MemberWithdrawLog> selectMemberWithdrawLogList( MemberWithdrawLog memberWithdrawLog );

	/**
	 * 新增会员提现信息
	 *
	 * @param memberWithdrawLog 会员提现信息
	 * @return 结果
	 */
	public int insertMemberWithdrawLog( MemberWithdrawLog memberWithdrawLog );

	/**
	 * 修改会员提现信息
	 *
	 * @param memberWithdrawLog 会员提现信息
	 * @return 结果
	 */
	public int updateMemberWithdrawLog( MemberWithdrawLog memberWithdrawLog );

	MemberWithdrawLog selectByOrderNo( String orderNo );

	Map getTotal( MemberWithdrawLog memberWithdrawLog );

	List<MemberWithdrawLog> getWithdrawLogList( @Param( "date" ) String date, @Param( "beginTime" ) String beginTime );

	List<MemberWithdrawLog> selectByIds( @Param( "array" ) List<String> ids );

	List<MemberWithdrawLog> selectLocksByIds( @Param( "array" ) List<String> ids );

	List<MemberWithdrawLogShunWei> selectMemberWithdrawLogShunWeiList( @Param( "array" ) List<String> ids );

	List<MemberWithdrawLog> selectPayAgentOrder( @Param( "withdrawOrderNos" ) List<String> withdrawOrderNos,
												 @Param( "userName" ) String userName );
}
