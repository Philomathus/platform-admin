package com.qiqilm.server.admin.controller;

import java.util.ArrayList;
import java.util.List;

import com.qiqilm.server.admin.domain.PayPlatformNew;
import com.qiqilm.server.admin.service.IPayPlatformNewService;
import com.qiqilm.server.admin.utils.StringUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.domain.PayChannelNew;
import com.qiqilm.server.admin.service.IPayChannelNewService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-27
 */
@RestController
@RequestMapping( "/pay/payChannelNew" )
public class PayChannelNewController extends BaseController {
	@Autowired
	private IPayChannelNewService payChannelNewService;
	@Autowired
	private IPayPlatformNewService payPlatformNewService;


	/**
	 * 查询【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payChannelNew:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(PayChannelNew payChannelNew) {
		startPage();
		List<PayChannelNew> list = payChannelNewService.selectPayChannelNewList(payChannelNew);
		return getDataTable( list );
	}
    
	/**
	 * 导出【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payChannelNew:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(PayChannelNew payChannelNew) {
		List<PayChannelNew>      list = payChannelNewService.selectPayChannelNewList(payChannelNew);
		ExcelUtil<PayChannelNew> util = new ExcelUtil<PayChannelNew>(PayChannelNew. class);
		return util.exportExcel( list, "payChannelNew" );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payChannelNew:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( payChannelNewService.selectPayChannelNewById(id) );
	}

	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payChannelNew:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody PayChannelNew payChannelNew) {
		return toAjax( payChannelNewService.insertPayChannelNew(payChannelNew) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payChannelNew:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayChannelNew payChannelNew) {
		return toAjax( payChannelNewService.updatePayChannelNew(payChannelNew) );
	}

	/**
	 * 删除【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payChannelNew:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( payChannelNewService.deletePayChannelNewByIds( ids ) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payChannelNew:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping("changeStatus")
	public AjaxResult changeStatus( @RequestBody PayChannelNew payChannelNew) {
		return toAjax( payChannelNewService.updatePayChannelNew(payChannelNew) );
	}

	/**
	 * 支付平台选择列表
	 *
	 * @return
	 */
	@GetMapping("/effect-pay-platform")
	public AjaxResult findEffectPayPlatform()
	{
		PayPlatformNew payPlatformNew=new PayPlatformNew();
		List<PayPlatformNew> data = payPlatformNewService.selectPayPlatformNewList(payPlatformNew);
		if ( StringUtils.isNull( data ) ) {
			data = new ArrayList<>();
		}
		return AjaxResult.success(data);
	}
}
