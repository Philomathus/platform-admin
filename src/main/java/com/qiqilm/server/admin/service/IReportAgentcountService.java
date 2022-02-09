package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ReportAgentcount;
import com.qiqilm.server.admin.domain.ReportPlamGameschilds;
import com.qiqilm.server.admin.domain.rsp.RspMemberAgent;
import com.qiqilm.server.admin.domain.vo.ReportPlamHome;

import java.text.ParseException;
import java.util.List;

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
	public Object selectReportAgentcountList(ReportAgentcount reportAgentcount) throws Exception;

    List<ReportPlamHome> findChartsOne(String classTwo, String time);

    int existsPromotionCode(ReportAgentcount reportAgentcount);

	boolean addPromotionCode(ReportAgentcount reportAgentcount);

	void delPromotionCode(ReportAgentcount reportAgentcount);

	AjaxResult plamagent_data(ReportAgentcount reportAgentcount);

	List<ReportAgentcount> exportAgentcountList(ReportAgentcount reportAgentcount);

    List<RspMemberAgent> selectMemberAgent(ReportAgentcount reportAgentcount);
}
