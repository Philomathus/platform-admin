package com.qiqilm.server.admin.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.qiqilm.server.admin.domain.ReportMoneyinfo;
import com.qiqilm.server.admin.mapper.ReportMoneyinfoMapper;
import com.qiqilm.server.admin.service.IReportMoneyinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
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
        if (null == reportMoneyinfo.getParams() || reportMoneyinfo.getParams().size() == 0) {
            HashMap m = new HashMap<>();
            m.put("beginTime", getPastDate(7));
            m.put("endTime", dateNowStr);
            reportMoneyinfo.setParams(m);
        }
        List allList = reportMoneyinfoMapper.selectReportMoneyinfoList(reportMoneyinfo);
        return allList;
    }

    @Override
    public Object storage(ReportMoneyinfo reportMoneyinfo) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        if (null == reportMoneyinfo.getParams() || reportMoneyinfo.getParams().size() == 0) {
            HashMap m = new HashMap<>();
            m.put("beginTime", dateNowStr);
            m.put("endTime", dateNowStr);
            reportMoneyinfo.setParams(m);
        }
        List allList = reportMoneyinfoMapper.selectReportMoneyinfoList(reportMoneyinfo);
        if (allList.size() == 0) {
            return reportMoneyinfoMapper.calldataProrepPlamcom(dateNowStr, dateNowStr);
        }
        return null;
    }

    //统计表头数据
    @Override
    public ReportMoneyinfo countMoneyData(ReportMoneyinfo reportMoneyinfo) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        if (null == reportMoneyinfo.getParams() || reportMoneyinfo.getParams().size() == 0) {
            HashMap m = new HashMap<>();
            m.put("beginTime", getPastDate(7));
            m.put("endTime", dateNowStr);
            reportMoneyinfo.setParams(m);
        }
        ReportMoneyinfo reportMoneyinfo1 = reportMoneyinfoMapper.countMoneyInfoData(reportMoneyinfo);
        if (!ObjectUtils.isEmpty(reportMoneyinfo1)) {
            BigDecimal paymentAmount = reportMoneyinfo1.getPaymentAmount();//入款总金额
            BigDecimal outMoney = reportMoneyinfo1.getOutMoney();//出款总金额
            reportMoneyinfo1.setCountMoney(paymentAmount.subtract(outMoney));
        }
        return reportMoneyinfo1;
    }


    private String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }

}