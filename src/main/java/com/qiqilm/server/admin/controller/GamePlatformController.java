package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IGamePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 游戏平台Controller
 *
 * @author 77tv
 * @date 2021-01-27
 */
@RestController
@RequestMapping( "/game/platform" )
public class GamePlatformController extends BaseController {
	@Autowired
	private IGamePlatformService gamePlatformService;

	/**
	 * 查询游戏平台列表
	 */
	@PreAuthorize( "@ss.hasPermi('game:platform:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( GamePlatform gamePlatform ) {
		startPage();
		List<GamePlatform> list = gamePlatformService.selectGamePlatformList( gamePlatform );
		return getDataTable( list );
	}


	/**
	 * 获取游戏平台详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('game:platform:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		return AjaxResult.success( gamePlatformService.selectGamePlatformById( id ) );
	}

	/**
	 * 新增游戏平台
	 */
	@PreAuthorize( "@ss.hasPermi('game:platform:add')" )
	@Log( title = "游戏平台新增", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody GamePlatform gamePlatform ) {
		return toAjax( gamePlatformService.insertGamePlatform( gamePlatform ) );
	}

	/**
	 * 修改游戏平台
	 */
	@PreAuthorize( "@ss.hasPermi('game:platform:edit')" )
	@Log( title = "游戏平台新增", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody GamePlatform gamePlatform ) {
		return toAjax( gamePlatformService.updateGamePlatform( gamePlatform ) );
	}

	@PreAuthorize( "@ss.hasPermi('game:platform:edit')" )
	@Log( title = "游戏平台修改状态", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeStatus" )
	public AjaxResult changeStatus( @RequestBody GamePlatform gamePlatform ) {
		return toAjax( gamePlatformService.changeStatus( gamePlatform ) );
	}

	/**
	 * 删除游戏平台
	 */
	@PreAuthorize( "@ss.hasPermi('game:platform:remove')" )
	@Log( title = "游戏平台删除", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( gamePlatformService.deleteGamePlatformByIds( ids ) );
	}
}