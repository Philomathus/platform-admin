package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.SysOperLog;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ISysOperLogService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 操作日志记录
 *
 * @author 77tv
 */
@RestController
@RequestMapping( "/monitor/operlog" )
public class SysOperlogController extends BaseController {
	@Autowired
	private ISysOperLogService operLogService;

	@PreAuthorize( "@ss.hasPermi('monitor:operlog:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( SysOperLog operLog ) {
		startPage();
		List<SysOperLog> list = operLogService.selectOperLogList( operLog );
		return getDataTable( list );
	}

	@Log( title = "操作日志", businessType = BusinessType.EXPORT )
	@PreAuthorize( "@ss.hasPermi('monitor:operlog:export')" )
	@GetMapping( "/export" )
	public void export( SysOperLog operLog, HttpServletResponse response ) {
		List<SysOperLog> list = operLogService.selectOperLogList( operLog );
		ExportExcelUtil.exportExcel( list, "操作日志", "操作日志表", SysOperLog.class, response );
	}

	@PreAuthorize( "@ss.hasPermi('monitor:operlog:remove')" )
	@DeleteMapping( "/{operIds}" )
	public AjaxResult remove( @PathVariable Long[] operIds ) {
		return toAjax( operLogService.deleteOperLogByIds( operIds ) );
	}

	@Log( title = "操作日志", businessType = BusinessType.CLEAN )
	@PreAuthorize( "@ss.hasPermi('monitor:operlog:remove')" )
	@DeleteMapping( "/clean" )
	public AjaxResult clean() {
		operLogService.cleanOperLog();
		return AjaxResult.success();
	}
}
