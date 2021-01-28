package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.domain.ReportMoneyinfo;
import com.qiqilm.server.admin.service.IReportMoneyinfoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping( "/web/report-moneyinfo" )
@Log4j2
public class ReportMoneyinfoController extends BaseController {
	@Autowired
	private IReportMoneyinfoService reportMoneyinfoService;

	/**
	 * 查询平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额列表
	 */
	@PreAuthorize( "@ss.hasPermi('web:report-moneyinfo:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list(ReportMoneyinfo reportMoneyinfo) {
		startPage();
		List<ReportMoneyinfo> list = reportMoneyinfoService.selectReportMoneyinfoList(reportMoneyinfo);
		return getDataTable( list );
	}



}