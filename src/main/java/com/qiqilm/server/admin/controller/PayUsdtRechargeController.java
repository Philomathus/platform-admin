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
import com.qiqilm.server.admin.domain.PayUsdtRecharge;
import com.qiqilm.server.admin.service.IPayUsdtRechargeService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * USDT充值提交记录Controller
 *
 * @author 77tv
 * @date 2021-09-14
 */
@RestController
@RequestMapping( "/admin/payUsdtRecharge" )
public class PayUsdtRechargeController extends BaseController {
	@Autowired
	private IPayUsdtRechargeService payUsdtRechargeService;

	/**
	 * 查询USDT充值提交记录列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:payUsdtRecharge:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(PayUsdtRecharge payUsdtRecharge) {
		startPage();
		List<PayUsdtRecharge> list = payUsdtRechargeService.selectPayUsdtRechargeList(payUsdtRecharge);
		return getDataTable( list );
	}
    
	/**
	 * 导出USDT充值提交记录列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:payUsdtRecharge:export')" )
	@Log( title = "USDT充值提交记录", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(PayUsdtRecharge payUsdtRecharge, HttpServletResponse response) {
		List<PayUsdtRecharge>      list = payUsdtRechargeService.selectPayUsdtRechargeList(payUsdtRecharge);
		ExportExcelUtil.exportExcel( list, "USDT充值提交记录", "USDT充值提交记录表", PayUsdtRecharge.class, response );
	}

	/**
	 * 获取USDT充值提交记录详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:payUsdtRecharge:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( payUsdtRechargeService.selectPayUsdtRechargeById(id) );
	}

//	/**
//	 * 新增USDT充值提交记录
//	 */
//	@PreAuthorize( "@ss.hasPermi('admin:payUsdtRecharge:add')" )
//	@Log( title = "新增USDT充值提交记录", businessType = BusinessType.INSERT )
//	@PostMapping
//	public AjaxResult add( @RequestBody PayUsdtRecharge payUsdtRecharge) {
//		return toAjax( payUsdtRechargeService.insertPayUsdtRecharge(payUsdtRecharge) );
//	}

	/**
	 * 修改USDT充值提交记录
	 */
	@PreAuthorize( "@ss.hasPermi('admin:payUsdtRecharge:edit')" )
	@Log( title = "修改USDT充值提交记录", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayUsdtRecharge payUsdtRecharge) {
		return payUsdtRechargeService.updatePayUsdtRecharge(payUsdtRecharge);
	}

//	/**
//	 * 删除USDT充值提交记录
//	 */
//	@PreAuthorize( "@ss.hasPermi('admin:payUsdtRecharge:remove')" )
//	@Log( title = "删除USDT充值提交记录", businessType = BusinessType.DELETE )
//	@DeleteMapping( "/{ids}" )
//	public AjaxResult remove( @PathVariable Long[] ids ) {
//		return toAjax( payUsdtRechargeService.deletePayUsdtRechargeByIds( ids ) );
//	}
}
