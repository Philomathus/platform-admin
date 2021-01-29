package com.qiqilm.server.admin.controller;

import java.util.List;

import com.qiqilm.server.admin.domain.PayAgentRechargeAccount;
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
import com.qiqilm.server.admin.domain.PayAgentRechargeBank;
import com.qiqilm.server.admin.service.IPayAgentRechargeBankService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/pay/payAgentRechargeBank" )
public class PayAgentRechargeBankController extends BaseController {
	@Autowired
	private IPayAgentRechargeBankService payAgentRechargeBankService;

	/**
	 * 查询【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeBank:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(PayAgentRechargeBank payAgentRechargeBank) {
		startPage();
		List<PayAgentRechargeBank> list = payAgentRechargeBankService.selectPayAgentRechargeBankList(payAgentRechargeBank);
		return getDataTable( list );
	}
    
	/**
	 * 导出【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeBank:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(PayAgentRechargeBank payAgentRechargeBank) {
		List<PayAgentRechargeBank>      list = payAgentRechargeBankService.selectPayAgentRechargeBankList(payAgentRechargeBank);
		ExcelUtil<PayAgentRechargeBank> util = new ExcelUtil<PayAgentRechargeBank>(PayAgentRechargeBank. class);
		return util.exportExcel( list, "payAgentRechargeBank" );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeBank:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( payAgentRechargeBankService.selectPayAgentRechargeBankById(id) );
	}

	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeBank:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody PayAgentRechargeBank payAgentRechargeBank) {
		return toAjax( payAgentRechargeBankService.insertPayAgentRechargeBank(payAgentRechargeBank) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeBank:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayAgentRechargeBank payAgentRechargeBank) {
		return toAjax( payAgentRechargeBankService.updatePayAgentRechargeBank(payAgentRechargeBank) );
	}

	/**
	 * 删除【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeBank:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( payAgentRechargeBankService.deletePayAgentRechargeBankByIds( ids ) );
	}

	/**
	 * 状态修改
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeBank:edit')" )
	@Log( title = "代充人", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeStatus" )
	public AjaxResult changeStatus( @RequestBody PayAgentRechargeBank payAgentRechargeBank ) {
		return toAjax( payAgentRechargeBankService.updatePayAgentRechargeBank(payAgentRechargeBank));
	}
}
