package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.SysLogininfor;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ISysLogininforService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 系统访问记录
 *
 * @author 77tv
 */
@RestController
@RequestMapping( "/monitor/logininfor" )
public class SysLogininforController extends BaseController {
	@Autowired
	private ISysLogininforService logininforService;

	@PreAuthorize( "@ss.hasPermi('monitor:logininfor:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( SysLogininfor logininfor ) {
		startPage();
		List<SysLogininfor> list = logininforService.selectLogininforList( logininfor );
		return getDataTable( list );
	}

	@Log( title = "登录日志", businessType = BusinessType.EXPORT )
	@PreAuthorize( "@ss.hasPermi('monitor:logininfor:export')" )
	@GetMapping( "/export" )
	public void export( SysLogininfor logininfor, HttpServletResponse response ) {
		List<SysLogininfor> list = logininforService.selectLogininforList( logininfor );
		ExportExcelUtil.exportExcel( list, "登录日志", "登录日志表", SysLogininfor.class, response );
	}

//	@PreAuthorize( "@ss.hasPermi('monitor:logininfor:remove')" )
//	@Log( title = "登录日志", businessType = BusinessType.DELETE )
//	@DeleteMapping( "/{infoIds}" )
//	public AjaxResult remove( @PathVariable Long[] infoIds ) {
//		return toAjax( logininforService.deleteLogininforByIds( infoIds ) );
//	}
//
//	@PreAuthorize( "@ss.hasPermi('monitor:logininfor:remove')" )
//	@Log( title = "登录日志", businessType = BusinessType.CLEAN )
//	@DeleteMapping( "/clean" )
//	public AjaxResult clean() {
//		logininforService.cleanLogininfor();
//		return AjaxResult.success();
//	}
}
