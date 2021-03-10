package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveUserMount;

/**
 * 直播间会员坐骑Service接口
 *
 * @author 77tv
 * @date 2021-03-09
 */
public interface ILiveUserMountService {
	/**
	 * 查询直播间会员坐骑
	 *
	 * @param id 直播间会员坐骑ID
	 * @return 直播间会员坐骑
	 */
	public LiveUserMount selectLiveUserMountById(Long id);

	/**
	 * 查询直播间会员坐骑列表
	 *
	 * @param liveUserMount 直播间会员坐骑
	 * @return 直播间会员坐骑集合
	 */
	public List<LiveUserMount> selectLiveUserMountList(LiveUserMount liveUserMount);

	/**
	 * 新增直播间会员坐骑
	 *
	 * @param liveUserMount 直播间会员坐骑
	 * @return 结果
	 */
	public int insertLiveUserMount(LiveUserMount liveUserMount);

	/**
	 * 修改直播间会员坐骑
	 *
	 * @param liveUserMount 直播间会员坐骑
	 * @return 结果
	 */
	public int updateLiveUserMount(LiveUserMount liveUserMount);

	/**
	 * 批量删除直播间会员坐骑
	 *
	 * @param ids 需要删除的直播间会员坐骑ID
	 * @return 结果
	 */
	public int deleteLiveUserMountByIds(Long[] ids );

	/**
	 * 删除直播间会员坐骑信息
	 *
	 * @param id 直播间会员坐骑ID
	 * @return 结果
	 */
	public int deleteLiveUserMountById(Long id);
}
