package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.ReportPlamCom;


/**
 * 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间Service接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface IReportPlamComService {
	/**
	 * 查询综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间列表
	 *
	 * @param reportPlamCom 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
	 * @return 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间集合
	 */
	public Object selectReportPlamComList(ReportPlamCom reportPlamCom);


	List<ReportPlamCom> exportPlamComList(ReportPlamCom reportPlamCom);
}