package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.GameType;
import com.qiqilm.server.admin.domain.req.ReqTypeGame;
import com.qiqilm.server.admin.domain.rsp.RspTypeGames;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IGameTypeService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 游戏类型Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping( "/game/type" )
public class GameTypeController extends BaseController {
	@Autowired
	private IGameTypeService gameTypeService;

	/**
	 * 查询游戏类型列表
	 */
	@PreAuthorize( "@ss.hasPermi('game:type:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( GameType gameType ) {
		startPage();
		List<GameType> list = gameTypeService.selectGameTypeList( gameType );
		return getDataTable( list );
	}

	/**
	 * 导出游戏类型列表
	 */
	@PreAuthorize( "@ss.hasPermi('game:type:export')" )
	@Log( title = "游戏类型", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export( GameType gameType ) {
		List<GameType>      list = gameTypeService.selectGameTypeList( gameType );
		ExcelUtil<GameType> util = new ExcelUtil<>( GameType.class );
		return util.exportExcel( list, "type" );
	}

	/**
	 * 获取游戏类型详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('game:type:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id ) {
		return AjaxResult.success( gameTypeService.selectGameTypeById( id ) );
	}

	/**
	 * 新增游戏类型
	 */
	@PreAuthorize( "@ss.hasPermi('game:type:add')" )
	@Log( title = "游戏类型", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody GameType gameType ) {
		gameType.setId(UuidUtil.getRandomUuid());
		return toAjax( gameTypeService.insertGameType( gameType ) );
	}

	/**
	 * 修改游戏类型
	 */
	@PreAuthorize( "@ss.hasPermi('game:type:edit')" )
	@Log( title = "游戏类型", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody GameType gameType ) {
		return toAjax( gameTypeService.updateGameType( gameType ) );
	}

	/**
	 * 删除游戏类型
	 */
	@PreAuthorize( "@ss.hasPermi('game:type:remove')" )
	@Log( title = "游戏类型", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( gameTypeService.deleteGameTypeByIds( ids ) );
	}

	@PreAuthorize( "@ss.hasPermi('game:type:edit')" )
	@Log( title = "游戏类型", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeStatus" )
	public AjaxResult changeStatus( @RequestBody GameType gameType ) {
		return toAjax( gameTypeService.updateGameType( gameType ) );
	}

	/**
	 * 获取游戏类型详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('game:type:query')" )
	@GetMapping( value = "/getRelationGame/{id}" )
	public AjaxResult getRelationGame( @PathVariable( "id" ) String id ) {
		RspTypeGames rspTypeGames = gameTypeService.findTypeGames( id );
		return AjaxResult.success( rspTypeGames );
	}

	@RequestMapping( value = "/add-type-games", method = RequestMethod.POST )
	public void addTypeGames( HttpServletRequest request, @RequestBody ReqTypeGame dto ) {
		gameTypeService.addTypeGames( dto );

	}
}