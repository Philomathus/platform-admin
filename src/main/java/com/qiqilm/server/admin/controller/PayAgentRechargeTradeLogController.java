package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.PayAgentRechargeTradeLog;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IPayAgentRechargeTradeLogService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-02-01
 */
@RestController
@RequestMapping( "/pay/payAgentRechargeTradeLog" )
public class PayAgentRechargeTradeLogController extends BaseController {
	@Autowired
	private IPayAgentRechargeTradeLogService payAgentRechargeTradeLogService;

	/**
	 * 查询【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeTradeLog:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( PayAgentRechargeTradeLog payAgentRechargeTradeLog ) {
		startPage();
		List<PayAgentRechargeTradeLog> list =
				payAgentRechargeTradeLogService.selectPayAgentRechargeTradeLogList( payAgentRechargeTradeLog );
		return getDataTable( list );
	}

	/**
	 * 导出【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeTradeLog:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( PayAgentRechargeTradeLog payAgentRechargeTradeLog, HttpServletResponse response ) {
		List<PayAgentRechargeTradeLog> list =
				payAgentRechargeTradeLogService.selectPayAgentRechargeTradeLogList( payAgentRechargeTradeLog );
		ExportExcelUtil.exportExcel( list, "代充存提", "代充存提表", PayAgentRechargeTradeLog.class, response );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeTradeLog:query')" )
	@GetMapping( value = "/{orderNo}" )
	public AjaxResult getInfo( @PathVariable( "orderNo" ) String orderNo ) {
		return AjaxResult.success( payAgentRechargeTradeLogService.selectPayAgentRechargeTradeLogById( orderNo ) );
	}

	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeTradeLog:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody PayAgentRechargeTradeLog payAgentRechargeTradeLog ) {
		return toAjax( payAgentRechargeTradeLogService.insertPayAgentRechargeTradeLog( payAgentRechargeTradeLog ) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeTradeLog:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayAgentRechargeTradeLog payAgentRechargeTradeLog ) {
		return toAjax( payAgentRechargeTradeLogService.updatePayAgentRechargeTradeLog( payAgentRechargeTradeLog ) );
	}

	/**
	 * 删除【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeTradeLog:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{orderNos}" )
	public AjaxResult remove( @PathVariable String[] orderNos ) {
		return toAjax( payAgentRechargeTradeLogService.deletePayAgentRechargeTradeLogByIds( orderNos ) );
	}
}
