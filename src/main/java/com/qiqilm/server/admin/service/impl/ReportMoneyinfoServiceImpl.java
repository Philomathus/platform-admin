package com.qiqilm.server.admin.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    public List<ReportMoneyinfo> selectReportMoneyinfoList(ReportMoneyinfo reportMoneyinfo) throws ParseException {
        List<ReportMoneyinfo> allList =new ArrayList<>();
        String dateNowStr=dateNowStr();//获取当天时间字符串
        setSelectTime(dateNowStr,reportMoneyinfo);//首次进入查询7天的数据
        String beginTime = (String) reportMoneyinfo.getParams().get("beginTime");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(beginTime);
        boolean flag = date.before(new Date());
        if (flag){
             allList = reportMoneyinfoMapper.selectReportMoneyinfoList(reportMoneyinfo);
            return allList;
        }else {
            return allList;
        }

    }

    @Override
    public Object storage(ReportMoneyinfo reportMoneyinfo) {
        String dateNowStr=dateNowStr();//获取当天时间字符串
        setSelectTime(dateNowStr,reportMoneyinfo);//首次进入查询7天的数据
//        List allList = reportMoneyinfoMapper.selectReportMoneyinfoList(reportMoneyinfo);
        if (reportMoneyinfo.getParams().get("endTime").equals(dateNowStr)){
//            if (allList.size() == 0) {
                return reportMoneyinfoMapper.calldataProrepPlamcom(dateNowStr, dateNowStr);
            }
//        }
        return null;
    }

    //统计表头数据
    @Override
    public ReportMoneyinfo countMoneyData(ReportMoneyinfo reportMoneyinfo) throws ParseException {
        String dateNowStr=dateNowStr();//获取当天时间字符串
        setSelectTime(dateNowStr,reportMoneyinfo);//首次进入查询7天的数据
        String beginTime = (String) reportMoneyinfo.getParams().get("beginTime");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(beginTime);
        boolean flag = date.before(new Date());
        if (!flag){
            reportMoneyinfo.setPaymentAmount(BigDecimal.ZERO);
            reportMoneyinfo.setOutMoney(BigDecimal.ZERO);
            reportMoneyinfo.setCountMoney(BigDecimal.ZERO);
            reportMoneyinfo.setTotalAccountGifts(BigDecimal.ZERO);
            return reportMoneyinfo;
        }
        ReportMoneyinfo reportMoneyinfo1 = reportMoneyinfoMapper.countMoneyInfoData(reportMoneyinfo);
        if (!ObjectUtils.isEmpty(reportMoneyinfo1)) {
            BigDecimal paymentAmount = reportMoneyinfo1.getPaymentAmount();//入款总金额
            BigDecimal outMoney = reportMoneyinfo1.getOutMoney();//出款总金额
            reportMoneyinfo1.setCountMoney(paymentAmount.subtract(outMoney));
            return reportMoneyinfo1;
        }else {
            reportMoneyinfo.setPaymentAmount(BigDecimal.ZERO);
            reportMoneyinfo.setOutMoney(BigDecimal.ZERO);
            reportMoneyinfo.setCountMoney(BigDecimal.ZERO);
            reportMoneyinfo.setTotalAccountGifts(BigDecimal.ZERO);
            return reportMoneyinfo;
        }
    }

    private void setSelectTime(String dateNowStr, ReportMoneyinfo reportMoneyinfo) {
        if (null == reportMoneyinfo.getParams() || reportMoneyinfo.getParams().size() == 0 ||
                reportMoneyinfo.getParams().get("beginTime") == "") {
            HashMap m = new HashMap<>();
            m.put("beginTime", getPastDate(7));
            m.put("endTime", dateNowStr);
            reportMoneyinfo.setParams(m);
        }
    }
    private String dateNowStr() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        return dateNowStr;
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