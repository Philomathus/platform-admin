package com.qiqilm.server.admin.controller;

import java.util.List;

import com.qiqilm.server.admin.domain.BankList;
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
import com.qiqilm.server.admin.domain.BankCardAddress;
import com.qiqilm.server.admin.service.IBankCardAddressService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-04-21
 */
@RestController
@RequestMapping( "/admin/bankCardAddress" )
public class BankCardAddressController extends BaseController {
	@Autowired
	private IBankCardAddressService bankCardAddressService;

//	/**
//	 * 查询【请填写功能名称】列表
//	 */
//	@PreAuthorize( "@ss.hasPermi('admin:bankCardAddress:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(BankCardAddress bankCardAddress) {
		startPage();
		List<BankCardAddress> list = bankCardAddressService.selectBankCardAddressList(bankCardAddress);
		return getDataTable( list );
	}
    
	/**
	 * 导出【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:bankCardAddress:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(BankCardAddress bankCardAddress, HttpServletResponse response) {
		List<BankCardAddress>      list = bankCardAddressService.selectBankCardAddressList(bankCardAddress);
		ExportExcelUtil.exportExcel( list, "【请填写功能名称】", "【请填写功能名称】表", BankCardAddress.class, response );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:bankCardAddress:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( bankCardAddressService.selectBankCardAddressById(id) );
	}

	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:bankCardAddress:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody BankCardAddress bankCardAddress) {
		return toAjax( bankCardAddressService.insertBankCardAddress(bankCardAddress) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:bankCardAddress:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody BankCardAddress bankCardAddress) {
		return toAjax( bankCardAddressService.updateBankCardAddress(bankCardAddress) );
	}

	/**
	 * 删除【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:bankCardAddress:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( bankCardAddressService.deleteBankCardAddressByIds( ids ) );
	}
	@PreAuthorize( "@ss.hasPermi('pay:configBank:edit')" )
	@Log( title = "出款银行状态", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeBankAddressStatus" )
	public AjaxResult changeStatus( @RequestBody BankCardAddress bankCardAddress ) {
		return toAjax(bankCardAddressService.updateBankCardAddress(bankCardAddress));
	}
}