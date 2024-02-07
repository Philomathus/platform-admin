package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.MemberPayJour;
import com.qiqilm.server.admin.domain.rsp.MemberPayJourRsp;
import com.qiqilm.server.admin.domain.rsp.RspPayJour;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 线上充值信息Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface MemberPayJourMapper {
	/**
	 * 查询线上充值信息
	 *
	 * @param id 线上充值信息ID
	 * @return 线上充值信息
	 */
	public MemberPayJour selectMemberPayJourById( String id );

	/**
	 * 查询线上充值信息列表
	 *
	 * @param req 线上充值信息
	 * @return 线上充值信息集合
	 */
	public List<MemberPayJour> selectMemberPayJourList( MemberPayJour req );

	/**
	 * 新增线上充值信息
	 *
	 * @param memberPayJour 线上充值信息
	 * @return 结果
	 */
	public int insertMemberPayJour( MemberPayJour memberPayJour );

	/**
	 * 修改线上充值信息
	 *
	 * @param memberPayJour 线上充值信息
	 * @return 结果
	 */
	public int updateMemberPayJour( MemberPayJour memberPayJour );

	public Map listCount( MemberPayJour req );

	MemberPayJour findByOrderNo( String orderNo );

	List<RspPayJour> selectMemberPayJourLists(MemberPayJour req );

	Map listCounts( MemberPayJour req );

	List<MemberPayJourRsp> selectMemberPayJourRspList(@Param("count") Long count);

	int successTodayCount( @Param( "memberId" ) String memberId, @Param( "platformId" ) String platformId );
}
