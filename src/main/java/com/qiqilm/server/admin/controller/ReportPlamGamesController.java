package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ReportPlamGames;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IReportPlamGamesService;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping("/admin/report-plam-games")
public class ReportPlamGamesController extends BaseController {
    @Autowired
    private IReportPlamGamesService reportPlamGamesService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private TokenService tokenService;
    /**
     * 查询【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('admin:report-plam-games:list')")
    @GetMapping("/list")
    public Object list(ReportPlamGames reportPlamGames) throws ParseException {
//        reportPlamGamesService.storage(reportPlamGames);


        return reportPlamGamesService.selectReportPlamGamesList(reportPlamGames);

    }

    @GetMapping(value = "/count")
    public AjaxResult countBetData(ReportPlamGames reportPlamGames) {
        String myString = reportPlamGames.getBegindate();
        if (StringUtils.isEmpty(myString)){
            reportPlamGames.setBegindate(getYestoday());
        }
        ReportPlamGames reportPlamGames1 = reportPlamGamesService.countBetData(reportPlamGames);
        return AjaxResult.success(reportPlamGames1);
    }
    //获取昨天数据
    private static String getYestoday(){
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,-1);
        Date time=cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time);
    }
    @PreAuthorize( "@ss.hasPermi('admin:reportPlamGames:export')" )
    @Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
    @GetMapping( "/export" )
    public AjaxResult export(ReportPlamGames reportPlamGames) {
        List<ReportPlamGames>      list = reportPlamGamesService.exportPlamGamesList(reportPlamGames);
        ExcelUtil<ReportPlamGames> util = new ExcelUtil<>(ReportPlamGames.class);
        return util.exportExcel( list, "reportPlamGames" );
    }
}
