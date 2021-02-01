package com.qiqilm.server.admin.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.qiqilm.server.admin.domain.GameInfo;
import com.qiqilm.server.admin.domain.SysRole;
import com.qiqilm.server.admin.domain.rsp.RspGameInfo;
import com.qiqilm.server.admin.service.IGameInfoService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.utils.SecurityUtils;
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
import com.qiqilm.server.admin.enums.BusinessType;;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-27
 */
@RestController
@RequestMapping( "/web/game-info" )
public class GameInfoController extends BaseController {
	@Autowired
	private IGameInfoService gameInfoService;

	/**
	 * 查询【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('web:game-info:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(GameInfo gameInfo) {
		startPage();
		List<RspGameInfo> list = gameInfoService.selectGameInfoList(gameInfo);
		return getDataTable( list );
	}
	@PreAuthorize( "@ss.hasPermi('web:game-info:edit')" )
	@Log( title = "游戏信息修改状态", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeStatus" )
	public AjaxResult changeStatus( @RequestBody GameInfo gameInfo ) {

		return toAjax( gameInfoService.updateStatus( gameInfo ) );
	}

	@PreAuthorize( "@ss.hasPermi('web:game-info:edit')" )
	@Log( title = "游戏信息修改是否维护", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeIsWh" )
	public AjaxResult changeIsWh( @RequestBody GameInfo gameInfo ) {

		return toAjax( gameInfoService.changeIsWh( gameInfo ) );
	}
	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('web:game-info:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( gameInfoService.selectGameInfoById(id) );
	}

	@GetMapping( value = "/listGame" )
	public AjaxResult getGameListInfo() {
		return AjaxResult.success( gameInfoService.getGameListInfo() );
	}


	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('web:game-info:add')" )
	@Log( title = "游戏信息新增", businessType = BusinessType.INSERT )
	@PostMapping(value = "/add")
	public AjaxResult add( @RequestBody GameInfo gameInfo) {

         gameInfo.setCreateTime(new Date());
		gameInfo.setId( UuidUtil.getRandomUuid() );
		return toAjax( gameInfoService.insertGameInfo(gameInfo) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('web:game-info:edit')" )
	@Log( title = "游戏信息编辑", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody GameInfo gameInfo) {
		return toAjax( gameInfoService.updateGameInfo(gameInfo) );
	}

	/**
	 * 删除【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('web:game-info:remove')" )
	@Log( title = "游戏信息删除", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{id}" )
	public AjaxResult remove( @PathVariable String id ) {
		return toAjax( gameInfoService.deleteGameInfoByIds( id) );
	}
}