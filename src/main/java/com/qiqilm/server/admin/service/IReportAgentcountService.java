package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.ReportAgentcount;

/**
 * 代理统计，主要用于代理渠道的统计Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IReportAgentcountService {


	/**
	 * 查询代理统计，主要用于代理渠道的统计列表
	 *
	 * @param reportAgentcount 代理统计，主要用于代理渠道的统计
	 * @return 代理统计，主要用于代理渠道的统计集合
	 */
	public List<ReportAgentcount> selectReportAgentcountList(ReportAgentcount reportAgentcount);


}