package com.qiqilm.server.admin.service.impl;


import com.qiqilm.server.admin.domain.ReportPlamGames;
import com.qiqilm.server.admin.mapper.ReportPlamGamesMapper;
import com.qiqilm.server.admin.service.IReportPlamGamesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class ReportPlamGamesServiceImpl implements IReportPlamGamesService {
    @Autowired
    private ReportPlamGamesMapper reportPlamGamesMapper;




    /**
     * 查询【请填写功能名称】列表
     *
     * @param reportPlamGames 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ReportPlamGames> selectReportPlamGamesList(ReportPlamGames reportPlamGames) {
        List<ReportPlamGames> allList =reportPlamGamesMapper.selectReportPlamGamesList(reportPlamGames);
        if(reportPlamGames.getBegindate()!=null){
            if(allList.isEmpty()){
                reportPlamGamesMapper.calldataProrepPlamcom(reportPlamGames.getBegindate());
                allList =reportPlamGamesMapper.selectReportPlamGamesList(reportPlamGames);
            }
        }

        return allList;
    }

    @Override
    public ReportPlamGames countBetData(ReportPlamGames reportPlamGames) {

        return reportPlamGamesMapper.countBetData(reportPlamGames);
    }


}