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
import com.qiqilm.server.admin.domain.ChatComplaint;
import com.qiqilm.server.admin.service.IChatComplaintService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 客服投诉Controller
 *
 * @author 77tv
 * @date 2021-09-10
 */
@RestController
@RequestMapping( "/admin/chatComplaint" )
public class ChatComplaintController extends BaseController {
	@Autowired
	private IChatComplaintService chatComplaintService;

	/**
	 * 查询客服投诉列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:chatComplaint:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(ChatComplaint chatComplaint) {
		startPage();
		List<ChatComplaint> list = chatComplaintService.selectChatComplaintList(chatComplaint);
		return getDataTable( list );
	}
    
	/**
	 * 导出客服投诉列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:chatComplaint:export')" )
	@Log( title = "客服投诉", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(ChatComplaint chatComplaint, HttpServletResponse response) {
		List<ChatComplaint>      list = chatComplaintService.selectChatComplaintList(chatComplaint);
		ExportExcelUtil.exportExcel( list, "客服投诉", "客服投诉表", ChatComplaint.class, response );
	}

	/**
	 * 获取客服投诉详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:chatComplaint:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( chatComplaintService.selectChatComplaintById(id) );
	}

	/**
	 * 新增客服投诉
	 */
	@PreAuthorize( "@ss.hasPermi('admin:chatComplaint:add')" )
	@Log( title = "客服投诉", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ChatComplaint chatComplaint) {
		return toAjax( chatComplaintService.insertChatComplaint(chatComplaint) );
	}

	/**
	 * 修改客服投诉
	 */
	@PreAuthorize( "@ss.hasPermi('admin:chatComplaint:edit')" )
	@Log( title = "客服投诉", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ChatComplaint chatComplaint) {
		return toAjax( chatComplaintService.updateChatComplaint(chatComplaint) );
	}

	/**
	 * 删除客服投诉
	 */
	@PreAuthorize( "@ss.hasPermi('admin:chatComplaint:remove')" )
	@Log( title = "客服投诉", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( chatComplaintService.deleteChatComplaintByIds( ids ) );
	}
}
