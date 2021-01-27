package com.qiqilm.server.admin.controller;

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
import com.qiqilm.server.admin.domain.MessageGameNotice;
import com.qiqilm.server.admin.service.IMessageGameNoticeService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 游戏公告Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping( "/admin/messageGameNotice" )
public class MessageGameNoticeController extends BaseController {
	@Autowired
	private IMessageGameNoticeService messageGameNoticeService;

	/**
	 * 查询游戏公告列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:messageGameNotice:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(MessageGameNotice messageGameNotice) {
		startPage();
		List<MessageGameNotice> list = messageGameNoticeService.selectMessageGameNoticeList(messageGameNotice);
		return getDataTable( list );
	}
    
	/**
	 * 导出游戏公告列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:messageGameNotice:export')" )
	@Log( title = "游戏公告", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(MessageGameNotice messageGameNotice) {
		List<MessageGameNotice>      list = messageGameNoticeService.selectMessageGameNoticeList(messageGameNotice);
		ExcelUtil<MessageGameNotice> util = new ExcelUtil<MessageGameNotice>(MessageGameNotice. class);
		return util.exportExcel( list, "messageGameNotice" );
	}

	/**
	 * 获取游戏公告详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:messageGameNotice:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( messageGameNoticeService.selectMessageGameNoticeById(id) );
	}

	/**
	 * 新增游戏公告
	 */
	@PreAuthorize( "@ss.hasPermi('admin:messageGameNotice:add')" )
	@Log( title = "游戏公告", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody MessageGameNotice messageGameNotice) {
		messageGameNotice.setId(UuidUtil.getRandomUuidWithoutSeparator());
		return toAjax( messageGameNoticeService.insertMessageGameNotice(messageGameNotice) );
	}

	/**
	 * 修改游戏公告
	 */
	@PreAuthorize( "@ss.hasPermi('admin:messageGameNotice:edit')" )
	@Log( title = "游戏公告", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody MessageGameNotice messageGameNotice) {
		return toAjax( messageGameNoticeService.updateMessageGameNotice(messageGameNotice) );
	}

	/**
	 * 删除游戏公告
	 */
	@PreAuthorize( "@ss.hasPermi('admin:messageGameNotice:remove')" )
	@Log( title = "游戏公告", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( messageGameNoticeService.deleteMessageGameNoticeByIds( ids ) );
	}
}
