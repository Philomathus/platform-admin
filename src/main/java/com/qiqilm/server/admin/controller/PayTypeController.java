package com.qiqilm.server.admin.controller;

import java.util.List;

import com.qiqilm.server.admin.domain.SysRole;
import com.qiqilm.server.admin.utils.SecurityUtils;
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
import com.qiqilm.server.admin.domain.PayType;
import com.qiqilm.server.admin.service.IPayTypeService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping( "/pay/payType" )
public class PayTypeController extends BaseController {
	@Autowired
	private IPayTypeService payTypeService;

/**
 * 查询【支付类型】列表
 */
@PreAuthorize( "@ss.hasPermi('pay:payType:list')" )
@GetMapping( "/list" )
    	public TableDataInfo list(PayType payType) {
		startPage();
		List<PayType> list = payTypeService.selectPayTypeList(payType);
		return getDataTable( list );
	}
    
	/**
	 * 导出【支付类型】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payType:export')" )
	@Log( title = "【支付类型】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(PayType payType) {
		List<PayType>      list = payTypeService.selectPayTypeList(payType);
		ExcelUtil<PayType> util = new ExcelUtil<PayType>(PayType. class);
		return util.exportExcel( list, "payType" );
	}

	/**
	 * 获取【支付类型】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payType:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( payTypeService.selectPayTypeById(id) );
	}

	/**
	 * 新增【支付类型】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payType:add')" )
	@Log( title = "【支付类型】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody PayType payType) {
		return toAjax( payTypeService.insertPayType(payType) );
	}

	/**
	 * 修改【支付类型】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payType:edit')" )
	@Log( title = "【支付类型】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayType payType) {
		return toAjax( payTypeService.updatePayType(payType) );
	}

	/**
	 * 删除【支付类型】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payType:remove')" )
	@Log( title = "【支付类型】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( payTypeService.deletePayTypeByIds( ids ) );
	}

	/**
	 * 支付状态修改
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payType:edit')" )
	@Log( title = "支付类型", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeStatus" )
	public AjaxResult changeStatus( @RequestBody PayType payType ) {
		return toAjax( payTypeService.updatePayType(payType));
	}
}
