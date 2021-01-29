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
import com.qiqilm.server.admin.domain.ConfigBank;
import com.qiqilm.server.admin.service.IConfigBankService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/pay/configBank" )
public class ConfigBankController extends BaseController {
	@Autowired
	private IConfigBankService configBankService;

	/**
	 * 查询【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configBank:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(ConfigBank configBank) {
		startPage();
		List<ConfigBank> list = configBankService.selectConfigBankList(configBank);
		return getDataTable( list );
	}
    
	/**
	 * 导出【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configBank:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(ConfigBank configBank) {
		List<ConfigBank>      list = configBankService.selectConfigBankList(configBank);
		ExcelUtil<ConfigBank> util = new ExcelUtil<ConfigBank>(ConfigBank. class);
		return util.exportExcel( list, "configBank" );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configBank:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( configBankService.selectConfigBankById(id) );
	}

	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configBank:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ConfigBank configBank) {
		return toAjax( configBankService.insertConfigBank(configBank) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configBank:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ConfigBank configBank) {
		return toAjax( configBankService.updateConfigBank(configBank) );
	}

	/**
	 * 删除【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configBank:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( configBankService.deleteConfigBankByIds( ids ) );
	}

	/**
	 * 支付状态修改
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configBank:edit')" )
	@Log( title = "支付类型", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeStatus" )
	public AjaxResult changeStatus( @RequestBody ConfigBank configBank ) {
		return toAjax(configBankService.updateConfigBank(configBank));
	}
}
