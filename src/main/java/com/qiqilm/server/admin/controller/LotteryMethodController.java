package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.domain.LotteryMethod;
import com.qiqilm.server.admin.service.ILotteryMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 彩票种类Controller
 *
 * @author 77tv
 * @date 2021-02-23
 */
@RestController
@RequestMapping( "/admin/lotteryMethod" )
public class LotteryMethodController extends BaseController {
	@Autowired
	private ILotteryMethodService lotteryMethodService;

	/**
	 * 查询彩票种类列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryMethod:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( LotteryMethod lotteryMethod ) {
		startPage();
		List<LotteryMethod> list = lotteryMethodService.selectLotteryMethodList( lotteryMethod );
		return getDataTable( list );
	}

}
