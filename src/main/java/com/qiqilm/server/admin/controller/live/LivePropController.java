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
import com.qiqilm.server.admin.domain.LiveProp;
import com.qiqilm.server.admin.service.ILivePropService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 礼物列Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping( "/admin/liveProp" )
public class LivePropController extends BaseController {
	@Autowired
	private ILivePropService livePropService;

	/**
	 * 查询礼物列列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveProp:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LiveProp liveProp) {
		startPage();
		List<LiveProp> list = livePropService.selectLivePropList(liveProp);
		return getDataTable( list );
	}

	/**
	 * 查询礼物列列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveProp:list')" )
	@GetMapping( "/getList" )
	public TableDataInfo getList() {
		List<LiveProp> list = livePropService.getList();
		return getDataTable( list );
	}

	/**
	 * 导出礼物列列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveProp:export')" )
	@Log( title = "礼物列", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(LiveProp liveProp) {
		List<LiveProp>      list = livePropService.selectLivePropList(liveProp);
		ExcelUtil<LiveProp> util = new ExcelUtil<LiveProp>(LiveProp. class);
		return util.exportExcel( list, "liveProp" );
	}

	/**
	 * 获取礼物列详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveProp:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( livePropService.selectLivePropById(id) );
	}

	/**
	 * 新增礼物列
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveProp:add')" )
	@Log( title = "礼物列", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveProp liveProp) {
		return toAjax( livePropService.insertLiveProp(liveProp) );
	}

	/**
	 * 修改礼物列
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveProp:edit')" )
	@Log( title = "礼物列", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveProp liveProp) {
		return toAjax( livePropService.updateLiveProp(liveProp) );
	}

	/**
	 * 删除礼物列
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveProp:remove')" )
	@Log( title = "礼物列", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( livePropService.deleteLivePropByIds( ids ) );
	}
}
