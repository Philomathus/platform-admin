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
import com.qiqilm.server.admin.domain.ConfigUsdtRecharge;
import com.qiqilm.server.admin.service.IConfigUsdtRechargeService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * USDT渠道 Controller
 *
 * @author 77tv
 * @date 2021-09-11
 */
@RestController
@RequestMapping( "/admin/configUsdtRecharge" )
public class ConfigUsdtRechargeController extends BaseController {
	@Autowired
	private IConfigUsdtRechargeService configUsdtRechargeService;

	/**
	 * USDT渠道列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configUsdtRecharge:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(ConfigUsdtRecharge configUsdtRecharge) {
		startPage();
		List<ConfigUsdtRecharge> list = configUsdtRechargeService.selectConfigUsdtRechargeList(configUsdtRecharge);
		return getDataTable( list );
	}
    
	/**
	 * 导出USDT渠道列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configUsdtRecharge:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(ConfigUsdtRecharge configUsdtRecharge, HttpServletResponse response) {
		List<ConfigUsdtRecharge>      list = configUsdtRechargeService.selectConfigUsdtRechargeList(configUsdtRecharge);
		ExportExcelUtil.exportExcel( list, "【请填写功能名称】", "【请填写功能名称】表", ConfigUsdtRecharge.class, response );
	}

	/**
	 * 获取USDT渠道详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configUsdtRecharge:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( configUsdtRechargeService.selectConfigUsdtRechargeById(id) );
	}

	/**
	 * 新增USDT渠道
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configUsdtRecharge:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ConfigUsdtRecharge configUsdtRecharge) {
		return toAjax( configUsdtRechargeService.insertConfigUsdtRecharge(configUsdtRecharge) );
	}

	/**
	 * 修改USDT渠道
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configUsdtRecharge:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ConfigUsdtRecharge configUsdtRecharge) {
		return toAjax( configUsdtRechargeService.updateConfigUsdtRecharge(configUsdtRecharge) );
	}

	/**
	 * 删除USDT渠道
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configUsdtRecharge:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( configUsdtRechargeService.deleteConfigUsdtRechargeByIds( ids ) );
	}
}
