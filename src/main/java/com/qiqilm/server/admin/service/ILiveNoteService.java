package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveNote;

/**
 * 彩票注单Service接口
 *
 * @author 77tv
 * @date 2021-03-19
 */
public interface ILiveNoteService {
	/**
	 * 查询彩票注单
	 *
	 * @param id 彩票注单ID
	 * @return 彩票注单
	 */
	public LiveNote selectLiveNoteById(String id);

	/**
	 * 查询彩票注单列表
	 *
	 * @param liveNote 彩票注单
	 * @return 彩票注单集合
	 */
	public List<LiveNote> selectLiveNoteList(LiveNote liveNote);

	/**
	 * 新增彩票注单
	 *
	 * @param liveNote 彩票注单
	 * @return 结果
	 */
	public int insertLiveNote(LiveNote liveNote);

	/**
	 * 修改彩票注单
	 *
	 * @param liveNote 彩票注单
	 * @return 结果
	 */
	public int updateLiveNote(LiveNote liveNote);

	/**
	 * 批量删除彩票注单
	 *
	 * @param ids 需要删除的彩票注单ID
	 * @return 结果
	 */
	public int deleteLiveNoteByIds(String[] ids );

	/**
	 * 删除彩票注单信息
	 *
	 * @param id 彩票注单ID
	 * @return 结果
	 */
	public int deleteLiveNoteById(String id);
}
