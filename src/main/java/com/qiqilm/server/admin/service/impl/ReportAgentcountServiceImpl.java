package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ReportAgentcount;
import com.qiqilm.server.admin.domain.rsp.RspMemberAgent;
import com.qiqilm.server.admin.domain.vo.ReportPlamHome;
import com.qiqilm.server.admin.mapper.ReportAgentcountMapper;
import com.qiqilm.server.admin.service.IReportAgentcountService;
import com.qiqilm.server.admin.utils.LocalDateTimeUtils;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 代理统计，主要用于代理渠道的统计Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
@Log4j2
public class ReportAgentcountServiceImpl implements IReportAgentcountService {
    @Resource
    private ReportAgentcountMapper reportAgentcountMapper;

    /**
     * 查询代理统计，主要用于代理渠道的统计列表
     *
     * @param reportAgentcount 代理统计，主要用于代理渠道的统计
     * @return 代理统计，主要用于代理渠道的统计
     */
    @Override
    public Object selectReportAgentcountList(ReportAgentcount reportAgentcount) throws Exception {
        String beginTime = reportAgentcount.getParams().getOrDefault("beginTime", "").toString();
        String endTime = reportAgentcount.getParams().getOrDefault("endTime", "").toString();
        String today = LocalDateTimeUtils.format(LocalDate.now());
        if (StringUtils.isBlank(beginTime)) {
            beginTime = today;
            endTime = today;
        }
        if (beginTime.equals(endTime)) { // 同一天
            if (today.equals(beginTime)) {
                //如果是当天，校验是否是一个小时之前的数据
                String s = reportAgentcountMapper.rmemberInfoLately();
                if(StringUtils.isBlank( s )){
                    return AjaxResult.error("请重新生成" + beginTime + "数据1");
                }
                String l = reportAgentcountMapper.memberInfoLately();
                LocalDateTime time = LocalDateTimeUtils.parseLocalDateTime(s);
                Duration between = Duration.between(time, LocalDateTime.now());
                if (between.getSeconds() > 1200 && !s.equals( l )) {
                    return AjaxResult.error("请重新生成" + beginTime + "数据2");
                }
            } else {
                //昨天的数据，判断数量是否相等
                int i = reportAgentcountMapper.memberInfoCounts(beginTime + " 00:00:00", beginTime + " 23:59:59");
                int r = reportAgentcountMapper.rmemberInfoCounts(beginTime + " 00:00:00", beginTime + " 23:59:59");
                if (i != r) {
                    return AjaxResult.error("请重新生成" + beginTime + "数据3");
                }
            }
            reportAgentcount.setAgenttime(beginTime);
        } else {
            reportAgentcount.setAgenttime(beginTime + "-" + endTime);
        }
        if (reportAgentcount.getAgentcode() != null) {//判断代理号是否为空，代理号不为空，并且没有查询到数据，
            reportAgentcountMapper.calldataProrepPlamcom(beginTime, endTime, reportAgentcount.getAgentcode().trim());// 调用存储过程
            List<ReportAgentcount> allList1 = reportAgentcountMapper.selectReportAgentcountList(reportAgentcount);
            return AjaxResult.success(allList1);
        }
        List<ReportAgentcount> allList = reportAgentcountMapper.selectReportAgentcountList(reportAgentcount);
        return AjaxResult.success(allList);
    }

    //
    //    public void storage(ReportAgentcount reportAgentcount) {
    //        String dateNowStr = dateNowStr();//获取当天时间字符串
    //        if (reportAgentcount.getAgentcode() == null) {
    //            reportAgentcount.setAgentcode("");
    //        }
    //        if (!redisUtil.exists("admin-reportAgentcount") &&
    //                redisUtil.strSetIfAbsent("admin-reportAgentcount", "0", Duration.ofMinutes(5))) {
    //            redisUtil.strSet("admin-reportAgentcount", "0", Duration.ofMinutes(5));
    //            threadPoolTaskExecutor.execute(() -> {
    //                String result = reportAgentcountMapper.calldataProrepPlamcom(dateNowStr, reportAgentcount.getAgentcode());
    //                if (StringUtils.hasText(result) && redisUtil.exists("admin-reportAgentcount")) {
    //                    redisUtil.strIncrement("admin-reportAgentcount");
    //                }
    //            });
    //        }
    //    }

    @Override
    public List<ReportPlamHome> findChartsOne(String classTwo, String time) {
        return reportAgentcountMapper.findChartsOne(classTwo, time);
    }

    @Override
    public int existsPromotionCode(ReportAgentcount reportAgentcount) {
        return reportAgentcountMapper.existsPromotionCode(reportAgentcount);
    }

    @Override
    public void addPromotionCode(ReportAgentcount reportAgentcount) {
        reportAgentcountMapper.addPromotionCode(reportAgentcount);
    }

    @Override
    public void delPromotionCode(ReportAgentcount reportAgentcount) {
        reportAgentcountMapper.delPromotionCode(reportAgentcount);
    }

    @Override
    public AjaxResult plamagent_data(ReportAgentcount reportAgentcount) {
        String beginTime = reportAgentcount.getParams().getOrDefault("beginTime", "").toString();
        String endTime = reportAgentcount.getParams().getOrDefault("endTime", "").toString();
        String today = LocalDateTimeUtils.format(LocalDate.now());
        if (StringUtils.isBlank(beginTime)) {
            beginTime = today;
            endTime = today;
        }
        if (beginTime.equals(endTime)) {
            reportAgentcount.setAgenttime(beginTime);
        } else {
            return AjaxResult.error("跨天无需预生成");
        }
        reportAgentcountMapper.callplamagentData(reportAgentcount.getAgenttime());
        return AjaxResult.success("预生成数据成功");
    }

    @Override
    public List<ReportAgentcount> exportAgentcountList(ReportAgentcount reportAgentcount) {
        return reportAgentcountMapper.selectReportAgentcountList(reportAgentcount);
    }

    @Override
    public List<RspMemberAgent> selectMemberAgent(ReportAgentcount reportAgentcount) {
        return reportAgentcountMapper.selectMemberAgent(reportAgentcount);
    }

    private void setSelectTime(String dateNowStr, ReportAgentcount reportAgentcount) {
        if (null == reportAgentcount.getParams() || reportAgentcount.getParams().size() == 0 ||
                reportAgentcount.getParams().get("beginTime") == "") {
            HashMap m = new HashMap<>();
            m.put("beginTime", getPastDate(7));
            m.put("endTime", dateNowStr);
            reportAgentcount.setParams(m);
        }
    }

    private String dateNowStr() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        return dateNowStr;
    }

    private String dateYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date d = cal.getTime();
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
        String yesterday = sp.format(d);//获取昨天日期
        return yesterday;
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
