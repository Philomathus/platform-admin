package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.LiveUser;
import com.qiqilm.server.admin.domain.req.ReqLotteryBat;
import com.qiqilm.server.admin.domain.rsp.RspLotteryBet;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * 主播用户信息Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface LiveUserMapper {
	/**
	 * 查询主播用户信息
	 *
	 * @param id 主播用户信息ID
	 * @return 主播用户信息
	 */
	public LiveUser selectLiveUserById(Long id);

	/**
	 * 查询主播用户信息列表
	 *
	 * @param liveUser 主播用户信息
	 * @return 主播用户信息集合
	 */
	public List<LiveUser> selectLiveUserList(LiveUser liveUser);

	/**
	 * 新增主播用户信息
	 *
	 * @param liveUser 主播用户信息
	 * @return 结果
	 */
	public int insertLiveUser(LiveUser liveUser);

	/**
	 * 修改主播用户信息
	 *
	 * @param liveUser 主播用户信息
	 * @return 结果
	 */
	public int updateLiveUser(LiveUser liveUser);

    @Select( "SELECT family_id FROM ${dbLive}.live_user where id = ${userId}" )
    int getFamilyId(@Param( "userId" ) Long userId);

    @Update( "update ${dbLive}.live_user set family_id = ${familyID} where id= ${userId}" )
    int updateFamilyID( @Param( "familyID" ) Long familyID, @Param( "userId" ) Long userId );

    int updateTicket(@Param( "ticket" ) BigDecimal ticket, @Param( "userId" ) Long userId );

    @Select( "SELECT count(id) as num FROM ${dbLive}.live_user where family_id = ${family_id}" )
    int getNumFamily(@Param( "family_id" ) Integer family_id);

    List<RspLotteryBet> selectAnchorAward( ReqLotteryBat req);

    int updateLiveUserByFamilyId(Integer id);
    int updateLiveUserIsBanStopByFamilyId(@Param( "familyId" )Integer id,@Param( "remark" )String remark);
    int updateLiveUserIsBanKeepByFamilyId(@Param( "familyId" )Integer id,@Param( "remark" )String remark);

    List<LiveUser> selectLiveUsersByMobile(@Param("mobile") String mobile);

    Integer checkMobile(@Param("newMobile") String newMobile);

	List<LiveUser> selectLiveUserBankById(Integer userId);
	int updateLiveUserBank(LiveUser liveUser);
	int delLiveUserBankById(String bankAccount);

	void insertLiveUser7701(LiveUser liveUser);

	void insertLiveUser7704(LiveUser liveUser);

	void insertLiveUser7708(LiveUser liveUser);
}
