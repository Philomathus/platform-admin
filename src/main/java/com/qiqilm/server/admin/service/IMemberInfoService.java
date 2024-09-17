package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.core.vo.RspBase;
import com.qiqilm.server.admin.domain.LiveGuardUser;
import com.qiqilm.server.admin.domain.MemberCard;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.req.ReqSmallFeatures;
import com.qiqilm.server.admin.domain.rsp.RspMemberChannel;
import com.qiqilm.server.admin.domain.vo.PageBO;
import com.qiqilm.server.admin.domain.vo.ReqAddScore;
import com.qiqilm.server.admin.domain.vo.WithdrawReport;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 会员信息Service接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface IMemberInfoService {
	/**
	 * 查询会员信息
	 *
	 * @param id 会员信息ID
	 * @return 会员信息
	 */
	public MemberInfo selectMemberInfoById(String id);

	/**
	 * 查询会员信息列表
	 *
	 * @param memberInfo 会员信息
	 * @return 会员信息集合
	 */
	public List<MemberInfo> selectMemberInfoList(MemberInfo memberInfo);

	/**
	 * 新增会员信息
	 *
	 * @param memberInfo 会员信息
	 * @return 结果
	 */
	public AjaxResult insertMemberInfo(MemberInfo memberInfo);

	/**
	 * 修改会员信息
	 *
	 * @param memberInfo 会员信息
	 * @return 结果
	 */
	public int updateMemberInfo(MemberInfo memberInfo);


    RspBase addMemberMoneyOnly(String ip, LoginUser loginUser, ReqAddScore req);

    PageBO<WithdrawReport> withdrawReport(String memberId, Integer page, Integer limit);

    PageBO<MemberCard> findMemberCardPage(String memberid, Integer page, Integer pageSize, String orderBy);

	void outGameFail( String orderId, String userId, Integer platformId );

	void outGMGameSucess( String orderId, String userId, Integer platformId, BigDecimal money, String account );

	int changeSpeak(MemberInfo memberInfo);

	AjaxResult updatePhones(ReqSmallFeatures req);
	AjaxResult queryPhones(ReqSmallFeatures req);

	AjaxResult unbindCard(MemberCard memberCard);

    AjaxResult changeBank(MemberCard memberCard);

    void repairMemberBcode(String memberId);

    void updateVip(String memberId, Integer vip, String nickName);
	void updataStatus(MemberInfo memberInfo);

    AjaxResult updateInviterCode(String inviterCode,  String memberId);

    AjaxResult changeEmail(MemberInfo memberInfo);

    String getMemberLoginAddress(String id);

    String getHistoryRecharge(String id);

	List<RspMemberChannel> memberstatistics(MemberInfo memberInfo);

	Map listCount(MemberInfo memberInfo);

	AjaxResult findMemberFollowList(String id);

	public int banStatus(MemberInfo memberInfo);

	AjaxResult personalReport(String startTime,String endTime , String memberId);

    RspBase<?> boxDish( String memberId );

	int unBlockStatus( MemberInfo memberInfo );

    List<LiveGuardUser> selectLiveGuard(LiveGuardUser liveGuardUser);

    int withdrawStatus( MemberInfo memberInfo );

	int updateCodeTotalVipLevel( MemberInfo memberInfo );
}
