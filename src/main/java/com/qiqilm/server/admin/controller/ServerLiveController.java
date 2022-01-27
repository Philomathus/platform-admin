package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ServerLive;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IServerLiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 直播流服务配置Controller
 *
 * @author 77tv
 * @date 2021-01-27
 */
@RestController
@RequestMapping( "/server/live" )
public class ServerLiveController extends BaseController {
	@Autowired
	private IServerLiveService serverLiveService;

	/**
	 * 查询直播流服务配置列表
	 */
	@PreAuthorize( "@ss.hasPermi('server:live:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( ServerLive serverLive ) {
		startPage();
		List<ServerLive> list = serverLiveService.selectServerLiveList( serverLive );
		return getDataTable( list );
	}

	/**
	 * 获取直播流服务配置详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('server:live:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		return AjaxResult.success( serverLiveService.selectServerLiveById( id ) );
	}

	/**
	 * 新增直播流服务配置
	 */
	@PreAuthorize( "@ss.hasPermi('server:live:add')" )
	@Log( title = "直播流服务配置", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ServerLive serverLive ) {
		if(StringUtils.hasText(serverLive.getName())) {
			ServerLive serverLive1 = new ServerLive();
			serverLive1.setName(serverLive.getName());
			List<ServerLive> list = serverLiveService.selectServerLiveList(serverLive1);
			if(list != null && list.size() > 0){
				return AjaxResult.error("该直播流服务名称已存在");
			}
		}
		return toAjax( serverLiveService.insertServerLive( serverLive ) );
	}

	/**
	 * 修改直播流服务配置
	 */
	@PreAuthorize( "@ss.hasPermi('server:live:edit')" )
	@Log( title = "直播流服务配置", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ServerLive serverLive ) {
		return toAjax( serverLiveService.updateServerLive( serverLive ) );
	}

	/**
	 * 删除直播流服务配置
	 */
	@PreAuthorize( "@ss.hasPermi('server:live:remove')" )
	@Log( title = "直播流服务配置", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( serverLiveService.deleteServerLiveByIds( ids ) );
	}

	@PreAuthorize( "@ss.hasPermi('server:live:effect')" )
	@Log( title = "直播流服务配置", businessType = BusinessType.EFFECT )
	@PutMapping( "/changeStatus/{id}/{status}" )
	public AjaxResult changeStatus( @PathVariable long id, @PathVariable int status ) {
		return  toAjax(serverLiveService.changeStatus( id, status ));
	}

	@PreAuthorize( "@ss.hasPermi('server:live:list')" )
	@GetMapping( "/getAlllist" )
	public TableDataInfo getAlllist( ServerLive serverLive ) {
        serverLive.setType(1);
		List<ServerLive> list = serverLiveService.selectServerLiveList( serverLive);
		return getDataTable( list );
	}
}
