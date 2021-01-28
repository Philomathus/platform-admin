package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveVideoChat;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveVideoChatService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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
	public TableDataInfo list( LiveVideoChat liveVideoChat ) {
		startPage();
		List<LiveVideoChat> list = liveVideoChatService.selectLiveVideoChatList( liveVideoChat );

		liveVideoChatService.setSpeakForbid( list );

		return getDataTable( list );
	}

	/**
	 * 直播间用户封停
	 *
	 * @return
	 */
	@PostMapping( "suspendUser" )
	@Log( title = "用户封停", businessType = BusinessType.UPDATE )
	public AjaxResult suspendUser( HttpServletRequest request,
						  @RequestBody Map<String, Object> requestMap ) {
		String pUserId = ( String ) requestMap.get( "pUserId" );
		boolean flag = ( boolean ) requestMap.get( "flag" );
		int num = ( int ) requestMap.get( "num" );
		if ( !StringUtils.hasText( pUserId ) ) {
			return AjaxResult.error( "会员平台ID不得为空" );
		}
		liveVideoChatService.suspendUser(pUserId,flag,num);
		return AjaxResult.success();
	}

	/**
	 * 直播间用户禁言10分钟
	 *
	 * @return
	 */
	@PostMapping( "forbidSendMsg" )
	@Log( title = "用户禁言", businessType = BusinessType.UPDATE )
	public AjaxResult forbidSendMsg(@RequestBody Map<String, Object> requestMap ) {
		String  pUserId    = ( String ) requestMap.get( "pUserId" );
		Integer videoId    = ( Integer ) requestMap.get( "videoId" );
		Integer forbidTime = 600;
		if ( !StringUtils.hasText( pUserId ) ) {
			return AjaxResult.error( "会员平台ID不得为空" );
		}
		liveVideoChatService.forbidSendMsg(pUserId,forbidTime,videoId);
		return AjaxResult.success();
	}
	/**
	 * 导出会员发言列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoChat:export')" )
	@Log( title = "会员发言", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export( LiveVideoChat liveVideoChat ) {
		List<LiveVideoChat>      list = liveVideoChatService.selectLiveVideoChatList( liveVideoChat );
		ExcelUtil<LiveVideoChat> util = new ExcelUtil<>( LiveVideoChat.class );
		return util.exportExcel( list, "liveVideoChat" );
	}

	/**
	 * 获取会员发言详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoChat:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		return AjaxResult.success( liveVideoChatService.selectLiveVideoChatById( id ) );
	}

	/**
	 * 新增会员发言
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoChat:add')" )
	@Log( title = "会员发言", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveVideoChat liveVideoChat ) {
		return toAjax( liveVideoChatService.insertLiveVideoChat( liveVideoChat ) );
	}

	/**
	 * 修改会员发言
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoChat:edit')" )
	@Log( title = "会员发言", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveVideoChat liveVideoChat ) {
		return toAjax( liveVideoChatService.updateLiveVideoChat( liveVideoChat ) );
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
