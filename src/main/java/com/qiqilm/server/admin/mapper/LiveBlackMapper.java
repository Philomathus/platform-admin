package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.LiveBlack;

import java.util.List;
import java.util.Set;

/**
 * 拉黑Mapper接口
 *
 * @author 77tv
 * @date 2021-08-24
 */
public interface LiveBlackMapper {


	/**
	 * 查询拉黑列表
	 *
	 * @param liveBlack 拉黑
	 * @return 拉黑集合
	 */
	List<LiveBlack> selectLiveBlackList(LiveBlack liveBlack);
	List<LiveBlack> selectLiveBlackList7706(LiveBlack liveBlack);
	List<LiveBlack> selectLiveBlackList7705(LiveBlack liveBlack);
	List<LiveBlack> selectLiveBlackList7710( LiveBlack liveBlack );
	List<LiveBlack> selectLiveBlackList7711( LiveBlack liveBlack );
	List<LiveBlack> selectLiveBlackList7712( LiveBlack liveBlack );

	int deleteLiveBlackById(Long id);
	int deleteLiveBlackById7706(Long id);
	int deleteLiveBlackById7705(Long id);
	int deleteLiveBlackById7710( Long id );

	Set userBlackList(Long host_id);
}