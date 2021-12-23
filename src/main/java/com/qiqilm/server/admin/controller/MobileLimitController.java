package com.qiqilm.server.admin.controller;

import java.util.List;

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
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.domain.MobileLimit;
import com.qiqilm.server.admin.service.IMobileLimitService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 手机号限制Controller
 *
 * @author 77tv
 * @date 2021-12-09
 */
@RestController
@RequestMapping( "/admin/mobileLimit" )
public class MobileLimitController extends BaseController {
	@Autowired
	private IMobileLimitService mobileLimitService;

	/**
	 * 查询手机号限制列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:mobileLimit:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(MobileLimit mobileLimit) {
		startPage();
		List<MobileLimit> list = mobileLimitService.selectMobileLimitList(mobileLimit);
		return getDataTable( list );
	}
    
	/**
	 * 导出手机号限制列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:mobileLimit:export')" )
	@Log( title = "手机号限制", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(MobileLimit mobileLimit, HttpServletResponse response) {
		List<MobileLimit>      list = mobileLimitService.selectMobileLimitList(mobileLimit);
		ExportExcelUtil.exportExcel( list, "手机号限制", "手机号限制表", MobileLimit.class, response );
	}

	/**
	 * 获取手机号限制详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:mobileLimit:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( mobileLimitService.selectMobileLimitById(id) );
	}

	/**
	 * 新增手机号限制
	 */
	@PreAuthorize( "@ss.hasPermi('admin:mobileLimit:add')" )
	@Log( title = "手机号限制", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody MobileLimit mobileLimit) {
		return toAjax( mobileLimitService.insertMobileLimit(mobileLimit) );
	}

	/**
	 * 修改手机号限制
	 */
	@PreAuthorize( "@ss.hasPermi('admin:mobileLimit:edit')" )
	@Log( title = "手机号限制", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody MobileLimit mobileLimit) {
		return toAjax( mobileLimitService.updateMobileLimit(mobileLimit) );
	}

	/**
	 * 删除手机号限制
	 */
	@PreAuthorize( "@ss.hasPermi('admin:mobileLimit:remove')" )
	@Log( title = "手机号限制", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( mobileLimitService.deleteMobileLimitByIds( ids ) );
	}
}
