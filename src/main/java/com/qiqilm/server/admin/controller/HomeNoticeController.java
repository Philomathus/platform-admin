package com.qiqilm.server.admin.controller;

import java.util.List;

import com.qiqilm.server.admin.utils.ExportExcelUtil;
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
import com.qiqilm.server.admin.domain.HomeNotice;
import com.qiqilm.server.admin.service.IHomeNoticeService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 系统公告Controller
 *
 * @author 77tv
 * @date 2021-02-07
 */
@RestController
@RequestMapping( "/activity/homeNotice" )
public class HomeNoticeController extends BaseController {
	@Autowired
	private IHomeNoticeService homeNoticeService;

	/**
	 * 查询系统公告列表
	 */
	@PreAuthorize( "@ss.hasPermi('activity:homeNotice:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(HomeNotice homeNotice) {
		startPage();
		List<HomeNotice> list = homeNoticeService.selectHomeNoticeList(homeNotice);
		return getDataTable( list );
	}
    
	/**
	 * 导出系统公告列表
	 */
	@PreAuthorize( "@ss.hasPermi('activity:homeNotice:export')" )
	@Log( title = "系统公告", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(HomeNotice homeNotice, HttpServletResponse response) {
		List<HomeNotice>      list = homeNoticeService.selectHomeNoticeList(homeNotice);
		ExportExcelUtil.exportExcel( list, "系统公告", "系统公告表", HomeNotice.class, response );
	}

	/**
	 * 获取系统公告详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('activity:homeNotice:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( homeNoticeService.selectHomeNoticeById(id) );
	}

	/**
	 * 新增系统公告
	 */
	@PreAuthorize( "@ss.hasPermi('activity:homeNotice:add')" )
	@Log( title = "系统公告", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody HomeNotice homeNotice) {
		return toAjax( homeNoticeService.insertHomeNotice(homeNotice) );
	}

	/**
	 * 修改系统公告
	 */
	@PreAuthorize( "@ss.hasPermi('activity:homeNotice:edit')" )
	@Log( title = "系统公告", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody HomeNotice homeNotice) {
		return toAjax( homeNoticeService.updateHomeNotice(homeNotice) );
	}

	/**
	 * 删除系统公告
	 */
	@PreAuthorize( "@ss.hasPermi('activity:homeNotice:remove')" )
	@Log( title = "系统公告", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( homeNoticeService.deleteHomeNoticeByIds( ids ) );
	}
}
