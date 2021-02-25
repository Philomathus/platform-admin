package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.ReportPlamGames;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.IReportPlamGamesService;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.ServletUtil;
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
    public TableDataInfo list(ReportPlamGames reportPlamGames) throws ParseException {
        startPage();
        Date d = new Date();
        String myString = reportPlamGames.getBegindate();
        if (!StringUtils.isEmpty(myString)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dd = simpleDateFormat.parse(myString);
            boolean flag = dd.before(d);
            if (!flag) {
                reportPlamGames.setBegindate(null);
            }
        } else {
            reportPlamGames.setBegindate(getYestoday());
        }
        List<ReportPlamGames> list = reportPlamGamesService.selectReportPlamGamesList(reportPlamGames);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('admin:report-plam-games:list')")
    @GetMapping("/storage")
    public AjaxResult storage(ReportPlamGames reportPlamGames){
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String userId = loginUser.getUser().getUserId().toString();
        if ( !redisUtil.lock( EnumLock.adminUser, userId, "10", 120 ) ) {
          return AjaxResult.error("请勿连续点击搜索，2分钟后再搜索");
        }
        return AjaxResult.success(reportPlamGamesService.storage(reportPlamGames));
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
        List<ReportPlamGames>      list = reportPlamGamesService.selectReportPlamGamesList(reportPlamGames);
        ExcelUtil<ReportPlamGames> util = new ExcelUtil<>(ReportPlamGames.class);
        return util.exportExcel( list, "reportPlamGames" );
    }
}