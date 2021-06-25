package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ConfigWaiter;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IConfigWaiterService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 客服管理Controller
 *
 * @author 77tv
 * @date 2021-03-03
 */
@RestController
@RequestMapping( "/admin/configWaiter" )
public class ConfigWaiterController extends BaseController {
	@Autowired
	private IConfigWaiterService configWaiterService;

	/**
	 * 查询客服管理列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configWaiter:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( ConfigWaiter configWaiter ) {
		startPage();
		List<ConfigWaiter> list = configWaiterService.selectConfigWaiterList( configWaiter );
		return getDataTable( list );
	}

	/**
	 * 导出客服管理列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configWaiter:export')" )
	@Log( title = "客服管理", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( ConfigWaiter configWaiter, HttpServletResponse response ) {
		List<ConfigWaiter> list = configWaiterService.selectConfigWaiterList( configWaiter );
		ExportExcelUtil.exportExcel( list, "公司入款", "公司入款信息表", ConfigWaiter.class, response );
	}

	/**
	 * 获取客服管理详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configWaiter:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id ) {
		return AjaxResult.success( configWaiterService.selectConfigWaiterById( id ) );
	}

	/**
	 * 新增客服管理
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configWaiter:add')" )
	@Log( title = "客服管理", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ConfigWaiter configWaiter ) {
		configWaiter.setId( UuidUtil.getRandomUuidWithoutSeparator() );
		return toAjax( configWaiterService.insertConfigWaiter( configWaiter ) );
	}

	/**
	 * 修改客服管理
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configWaiter:edit')" )
	@Log( title = "客服管理", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ConfigWaiter configWaiter ) {
		return toAjax( configWaiterService.updateConfigWaiter( configWaiter ) );
	}

	/**
	 * 删除客服管理
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configWaiter:remove')" )
	@Log( title = "客服管理", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( configWaiterService.deleteConfigWaiterByIds( ids ) );
	}

	/**
	 * 修改启用状态
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configWaiter:edit')" )
	@Log( title = "客服管理", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeStatus" )
	public AjaxResult changeStatus( @RequestBody ConfigWaiter configWaitere ) {
		return toAjax( configWaiterService.updateConfigWaiter( configWaitere ) );
	}
}
