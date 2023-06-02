package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.domain.ReportPlamGameschilds;
import com.qiqilm.server.admin.domain.vo.ReportPlamGamesChildVo;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IReportPlamGameschildsService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 游戏投注报表子表Controller
 *
 * @author 77tv
 * @date 2021-02-20
 */
@RestController
@RequestMapping("/admin/reportPlamGameschilds")
public class ReportPlamGameschildsController extends BaseController {
    @Autowired
    private IReportPlamGameschildsService reportPlamGameschildsService;

    /**
     * 查询游戏投注报表子表列表
     */
    @PreAuthorize("@ss.hasPermi('admin:report-plam-games:list')")
    @GetMapping("/list")
    public TableDataInfo list(ReportPlamGameschilds reportPlamGameschilds) {
        startPage();
        List<ReportPlamGameschilds> list = reportPlamGameschildsService.selectReportPlamGameschildsList(reportPlamGameschilds);
        return getDataTable(list);
    }

    @GetMapping(value = "/plamGameListData")
    public TableDataInfo listData(ReportPlamGameschilds reportPlamGameschilds) {
        startPage();
        return getDataTable(reportPlamGameschildsService.selectByBettorsCount(reportPlamGameschilds));
    }

    /**
     * 导出投注详情 Export betting details
     */
    @Log(title = "", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(ReportPlamGameschilds reportPlamGamesChilds, HttpServletResponse response) {
        List<ReportPlamGameschilds> list = reportPlamGameschildsService.selectByBettorsCount(reportPlamGamesChilds);
		List<ReportPlamGamesChildVo> voList = list.stream().map(child ->
                ReportPlamGamesChildVo
                        .builder()
                        .gamecell(child.getGamecell().toString())
                        .gameprofit(child.getGameprofit().toString())
                        .agentchild(child.getAgentchild()).build()
		).collect(Collectors.toList());
        ExportExcelUtil.exportExcel(voList, "综合数据报表", "综合数据报表", ReportPlamGamesChildVo.class, response);
    }
}
