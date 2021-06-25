package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveFamily;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveFamilyService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 家族Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping( "/admin/liveFamily" )
public class LiveFamilyController extends BaseController {
	@Autowired
	private ILiveFamilyService liveFamilyService;

	/**
	 * 查询家族列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveFamily:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( LiveFamily liveFamily ) {
		startPage();
		List<LiveFamily> list = liveFamilyService.selectLiveFamilyList( liveFamily );
		return getDataTable( list );
	}

	/**
	 * 导出家族列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveFamily:export')" )
	@Log( title = "导出家族列表", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( LiveFamily liveFamily, HttpServletResponse response ) {
		List<LiveFamily> list = liveFamilyService.selectLiveFamilyList( liveFamily );
		ExportExcelUtil.exportExcel( list, "家族列表", "家族列表表", LiveFamily.class, response );
	}

	/**
	 * 获取家族详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveFamily:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		return AjaxResult.success( liveFamilyService.selectLiveFamilyById( id ) );
	}

	/**
	 * 新增家族
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveFamily:add')" )
	@Log( title = "新增家族", businessType = BusinessType.INSERT )
	@PostMapping
	public Object add( @RequestBody LiveFamily liveFamily ) {
		return AjaxResult.success( liveFamilyService.insertLiveFamily( liveFamily ) );
	}

	/**
	 * 修改家族
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveFamily:edit')" )
	@Log( title = "修改家族", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveFamily liveFamily ) {
		return AjaxResult.success( liveFamilyService.updateLiveFamily( liveFamily ) );
	}

	/**
	 * 删除家族
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveFamily:remove')" )
	@Log( title = "删除家族", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{id}" )
	public AjaxResult remove( @PathVariable Long id ) {
		return toAjax( liveFamilyService.deleteLiveFamilyById( id ) );
	}
}
