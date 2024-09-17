package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LogMoney;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILogMoneyService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 会员资金信息Controller
 *
 * @author 77tv
 * @date 2021-01-29
 */
@RestController
@RequestMapping( "/pay/logMoney" )
public class LogMoneyController extends BaseController {
	@Autowired
	private ILogMoneyService logMoneyService;

	/**
	 * 查询 会员资金信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:logMoney:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( LogMoney logMoney ) {
		startPage();
		List<LogMoney> list = logMoneyService.selectLogMoneyList( logMoney );
		return getDataTable( list );
	}

	/**
	 * 行为类型统计
	 */
	@PreAuthorize( "@ss.hasPermi('pay:logMoney:list')" )
	@GetMapping( "/listCount" )
	public AjaxResult listCount( LogMoney logMoney ) {
		return logMoneyService.listCount( logMoney );
	}

	/**
	 * 导出 会员资金信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:logMoney:export')" )
	@Log( title = " 会员资金信息", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( LogMoney logMoney, HttpServletResponse response ) {
		List<LogMoney> list = logMoneyService.selectLogMoneyList( logMoney );
		ExportExcelUtil.exportExcel( list, "会员资金信息", "会员资金信息表", LogMoney.class, response );
	}
}
