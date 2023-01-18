package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.ReportAnchorhotDay;
import com.qiqilm.server.admin.mapper.ReportAnchorhotDayMapper;
import com.qiqilm.server.admin.service.IReportAnchorhotDayService;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 贡献榜Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-28
 */
@Service
public class ReportAnchorhotDayServiceImpl implements IReportAnchorhotDayService {
    @Autowired
    private ReportAnchorhotDayMapper reportAnchorhotDayMapper;

    /**
     * 查询贡献榜
     *
     * @param repId 贡献榜ID
     * @return 贡献榜
     */
    @Override
    public ReportAnchorhotDay selectReportAnchorhotDayById(String repId) {
        return reportAnchorhotDayMapper.selectReportAnchorhotDayById(repId);
    }

    /**
     * 查询贡献榜列表
     *
     * @param reportAnchorhotDay 贡献榜
     * @return 贡献榜
     */
    @Override
    public List<ReportAnchorhotDay> selectReportAnchorhotDayList(ReportAnchorhotDay reportAnchorhotDay) {
        Calendar ca = Calendar.getInstance();
        if (reportAnchorhotDay.getReptime() == null) {
            reportAnchorhotDay.setReptime(new Date());
        }
        if (reportAnchorhotDay.getType() == null) {
            reportAnchorhotDay.setType(0);
        }
        ca.setTime(reportAnchorhotDay.getReptime());
        int year = ca.get(Calendar.YEAR);
        if (reportAnchorhotDay.getType() == 0) {
            int days = ca.get(Calendar.DAY_OF_YEAR);
            reportAnchorhotDay.setNum(year + "-" + days);
            return reportAnchorhotDayMapper.selectReportAnchorhotDayByDay(reportAnchorhotDay);
        } else if (reportAnchorhotDay.getType() == 1) {
            LocalDate today = reportAnchorhotDay.getReptime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            int week = DateFormatUtils.getWeekOfYear(monday);
            reportAnchorhotDay.setNum(DateFormatUtils.getYear(monday) + "-" + week);
            return reportAnchorhotDayMapper.selectReportAnchorhotDayByWeek(reportAnchorhotDay);
        } else if (reportAnchorhotDay.getType() == 2) {
            int month = ca.get(Calendar.MONTH) + 1;//获取是第几周
            //月份小于10补0
            if (month < 10) {
                reportAnchorhotDay.setNum(year + "-0" + month);
            } else {
                reportAnchorhotDay.setNum(year + "-" + month);
            }
            return reportAnchorhotDayMapper.selectReportAnchorhotDayListByMonth(reportAnchorhotDay);
        }
        return null;
    }

    /**
     * 新增贡献榜
     *
     * @param reportAnchorhotDay 贡献榜
     * @return 结果
     */
    @Override
    public int insertReportAnchorhotDay(ReportAnchorhotDay reportAnchorhotDay) {
        return reportAnchorhotDayMapper.insertReportAnchorhotDay(reportAnchorhotDay);
    }

    /**
     * 修改贡献榜
     *
     * @param reportAnchorhotDay 贡献榜
     * @return 结果
     */
    @Override
    public int updateReportAnchorhotDay(ReportAnchorhotDay reportAnchorhotDay) {
        return reportAnchorhotDayMapper.updateReportAnchorhotDay(reportAnchorhotDay);
    }

    /**
     * 批量删除贡献榜
     *
     * @param repIds 需要删除的贡献榜ID
     * @return 结果
     */
    @Override
    public int deleteReportAnchorhotDayByIds(String[] repIds) {
        return reportAnchorhotDayMapper.deleteReportAnchorhotDayByIds(repIds);
    }

    /**
     * 删除贡献榜信息
     *
     * @param repId 贡献榜ID
     * @return 结果
     */
    @Override
    public int deleteReportAnchorhotDayById(String repId) {
        return reportAnchorhotDayMapper.deleteReportAnchorhotDayById(repId);
    }
}
