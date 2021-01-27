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
import com.qiqilm.server.admin.domain.ConfigEnvironment;
import com.qiqilm.server.admin.service.IConfigEnvironmentService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-27
 */
@RestController
@RequestMapping( "/admin/configEnvironment" )
public class ConfigEnvironmentController extends BaseController {
	@Autowired
	private IConfigEnvironmentService configEnvironmentService;

	/**
	 * 查询【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configEnvironment:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(ConfigEnvironment configEnvironment) {
		startPage();
		List<ConfigEnvironment> list = configEnvironmentService.selectConfigEnvironmentList(configEnvironment);
		return getDataTable( list );
	}
    
	/**
	 * 导出【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configEnvironment:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(ConfigEnvironment configEnvironment) {
		List<ConfigEnvironment>      list = configEnvironmentService.selectConfigEnvironmentList(configEnvironment);
		ExcelUtil<ConfigEnvironment> util = new ExcelUtil<ConfigEnvironment>(ConfigEnvironment. class);
		return util.exportExcel( list, "configEnvironment" );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configEnvironment:query')" )
	@GetMapping( value = "/{envCode}" )
	public AjaxResult getInfo( @PathVariable( "envCode" ) String envCode) {
		return AjaxResult.success( configEnvironmentService.selectConfigEnvironmentById(envCode) );
	}

	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configEnvironment:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ConfigEnvironment configEnvironment) {
		return toAjax( configEnvironmentService.insertConfigEnvironment(configEnvironment) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configEnvironment:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ConfigEnvironment configEnvironment) {
		return toAjax( configEnvironmentService.updateConfigEnvironment(configEnvironment) );
	}

	/**
	 * 删除【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configEnvironment:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{envCodes}" )
	public AjaxResult remove( @PathVariable String[] envCodes ) {
		return toAjax( configEnvironmentService.deleteConfigEnvironmentByIds( envCodes ) );
	}
}
