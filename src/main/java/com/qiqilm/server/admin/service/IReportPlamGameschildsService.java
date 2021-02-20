package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.ReportPlamGameschilds;

/**
 * 游戏投注报表子表Service接口
 *
 * @author 77tv
 * @date 2021-02-20
 */
public interface IReportPlamGameschildsService {
	/**
	 * 查询游戏投注报表子表
	 *
	 * @param gameUuid 游戏投注报表子表ID
	 * @return 游戏投注报表子表
	 */
	public ReportPlamGameschilds selectReportPlamGameschildsById(String gameUuid);

	/**
	 * 查询游戏投注报表子表列表
	 *
	 * @param reportPlamGameschilds 游戏投注报表子表
	 * @return 游戏投注报表子表集合
	 */
	public List<ReportPlamGameschilds> selectReportPlamGameschildsList(ReportPlamGameschilds reportPlamGameschilds);

	/**
	 * 新增游戏投注报表子表
	 *
	 * @param reportPlamGameschilds 游戏投注报表子表
	 * @return 结果
	 */
	public int insertReportPlamGameschilds(ReportPlamGameschilds reportPlamGameschilds);

	/**
	 * 修改游戏投注报表子表
	 *
	 * @param reportPlamGameschilds 游戏投注报表子表
	 * @return 结果
	 */
	public int updateReportPlamGameschilds(ReportPlamGameschilds reportPlamGameschilds);

	/**
	 * 批量删除游戏投注报表子表
	 *
	 * @param gameUuids 需要删除的游戏投注报表子表ID
	 * @return 结果
	 */
	public int deleteReportPlamGameschildsByIds(String[] gameUuids );

	/**
	 * 删除游戏投注报表子表信息
	 *
	 * @param gameUuid 游戏投注报表子表ID
	 * @return 结果
	 */
	public int deleteReportPlamGameschildsById(String gameUuid);
}
