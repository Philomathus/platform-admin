package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ReportIncomeDay;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IReportIncomeDayService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 平台充值报表Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/admin/reportIncomeDay" )
public class ReportIncomeDayController extends BaseController {
	@Autowired
	private IReportIncomeDayService reportIncomeDayService;

	//获取昨天数据
	private static String getYestoday() {
		Calendar cal = Calendar.getInstance();
		cal.add( Calendar.DATE, -1 );
		Date time = cal.getTime();
		return new SimpleDateFormat( "yyyy-MM-dd" ).format( time );
	}

	/**
	 * 查询平台充值报表列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:reportIncomeDay:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( ReportIncomeDay reportIncomeDay ) throws ParseException {
		startPage();
		Date   d        = new Date();
		String myString = reportIncomeDay.getPaydate();
		if ( !StringUtils.isEmpty( myString ) ) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
			Date             dd               = simpleDateFormat.parse( myString );
			boolean          flag             = dd.before( d );
			if ( !flag ) {
				reportIncomeDay.setPaydate( null );
			}
		} else {
			reportIncomeDay.setPaydate( getYestoday() );
		}
		List<ReportIncomeDay> list = reportIncomeDayService.selectReportIncomeDayList( reportIncomeDay );
		return getDataTable( list );
	}

	@GetMapping( value = "/count" )
	public AjaxResult countMoneyData( ReportIncomeDay reportIncomeDay ) {
		String myString = reportIncomeDay.getPaydate();
		if ( StringUtils.isEmpty( myString ) ) {
			reportIncomeDay.setPaydate( getYestoday() );
		}
		ReportIncomeDay reportIncomeDay1 = reportIncomeDayService.countSuccessData( reportIncomeDay );
		return AjaxResult.success( reportIncomeDay1 );
	}

	@PreAuthorize( "@ss.hasPermi('admin:reportIncomeDay:export')" )
	@Log( title = "平台充值报表", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( ReportIncomeDay reportIncomeDay, HttpServletResponse response ) {
		List<ReportIncomeDay> list = reportIncomeDayService.selectReportIncomeDayList( reportIncomeDay );
		ExportExcelUtil.exportExcel( list, "平台充值报表", "平台充值报表", ReportIncomeDay.class, response );
	}
}