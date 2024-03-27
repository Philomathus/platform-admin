package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.MemberRechargeLog;
import com.qiqilm.server.admin.domain.req.ReqMemberRechargeLog;
import com.qiqilm.server.admin.domain.rsp.RspBankRecharge;
import com.qiqilm.server.admin.domain.vo.MemberSumRecharge;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 公司入款信息Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface MemberRechargeLogMapper {
	/**
	 * 查询公司入款信息
	 *
	 * @param id 公司入款信息ID
	 * @return 公司入款信息
	 */
	public MemberRechargeLog selectMemberRechargeLogById( String id );

	/**
	 * 查询公司入款信息列表
	 *
	 * @param req 公司入款信息
	 * @return 公司入款信息集合
	 */
	public List<MemberRechargeLog> selectMemberRechargeLogList( @Param( "req" ) ReqMemberRechargeLog req );


	public List<MemberRechargeLog> MemberRechargeLogLists( );
	/**
	 * 新增公司入款信息
	 *
	 * @param memberRechargeLog 公司入款信息
	 * @return 结果
	 */
	public int insertMemberRechargeLog( MemberRechargeLog memberRechargeLog );

	/**
	 * 修改公司入款信息
	 *
	 * @param memberRechargeLog 公司入款信息
	 * @return 结果
	 */
	public int updateMemberRechargeLog( MemberRechargeLog memberRechargeLog );

	public Map listCount( @Param( "req" ) ReqMemberRechargeLog req );
	int checkRechargeLogFail();


	int countRechargeDaySucess(@Param("memberId") String memberId);

	List<RspBankRecharge> selectMemberBankRecharge(@Param( "req" )ReqMemberRechargeLog req);

	public Map listCounts( @Param( "req" ) ReqMemberRechargeLog req );

    List<MemberSumRecharge> bankRechargeSum();

	List<MemberSumRecharge> allRechargeSum();
}
