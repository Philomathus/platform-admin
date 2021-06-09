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
import com.qiqilm.server.admin.domain.ActivityCashBack;
import com.qiqilm.server.admin.service.IActivityCashBackService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-06-07
 */
@RestController
@RequestMapping( "/admin/activityCashBack" )
public class ActivityCashBackController extends BaseController {
	@Autowired
	private IActivityCashBackService activityCashBackService;

	/**
	 * 查询【返现活动】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityCashBack:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(ActivityCashBack activityCashBack) {
		startPage();
		List<ActivityCashBack> list = activityCashBackService.selectActivityCashBackList(activityCashBack);
		return getDataTable( list );
	}
    
	/**
	 * 导出【返现活动】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityCashBack:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(ActivityCashBack activityCashBack, HttpServletResponse response) {
		List<ActivityCashBack>      list = activityCashBackService.selectActivityCashBackList(activityCashBack);
		ExportExcelUtil.exportExcel( list, "【请填写功能名称】", "【请填写功能名称】表", ActivityCashBack.class, response );
	}

	/**
	 * 获取【返现活动】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityCashBack:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( activityCashBackService.selectActivityCashBackById(id) );
	}

	/**
	 * 新增【返现活动】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityCashBack:add')" )
	@Log( title = "【返现活动】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ActivityCashBack activityCashBack) {
		return toAjax( activityCashBackService.insertActivityCashBack(activityCashBack) );
	}

	/**
	 * 修改【返现活动】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityCashBack:edit')" )
	@Log( title = "【返现活动】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ActivityCashBack activityCashBack) {
		return toAjax( activityCashBackService.updateActivityCashBack(activityCashBack) );
	}

	/**
	 * 删除【返现活动】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityCashBack:remove')" )
	@Log( title = "【返现活动】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( activityCashBackService.deleteActivityCashBackByIds( ids ) );
	}
}
