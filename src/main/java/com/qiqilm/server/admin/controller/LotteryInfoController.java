package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveUser;
import com.qiqilm.server.admin.domain.LotteryInfo;
import com.qiqilm.server.admin.domain.LotteryPrizepool;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILotteryInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 彩票名称Controller
 *
 * @author 77tv
 * @date 2021-02-23
 */
@RestController
@RequestMapping( "/admin/lotteryInfo" )
public class LotteryInfoController extends BaseController {
	@Autowired
	private ILotteryInfoService lotteryInfoService;

	/**
	 * 查询彩票名称列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryInfo:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( LotteryInfo lotteryInfo ) {
		startPage();
		List<LotteryInfo> list = lotteryInfoService.selectLotteryInfoList( lotteryInfo );
		return getDataTable( list );
	}

	/**
	 * 获取彩票名称详细
	 */
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo(@PathVariable( "id" ) String id ) {
		return AjaxResult.success( lotteryInfoService.selectLotteryInfoListById( id ) );
	}

	/**
	 * 修改彩票名称
	 */
	@PutMapping
	public AjaxResult edit( @RequestBody LotteryInfo lotteryInfo) {
		return toAjax( lotteryInfoService.updateLotteryInfo(lotteryInfo) );
	}

	/**
	 * Update Status controller
	 */
	@PutMapping( "/statusDetail" )
	public AjaxResult statusDetail(LotteryInfo lotteryInfoSetStatus ) {
		return toAjax( lotteryInfoService.updateLiveLotterySetStatus(lotteryInfoSetStatus) );
	}

}
