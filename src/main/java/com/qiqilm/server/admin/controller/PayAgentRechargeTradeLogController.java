package com.qiqilm.server.admin.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.domain.PayAgentRechargeTradeLog;
import com.qiqilm.server.admin.service.IPayAgentRechargeTradeLogService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

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
    	public TableDataInfo list(PayAgentRechargeTradeLog payAgentRechargeTradeLog) {
		startPage();
		List<PayAgentRechargeTradeLog> list = payAgentRechargeTradeLogService.selectPayAgentRechargeTradeLogList(payAgentRechargeTradeLog);
		return getDataTable( list );
	}
    
	/**
	 * 导出【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeTradeLog:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(PayAgentRechargeTradeLog payAgentRechargeTradeLog) {
		List<PayAgentRechargeTradeLog>      list = payAgentRechargeTradeLogService.selectPayAgentRechargeTradeLogList(payAgentRechargeTradeLog);
		ExcelUtil<PayAgentRechargeTradeLog> util = new ExcelUtil<>(PayAgentRechargeTradeLog.class);
		return util.exportExcel( list, "payAgentRechargeTradeLog" );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeTradeLog:query')" )
	@GetMapping( value = "/{orderNo}" )
	public AjaxResult getInfo( @PathVariable( "orderNo" ) String orderNo) {
		return AjaxResult.success( payAgentRechargeTradeLogService.selectPayAgentRechargeTradeLogById(orderNo) );
	}

	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeTradeLog:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody PayAgentRechargeTradeLog payAgentRechargeTradeLog) {
		return toAjax( payAgentRechargeTradeLogService.insertPayAgentRechargeTradeLog(payAgentRechargeTradeLog) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeTradeLog:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayAgentRechargeTradeLog payAgentRechargeTradeLog) {
		return toAjax( payAgentRechargeTradeLogService.updatePayAgentRechargeTradeLog(payAgentRechargeTradeLog) );
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
