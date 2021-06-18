package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.domain.LotteryInfo;
import com.qiqilm.server.admin.service.ILotteryInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
