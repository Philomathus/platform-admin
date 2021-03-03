package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.ReportMoneyinfo;
import com.qiqilm.server.admin.domain.ReportPlamCom;
import com.qiqilm.server.admin.domain.ReportPlamGames;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.IReportMoneyinfoService;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.ServletUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private TokenService tokenService;
	/**
	 * 查询平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额列表
	 */
	@PreAuthorize( "@ss.hasPermi('web:report-moneyinfo:list')" )
	@GetMapping( "/list" )
	public Object list(ReportMoneyinfo reportMoneyinfo) throws ParseException {
		reportMoneyinfoService.storage(reportMoneyinfo);

		String keyVal = redisUtil.strGet( "admin-reportMoneyInfo" );
		if("0".equals( keyVal )){
			return AjaxResult.error("报表正在生成，请稍后...");
		}
		startPage();
		List<ReportMoneyinfo> list = reportMoneyinfoService.selectReportMoneyinfoList(reportMoneyinfo);
		return getDataTable( list );
	}
	@GetMapping( value = "/count" )
	public AjaxResult countMoneyData(ReportMoneyinfo reportMoneyinfo) throws ParseException {
		ReportMoneyinfo reportMoneyinfo1 = reportMoneyinfoService.countMoneyData(reportMoneyinfo);
		return AjaxResult.success(reportMoneyinfo1);
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
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(ReportMoneyinfo reportMoneyinfo) throws ParseException {
		List<ReportMoneyinfo> list = reportMoneyinfoService.selectReportMoneyinfoList(reportMoneyinfo);
		ExcelUtil<ReportMoneyinfo> util = new ExcelUtil<>(ReportMoneyinfo.class);
		return util.exportExcel( list, "reportMoneyinfo" );
	}

}