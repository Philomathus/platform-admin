package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveProplog;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveProplogService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户送礼日志Controller
 *
 * @author 77tv
 * @date 2021-01-29
 */
@RestController
@RequestMapping( "/admin/liveProplog" )
public class LiveProplogController extends BaseController {
	@Autowired
	private ILiveProplogService liveProplogService;

	/**
	 * 查询用户送礼日志列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveProplog:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( LiveProplog liveProplog ) {
		startPage();
		List<LiveProplog> list = liveProplogService.selectLiveProplogList( liveProplog );
		return getDataTable( list );
	}

	/**
	 * 导出用户送礼日志列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveProplog:export')" )
	@Log( title = "用户送礼日志", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( LiveProplog liveProplog, HttpServletResponse response ) {
		List<LiveProplog> list = liveProplogService.selectLiveProplogList( liveProplog );
		ExportExcelUtil.exportExcel( list, "用户送礼日志", "用户送礼日志表", LiveProplog.class, response );
	}

	/**
	 * 获取用户送礼日志详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveProplog:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		return AjaxResult.success( liveProplogService.selectLiveProplogById( id ) );
	}

	/**
	 * 新增用户送礼日志
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveProplog:add')" )
	@Log( title = "用户送礼日志", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveProplog liveProplog ) {
		return toAjax( liveProplogService.insertLiveProplog( liveProplog ) );
	}

	/**
	 * 修改用户送礼日志
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveProplog:edit')" )
	@Log( title = "用户送礼日志", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveProplog liveProplog ) {
		return toAjax( liveProplogService.updateLiveProplog( liveProplog ) );
	}

	/**
	 * 删除用户送礼日志
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveProplog:remove')" )
	@Log( title = "用户送礼日志", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( liveProplogService.deleteLiveProplogByIds( ids ) );
	}

	@PreAuthorize( "@ss.hasPermi('admin:liveProplog:list')" )
	@GetMapping( "/getCount" )
	public AjaxResult getCount( LiveProplog liveProplog ) {
		return liveProplogService.getCount( liveProplog );
	}
}
