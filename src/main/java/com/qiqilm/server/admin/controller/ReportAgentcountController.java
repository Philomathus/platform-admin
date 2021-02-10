package com.qiqilm.server.admin.controller;

import java.text.ParseException;
import java.util.List;

import com.qiqilm.server.admin.domain.ReportMoneyinfo;
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
import com.qiqilm.server.admin.domain.ReportAgentcount;
import com.qiqilm.server.admin.service.IReportAgentcountService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 代理统计，主要用于代理渠道的统计Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/admin/reportAgentcount" )
public class ReportAgentcountController extends BaseController {
	@Autowired
	private IReportAgentcountService reportAgentcountService;

	/**
	 * 查询代理统计，主要用于代理渠道的统计列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:reportAgentcount:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(ReportAgentcount reportAgentcount) throws ParseException {
		startPage();
		List<ReportAgentcount> list = reportAgentcountService.selectReportAgentcountList(reportAgentcount);
		return getDataTable( list );
	}
	@PreAuthorize( "@ss.hasPermi('admin:reportAgentcount:list')" )
	@GetMapping( "/storage" )
	public AjaxResult storage(ReportAgentcount reportAgentcount) throws ParseException {
		return AjaxResult.success( reportAgentcountService.storage(reportAgentcount));
	}
	@PreAuthorize( "@ss.hasPermi('admin:reportAgentcount:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(ReportAgentcount reportAgentcount) throws ParseException {
		List<ReportAgentcount> list = reportAgentcountService.selectReportAgentcountList(reportAgentcount);
		ExcelUtil<ReportAgentcount> util = new ExcelUtil<>(ReportAgentcount.class);
		return util.exportExcel( list, "reportAgentcount" );
	}

}