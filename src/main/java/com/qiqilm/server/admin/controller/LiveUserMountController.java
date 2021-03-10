package com.qiqilm.server.admin.controller;

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
import com.qiqilm.server.admin.domain.LiveUserMount;
import com.qiqilm.server.admin.service.ILiveUserMountService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 直播间会员坐骑Controller
 *
 * @author 77tv
 * @date 2021-03-09
 */
@RestController
@RequestMapping( "/live/liveUserMount" )
public class LiveUserMountController extends BaseController {
	@Autowired
	private ILiveUserMountService liveUserMountService;

	/**
	 * 查询直播间会员坐骑列表
	 */
	@PreAuthorize( "@ss.hasPermi('live:liveUserMount:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LiveUserMount liveUserMount) {
		startPage();
		List<LiveUserMount> list = liveUserMountService.selectLiveUserMountList(liveUserMount);
		return getDataTable( list );
	}
    
	/**
	 * 导出直播间会员坐骑列表
	 */
	@PreAuthorize( "@ss.hasPermi('live:liveUserMount:export')" )
	@Log( title = "直播间会员坐骑", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(LiveUserMount liveUserMount) {
		List<LiveUserMount>      list = liveUserMountService.selectLiveUserMountList(liveUserMount);
		ExcelUtil<LiveUserMount> util = new ExcelUtil<>(LiveUserMount.class);
		return util.exportExcel( list, "liveUserMount" );
	}

	/**
	 * 获取直播间会员坐骑详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('live:liveUserMount:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( liveUserMountService.selectLiveUserMountById(id) );
	}

	/**
	 * 新增直播间会员坐骑
	 */
	@PreAuthorize( "@ss.hasPermi('live:liveUserMount:add')" )
	@Log( title = "直播间会员坐骑", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveUserMount liveUserMount) {
		return toAjax( liveUserMountService.insertLiveUserMount(liveUserMount) );
	}

	/**
	 * 修改直播间会员坐骑
	 */
	@PreAuthorize( "@ss.hasPermi('live:liveUserMount:edit')" )
	@Log( title = "直播间会员坐骑", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveUserMount liveUserMount) {
		return toAjax( liveUserMountService.updateLiveUserMount(liveUserMount) );
	}

	/**
	 * 删除直播间会员坐骑
	 */
	@PreAuthorize( "@ss.hasPermi('live:liveUserMount:remove')" )
	@Log( title = "直播间会员坐骑", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( liveUserMountService.deleteLiveUserMountByIds( ids ) );
	}
}
