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
import com.qiqilm.server.admin.domain.PayAgentRechargeRecord;
import com.qiqilm.server.admin.service.IPayAgentRechargeRecordService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 【代充存提】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/pay/payAgentRechargeRecord" )
public class PayAgentRechargeRecordController extends BaseController {
	@Autowired
	private IPayAgentRechargeRecordService payAgentRechargeRecordService;

	/**
	 * 查询【代充存提】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeRecord:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(PayAgentRechargeRecord payAgentRechargeRecord) {
		startPage();
		List<PayAgentRechargeRecord> list = payAgentRechargeRecordService.selectPayAgentRechargeRecordList(payAgentRechargeRecord);
		return getDataTable( list );
	}
    
	/**
	 * 导出【代充存提】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeRecord:export')" )
	@Log( title = "【代充存提】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(PayAgentRechargeRecord payAgentRechargeRecord) {
		List<PayAgentRechargeRecord>      list = payAgentRechargeRecordService.selectPayAgentRechargeRecordList(payAgentRechargeRecord);
		ExcelUtil<PayAgentRechargeRecord> util = new ExcelUtil<PayAgentRechargeRecord>(PayAgentRechargeRecord. class);
		return util.exportExcel( list, "payAgentRechargeRecord" );
	}

	/**
	 * 获取【代充存提】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeRecord:query')" )
	@GetMapping( value = "/{orderNo}" )
	public AjaxResult getInfo( @PathVariable( "orderNo" ) String orderNo) {
		return AjaxResult.success( payAgentRechargeRecordService.selectPayAgentRechargeRecordById(orderNo) );
	}

	/**
	 * 新增【代充存提】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeRecord:add')" )
	@Log( title = "【代充存提】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody PayAgentRechargeRecord payAgentRechargeRecord) {
		return toAjax( payAgentRechargeRecordService.insertPayAgentRechargeRecord(payAgentRechargeRecord) );
	}

	/**
	 * 修改【代充存提】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeRecord:edit')" )
	@Log( title = "【代充存提】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayAgentRechargeRecord payAgentRechargeRecord) {
		return toAjax( payAgentRechargeRecordService.updatePayAgentRechargeRecord(payAgentRechargeRecord) );
	}

	/**
	 * 删除【代充存提】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeRecord:remove')" )
	@Log( title = "【代充存提】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{orderNos}" )
	public AjaxResult remove( @PathVariable String[] orderNos ) {
		return toAjax( payAgentRechargeRecordService.deletePayAgentRechargeRecordByIds( orderNos ) );
	}
}
