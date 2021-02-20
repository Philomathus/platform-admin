package com.qiqilm.server.admin.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.domain.ReportPlamGameschilds;
import com.qiqilm.server.admin.service.IReportPlamGameschildsService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 游戏投注报表子表Controller
 *
 * @author 77tv
 * @date 2021-02-20
 */
@RestController
@RequestMapping( "/admin/reportPlamGameschilds" )
public class ReportPlamGameschildsController extends BaseController {
	@Autowired
	private IReportPlamGameschildsService reportPlamGameschildsService;

	/**
	 * 查询游戏投注报表子表列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:report-plam-games:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(ReportPlamGameschilds reportPlamGameschilds) {
		startPage();
		List<ReportPlamGameschilds> list = reportPlamGameschildsService.selectReportPlamGameschildsList(reportPlamGameschilds);
		return getDataTable( list );
	}
}
