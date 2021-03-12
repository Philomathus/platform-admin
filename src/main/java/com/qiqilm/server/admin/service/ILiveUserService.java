package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveUser;
import com.qiqilm.server.admin.domain.req.ReqLotteryBat;
import com.qiqilm.server.admin.domain.rsp.RspLotteryBet;

import java.util.List;

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
	 * 新增用户信息
	 *
	 * @param liveUser 用户信息
	 * @return 结果
	 */
	public int insertLiveUser( LiveUser liveUser );

	/**
	 * 修改用户信息
	 *
	 * @param liveUser 用户信息
	 * @return 结果
	 */
	public int updateLiveUser( LiveUser liveUser );

	public AjaxResult updateFamilyID( Long familyId, Long id );

	List<RspLotteryBet> selectAnchorAward( ReqLotteryBat req );
}
