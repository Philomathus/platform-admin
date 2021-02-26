package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.LiveHostWageNote;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface LiveHostWageNoteMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public LiveHostWageNote selectLiveHostWageNoteById( Long id );

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param liveHostWageNote 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<LiveHostWageNote> selectLiveHostWageNoteList( LiveHostWageNote liveHostWageNote );

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param liveHostWageNote 【请填写功能名称】
	 * @return 结果
	 */
	public int insertLiveHostWageNote( LiveHostWageNote liveHostWageNote );

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param liveHostWageNote 【请填写功能名称】
	 * @return 结果
	 */
	public int updateLiveHostWageNote( LiveHostWageNote liveHostWageNote );

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteLiveHostWageNoteById( Long id );

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLiveHostWageNoteByIds( Long[] ids );

	public LiveHostWageNote beforeNote( @Param( "userId" ) Long userId );

	public List<LiveHostWageNote> familyPage( @Param( "countTime" ) String countTime, @Param( "dto" ) LiveHostWageNote dto );

	public List<Map<String, Object>> selectFamilyName();

	public List<LiveHostWageNote> selectListMt( @Param( "dto" ) LiveHostWageNote dto );
}
