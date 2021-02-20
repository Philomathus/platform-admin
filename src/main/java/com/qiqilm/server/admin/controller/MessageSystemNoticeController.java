package com.qiqilm.server.admin.controller;

import java.util.Date;
import java.util.List;

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
import com.qiqilm.server.admin.domain.MessageSystemNotice;
import com.qiqilm.server.admin.service.IMessageSystemNoticeService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 系统公告Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping( "/admin/messageSystemNotice" )
public class MessageSystemNoticeController extends BaseController {
	@Autowired
	private IMessageSystemNoticeService messageSystemNoticeService;

	/**
	 * 查询系统公告列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:messageSystemNotice:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(MessageSystemNotice messageSystemNotice) {
		startPage();
		List<MessageSystemNotice> list = messageSystemNoticeService.selectMessageSystemNoticeList(messageSystemNotice);
		return getDataTable( list );
	}
    
	/**
	 * 导出系统公告列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:messageSystemNotice:export')" )
	@Log( title = "系统公告", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(MessageSystemNotice messageSystemNotice) {
		List<MessageSystemNotice>      list = messageSystemNoticeService.selectMessageSystemNoticeList(messageSystemNotice);
		ExcelUtil<MessageSystemNotice> util = new ExcelUtil<MessageSystemNotice>(MessageSystemNotice. class);
		return util.exportExcel( list, "messageSystemNotice" );
	}

	/**
	 * 获取系统公告详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:messageSystemNotice:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( messageSystemNoticeService.selectMessageSystemNoticeById(id) );
	}

	/**
	 * 新增系统公告
	 */
	@PreAuthorize( "@ss.hasPermi('admin:messageSystemNotice:add')" )
	@Log( title = "系统公告", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody MessageSystemNotice messageSystemNotice) {
		messageSystemNotice.setId(UuidUtil.getRandomUuidWithoutSeparator());
		messageSystemNotice.setAction("DIALOG");
		messageSystemNotice.setDevice("ALL");
		messageSystemNotice.setCreateTime(new Date());
		return toAjax( messageSystemNoticeService.insertMessageSystemNotice(messageSystemNotice) );
	}

	/**
	 * 修改系统公告
	 */
	@PreAuthorize( "@ss.hasPermi('admin:messageSystemNotice:edit')" )
	@Log( title = "系统公告", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody MessageSystemNotice messageSystemNotice) {
		return toAjax( messageSystemNoticeService.updateMessageSystemNotice(messageSystemNotice) );
	}

	/**
	 * 删除系统公告
	 */
	@PreAuthorize( "@ss.hasPermi('admin:messageSystemNotice:remove')" )
	@Log( title = "系统公告", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( messageSystemNoticeService.deleteMessageSystemNoticeByIds( ids ) );
	}
}
