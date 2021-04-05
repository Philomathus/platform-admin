package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.LiveHostWageNote;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageNoteFamily;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageNoteList;

import java.text.ParseException;
import java.util.List;

/**
 * 主播时长Service接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface ILiveHostWageNoteService {
	/**
	 * 查询主播时长
	 *
	 * @param id 主播时长ID
	 * @return 主播时长
	 */
	public LiveHostWageNote selectLiveHostWageNoteById(Long id);

	/**
	 * 查询主播时长列表
	 *
	 * @param liveHostWageNote 主播时长
	 * @return 主播时长集合
	 */
	public List<LiveHostWageNote> selectLiveHostWageNoteList(LiveHostWageNote liveHostWageNote);

    public List<RspLiveHostWageNoteFamily> familyPage( LiveHostWageNote dto);

    public List<RspLiveHostWageNoteList> hostPage( LiveHostWageNote dto) throws ParseException;
}
