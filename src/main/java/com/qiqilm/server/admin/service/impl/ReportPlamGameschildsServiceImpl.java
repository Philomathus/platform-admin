package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ReportPlamGameschildsMapper;
import com.qiqilm.server.admin.domain.ReportPlamGameschilds;
import com.qiqilm.server.admin.service.IReportPlamGameschildsService;

/**
 * 游戏投注报表子表Service业务层处理
 *
 * @author 77tv
 * @date 2021-02-20
 */
@Service
public class ReportPlamGameschildsServiceImpl implements IReportPlamGameschildsService {
    @Autowired
    private ReportPlamGameschildsMapper reportPlamGameschildsMapper;

    /**
     * 查询游戏投注报表子表
     *
     * @param gameUuid 游戏投注报表子表ID
     * @return 游戏投注报表子表
     */
    @Override
    public ReportPlamGameschilds selectReportPlamGameschildsById(String gameUuid) {
        return reportPlamGameschildsMapper.selectReportPlamGameschildsById(gameUuid);
    }

    /**
     * 查询游戏投注报表子表列表
     *
     * @param reportPlamGameschilds 游戏投注报表子表
     * @return 游戏投注报表子表
     */
    @Override
    public List<ReportPlamGameschilds> selectReportPlamGameschildsList(ReportPlamGameschilds reportPlamGameschilds) {
        return reportPlamGameschildsMapper.selectReportPlamGameschildsList(reportPlamGameschilds);
    }

    /**
     * 新增游戏投注报表子表
     *
     * @param reportPlamGameschilds 游戏投注报表子表
     * @return 结果
     */
    @Override
    public int insertReportPlamGameschilds(ReportPlamGameschilds reportPlamGameschilds) {
        return reportPlamGameschildsMapper.insertReportPlamGameschilds(reportPlamGameschilds);
    }

    /**
     * 修改游戏投注报表子表
     *
     * @param reportPlamGameschilds 游戏投注报表子表
     * @return 结果
     */
    @Override
    public int updateReportPlamGameschilds(ReportPlamGameschilds reportPlamGameschilds) {
        return reportPlamGameschildsMapper.updateReportPlamGameschilds(reportPlamGameschilds);
    }

    /**
     * 批量删除游戏投注报表子表
     *
     * @param gameUuids 需要删除的游戏投注报表子表ID
     * @return 结果
     */
    @Override
    public int deleteReportPlamGameschildsByIds(String[] gameUuids) {
        return reportPlamGameschildsMapper.deleteReportPlamGameschildsByIds(gameUuids);
    }

    /**
     * 删除游戏投注报表子表信息
     *
     * @param gameUuid 游戏投注报表子表ID
     * @return 结果
     */
    @Override
    public int deleteReportPlamGameschildsById(String gameUuid) {
        return reportPlamGameschildsMapper.deleteReportPlamGameschildsById(gameUuid);
    }
}
