package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.ReportIncomeDay;
import org.apache.ibatis.annotations.Param;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface ReportIncomeDayMapper {


	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param reportIncomeDay 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<ReportIncomeDay> selectReportIncomeDayList(ReportIncomeDay reportIncomeDay);
	String calldataProrepPlamcom(@Param( "timedateta" ) String timedateta);

    ReportIncomeDay countSuccessMoney(ReportIncomeDay reportIncomeDay);
}