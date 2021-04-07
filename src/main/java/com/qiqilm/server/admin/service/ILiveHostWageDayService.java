package com.qiqilm.server.admin.service;

import java.text.ParseException;
import java.util.List;

import com.qiqilm.server.admin.domain.LiveHostWageDay;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageDayFamily;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageDayList;


/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-03-29
 */
public interface ILiveHostWageDayService {
	/**
	 * 查询主播时长
	 *
	 * @param id 主播时长ID
	 * @return 主播时长
	 */
	public LiveHostWageDay selectLiveHostWageDayById(String id);

	/**
	 * 查询主播时长列表
	 *
	 * @param liveHostWageDay 主播时长
	 * @return 主播时长集合
	 */
	public List<LiveHostWageDay> selectLiveHostWageDayList(LiveHostWageDay liveHostWageDay);

	public List<RspLiveHostWageDayFamily> familyPage(LiveHostWageDay dto) throws ParseException;

	public List<RspLiveHostWageDayList> hostPage(LiveHostWageDay dto) throws ParseException;
}