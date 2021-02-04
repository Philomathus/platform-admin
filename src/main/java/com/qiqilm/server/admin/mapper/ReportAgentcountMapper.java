package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.ReportAgentcount;
import org.apache.ibatis.annotations.Param;

/**
 * 代理统计，主要用于代理渠道的统计Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface ReportAgentcountMapper {

	/**
	 * 查询代理统计，主要用于代理渠道的统计列表
	 *
	 * @param reportAgentcount 代理统计，主要用于代理渠道的统计
	 * @return 代理统计，主要用于代理渠道的统计集合
	 */
	public List<ReportAgentcount> selectReportAgentcountList(ReportAgentcount reportAgentcount);
	String calldataProrepPlamcom(@Param( "timedateta" ) String timedateta, @Param( "agentcode" ) String agentcode);
}