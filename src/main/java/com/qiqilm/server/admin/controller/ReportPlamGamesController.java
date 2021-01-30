package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ReportPlamGames;
import com.qiqilm.server.admin.service.IReportPlamGamesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
	@RequestMapping( "/admin/report-plam-games" )
public class ReportPlamGamesController extends BaseController {
	@Autowired
	private IReportPlamGamesService reportPlamGamesService;

/**
 * 查询【请填写功能名称】列表
 */
@PreAuthorize( "@ss.hasPermi('admin:report-plam-games:list')" )
@GetMapping( "/list" )
    	public TableDataInfo list(ReportPlamGames reportPlamGames) {
		startPage();
		List<ReportPlamGames> list = reportPlamGamesService.selectReportPlamGamesList(reportPlamGames);
		return getDataTable( list );
	}

	@GetMapping( value = "/count" )
	public AjaxResult countBetData(ReportPlamGames reportPlamGames) {
		ReportPlamGames reportPlamGames1 = reportPlamGamesService.countBetData(reportPlamGames);
		return AjaxResult.success(reportPlamGames1);
	}

}