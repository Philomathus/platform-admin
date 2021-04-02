package com.qiqilm.server.admin.controller.live;

import java.util.List;

import com.qiqilm.server.admin.cache.LiveCacheUtil;
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
import com.qiqilm.server.admin.domain.LiveMount;
import com.qiqilm.server.admin.service.ILiveMountService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 礼物列Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/admin/liveMount" )
public class LiveMountController extends BaseController {
	@Autowired
	private ILiveMountService liveMountService;

	@Autowired
	private LiveCacheUtil global;
	/**
	 * 查询礼物列列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveMount:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LiveMount liveMount) {
		startPage();
		List<LiveMount> list = liveMountService.selectLiveMountList(liveMount);
		return getDataTable( list );
	}
    
	/**
	 * 导出礼物列列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveMount:export')" )
	@Log( title = "礼物列", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(LiveMount liveMount) {
		List<LiveMount>      list = liveMountService.selectLiveMountList(liveMount);
		ExcelUtil<LiveMount> util = new ExcelUtil<LiveMount>(LiveMount. class);
		return util.exportExcel( list, "liveMount" );
	}

	/**
	 * 获取礼物列详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveMount:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( liveMountService.selectLiveMountById(id) );
	}

	/**
	 * 新增礼物列
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveMount:add')" )
	@Log( title = "礼物列", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveMount liveMount) {
		liveMountService.insertLiveMount(liveMount);
		global.refreshMountConfCache();
		return toAjax(1);
	}

	/**
	 * 修改礼物列
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveMount:edit')" )
	@Log( title = "礼物列", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveMount liveMount) {
		liveMountService.updateLiveMount(liveMount);
		global.refreshMountConfCache();
		return toAjax( 1);
	}

	/**
	 * 删除礼物列
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveMount:remove')" )
	@Log( title = "礼物列", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		liveMountService.deleteLiveMountByIds( ids );
		global.refreshMountConfCache();
		return toAjax( 1);
	}
}
