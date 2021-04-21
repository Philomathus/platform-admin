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
import com.qiqilm.server.admin.domain.SpeakIpBlackList;
import com.qiqilm.server.admin.service.ISpeakIpBlackListService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-02-22
 */
@RestController
@RequestMapping( "/admin/speakIpBlackList" )
public class SpeakIpBlackListController extends BaseController {
	@Autowired
	private ISpeakIpBlackListService speakIpBlackListService;

	/**
	 * 查询【请填写功能名称】列表
	 */
	@GetMapping( "/list" )
    	public TableDataInfo list(SpeakIpBlackList speakIpBlackList) {
		startPage();
		List<SpeakIpBlackList> list = speakIpBlackListService.selectSpeakIpBlackListList(speakIpBlackList);
		return getDataTable( list );
	}
    
	/**
	 * 导出【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:speakIpBlackList:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(SpeakIpBlackList speakIpBlackList, HttpServletResponse response) {
		List<SpeakIpBlackList>      list = speakIpBlackListService.selectSpeakIpBlackListList(speakIpBlackList);
		ExportExcelUtil.exportExcel( list, "禁言IP", "禁言IP表", SpeakIpBlackList.class, response );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:speakIpBlackList:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( speakIpBlackListService.selectSpeakIpBlackListById(id) );
	}

	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:speakIpBlackList:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody SpeakIpBlackList speakIpBlackList) {
		return toAjax( speakIpBlackListService.insertSpeakIpBlackList(speakIpBlackList) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody SpeakIpBlackList speakIpBlackList) {
		return toAjax( speakIpBlackListService.updateSpeakIpBlackList(speakIpBlackList) );
	}

	/**
	 * 删除【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:speakIpBlackList:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( speakIpBlackListService.deleteSpeakIpBlackListByIds( ids ) );
	}
}