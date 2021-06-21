package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LogGameOrder;
import com.qiqilm.server.admin.domain.LotteryBet0;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILotteryBet0Service;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户投资行为Controller
 *
 * @author 77tv
 * @date 2021-03-03
 */
@RestController
@RequestMapping( "/admin/lotteryBet0" )
public class LotteryBet0Controller extends BaseController {
	@Autowired
	private ILotteryBet0Service lotteryBet0Service;

	/**
	 * 查询用户投资行为列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryBet:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( LotteryBet0 lotteryBet0 ) {
		startPage();
		List<LotteryBet0> list = lotteryBet0Service.selectLotteryBet0List( lotteryBet0 );
		return getDataTable( list );
	}

	/**
	 * 用户投资行为统计
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryBet:list')" )
	@GetMapping( "/getCount" )
	public AjaxResult getCount(LotteryBet0 lotteryBet0) {
		return lotteryBet0Service.getCount(lotteryBet0);
	}

	/**
	 * 导出用户投资行为
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryBet:list')" )
	@GetMapping( "/export" )
	public void export(LotteryBet0 lotteryBet0, HttpServletResponse response ) {
		List<LotteryBet0> list = lotteryBet0Service.selectLotteryBet0List( lotteryBet0 );
		ExportExcelUtil.exportExcel( list, "投注記錄", "投注記錄表", LotteryBet0.class, response );
	}

}
