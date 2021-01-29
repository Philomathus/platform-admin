package com.qiqilm.server.admin.controller;

import java.util.List;

import com.qiqilm.server.admin.domain.PayType;
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
import com.qiqilm.server.admin.domain.PayAgentRechargeAccount;
import com.qiqilm.server.admin.service.IPayAgentRechargeAccountService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 【代充人管理】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/pay/payAgentRechargeAccount" )
public class PayAgentRechargeAccountController extends BaseController {
	@Autowired
	private IPayAgentRechargeAccountService payAgentRechargeAccountService;

	/**
	 * 查询【代充人管理】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccount:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(PayAgentRechargeAccount payAgentRechargeAccount) {
		startPage();
		List<PayAgentRechargeAccount> list = payAgentRechargeAccountService.selectPayAgentRechargeAccountList(payAgentRechargeAccount);
		return getDataTable( list );
	}
    
	/**
	 * 导出【代充人管理】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccount:export')" )
	@Log( title = "【代充人管理】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(PayAgentRechargeAccount payAgentRechargeAccount) {
		List<PayAgentRechargeAccount>      list = payAgentRechargeAccountService.selectPayAgentRechargeAccountList(payAgentRechargeAccount);
		ExcelUtil<PayAgentRechargeAccount> util = new ExcelUtil<PayAgentRechargeAccount>(PayAgentRechargeAccount. class);
		return util.exportExcel( list, "payAgentRechargeAccount" );
	}

	/**
	 * 获取【代充人管理】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccount:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( payAgentRechargeAccountService.selectPayAgentRechargeAccountById(id) );
	}

	/**
	 * 新增【代充人管理】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccount:add')" )
	@Log( title = "【代充人管理】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody PayAgentRechargeAccount payAgentRechargeAccount) {
		return toAjax( payAgentRechargeAccountService.insertPayAgentRechargeAccount(payAgentRechargeAccount) );
	}

	/**
	 * 修改【代充人管理】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccount:edit')" )
	@Log( title = "【代充人管理】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayAgentRechargeAccount payAgentRechargeAccount) {
		return toAjax( payAgentRechargeAccountService.updatePayAgentRechargeAccount(payAgentRechargeAccount) );
	}

	/**
	 * 删除【代充人管理】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccount:remove')" )
	@Log( title = "【代充人管理】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( payAgentRechargeAccountService.deletePayAgentRechargeAccountByIds( ids ) );
	}

	/**
	 * 状态修改
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccount:edit')" )
	@Log( title = "代充人", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeStatus" )
	public AjaxResult changeStatus( @RequestBody PayAgentRechargeAccount payAgentRechargeAccount ) {
		return toAjax( payAgentRechargeAccountService.updatePayAgentRechargeAccount(payAgentRechargeAccount));
	}
}
