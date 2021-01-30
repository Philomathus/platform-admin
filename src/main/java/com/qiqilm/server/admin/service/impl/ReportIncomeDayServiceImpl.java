package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ReportIncomeDayMapper;
import com.qiqilm.server.admin.domain.ReportIncomeDay;
import com.qiqilm.server.admin.service.IReportIncomeDayService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class ReportIncomeDayServiceImpl implements IReportIncomeDayService {
    @Autowired
    private ReportIncomeDayMapper reportIncomeDayMapper;



    /**
     * 查询【请填写功能名称】列表
     *
     * @param reportIncomeDay 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ReportIncomeDay> selectReportIncomeDayList(ReportIncomeDay reportIncomeDay) {


        List<ReportIncomeDay> allList =reportIncomeDayMapper.selectReportIncomeDayList(reportIncomeDay);
        if(reportIncomeDay.getPaydate()!=null){
            if(allList.isEmpty()){
                reportIncomeDayMapper.calldataProrepPlamcom(reportIncomeDay.getPaydate());
                allList =reportIncomeDayMapper.selectReportIncomeDayList(reportIncomeDay);
            }
        }
        return allList;
    }

    @Override
    public ReportIncomeDay countSuccessData(ReportIncomeDay reportIncomeDay) {
        return reportIncomeDayMapper.countSuccessMoney(reportIncomeDay);
    }


}