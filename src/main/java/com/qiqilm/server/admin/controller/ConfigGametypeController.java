package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ConfigGametype;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IConfigGametypeService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/web/config-gametype" )
public class ConfigGametypeController extends BaseController {
	@Autowired
	private IConfigGametypeService configGametypeService;

/**
 * 查询【请填写功能名称】列表
 */
@PreAuthorize( "@ss.hasPermi('web:config-gametype:list')" )
@GetMapping( "/list" )
    	public TableDataInfo list(ConfigGametype configGametype) {
		startPage();
		List<ConfigGametype> list = configGametypeService.selectConfigGametypeList(configGametype);
		return getDataTable( list );
	}
    
	/**
	 * 导出【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('web:config-gametype:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(ConfigGametype configGametype) {
		List<ConfigGametype>      list = configGametypeService.selectConfigGametypeList(configGametype);
		ExcelUtil<ConfigGametype> util = new ExcelUtil<ConfigGametype>(ConfigGametype. class);
		return util.exportExcel( list, "config-gametype" );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('web:config-gametype:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( configGametypeService.selectConfigGametypeById(id) );
	}

	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('web:config-gametype:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ConfigGametype configGametype) {
		return toAjax( configGametypeService.insertConfigGametype(configGametype) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('web:config-gametype:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ConfigGametype configGametype) {
		return toAjax( configGametypeService.updateConfigGametype(configGametype) );
	}

	/**
	 * 删除【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('web:config-gametype:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( configGametypeService.deleteConfigGametypeByIds( ids ) );
	}
}