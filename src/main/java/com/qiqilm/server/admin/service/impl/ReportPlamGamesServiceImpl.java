package com.qiqilm.server.admin.service.impl;


import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ReportPlamGames;
import com.qiqilm.server.admin.domain.rsp.RspPlamGamesMonth;
import com.qiqilm.server.admin.mapper.ReportPlamGamesMapper;
import com.qiqilm.server.admin.service.IReportPlamGamesService;
import com.qiqilm.server.admin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

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
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 查询【请填写功能名称】列表
     *
     * @param reportPlamGames 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public Object selectReportPlamGamesList(ReportPlamGames reportPlamGames) {

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);

//        Calendar beforeTime = Calendar.getInstance();
//        beforeTime.add(Calendar.MINUTE, -5);// 5分钟之前的时间
//        Date beforeD = beforeTime.getTime();
//      List<ReportPlamGames> allList = reportPlamGamesMapper.selectReportPlamGamesList(reportPlamGames);
        Map<String, Object> resultMap = new HashMap<>();
//        if (allList.size() == 0 && reportPlamGames.getBegindate().equals(dateNowStr)) {
//            storage(dateNowStr);
//            return new AjaxResult(900, "报表正在生成，请稍后...");
//        }
//        if (allList.size() != 0 && reportPlamGames.getBegindate().equals(dateNowStr)) {
//            Date updateTime = allList.get(0).getUpdateTime();
//            String reportCache = redisUtil.strGet("admin-reportPlamGames");
//            if ("1".equals(reportCache)) {
//                resultMap.put("rows", allList);
//            } else if ("0".equals(reportCache)) {
//                return new AjaxResult(900, "报表正在生成，请稍后...");
//            } else if (updateTime.getTime() <= beforeD.getTime()) {
//                storage(dateNowStr);
//                return new AjaxResult(900, "报表正在生成，请稍后...");
//            }
//        } else {
//            resultMap.put("rows", allList);
//        }

        if (dateNowStr.equals(reportPlamGames.getBegindate())) {
            if (!redisUtil.exists("admin-reportPlamGames")){
                storage(dateNowStr);
                //return new AjaxResult(900, "报表正在生成，请稍后...");
            }
        }
        List<ReportPlamGames> allList = reportPlamGamesMapper.selectReportPlamGamesList(reportPlamGames);
        resultMap.put("rows", allList);
        return resultMap;
    }

    @Override
    public ReportPlamGames countBetData(ReportPlamGames reportPlamGames) {

        return reportPlamGamesMapper.countBetData(reportPlamGames);
    }


    public void storage(String dateNowStr) {
//        if (!redisUtil.exists("admin-reportPlamGames")
//                && redisUtil.strSetIfAbsent("admin-reportPlamGames", "0", Duration.ofMinutes(5))) {
//            redisUtil.strSet("admin-reportPlamGames", "0", Duration.ofMinutes(5));
//            threadPoolTaskExecutor.execute(() -> {
//                String result = reportPlamGamesMapper.calldataProrepPlamcom(dateNowStr);
//                if (StringUtils.hasText(result) && redisUtil.exists("admin-reportPlamGames")) {
//                    redisUtil.strIncrement("admin-reportPlamGames");
//                }
//            });
//        }
        redisUtil.strSet("admin-reportPlamGames", "0", Duration.ofMinutes(5));
        reportPlamGamesMapper.calldataProrepPlamcom(dateNowStr);

    }

    @Override
    public List<ReportPlamGames> exportPlamGamesList(ReportPlamGames reportPlamGames) {
        List<ReportPlamGames> allList = reportPlamGamesMapper.selectReportPlamGamesList(reportPlamGames);
        return allList;
    }

    @Override
    public List<RspPlamGamesMonth> selectReportPlamGamesListMonth(ReportPlamGames reportPlamGames) {
        String begindate=null;
        if (reportPlamGames.getBegindate()==null){
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            String dateNowStr = sdf.format(d);
            begindate=dateNowStr+"-01";
        }else {
            begindate = reportPlamGames.getBegindate();
        }
        reportPlamGames.setEndDate(begindate.replace("01","31"));
        List<RspPlamGamesMonth> allList = reportPlamGamesMapper.selectReportPlamGamesListMonth(reportPlamGames);
        for (RspPlamGamesMonth rsplist:allList) {
            rsplist.setDate(begindate.substring(0,7));
        }
        return allList;
    }


}