package com.qiqilm.server.admin.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.qiqilm.server.admin.domain.ReportMoneyinfo;
import com.qiqilm.server.admin.domain.ReportPlamCom;
import com.qiqilm.server.admin.domain.ReportPlamGames;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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
import com.qiqilm.server.admin.domain.ReportIncomeDay;
import com.qiqilm.server.admin.service.IReportIncomeDayService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/admin/reportIncomeDay" )
public class ReportIncomeDayController extends BaseController {
	@Autowired
	private IReportIncomeDayService reportIncomeDayService;

	/**
	 * 查询【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:reportIncomeDay:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(ReportIncomeDay reportIncomeDay) throws ParseException {
		startPage();
		Date d = new Date();
		String myString = reportIncomeDay.getPaydate();
		if (!StringUtils.isEmpty(myString)){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date dd = simpleDateFormat.parse(myString);
			boolean flag = dd.before(d);
			if(!flag){
				reportIncomeDay.setPaydate(null);
			}
		}else{
			reportIncomeDay.setPaydate(getYestoday());
		}
		List<ReportIncomeDay> list = reportIncomeDayService.selectReportIncomeDayList(reportIncomeDay);
		return getDataTable( list );
	}
	@GetMapping( value = "/count" )
	public AjaxResult countMoneyData(ReportIncomeDay reportIncomeDay) {
		String myString = reportIncomeDay.getPaydate();
		if (StringUtils.isEmpty(myString)){
			reportIncomeDay.setPaydate(getYestoday());
		}
		ReportIncomeDay reportIncomeDay1 = reportIncomeDayService.countSuccessData(reportIncomeDay);
		return AjaxResult.success(reportIncomeDay1);
	}
	//获取昨天数据
	private static String getYestoday(){
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE,-1);
		Date time=cal.getTime();
		return new SimpleDateFormat("yyyy-MM-dd").format(time);
	}

	@PreAuthorize( "@ss.hasPermi('admin:reportIncomeDay:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(ReportIncomeDay reportIncomeDay) {
		List<ReportIncomeDay> list = reportIncomeDayService.selectReportIncomeDayList(reportIncomeDay);
		ExcelUtil<ReportIncomeDay> util = new ExcelUtil<>(ReportIncomeDay.class);
		return util.exportExcel( list, "reportIncomeDay" );
	}
}