package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.domain.LotteryTemp;
import com.qiqilm.server.admin.service.ILotteryTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 彩票即时信息Controller
 *
 * @author 77tv
 * @date 2021-02-23
 */
@RestController
@RequestMapping( "/admin/lotteryTemp" )
public class LotteryTempController extends BaseController {
	@Autowired
	private ILotteryTempService lotteryTempService;

	/**
	 * 查询彩票即时信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryTemp:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( LotteryTemp lotteryTemp ) {
		startPage();
		List<LotteryTemp> list = lotteryTempService.selectLotteryTempList( lotteryTemp );
		return getDataTable( list );
	}
}
