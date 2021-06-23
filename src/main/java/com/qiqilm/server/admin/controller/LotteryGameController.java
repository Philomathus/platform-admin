package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LotteryGame;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILotteryGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 下注Controller
 *
 * @author 77tv
 * @date 2021-02-23
 */
@RestController
@RequestMapping( "/admin/lotteryGame" )
public class LotteryGameController extends BaseController {
	@Autowired
	private ILotteryGameService lotteryGameService;

	/**
	 * 查询下注列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryGame:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( LotteryGame lotteryGame ) {
		startPage();
		List<LotteryGame> list = lotteryGameService.selectLotteryGameList( lotteryGame );
		return getDataTable( list );
	}


	/**
	 * 获取下注详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryGame:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id ) {
		return AjaxResult.success( lotteryGameService.selectLotteryGameById( id ) );
	}


	/**
	 * 修改下注
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryGame:edit')" )
	@Log( title = "下注", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LotteryGame lotteryGame ) {
		return toAjax( lotteryGameService.updateLotteryGame( lotteryGame ) );
	}

}
