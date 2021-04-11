package com.qiqilm.server.admin.mapper;

import java.util.List;
import java.util.Map;

import com.qiqilm.server.admin.domain.LiveHostWageDay;
import com.qiqilm.server.admin.domain.LiveHostWageDay;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageDayFamily;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageDayList;
import org.apache.ibatis.annotations.Param;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-03-29
 */
public interface LiveHostWageDayMapper {
	/**
	 * 查询主播时长
	 *
	 * @param id 主播时长ID
	 * @return 主播时长
	 */
	public LiveHostWageDay selectLiveHostWageDayById(String id );

	/**
	 * 查询主播时长列表
	 *
	 * @param liveHostWageDay 主播时长
	 * @return 主播时长集合
	 */
	public List<LiveHostWageDay> selectLiveHostWageDayList( LiveHostWageDay liveHostWageDay );

	/**
	 * 新增主播时长
	 *
	 * @param liveHostWageDay 主播时长
	 * @return 结果
	 */
	public int insertLiveHostWageDay( LiveHostWageDay liveHostWageDay );

	/**
	 * 修改主播时长
	 *
	 * @param liveHostWageDay 主播时长
	 * @return 结果
	 */
	public int updateLiveHostWageDay( LiveHostWageDay liveHostWageDay );

	public LiveHostWageDay beforeDay( @Param( "userId" ) Long userId );

	public List<RspLiveHostWageDayFamily> familyPage(@Param( "dto" ) LiveHostWageDay dto );

	public List<Map<String, Object>> selectFamilyName();

	public List<RspLiveHostWageDayList> hostPage(@Param( "dto" ) LiveHostWageDay dto );

	List<String> getliveHostWageDay( @Param( "createTime" ) String createTime, @Param( "familyId" ) Long familyId);
}