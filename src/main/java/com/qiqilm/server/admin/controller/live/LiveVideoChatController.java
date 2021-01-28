package com.qiqilm.server.admin.controller.live;

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
import com.qiqilm.server.admin.domain.LiveVideoChat;
import com.qiqilm.server.admin.service.ILiveVideoChatService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 会员发言Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/admin/liveVideoChat" )
public class LiveVideoChatController extends BaseController {
	@Autowired
	private ILiveVideoChatService liveVideoChatService;

	/**
	 * 查询会员发言列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoChat:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LiveVideoChat liveVideoChat) {
		startPage();
		List<LiveVideoChat> list = liveVideoChatService.selectLiveVideoChatList(liveVideoChat);
		return getDataTable( list );
	}
    
	/**
	 * 导出会员发言列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoChat:export')" )
	@Log( title = "会员发言", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(LiveVideoChat liveVideoChat) {
		List<LiveVideoChat>      list = liveVideoChatService.selectLiveVideoChatList(liveVideoChat);
		ExcelUtil<LiveVideoChat> util = new ExcelUtil<LiveVideoChat>(LiveVideoChat. class);
		return util.exportExcel( list, "liveVideoChat" );
	}

	/**
	 * 获取会员发言详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoChat:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( liveVideoChatService.selectLiveVideoChatById(id) );
	}

	/**
	 * 新增会员发言
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoChat:add')" )
	@Log( title = "会员发言", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveVideoChat liveVideoChat) {
		return toAjax( liveVideoChatService.insertLiveVideoChat(liveVideoChat) );
	}

	/**
	 * 修改会员发言
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoChat:edit')" )
	@Log( title = "会员发言", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveVideoChat liveVideoChat) {
		return toAjax( liveVideoChatService.updateLiveVideoChat(liveVideoChat) );
	}

	/**
	 * 删除会员发言
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoChat:remove')" )
	@Log( title = "会员发言", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( liveVideoChatService.deleteLiveVideoChatByIds( ids ) );
	}
}
