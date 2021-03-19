package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ReportMoneyinfo;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IReportMoneyinfoService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
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
	public Object list( ReportMoneyinfo reportMoneyinfo ) throws ParseException {
		return reportMoneyinfoService.selectReportMoneyinfoList( reportMoneyinfo );
	}

	@GetMapping( value = "/count" )
	public AjaxResult countMoneyData( ReportMoneyinfo reportMoneyinfo ) throws ParseException {
		ReportMoneyinfo reportMoneyinfo1 = reportMoneyinfoService.countMoneyData( reportMoneyinfo );
		return AjaxResult.success( reportMoneyinfo1 );
	}

	//	@PreAuthorize( "@ss.hasPermi('web:report-moneyinfo:list')" )
	//	@GetMapping( "/storage" )
	//	public AjaxResult storage(ReportMoneyinfo reportMoneyinfo) throws ParseException {
	//		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
	//		String userId = loginUser.getUser().getUserId().toString();
	//		if ( !redisUtil.lock( EnumLock.adminUser, userId, "10", 120 ) ) {
	//			return AjaxResult.error("请勿连续点击搜索，2分钟后再搜索");
	//		}
	//		return AjaxResult.success( reportMoneyinfoService.storage(reportMoneyinfo));
	//	}

	@PreAuthorize( "@ss.hasPermi('web:report-moneyinfo:export')" )
	@Log( title = "平台资金报表导出", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( ReportMoneyinfo reportMoneyinfo, HttpServletResponse response ) throws ParseException {
		List<ReportMoneyinfo> list = reportMoneyinfoService.exportMoneyinfoList( reportMoneyinfo );
		ExportExcelUtil.exportExcel( list, "平台资金报表", "平台资金报表", ReportMoneyinfo.class, response );
	}

}