package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IGameBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 游戏处理Controller
 *
 * @author 77tv
 * @date 2021-01-27
 */
@RestController
@RequestMapping( "/game/base" )
public class GameController extends BaseController {
	@Autowired
	private IGameBaseService gameBaseService;

	/**
	 * 查询用户游戏余额(积分明细)
	 */
	@PreAuthorize( "@ss.hasPermi('game:base:balance')" )
	@GetMapping( "/balance" )
	public AjaxResult balance( String userId ) {
		return gameBaseService.balance( userId );
	}

	/**
	 * 游戏人工下分
	 */
	@PreAuthorize( "@ss.hasPermi('game:base:esc')" )
	@Log( title = "游戏人工下分", businessType = BusinessType.AUDIT )
	@GetMapping( "/esc" )
	public AjaxResult esc( String userId, Integer platformId ) {
		return gameBaseService.esc( userId, platformId );
	}
}
