package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveNote;
import org.apache.ibatis.annotations.Param;

/**
 * 彩票注单Mapper接口
 *
 * @author 77tv
 * @date 2021-03-19
 */
public interface LiveNoteMapper {
	/**
	 * 查询彩票注单
	 *
	 * @param id 彩票注单ID
	 * @return 彩票注单
	 */
	public LiveNote selectLiveNoteById(String id);


	List<LiveNote> findLotteryNoteList( @Param("start") String start, @Param("end") String end);

	/**
	 * 查询彩票注单列表
	 *
	 * @param liveNote 彩票注单
	 * @return 彩票注单集合
	 */
	public List<LiveNote> selectLiveNoteList(LiveNote liveNote);


	/**
	 * 删除彩票注单
	 *
	 * @param id 彩票注单ID
	 * @return 结果
	 */
	public int deleteLiveNoteById(String id);

	/**
	 * 批量删除彩票注单
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLiveNoteByIds(String[] ids );
}
