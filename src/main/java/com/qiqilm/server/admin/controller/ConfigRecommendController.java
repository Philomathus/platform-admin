package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ConfigRecommend;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IConfigRecommendService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 推广设置Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/admin/configRecommend" )
public class ConfigRecommendController extends BaseController {
	@Autowired
	private IConfigRecommendService configRecommendService;

	/**
	 * 查询推广设置列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configRecommend:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( ConfigRecommend configRecommend ) {
		startPage();
		List<ConfigRecommend> list = configRecommendService.selectConfigRecommendList( configRecommend );
		return getDataTable( list );
	}

	/**
	 * 导出推广设置列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configRecommend:export')" )
	@Log( title = "推广设置", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( ConfigRecommend configRecommend, HttpServletResponse response ) {
		List<ConfigRecommend> list = configRecommendService.selectConfigRecommendList( configRecommend );
		ExportExcelUtil.exportExcel( list, "推广设置", "推广设置表", ConfigRecommend.class, response );
	}

	/**
	 * 获取推广设置详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configRecommend:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id ) {
		return AjaxResult.success( configRecommendService.selectConfigRecommendById( id ) );
	}

	/**
	 * 新增推广设置
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configRecommend:add')" )
	@Log( title = "推广设置", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ConfigRecommend configRecommend ) {
		ConfigRecommend       a    = new ConfigRecommend();
		List<ConfigRecommend> list = configRecommendService.selectConfigRecommendList( a );
		configRecommend.setId( String.valueOf( list.size() + 1 ) );
		//		configRecommend.setId(UuidUtil.getRandomUuidWithoutSeparator());
		return toAjax( configRecommendService.insertConfigRecommend( configRecommend ) );
	}

	/**
	 * 修改推广设置
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configRecommend:edit')" )
	@Log( title = "推广设置", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ConfigRecommend configRecommend ) {
		return toAjax( configRecommendService.updateConfigRecommend( configRecommend ) );
	}

	/**
	 * 删除推广设置
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configRecommend:remove')" )
	@Log( title = "推广设置", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( configRecommendService.deleteConfigRecommendByIds( ids ) );
	}
}
