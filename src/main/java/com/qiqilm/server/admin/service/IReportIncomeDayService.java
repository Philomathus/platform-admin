package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.ReportIncomeDay;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IReportIncomeDayService {



	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param reportIncomeDay 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<ReportIncomeDay> selectReportIncomeDayList(ReportIncomeDay reportIncomeDay);

}