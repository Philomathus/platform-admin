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
import com.qiqilm.server.admin.domain.HomeBanner;
import com.qiqilm.server.admin.service.IHomeBannerService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 首页轮播图Controller
 *
 * @author 77tv
 * @date 2021-02-07
 */
@RestController
@RequestMapping( "/activity/homeBanner" )
public class HomeBannerController extends BaseController {
	@Autowired
	private IHomeBannerService homeBannerService;

	/**
	 * 查询首页轮播图列表
	 */
	@PreAuthorize( "@ss.hasPermi('activity:homeBanner:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(HomeBanner homeBanner) {
		startPage();
		List<HomeBanner> list = homeBannerService.selectHomeBannerList(homeBanner);
		return getDataTable( list );
	}
    
	/**
	 * 导出首页轮播图列表
	 */
	@PreAuthorize( "@ss.hasPermi('activity:homeBanner:export')" )
	@Log( title = "首页轮播图", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(HomeBanner homeBanner) {
		List<HomeBanner>      list = homeBannerService.selectHomeBannerList(homeBanner);
		ExcelUtil<HomeBanner> util = new ExcelUtil<>(HomeBanner.class);
		return util.exportExcel( list, "homeBanner" );
	}

	/**
	 * 获取首页轮播图详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('activity:homeBanner:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( homeBannerService.selectHomeBannerById(id) );
	}

	/**
	 * 新增首页轮播图
	 */
	@PreAuthorize( "@ss.hasPermi('activity:homeBanner:add')" )
	@Log( title = "首页轮播图", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody HomeBanner homeBanner) {
		return toAjax( homeBannerService.insertHomeBanner(homeBanner) );
	}

	/**
	 * 修改首页轮播图
	 */
	@PreAuthorize( "@ss.hasPermi('activity:homeBanner:edit')" )
	@Log( title = "首页轮播图", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody HomeBanner homeBanner) {
		return toAjax( homeBannerService.updateHomeBanner(homeBanner) );
	}

	/**
	 * 删除首页轮播图
	 */
	@PreAuthorize( "@ss.hasPermi('activity:homeBanner:remove')" )
	@Log( title = "首页轮播图", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( homeBannerService.deleteHomeBannerByIds( ids ) );
	}
}
