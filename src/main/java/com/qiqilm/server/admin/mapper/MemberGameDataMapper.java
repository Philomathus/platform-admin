package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.MemberGameData;
import com.qiqilm.server.admin.domain.req.ReqMemberGameData;
import com.qiqilm.server.admin.domain.rsp.RspLotteryBetLog;
import com.qiqilm.server.admin.domain.rsp.RspMemberGameData;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 会员注单数据Mapper接口
 *
 * @author 77tv
 * @date 2021-01-29
 */
public interface MemberGameDataMapper {
	/**
	 * 查询会员注单数据列表
	 *
	 * @param reqMemberGameData 会员注单数据
	 * @return 会员注单数据集合
	 */
	public List<RspMemberGameData> selectMemberGameDataSingleList(ReqMemberGameData reqMemberGameData);

	public List<RspMemberGameData> selectMemberGameDataList(ReqMemberGameData reqMemberGameData);

    public MemberGameData getCountMemberGameDataSingleList(ReqMemberGameData reqMemberGameData);

    public MemberGameData getCountMemberGameDataList(ReqMemberGameData reqMemberGameData);

	public int insertMemberGameData(@Param("req")  MemberGameData memberGameData , @Param( "dbNodes" ) String dbNodes);

    RspLotteryBetLog findBetList(String gameId);

	RspLotteryBetLog findBetLists(String gameId);

	Integer findExist(@Param( "dbNodes" ) String dbNodes,@Param("keyId") String id);

}
