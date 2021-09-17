package com.qiqilm.server.admin.controller;

import java.util.List;

import com.qiqilm.server.admin.domain.ActivityCashBack;
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
import com.qiqilm.server.admin.domain.WheelDiceConfig;
import com.qiqilm.server.admin.service.IWheelDiceConfigService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 【抽奖配置】Controller
 *
 * @author 77tv
 * @date 2021-09-01
 */
@RestController
@RequestMapping( "/admin/wheelDiceConfig" )
public class WheelDiceConfigController extends BaseController {
	@Autowired
	private IWheelDiceConfigService wheelDiceConfigService;

	/**
	 * 查询【抽奖配置】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelDiceConfig:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(WheelDiceConfig wheelDiceConfig) {
		startPage();
		List<WheelDiceConfig> list = wheelDiceConfigService.selectWheelDiceConfigList(wheelDiceConfig);
		return getDataTable( list );
	}
    
	/**
	 * 导出【抽奖配置】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelDiceConfig:export')" )
	@Log( title = "抽奖配置", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(WheelDiceConfig wheelDiceConfig, HttpServletResponse response) {
		List<WheelDiceConfig>      list = wheelDiceConfigService.selectWheelDiceConfigList(wheelDiceConfig);
		ExportExcelUtil.exportExcel( list, "【抽奖配置】", "【抽奖配置】表", WheelDiceConfig.class, response );
	}

	/**
	 * 获取【抽奖配置】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelDiceConfig:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( wheelDiceConfigService.selectWheelDiceConfigById(id) );
	}

	/**
	 * 新增【抽奖配置】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelDiceConfig:add')" )
	@Log( title = "【抽奖配置】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody WheelDiceConfig wheelDiceConfig) {
		return toAjax( wheelDiceConfigService.insertWheelDiceConfig(wheelDiceConfig) );
	}

	/**
	 * 修改【抽奖配置】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelDiceConfig:edit')" )
	@Log( title = "【抽奖配置】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody WheelDiceConfig wheelDiceConfig) {
		return toAjax( wheelDiceConfigService.updateWheelDiceConfig(wheelDiceConfig) );
	}

	/**
	 * 删除【抽奖配置】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelDiceConfig:remove')" )
	@Log( title = "【抽奖配置】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( wheelDiceConfigService.deleteWheelDiceConfigByIds( ids ) );
	}

	/**
	 * 返现活动状态修改
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelDiceConfig:edit')" )
	@Log( title = "抽奖配置状态", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeStatus" )
	public AjaxResult changeStatus( @RequestBody WheelDiceConfig wheelDiceConfig ) {
		return toAjax(wheelDiceConfigService.updateWheelDiceConfig(wheelDiceConfig));
	}
}
