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
import com.qiqilm.server.admin.domain.LiveMsgEngage;
import com.qiqilm.server.admin.service.ILiveMsgEngageService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 主播互动信息Controller
 *
 * @author 77tv
 * @date 2021-03-22
 */
@RestController
@RequestMapping( "/admin/liveMsgEngage" )
public class LiveMsgEngageController extends BaseController {
	@Autowired
	private ILiveMsgEngageService liveMsgEngageService;

	/**
	 * 查询
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveMsgEngage:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LiveMsgEngage liveMsgEngage) {
		startPage();
		List<LiveMsgEngage> list = liveMsgEngageService.selectLiveMsgEngageList(liveMsgEngage);
		return getDataTable( list );
	}
    
	/**
	 *
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveMsgEngage:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(LiveMsgEngage liveMsgEngage, HttpServletResponse response) {
		List<LiveMsgEngage>      list = liveMsgEngageService.selectLiveMsgEngageList(liveMsgEngage);
		ExportExcelUtil.exportExcel( list, "【请填写功能名称】", "【请填写功能名称】表", LiveMsgEngage.class, response );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveMsgEngage:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Integer id) {
		return AjaxResult.success( liveMsgEngageService.selectLiveMsgEngageById(id) );
	}

	/**
	 * 新增
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveMsgEngage:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveMsgEngage liveMsgEngage) {
		AjaxResult ajaxResult=liveMsgEngageService.insertLiveMsgEngage(liveMsgEngage);
		return ajaxResult;
	}

	/**
	 * 修改
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveMsgEngage:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveMsgEngage liveMsgEngage) {
		AjaxResult ajaxResult=liveMsgEngageService.updateLiveMsgEngage(liveMsgEngage);
		return ajaxResult;
	}

	/**
	 * 删除
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveMsgEngage:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Integer[] ids ) {
		return toAjax( liveMsgEngageService.deleteLiveMsgEngageByIds( ids ) );
	}
}