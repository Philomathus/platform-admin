package com.qiqilm.server.admin.mapper;


import com.qiqilm.server.admin.domain.ReportPlamGames;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface ReportPlamGamesMapper {


	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param reportPlamGames 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<ReportPlamGames> selectReportPlamGamesList(ReportPlamGames reportPlamGames);
	String calldataProrepPlamcom(@Param( "timedateta" ) String timedateta);

    ReportPlamGames countBetData(ReportPlamGames reportPlamGames);
}