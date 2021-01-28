package com.qiqilm.server.admin.controller.live;

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
import com.qiqilm.server.admin.domain.LiveGuardConfig;
import com.qiqilm.server.admin.service.ILiveGuardConfigService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/admin/liveGuardConfig" )
public class LiveGuardConfigController extends BaseController {
	@Autowired
	private ILiveGuardConfigService liveGuardConfigService;

	/**
	 * 查询【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveGuardConfig:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LiveGuardConfig liveGuardConfig) {
		startPage();
		List<LiveGuardConfig> list = liveGuardConfigService.selectLiveGuardConfigList(liveGuardConfig);
		return getDataTable( list );
	}
    
	/**
	 * 导出【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveGuardConfig:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(LiveGuardConfig liveGuardConfig) {
		List<LiveGuardConfig>      list = liveGuardConfigService.selectLiveGuardConfigList(liveGuardConfig);
		ExcelUtil<LiveGuardConfig> util = new ExcelUtil<LiveGuardConfig>(LiveGuardConfig. class);
		return util.exportExcel( list, "liveGuardConfig" );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveGuardConfig:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( liveGuardConfigService.selectLiveGuardConfigById(id) );
	}

	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveGuardConfig:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveGuardConfig liveGuardConfig) {
		return toAjax( liveGuardConfigService.insertLiveGuardConfig(liveGuardConfig) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveGuardConfig:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveGuardConfig liveGuardConfig) {
		return toAjax( liveGuardConfigService.updateLiveGuardConfig(liveGuardConfig) );
	}

	/**
	 * 删除【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveGuardConfig:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( liveGuardConfigService.deleteLiveGuardConfigByIds( ids ) );
	}
}
