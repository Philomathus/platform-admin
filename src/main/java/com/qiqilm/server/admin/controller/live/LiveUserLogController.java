package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveUserLog;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveUserLogService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * //帐户资金变动日志Controller
 *
 * @author 77tv
 * @date 2021-01-27
 */
@RestController
@RequestMapping( "/admin/liveUserLog" )
public class LiveUserLogController extends BaseController {
	@Autowired
	private ILiveUserLogService liveUserLogService;

	/**
	 * 查询//帐户资金变动日志列表
	 */
//	@PreAuthorize( "@ss.hasPermi('admin:liveUserLog:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LiveUserLog liveUserLog) {
		startPage();
		List<LiveUserLog> list = liveUserLogService.selectLiveUserLogList(liveUserLog);
		return getDataTable( list );
	}

	/**
	 * 导出//帐户资金变动日志列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUserLog:export')" )
	@Log( title = "帐户资金变动日志", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(LiveUserLog liveUserLog, HttpServletResponse response) {
		List<LiveUserLog>      list = liveUserLogService.selectLiveUserLogList(liveUserLog);
		ExportExcelUtil.exportExcel( list, "帐户资金变动日志", "帐户资金变动日志表", LiveUserLog.class, response );
	}

	/**
	 * 获取//帐户资金变动日志详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUserLog:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( liveUserLogService.selectLiveUserLogById(id) );
	}

	/**
	 * 新增//帐户资金变动日志
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUserLog:add')" )
	@Log( title = "//帐户资金变动日志", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveUserLog liveUserLog) {
		return toAjax( liveUserLogService.insertLiveUserLog(liveUserLog) );
	}

	/**
	 * 修改//帐户资金变动日志
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUserLog:edit')" )
	@Log( title = "//帐户资金变动日志", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveUserLog liveUserLog) {
		return toAjax( liveUserLogService.updateLiveUserLog(liveUserLog) );
	}

	/**
	 * 删除//帐户资金变动日志
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUserLog:remove')" )
	@Log( title = "//帐户资金变动日志", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( liveUserLogService.deleteLiveUserLogByIds( ids ) );
	}
}
