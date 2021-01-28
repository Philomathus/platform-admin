package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ReportAnchorhotDayMapper;
import com.qiqilm.server.admin.domain.ReportAnchorhotDay;
import com.qiqilm.server.admin.service.IReportAnchorhotDayService;

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
        return reportAnchorhotDayMapper.selectReportAnchorhotDayList(reportAnchorhotDay);
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
