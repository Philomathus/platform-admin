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
	 * 查询综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
	 *
	 * @param repId 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间ID
	 * @return 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
	 */
	@Override
	public ReportPlamCom selectReportPlamComById(String repId) {
		return reportPlamComMapper.selectReportPlamComById(repId);
	}

	/**
	 * 查询综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间列表
	 *
	 * @param reportPlamCom 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
	 * @return 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
	 */
	@Override
	public List<ReportPlamCom> selectReportPlamComList(ReportPlamCom reportPlamCom) {
		return reportPlamComMapper.selectReportPlamComList(reportPlamCom);
	}

	/**
	 * 新增综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
	 *
	 * @param reportPlamCom 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
	 * @return 结果
	 */
	@Override
	public int insertReportPlamCom(ReportPlamCom reportPlamCom) {
		return reportPlamComMapper.insertReportPlamCom(reportPlamCom);
	}

	/**
	 * 修改综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
	 *
	 * @param reportPlamCom 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
	 * @return 结果
	 */
	@Override
	public int updateReportPlamCom(ReportPlamCom reportPlamCom) {
		return reportPlamComMapper.updateReportPlamCom(reportPlamCom);
	}

	/**
	 * 批量删除综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
	 *
	 * @param repIds 需要删除的综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间ID
	 * @return 结果
	 */
	@Override
	public int deleteReportPlamComByIds(String[] repIds) {
		return reportPlamComMapper.deleteReportPlamComByIds(repIds);
	}

	/**
	 * 删除综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间信息
	 *
	 * @param repId 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间ID
	 * @return 结果
	 */
	@Override
	public int deleteReportPlamComById(String repId) {
		return reportPlamComMapper.deleteReportPlamComById(repId);
	}
}