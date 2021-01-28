package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.vo.WithdrawReport;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 会员信息Mapper接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface MemberInfoMapper {
	/**
	 * 查询会员信息
	 *
	 * @param id 会员信息ID
	 * @return 会员信息
	 */
	public MemberInfo selectMemberInfoById( String id );

	/**
	 * 查询会员信息列表
	 *
	 * @param memberInfo 会员信息
	 * @return 会员信息集合
	 */
	public List<MemberInfo> selectMemberInfoList( MemberInfo memberInfo );

	/**
	 * 新增会员信息
	 *
	 * @param memberInfo 会员信息
	 * @return 结果
	 */
	public int insertMemberInfo( MemberInfo memberInfo );

	/**
	 * 修改会员信息
	 *
	 * @param memberInfo 会员信息
	 * @return 结果
	 */
	public int updateMemberInfo( MemberInfo memberInfo );

	/**
	 * 删除会员信息
	 *
	 * @param id 会员信息ID
	 * @return 结果
	 */
	public int deleteMemberInfoById( String id );

	/**
	 * 批量删除会员信息
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteMemberInfoByIds( String[] ids );

	int selectMaxMemberCode();

	int updateMoneySelect( @Param( "userId" ) String userId, @Param( "money" ) BigDecimal money,
						   @Param( "invite_money" ) BigDecimal invite_money,
						   @Param( "level_integral" ) BigDecimal level_integral,
						   @Param( "code_account" ) BigDecimal code_account, @Param( "code_total" ) BigDecimal code_total );

	void call_pro_useranalysis( @Param( "userid" ) String userId );

	List<WithdrawReport> userWithdrawReportList();

	List<String> selectMemberSpeak( String[] ids );

	void updateSpeak(@Param( "pUserId" ) String pUserId, @Param( "speak" ) int speak );
}
