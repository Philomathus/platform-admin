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
import com.qiqilm.server.admin.domain.LiveComplaint;
import com.qiqilm.server.admin.service.ILiveComplaintService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 主播投诉记录Controller
 *
 * @author 77tv
 * @date 2021-08-14
 */
@RestController
@RequestMapping( "/admin/liveComplaint" )
public class LiveComplaintController extends BaseController {
	@Autowired
	private ILiveComplaintService liveComplaintService;

	/**
	 * 查询主播投诉记录列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveComplaint:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list(LiveComplaint liveComplaint) {
	startPage();
	List<LiveComplaint> list = liveComplaintService.selectLiveComplaintList(liveComplaint);
	return getDataTable( list );
	}
    
	/**
	 * 导出主播投诉记录列表
	 */
//	@PreAuthorize( "@ss.hasPermi('admin:liveComplaint:export')" )
//	@Log( title = "主播投诉记录", businessType = BusinessType.EXPORT )
//	@GetMapping( "/export" )
//	public void export(LiveComplaint liveComplaint, HttpServletResponse response) {
//		List<LiveComplaint>      list = liveComplaintService.selectLiveComplaintList(liveComplaint);
//		ExportExcelUtil.exportExcel( list, "主播投诉记录", "主播投诉记录表", LiveComplaint.class, response );
//	}

//	/**
//	 * 获取主播投诉记录详细信息
//	 */
//	@PreAuthorize( "@ss.hasPermi('admin:liveComplaint:query')" )
//	@GetMapping( value = "/{id}" )
//	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
//		return AjaxResult.success( liveComplaintService.selectLiveComplaintById(id) );
//	}

//	/**
//	 * 新增主播投诉记录
//	 */
//	@PreAuthorize( "@ss.hasPermi('admin:liveComplaint:add')" )
//	@Log( title = "主播投诉记录", businessType = BusinessType.INSERT )
//	@PostMapping
//	public AjaxResult add( @RequestBody LiveComplaint liveComplaint) {
//		return toAjax( liveComplaintService.insertLiveComplaint(liveComplaint) );
//	}
//
//	/**
//	 * 修改主播投诉记录
//	 */
//	@PreAuthorize( "@ss.hasPermi('admin:liveComplaint:edit')" )
//	@Log( title = "主播投诉记录", businessType = BusinessType.UPDATE )
//	@PutMapping
//	public AjaxResult edit( @RequestBody LiveComplaint liveComplaint) {
//		return toAjax( liveComplaintService.updateLiveComplaint(liveComplaint) );
//	}
//
//	/**
//	 * 删除主播投诉记录
//	 */
//	@PreAuthorize( "@ss.hasPermi('admin:liveComplaint:remove')" )
//	@Log( title = "主播投诉记录", businessType = BusinessType.DELETE )
//	@DeleteMapping( "/{ids}" )
//	public AjaxResult remove( @PathVariable Long[] ids ) {
//		return toAjax( liveComplaintService.deleteLiveComplaintByIds( ids ) );
//	}
}
