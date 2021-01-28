package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.ReportAnchorhotDay;

/**
 * 贡献榜Service接口
 *
 * @author 77tv
 * @date 2021-01-28
 */
public interface IReportAnchorhotDayService {
	/**
	 * 查询贡献榜
	 *
	 * @param repId 贡献榜ID
	 * @return 贡献榜
	 */
	public ReportAnchorhotDay selectReportAnchorhotDayById(String repId);

	/**
	 * 查询贡献榜列表
	 *
	 * @param reportAnchorhotDay 贡献榜
	 * @return 贡献榜集合
	 */
	public List<ReportAnchorhotDay> selectReportAnchorhotDayList(ReportAnchorhotDay reportAnchorhotDay);

	/**
	 * 新增贡献榜
	 *
	 * @param reportAnchorhotDay 贡献榜
	 * @return 结果
	 */
	public int insertReportAnchorhotDay(ReportAnchorhotDay reportAnchorhotDay);

	/**
	 * 修改贡献榜
	 *
	 * @param reportAnchorhotDay 贡献榜
	 * @return 结果
	 */
	public int updateReportAnchorhotDay(ReportAnchorhotDay reportAnchorhotDay);

	/**
	 * 批量删除贡献榜
	 *
	 * @param repIds 需要删除的贡献榜ID
	 * @return 结果
	 */
	public int deleteReportAnchorhotDayByIds(String[] repIds );

	/**
	 * 删除贡献榜信息
	 *
	 * @param repId 贡献榜ID
	 * @return 结果
	 */
	public int deleteReportAnchorhotDayById(String repId);
}
