package com.qiqilm.server.admin.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.qiqilm.server.admin.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ReportAgentcountMapper;
import com.qiqilm.server.admin.domain.ReportAgentcount;
import com.qiqilm.server.admin.service.IReportAgentcountService;
import org.springframework.util.StringUtils;

/**
 * 代理统计，主要用于代理渠道的统计Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class ReportAgentcountServiceImpl implements IReportAgentcountService {
    @Autowired
    private ReportAgentcountMapper reportAgentcountMapper;



    /**
     * 查询代理统计，主要用于代理渠道的统计列表
     *
     * @param reportAgentcount 代理统计，主要用于代理渠道的统计
     * @return 代理统计，主要用于代理渠道的统计
     */
    @Override
    public List<ReportAgentcount> selectReportAgentcountList(ReportAgentcount reportAgentcount) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        if (null==reportAgentcount.getParams()||reportAgentcount.getParams().size()==0){
           HashMap m=new HashMap<>();
           m.put("beginTime",getPastDate(7));
           m.put("endTime",dateNowStr);
            reportAgentcount.setParams(m);
        }
        List<ReportAgentcount> allList = reportAgentcountMapper.selectReportAgentcountList(reportAgentcount);
        return allList;
    }

    @Override
    public Object storage(ReportAgentcount reportAgentcount) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        if (null==reportAgentcount.getParams()||reportAgentcount.getParams().size()==0){
            HashMap m=new HashMap<>();
            m.put("beginTime",getPastDate(7));
            m.put("endTime",dateNowStr);
            reportAgentcount.setParams(m);
        }
        List<ReportAgentcount> allList = reportAgentcountMapper.selectReportAgentcountList(reportAgentcount);
        if (allList.size()==0){
            if(reportAgentcount.getAgentcode()==null){
                reportAgentcount.setAgentcode("");
            }
            String endTime = (String) reportAgentcount.getParams().get("endTime");
            if(dateNowStr.equals(endTime)){
             return  reportAgentcountMapper.calldataProrepPlamcom(endTime,reportAgentcount.getAgentcode());
            }
        }
        return null;
    }


    private  String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }
}