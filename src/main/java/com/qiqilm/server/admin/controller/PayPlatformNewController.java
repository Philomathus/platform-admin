package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.PayPlatformNew;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IPayPlatformNewService;
import com.qiqilm.server.admin.service.IPayService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 支付平台Controller
 *
 * @author 77tv
 * @date 2021-01-27
 */
@RestController
@RequestMapping( "/pay/payPlatformNew" )
public class PayPlatformNewController extends BaseController {
	@Autowired
	private IPayPlatformNewService payPlatformNewService;
	@Autowired
	private IPayService            payService;

	/**
	 * 查询支付平台列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( PayPlatformNew payPlatformNew ) {
		startPage();
		List<PayPlatformNew> list = payPlatformNewService.selectPayPlatformNewList( payPlatformNew );
		return getDataTable( list );
	}

	/**
	 * 导出支付平台列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:export')" )
	@Log( title = "支付平台", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export( PayPlatformNew payPlatformNew ) {
		List<PayPlatformNew>      list = payPlatformNewService.selectPayPlatformNewList( payPlatformNew );
		ExcelUtil<PayPlatformNew> util = new ExcelUtil<PayPlatformNew>( PayPlatformNew.class );
		return util.exportExcel( list, "payPlatformNew" );
	}

	/**
	 * 获取支付平台详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		return AjaxResult.success( payPlatformNewService.selectPayPlatformNewById( id ) );
	}

	/**
	 * 新增支付平台
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:add')" )
	@Log( title = "支付平台", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody PayPlatformNew payPlatformNew ) {
		return toAjax( payPlatformNewService.insertPayPlatformNew( payPlatformNew ) );
	}

	/**
	 * 修改支付平台
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:edit')" )
	@Log( title = "支付平台", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayPlatformNew payPlatformNew ) {
		return toAjax( payPlatformNewService.updatePayPlatformNew( payPlatformNew ) );
	}

	/**
	 * 删除支付平台
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:remove')" )
	@Log( title = "支付平台", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( payPlatformNewService.deletePayPlatformNewByIds( ids ) );
	}

	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:patchOrder')" )
	@Log( title = "线上支付人工补单", businessType = BusinessType.AUDIT )
	@PutMapping( value = "/payPatchOrder" )
	public AjaxResult payPatchOrder( @RequestBody Map<String, Object> requestMap ) throws Exception {
		return payService.payPatchOrder( requestMap );
	}
}
