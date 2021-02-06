package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.LiveHostWageNote;

import java.util.List;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface ILiveHostWageNoteService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public LiveHostWageNote selectLiveHostWageNoteById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param liveHostWageNote 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<LiveHostWageNote> selectLiveHostWageNoteList(LiveHostWageNote liveHostWageNote);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param liveHostWageNote 【请填写功能名称】
	 * @return 结果
	 */
	public int insertLiveHostWageNote(LiveHostWageNote liveHostWageNote);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param liveHostWageNote 【请填写功能名称】
	 * @return 结果
	 */
	public int updateLiveHostWageNote(LiveHostWageNote liveHostWageNote);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteLiveHostWageNoteByIds(Long[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteLiveHostWageNoteById(Long id);

    public List<LiveHostWageNote> familyPage(LiveHostWageNote dto);

    public List<LiveHostWageNote> getPage(LiveHostWageNote dto);
}
