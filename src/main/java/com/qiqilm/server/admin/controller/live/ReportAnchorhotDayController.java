package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ReportAnchorhotDay;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IReportAnchorhotDayService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 贡献榜Controller
 *
 * @author 77tv
 * @date 2021-01-28
 */
@RestController
@RequestMapping( "/admin/reportAnchorhotDay" )
public class ReportAnchorhotDayController extends BaseController {
	@Autowired
	private IReportAnchorhotDayService reportAnchorhotDayService;

	/**
	 * 查询贡献榜列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:reportAnchorhotDay:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(ReportAnchorhotDay reportAnchorhotDay) {
		startPage();
		List<ReportAnchorhotDay> list = reportAnchorhotDayService.selectReportAnchorhotDayList(reportAnchorhotDay);
		return getDataTable( list );
	}
    
	/**
	 * 导出贡献榜列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:reportAnchorhotDay:export')" )
	@Log( title = "贡献榜", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(ReportAnchorhotDay reportAnchorhotDay, HttpServletResponse response) {
		List<ReportAnchorhotDay>      list = reportAnchorhotDayService.selectReportAnchorhotDayList(reportAnchorhotDay);
		ExportExcelUtil.exportExcel( list, "贡献榜", "贡献榜表", ReportAnchorhotDay.class, response );
	}

	/**
	 * 获取贡献榜详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:reportAnchorhotDay:query')" )
	@GetMapping( value = "/{repId}" )
	public AjaxResult getInfo( @PathVariable( "repId" ) String repId) {
		return AjaxResult.success( reportAnchorhotDayService.selectReportAnchorhotDayById(repId) );
	}

	/**
	 * 新增贡献榜
	 */
	@PreAuthorize( "@ss.hasPermi('admin:reportAnchorhotDay:add')" )
	@Log( title = "贡献榜", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ReportAnchorhotDay reportAnchorhotDay) {
		return toAjax( reportAnchorhotDayService.insertReportAnchorhotDay(reportAnchorhotDay) );
	}

	/**
	 * 修改贡献榜
	 */
	@PreAuthorize( "@ss.hasPermi('admin:reportAnchorhotDay:edit')" )
	@Log( title = "贡献榜", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ReportAnchorhotDay reportAnchorhotDay) {
		return toAjax( reportAnchorhotDayService.updateReportAnchorhotDay(reportAnchorhotDay) );
	}

	/**
	 * 删除贡献榜
	 */
	@PreAuthorize( "@ss.hasPermi('admin:reportAnchorhotDay:remove')" )
	@Log( title = "贡献榜", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{repIds}" )
	public AjaxResult remove( @PathVariable String[] repIds ) {
		return toAjax( reportAnchorhotDayService.deleteReportAnchorhotDayByIds( repIds ) );
	}
}
