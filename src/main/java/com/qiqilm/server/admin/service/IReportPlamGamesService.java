package com.qiqilm.server.admin.service;



import com.qiqilm.server.admin.domain.ReportPlamGames;
import com.qiqilm.server.admin.domain.rsp.RspPlamGamesMonth;

import java.text.ParseException;
import java.util.List;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IReportPlamGamesService {



	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param reportPlamGames 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public Object selectReportPlamGamesList(ReportPlamGames reportPlamGames);

	ReportPlamGames countBetData(ReportPlamGames reportPlamGames);

	List<ReportPlamGames> exportPlamGamesList(ReportPlamGames reportPlamGames);

    List<RspPlamGamesMonth> selectReportPlamGamesListMonth(ReportPlamGames reportPlamGames) throws ParseException;

	RspPlamGamesMonth countBet(ReportPlamGames reportPlamGames) throws ParseException;
}