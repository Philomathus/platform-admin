package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.H5Plugin;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IH5PluginService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * h5插件Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/admin/h5Plugin" )
public class H5PluginController extends BaseController {
	@Autowired
	private IH5PluginService h5PluginService;

	/**
	 * 查询h5插件列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:h5Plugin:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( H5Plugin h5Plugin ) {
		startPage();
		List<H5Plugin> list = h5PluginService.selectH5PluginList( h5Plugin );
		return getDataTable( list );
	}

	/**
	 * 导出h5插件列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:h5Plugin:export')" )
	@Log( title = "h5插件", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( H5Plugin h5Plugin, HttpServletResponse response ) {
		List<H5Plugin> list = h5PluginService.selectH5PluginList( h5Plugin );
		ExportExcelUtil.exportExcel( list, "h5插件", "h5插件表", H5Plugin.class, response );
	}

	/**
	 * 获取h5插件详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:h5Plugin:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		return AjaxResult.success( h5PluginService.selectH5PluginById( id ) );
	}

	/**
	 * 新增h5插件
	 */
	@PreAuthorize( "@ss.hasPermi('admin:h5Plugin:add')" )
	@Log( title = "h5插件", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody H5Plugin h5Plugin ) {
		return toAjax( h5PluginService.insertH5Plugin( h5Plugin ) );
	}

	/**
	 * 修改h5插件
	 */
	@PreAuthorize( "@ss.hasPermi('admin:h5Plugin:edit')" )
	@Log( title = "h5插件", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody H5Plugin h5Plugin ) {
		return toAjax( h5PluginService.updateH5Plugin( h5Plugin ) );
	}

	/**
	 * 删除h5插件
	 */
	@PreAuthorize( "@ss.hasPermi('admin:h5Plugin:remove')" )
	@Log( title = "h5插件", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( h5PluginService.deleteH5PluginByIds( ids ) );
	}
}
