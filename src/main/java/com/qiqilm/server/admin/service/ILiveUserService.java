package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveUser;
import com.qiqilm.server.admin.domain.rsp.RspLotteryBet;
import java.util.List;

/**
 * //用户信息Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface ILiveUserService {
	/**
	 * 查询//用户信息
	 *
	 * @param id //用户信息ID
	 * @return //用户信息
	 */
	public LiveUser selectLiveUserById(Long id);

	/**
	 * 查询//用户信息列表
	 *
	 * @param liveUser //用户信息
	 * @return //用户信息集合
	 */
	public List<LiveUser> selectLiveUserList(LiveUser liveUser);

	/**
	 * 新增//用户信息
	 *
	 * @param liveUser //用户信息
	 * @return 结果
	 */
	public int insertLiveUser(LiveUser liveUser);

	/**
	 * 修改//用户信息
	 *
	 * @param liveUser //用户信息
	 * @return 结果
	 */
	public int updateLiveUser(LiveUser liveUser);

	/**
	 * 批量删除//用户信息
	 *
	 * @param ids 需要删除的//用户信息ID
	 * @return 结果
	 */
	public int deleteLiveUserByIds(Long[] ids );

	/**
	 * 删除//用户信息信息
	 *
	 * @param id //用户信息ID
	 * @return 结果
	 */
	public int deleteLiveUserById(Long id);

    public AjaxResult updateFamilyID(Long familyId, Long id);

    List<RspLotteryBet> selectAnchorAward(LiveUser liveUser);
}
