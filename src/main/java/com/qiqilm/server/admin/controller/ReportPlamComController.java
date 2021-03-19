package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.domain.ReportPlamCom;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IReportPlamComService;
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
 * 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping( "/admin/report-plam-com" )
@Log4j2
public class ReportPlamComController extends BaseController {
	@Autowired
	private IReportPlamComService reportPlamComService;

	/**
	 * 查询综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:report-plam-com:list')" )
	@GetMapping( "/list" )
	public Object list( ReportPlamCom reportPlamCom ) throws ParseException {
		return reportPlamComService.selectReportPlamComList( reportPlamCom );
	}

	/**
	 * 导出综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:report-plam-com:export')" )
	@Log( title = "综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( ReportPlamCom reportPlamCom, HttpServletResponse response ) {
		List<ReportPlamCom> list = reportPlamComService.exportPlamComList( reportPlamCom );
		ExportExcelUtil.exportExcel( list, "综合数据报表", "综合数据报表", ReportPlamCom.class, response );
	}

	//	@PreAuthorize( "@ss.hasPermi('admin:report-plam-com:list')" )
	//	@GetMapping( "/storage" )
	//	public AjaxResult storage(ReportPlamCom reportPlamCom) throws ParseException {
	//		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
	//		String userId = loginUser.getUser().getUserId().toString();
	//		if ( !redisUtil.lock( EnumLock.adminUser, userId, "10", 120 ) ) {
	//			return AjaxResult.error("请勿连续点击搜索，2分钟后再搜索");
	//		}
	//		return AjaxResult.success( reportPlamComService.storage(reportPlamCom));
	//	}
}