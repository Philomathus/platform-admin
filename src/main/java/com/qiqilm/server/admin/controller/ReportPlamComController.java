package com.qiqilm.server.admin.controller;

import java.util.List;

import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.domain.ReportPlamCom;
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
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IReportPlamComService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping( "/admin/report-plam-com" )
public class ReportPlamComController extends BaseController {
	@Autowired
	private IReportPlamComService reportPlamComService;

	/**
	 * 查询综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:report-plam-com:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list(ReportPlamCom reportPlamCom) {
		startPage();
		List<ReportPlamCom> list = reportPlamComService.selectReportPlamComList(reportPlamCom);
		return getDataTable( list );
	}

	/**
	 * 导出综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:report-plam-com:export')" )
	@Log( title = "综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(ReportPlamCom reportPlamCom) {
		List<ReportPlamCom>      list = reportPlamComService.selectReportPlamComList(reportPlamCom);
		ExcelUtil<ReportPlamCom> util = new ExcelUtil<ReportPlamCom>(ReportPlamCom. class);
		return util.exportExcel( list, "report-plam-com" );
	}

	/**
	 * 获取综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:report-plam-com:query')" )
	@GetMapping( value = "/{repId}" )
	public AjaxResult getInfo( @PathVariable( "repId" ) String repId) {
		return AjaxResult.success( reportPlamComService.selectReportPlamComById(repId) );
	}

	/**
	 * 新增综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
	 */
	@PreAuthorize( "@ss.hasPermi('admin:report-plam-com:add')" )
	@Log( title = "综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ReportPlamCom reportPlamCom) {
		return toAjax( reportPlamComService.insertReportPlamCom(reportPlamCom) );
	}

	/**
	 * 修改综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
	 */
	@PreAuthorize( "@ss.hasPermi('admin:report-plam-com:edit')" )
	@Log( title = "综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ReportPlamCom reportPlamCom) {
		return toAjax( reportPlamComService.updateReportPlamCom(reportPlamCom) );
	}

	/**
	 * 删除综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
	 */
	@PreAuthorize( "@ss.hasPermi('admin:report-plam-com:remove')" )
	@Log( title = "综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{repIds}" )
	public AjaxResult remove( @PathVariable String[] repIds ) {
		return toAjax( reportPlamComService.deleteReportPlamComByIds( repIds ) );
	}
}