package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.LiveHostWageNote;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageNoteFamily;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageNoteList;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 主播时长Mapper接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface LiveHostWageNoteMapper {
	/**
	 * 查询主播时长
	 *
	 * @param id 主播时长ID
	 * @return 主播时长
	 */
	public LiveHostWageNote selectLiveHostWageNoteById( Long id );

	/**
	 * 查询主播时长列表
	 *
	 * @param liveHostWageNote 主播时长
	 * @return 主播时长集合
	 */
	public List<LiveHostWageNote> selectLiveHostWageNoteList( LiveHostWageNote liveHostWageNote );

	/**
	 * 新增主播时长
	 *
	 * @param liveHostWageNote 主播时长
	 * @return 结果
	 */
	public int insertLiveHostWageNote( LiveHostWageNote liveHostWageNote );

	/**
	 * 修改主播时长
	 *
	 * @param liveHostWageNote 主播时长
	 * @return 结果
	 */
	public int updateLiveHostWageNote( LiveHostWageNote liveHostWageNote );

	public LiveHostWageNote beforeNote( @Param( "userId" ) Long userId );

	public List<RspLiveHostWageNoteFamily> familyPage( @Param( "countTime" ) String countTime,
													   @Param( "dto" ) LiveHostWageNote dto );

	public List<Map<String, Object>> selectFamilyName();

	public List<RspLiveHostWageNoteList> hostPage( @Param( "countTime" ) String countTime,
													   @Param( "dto" ) LiveHostWageNote dto );
}
