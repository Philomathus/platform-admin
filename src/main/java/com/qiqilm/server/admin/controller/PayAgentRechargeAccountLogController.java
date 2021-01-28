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
import com.qiqilm.server.admin.domain.PayAgentRechargeAccountLog;
import com.qiqilm.server.admin.service.IPayAgentRechargeAccountLogService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/pay/payAgentRechargeAccountLog" )
public class PayAgentRechargeAccountLogController extends BaseController {
	@Autowired
	private IPayAgentRechargeAccountLogService payAgentRechargeAccountLogService;

	/**
	 * 查询【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccountLog:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(PayAgentRechargeAccountLog payAgentRechargeAccountLog) {
		startPage();
		List<PayAgentRechargeAccountLog> list = payAgentRechargeAccountLogService.selectPayAgentRechargeAccountLogList(payAgentRechargeAccountLog);
		return getDataTable( list );
	}
    
	/**
	 * 导出【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccountLog:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(PayAgentRechargeAccountLog payAgentRechargeAccountLog) {
		List<PayAgentRechargeAccountLog>      list = payAgentRechargeAccountLogService.selectPayAgentRechargeAccountLogList(payAgentRechargeAccountLog);
		ExcelUtil<PayAgentRechargeAccountLog> util = new ExcelUtil<PayAgentRechargeAccountLog>(PayAgentRechargeAccountLog. class);
		return util.exportExcel( list, "payAgentRechargeAccountLog" );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccountLog:query')" )
	@GetMapping( value = "/{orderNo}" )
	public AjaxResult getInfo( @PathVariable( "orderNo" ) String orderNo) {
		return AjaxResult.success( payAgentRechargeAccountLogService.selectPayAgentRechargeAccountLogById(orderNo) );
	}

	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccountLog:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody PayAgentRechargeAccountLog payAgentRechargeAccountLog) {
		return toAjax( payAgentRechargeAccountLogService.insertPayAgentRechargeAccountLog(payAgentRechargeAccountLog) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccountLog:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayAgentRechargeAccountLog payAgentRechargeAccountLog) {
		return toAjax( payAgentRechargeAccountLogService.updatePayAgentRechargeAccountLog(payAgentRechargeAccountLog) );
	}

	/**
	 * 删除【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccountLog:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{orderNos}" )
	public AjaxResult remove( @PathVariable String[] orderNos ) {
		return toAjax( payAgentRechargeAccountLogService.deletePayAgentRechargeAccountLogByIds( orderNos ) );
	}
}
