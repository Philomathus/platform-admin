package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveUser;
import com.qiqilm.server.admin.domain.req.ReqLotteryBat;
import com.qiqilm.server.admin.domain.rsp.RspLotteryBet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户信息Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface ILiveUserService {
	/**
	 * 查询用户信息
	 *
	 * @param id 用户信息ID
	 * @return 用户信息
	 */
	public LiveUser selectLiveUserById( Long id );

	/**
	 * 查询用户信息列表
	 *
	 * @param liveUser 用户信息
	 * @return 用户信息集合
	 */
	public List<LiveUser> selectLiveUserList( LiveUser liveUser );

	/**
	 * 修改用户信息
	 *
	 * @param liveUser 用户信息
	 * @return 结果
	 */
	public int updateLiveUser( LiveUser liveUser );

	public AjaxResult updateFamilyID( Long familyId, Long id );

	public AjaxResult updateTicket(BigDecimal ticket, Long id);

	List<RspLotteryBet> selectAnchorAward( ReqLotteryBat req );

    public AjaxResult insertLiveUser(LiveUser liveUser);

    AjaxResult openLive(Map map) throws Exception;

    AjaxResult closeLive(Map map);

    AjaxResult updateMobile(String newMobile, String oldMobile, String id);

	List<LiveUser> selectLiveUserBankById(Integer userId);
	AjaxResult updateLiveUserBank(LiveUser liveUser);
	int delLiveUserBankById(String id);
	int liveInStatus(Long id);
}
