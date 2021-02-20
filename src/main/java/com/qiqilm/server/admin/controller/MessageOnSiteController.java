package com.qiqilm.server.admin.controller;

import java.util.Date;
import java.util.List;

import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.UuidUtil;
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
import com.qiqilm.server.admin.domain.MessageOnSite;
import com.qiqilm.server.admin.service.IMessageOnSiteService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 站内信息Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping( "/admin/messageOnSite" )
public class MessageOnSiteController extends BaseController {
	@Autowired
	private IMessageOnSiteService messageOnSiteService;


	/**
	 * 查询站内信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:messageOnSite:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(MessageOnSite messageOnSite) {
		startPage();
		List<MessageOnSite> list = messageOnSiteService.selectMessageOnSiteList(messageOnSite);
		return getDataTable( list );
	}
    
	/**
	 * 导出站内信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:messageOnSite:export')" )
	@Log( title = "站内信息", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(MessageOnSite messageOnSite) {
		List<MessageOnSite>      list = messageOnSiteService.selectMessageOnSiteList(messageOnSite);
		ExcelUtil<MessageOnSite> util = new ExcelUtil<MessageOnSite>(MessageOnSite. class);
		return util.exportExcel( list, "messageOnSite" );
	}

	/**
	 * 获取站内信息详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:messageOnSite:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( messageOnSiteService.selectMessageOnSiteById(id) );
	}

	/**
	 * 新增站内信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:messageOnSite:add')" )
	@Log( title = "站内信息", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody MessageOnSite messageOnSite) {
		messageOnSite.setId(UuidUtil.getRandomUuidWithoutSeparator());
		messageOnSite.setCreateTime(new Date());
		messageOnSite.setReceiverType("ALL_MEMBER");
		return toAjax( messageOnSiteService.insertMessageOnSite(messageOnSite) );
	}

	/**
	 * 修改站内信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:messageOnSite:edit')" )
	@Log( title = "站内信息", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody MessageOnSite messageOnSite) {
		return toAjax( messageOnSiteService.updateMessageOnSite(messageOnSite) );
	}

	/**
	 * 删除站内信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:messageOnSite:remove')" )
	@Log( title = "站内信息", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( messageOnSiteService.deleteMessageOnSiteByIds( ids ) );
	}
}
