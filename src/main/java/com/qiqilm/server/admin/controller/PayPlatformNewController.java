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
import com.qiqilm.server.admin.domain.PayPlatformNew;
import com.qiqilm.server.admin.service.IPayPlatformNewService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-27
 */
@RestController
@RequestMapping( "/pay/payPlatformNew" )
public class PayPlatformNewController extends BaseController {
	@Autowired
	private IPayPlatformNewService payPlatformNewService;

	/**
	 * 查询【支付平台】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(PayPlatformNew payPlatformNew) {
		startPage();
		List<PayPlatformNew> list = payPlatformNewService.selectPayPlatformNewList(payPlatformNew);
		return getDataTable( list );
	}
    
	/**
	 * 导出【支付平台】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:export')" )
	@Log( title = "【支付平台】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(PayPlatformNew payPlatformNew) {
		List<PayPlatformNew>      list = payPlatformNewService.selectPayPlatformNewList(payPlatformNew);
		ExcelUtil<PayPlatformNew> util = new ExcelUtil<PayPlatformNew>(PayPlatformNew. class);
		return util.exportExcel( list, "payPlatformNew" );
	}

	/**
	 * 获取【支付平台】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( payPlatformNewService.selectPayPlatformNewById(id) );
	}

	/**
	 * 新增【支付平台】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:add')" )
	@Log( title = "【支付平台】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody PayPlatformNew payPlatformNew) {
		return toAjax( payPlatformNewService.insertPayPlatformNew(payPlatformNew) );
	}

	/**
	 * 修改【支付平台】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:edit')" )
	@Log( title = "【支付平台】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayPlatformNew payPlatformNew) {
		return toAjax( payPlatformNewService.updatePayPlatformNew(payPlatformNew) );
	}

	/**
	 * 删除【支付平台】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:remove')" )
	@Log( title = "【支付平台】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( payPlatformNewService.deletePayPlatformNewByIds( ids ) );
	}
}
