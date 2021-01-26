package com.qiqilm.server.admin.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.qiqilm.server.admin.domain.ReportMoneyinfo;
import com.qiqilm.server.admin.mapper.ReportMoneyinfoMapper;
import com.qiqilm.server.admin.service.IReportMoneyinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * 平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class ReportMoneyinfoServiceImpl implements IReportMoneyinfoService {
    @Autowired
    private ReportMoneyinfoMapper reportMoneyinfoMapper;

    /**
     * 查询平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额列表
     *
     * @param reportMoneyinfo 平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额
     * @return 平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额
     */
    @Override
    public List<ReportMoneyinfo> selectReportMoneyinfoList(ReportMoneyinfo reportMoneyinfo) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        String endDate = (String) reportMoneyinfo.getParams().get(1);
        String startDate = (String) reportMoneyinfo.getParams().get(0);
        if(dateNowStr.equals(reportMoneyinfo.getParams().get(1))){
            if(endDate!=null){
                reportMoneyinfoMapper.calldataProrepPlamcom(endDate,endDate);
            }
        }
        List allList=reportMoneyinfoMapper.selectReportMoneyinfoList(reportMoneyinfo);
        if(endDate!=null){
            if(StringUtils.isEmpty(allList)){
                reportMoneyinfoMapper.calldataProrepPlamcom(startDate,endDate);
                allList=reportMoneyinfoMapper.selectReportMoneyinfoList(reportMoneyinfo);
            }
        }

        return allList;
    }



}