package com.qiqilm.server.admin.mapper;

import java.util.List;
import com.qiqilm.server.admin.domain.ReportPlamGameschilds;

/**
 * 游戏投注报表子表Mapper接口
 *
 * @author 77tv
 * @date 2021-02-20
 */
public interface ReportPlamGameschildsMapper {
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
	 * 删除游戏投注报表子表
	 *
	 * @param gameUuid 游戏投注报表子表ID
	 * @return 结果
	 */
	public int deleteReportPlamGameschildsById(String gameUuid);

	/**
	 * 批量删除游戏投注报表子表
	 *
	 * @param gameUuids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteReportPlamGameschildsByIds(String[] gameUuids );

	public String getPlatformIdByGameUuid( String gameUuid );

	public List<ReportPlamGameschilds> selectByBettorsCounts( ReportPlamGameschilds reportPlamGameschilds );
}
