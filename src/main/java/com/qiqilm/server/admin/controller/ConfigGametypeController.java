package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ConfigGametype;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IConfigGametypeService;
import com.qiqilm.server.admin.service.IGamePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 游戏字典Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/game/config-gametype" )
public class ConfigGametypeController extends BaseController {
	@Autowired
	private IConfigGametypeService configGametypeService;
	@Autowired
	private IGamePlatformService   gamePlatformService;

	/**
	 * 查询游戏字典列表
	 */
	@PreAuthorize( "@ss.hasPermi('game:config-gametype:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( ConfigGametype configGametype ) {
		startPage();
		List<ConfigGametype> list = configGametypeService.selectConfigGametypeList( configGametype );
		return getDataTable( list );
	}

	@GetMapping( "/listGameType" )
	public AjaxResult list( GamePlatform gamePlatform ) {
		List<GamePlatform> list = gamePlatformService.selectGamePlatformList( gamePlatform );
		return AjaxResult.success( list );
	}


	/**
	 * 获取游戏字典详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('game:config-gametype:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id ) {
		return AjaxResult.success( configGametypeService.selectConfigGametypeById( id ) );
	}

	/**
	 * 新增游戏字典
	 */
	@PreAuthorize( "@ss.hasPermi('game:config-gametype:add')" )
	@Log( title = "游戏配置新增", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ConfigGametype configGametype ) {
		return toAjax( configGametypeService.insertConfigGametype( configGametype ) );
	}

	/**
	 * 修改游戏字典
	 */
	@PreAuthorize( "@ss.hasPermi('game:config-gametype:edit')" )
	@Log( title = "游戏配置编辑", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ConfigGametype configGametype ) {
		return toAjax( configGametypeService.updateConfigGametype( configGametype ) );
	}

	/**
	 * 删除游戏字典
	 */
	@PreAuthorize( "@ss.hasPermi('game:config-gametype:remove')" )
	@Log( title = "游戏配置删除", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( configGametypeService.deleteConfigGametypeByIds( ids ) );
	}
}