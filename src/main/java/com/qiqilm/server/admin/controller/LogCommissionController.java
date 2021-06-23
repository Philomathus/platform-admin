package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LogCommission;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILogCommissionService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 佣金领取日志Controller
 *
 * @author 77tv
 * @date 2021-01-27
 */
@RestController
@RequestMapping( "/admin/logCommission" )
public class LogCommissionController extends BaseController {
	@Autowired
	private ILogCommissionService logCommissionService;

	/**
	 * 查询佣金领取日志列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:logCommission:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( LogCommission logCommission ) {
		startPage();
		List<LogCommission> list = logCommissionService.selectLogCommissionList( logCommission );
		return getDataTable( list );
	}

	/**
	 * 导出佣金领取日志列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:logCommission:export')" )
	@Log( title = "佣金领取日志", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( LogCommission logCommission, HttpServletResponse response ) {
		List<LogCommission> list = logCommissionService.selectLogCommissionList( logCommission );
		ExportExcelUtil.exportExcel( list, "佣金领取日志", "佣金领取日志表", LogCommission.class, response );
	}

	/**
	 * 获取佣金领取日志详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:logCommission:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id ) {
		return AjaxResult.success( logCommissionService.selectLogCommissionById( id ) );
	}

	/**
	 * 新增佣金领取日志
	 */
	@PreAuthorize( "@ss.hasPermi('admin:logCommission:add')" )
	@Log( title = "佣金领取日志", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LogCommission logCommission ) {
		logCommission.setCreateTime( new Date() );
		logCommission.setId( UuidUtil.getRandomUuidWithoutSeparator() );
		return toAjax( logCommissionService.insertLogCommission( logCommission ) );
	}

	/**
	 * 修改佣金领取日志
	 */
	@PreAuthorize( "@ss.hasPermi('admin:logCommission:edit')" )
	@Log( title = "佣金领取日志", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LogCommission logCommission ) {
		return toAjax( logCommissionService.updateLogCommission( logCommission ) );
	}

	/**
	 * 删除佣金领取日志
	 */
	@PreAuthorize( "@ss.hasPermi('admin:logCommission:remove')" )
	@Log( title = "佣金领取日志", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( logCommissionService.deleteLogCommissionByIds( ids ) );
	}
}
