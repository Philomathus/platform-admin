package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveVideo;

import java.util.List;

/**
 * 直播Service接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface ILiveVideoService {
	/**
	 * 查询直播
	 *
	 * @param id 直播ID
	 * @return 直播
	 */
	public LiveVideo selectLiveVideoById( Long id );

	/**
	 * 查询直播列表
	 *
	 * @param liveVideo 直播
	 * @return 直播集合
	 */
	public List<LiveVideo> selectLiveVideoList( LiveVideo liveVideo );

	public boolean close( Long id, String cause );

	public String livePay( Long room_id, Integer live_fee, Integer live_pay_type );

	AjaxResult updateVideoSort( LiveVideo liveVideo );

	public void processVideoSort();

	List<String> selectOnlineLiveGroups();

	void updateNowLine();
}
