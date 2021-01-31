package com.qiqilm.server.admin.controller;

import com.google.common.collect.ImmutableMap;
import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ConfigTradeType;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IConfigTradeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资金交易类型Controller
 *
 * @author 77tv
 * @date 2021-01-29
 */
@RestController
@RequestMapping( "/config/tradeType" )
public class ConfigTradeTypeController extends BaseController {
	@Autowired
	private IConfigTradeTypeService configTradeTypeService;

	/**
	 * 查询资金交易类型列表
	 */
	@PreAuthorize( "@ss.hasPermi('config:tradeType:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( ConfigTradeType configTradeType ) {
		startPage();
		List<ConfigTradeType> list = configTradeTypeService.selectConfigTradeTypeList( configTradeType );
		return getDataTable( list );
	}

	/**
	 * 查询资金交易类型列表
	 */
	@PreAuthorize( "@ss.hasPermi('config:tradeType:list')" )
	@GetMapping( "/all" )
	public AjaxResult list() {
		ConfigTradeType configTradeType = new ConfigTradeType();
		configTradeType.setParams( ImmutableMap.of( "orderBy", "type asc" ) );
		return AjaxResult.success( configTradeTypeService.selectConfigTradeTypeList( configTradeType ) );
	}

	/**
	 * 获取资金交易类型详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('config:tradeType:query')" )
	@GetMapping( value = "/{type}" )
	public AjaxResult getInfo( @PathVariable( "type" ) Long type ) {
		return AjaxResult.success( configTradeTypeService.selectConfigTradeTypeById( type ) );
	}

	/**
	 * 新增资金交易类型
	 */
	@PreAuthorize( "@ss.hasPermi('config:tradeType:add')" )
	@Log( title = "资金交易类型", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ConfigTradeType configTradeType ) {
		return toAjax( configTradeTypeService.insertConfigTradeType( configTradeType ) );
	}

	/**
	 * 修改资金交易类型
	 */
	@PreAuthorize( "@ss.hasPermi('config:tradeType:edit')" )
	@Log( title = "资金交易类型", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ConfigTradeType configTradeType ) {
		return toAjax( configTradeTypeService.updateConfigTradeType( configTradeType ) );
	}

	/**
	 * 删除资金交易类型
	 */
	@PreAuthorize( "@ss.hasPermi('config:tradeType:remove')" )
	@Log( title = "资金交易类型", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{types}" )
	public AjaxResult remove( @PathVariable Long[] types ) {
		return toAjax( configTradeTypeService.deleteConfigTradeTypeByIds( types ) );
	}
}