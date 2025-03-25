package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.RspBase;
import com.qiqilm.server.admin.domain.ReportAgentcount;
import com.qiqilm.server.admin.domain.rsp.RspMemberAgent;
import com.qiqilm.server.admin.domain.vo.ReportPlamHome;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IReportAgentcountService;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * 首页注册人数
	 *
	 * @return
	 */

	@PostMapping( value = "/record" )
	public RspBase record( HttpServletRequest request, Map map ) {
		String time = DateFormatUtils.formate( new Date(), "yyyy-MM-dd" );
		if ( map.containsKey( "time" ) ) {
			time = map.get( "time" ).toString();
			DateFormatUtils.parse( time, "yyyy-MM-dd" );
		}
		RspBase rspBase = new RspBase();

		//查询出款人数classTwo=106
		List<ReportPlamHome> classTwo106 = reportAgentcountService.findChartsOne( "106", time );

		List<ReportPlamHome> classTwo105 = reportAgentcountService.findChartsOne( "105", time );

		List<ReportPlamHome> classTwo104 = reportAgentcountService.findChartsOne( "104", time );

		List<ReportPlamHome> classTwo103 = reportAgentcountService.findChartsOne( "103", time );

		List<ReportPlamHome> classTwox212 = reportAgentcountService.findChartsOne( "212", time );

		List<ReportPlamHome> classTwox102 = reportAgentcountService.findChartsOne( "102", time );

		List<ReportPlamHome> classTwox211 = reportAgentcountService.findChartsOne( "211", time );

		List<ReportPlamHome> classTwox101 = reportAgentcountService.findChartsOne( "101", time );

		List<ReportPlamHome> classTwox210 = reportAgentcountService.findChartsOne( "210", time );

		List<ReportPlamHome> classTwox110 = reportAgentcountService.findChartsOne( "110", time );

		List<ReportPlamHome> classTwox209 = reportAgentcountService.findChartsOne( "209", time );

		List<ReportPlamHome> classTwox208 = reportAgentcountService.findChartsOne( "208", time );

		List<ReportPlamHome> classTwox109 = reportAgentcountService.findChartsOne( "109", time );

		List<ReportPlamHome> classTwox108 = reportAgentcountService.findChartsOne( "108", time );

		List<ReportPlamHome> classTwox107 = reportAgentcountService.findChartsOne( "107", time );
		Map<String, Object>  resultMap    = new HashMap<>();
		resultMap.put( "x106", classTwo106 );
		resultMap.put( "x105", classTwo105 );
		resultMap.put( "x104", classTwo104 );
		resultMap.put( "x103", classTwo103 );
		resultMap.put( "x212", classTwox212 );
		resultMap.put( "x102", classTwox102 );
		resultMap.put( "x211", classTwox211 );
		resultMap.put( "x101", classTwox101 );
		resultMap.put( "x210", classTwox210 );
		resultMap.put( "x110", classTwox110 );
		resultMap.put( "x209", classTwox209 );
		resultMap.put( "x208", classTwox208 );
		resultMap.put( "x109", classTwox109 );
		resultMap.put( "x108", classTwox108 );
		resultMap.put( "x107", classTwox107 );
		rspBase.setCode( Constants.URC_SUCCESS );
		rspBase.setMsg( "成功" );
		rspBase.setData( resultMap );
		return rspBase;
	}

	/**
	 * 查询代理统计，主要用于代理渠道的统计列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:reportAgentcount:list')" )
	@GetMapping( "/list" )
	public Object list( ReportAgentcount reportAgentcount ) throws Exception {
		return reportAgentcountService.selectReportAgentcountList( reportAgentcount );
	}
	@PreAuthorize( "@ss.hasPermi('admin:reportAgentcount:list')" )
	@GetMapping( "/memberAgentList" )
	public TableDataInfo memberAgentList(ReportAgentcount reportAgentcount) {
		startPage();
		List<RspMemberAgent> list = reportAgentcountService.selectMemberAgent(reportAgentcount);
		return getDataTable( list );
	}
	//	@PreAuthorize( "@ss.hasPermi('admin:reportAgentcount:list')" )
	//	@GetMapping( "/storage" )
	//	public AjaxResult storage( ReportAgentcount reportAgentcount ) throws ParseException {
	//		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
	//		String userId = loginUser.getUser().getUserId().toString();
	//		if ( !redisUtil.lock( EnumLock.adminUser, userId, "10", 120 ) ) {
	//			return AjaxResult.error("请勿连续点击搜索，2分钟后再搜索");
	//		}
	//		return AjaxResult.success( reportAgentcountService.storage( reportAgentcount ) );
	//	}

	@PreAuthorize( "@ss.hasPermi('admin:reportAgentcount:export')" )
	@Log( title = "推广统计报表", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( ReportAgentcount reportAgentcount, HttpServletResponse response ) throws ParseException {
		List<ReportAgentcount> list = reportAgentcountService.exportAgentcountList( reportAgentcount );
		ExportExcelUtil.exportExcel( list, "推广统计报表", "推广统计报表", ReportAgentcount.class, response );
	}

	@PreAuthorize( "@ss.hasPermi('admin:reportAgentcount:list')" )
	@Log( title = "【新增推广码】", businessType = BusinessType.EXPORT )
	@PostMapping( "/add" )
	public AjaxResult add( @RequestBody ReportAgentcount reportAgentcount ) throws ParseException {
		int add = reportAgentcountService.existsPromotionCode( reportAgentcount );
		if ( add > 0 ) {
			return AjaxResult.error( 0, "此推广码已存在,请勿重复新增" );
		}
		reportAgentcount.setAgentcode(reportAgentcount.getCode().trim());
		reportAgentcountService.addPromotionCode( reportAgentcount );
		return AjaxResult.success( "新增成功" );
	}

	@PreAuthorize( "@ss.hasPermi('admin:reportAgentcount:list')" )
	@Log( title = "【删除推广码】", businessType = BusinessType.EXPORT )
	@DeleteMapping( "/del" )
	public AjaxResult del( @RequestBody ReportAgentcount reportAgentcount ) throws ParseException {
		int del = reportAgentcountService.existsPromotionCode( reportAgentcount );
		if ( del == 0 ) {
			return AjaxResult.error( 0, "此推广码不存在,不需要删除" );
		}
		reportAgentcountService.delPromotionCode( reportAgentcount );
		return AjaxResult.success( "删除成功" );
	}

	@PreAuthorize( "@ss.hasPermi('admin:reportAgentcount:generatedata')" )
	@GetMapping( "/generatedata" )
	public AjaxResult generatedata(ReportAgentcount reportAgentcount ) throws ParseException {
		return reportAgentcountService.plamagent_data(reportAgentcount);
	}

}
