package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveBlack;

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
	public List<LiveBlack> selectLiveBlackList(LiveBlack liveBlack);


	List<LiveBlack> selectLiveBlackList7706(LiveBlack liveBlack);

	int deleteLiveBlackById(Long id);

	int deleteLiveBlackById7706(Long id);
}