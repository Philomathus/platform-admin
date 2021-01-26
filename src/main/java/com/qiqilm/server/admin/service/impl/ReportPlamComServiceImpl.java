package com.qiqilm.server.admin.service.impl;

import java.util.List;

import com.qiqilm.server.admin.domain.ReportPlamCom;
import com.qiqilm.server.admin.mapper.ReportPlamComMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.service.IReportPlamComService;

/**
 * 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class ReportPlamComServiceImpl implements IReportPlamComService {
	@Autowired
	private ReportPlamComMapper reportPlamComMapper;


	/**
	 * 查询综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间列表
	 *
	 * @param reportPlamCom 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
	 * @return 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
	 */
	@Override
	public List<ReportPlamCom> selectReportPlamComList(ReportPlamCom reportPlamCom) {
		List<ReportPlamCom> allList = reportPlamComMapper.selectReportPlamComList(reportPlamCom);
		if(reportPlamCom.getParams().get(0)!=null){
			String startTime = (String) reportPlamCom.getParams().get(0);
			if(allList.isEmpty()){
				getCalldataProrepPlamcom(startTime);
				allList = reportPlamComMapper.selectReportPlamComList(reportPlamCom);
			}
		}

		return allList;
	}

	private String getCalldataProrepPlamcom(String a){
		String s = reportPlamComMapper.calldataProrepPlamcom(a);
		return s;
	}
}