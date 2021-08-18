package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.ReportAgentcount;
import com.qiqilm.server.admin.domain.rsp.RspMemberAgent;
import com.qiqilm.server.admin.domain.vo.ReportPlamHome;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
	String calldataProrepPlamcom(@Param( "beginTime" ) String beginTime,@Param( "endTime" ) String endTime, @Param( "agentcode" ) String agentcode);
    List<ReportPlamHome> findChartsOne(@Param("classTwo") String classTwo,@Param("time") String time);

	int existsPromotionCode(ReportAgentcount reportAgentcount);

	void addPromotionCode(ReportAgentcount reportAgentcount);

	void delPromotionCode(ReportAgentcount reportAgentcount);

    List<RspMemberAgent> selectMemberAgent(ReportAgentcount reportAgentcount);

	String callplamagentData(@Param( "p_begintime" ) String p_begintime);
}
