package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ConfigDomain;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IConfigDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 域名配置Controller
 *
 * @author 77tv
 * @date 2021-01-27
 */
@RestController
@RequestMapping( "/config/domain" )
public class ConfigDomainController extends BaseController {
	@Autowired
	private IConfigDomainService configDomainService;

	/**
	 * 查询域名配置列表
	 */
	@PreAuthorize( "@ss.hasPermi('config:domain:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list(ConfigDomain configDomain) {
		startPage();
		List<ConfigDomain> list = configDomainService.selectConfigDomainList(configDomain);
		return getDataTable( list );
	}

	/**
	 * 获取域名配置详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('config:domain:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( configDomainService.selectConfigDomainById(id) );
	}

	/**
	 * 新增域名配置
	 */
	@PreAuthorize( "@ss.hasPermi('config:domain:add')" )
	@Log( title = "域名配置", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ConfigDomain configDomain) {
		return toAjax( configDomainService.insertConfigDomain(configDomain) );
	}

	/**
	 * 修改域名配置
	 */
	@PreAuthorize( "@ss.hasPermi('config:domain:edit')" )
	@Log( title = "域名配置", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ConfigDomain configDomain) {
		return toAjax( configDomainService.updateConfigDomain(configDomain) );
	}

	/**
	 * 删除域名配置
	 */
	@PreAuthorize( "@ss.hasPermi('config:domain:remove')" )
	@Log( title = "域名配置", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( configDomainService.deleteConfigDomainByIds( ids ) );
	}
}