package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveVideoProp;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveVideoPropService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 送礼物Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/admin/liveVideoProp" )
public class LiveVideoPropController extends BaseController {
	@Autowired
	private ILiveVideoPropService liveVideoPropService;

	/**
	 * 查询送礼物列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LiveVideoProp liveVideoProp) {
		startPage();
		List<LiveVideoProp> list = liveVideoPropService.selectLiveVideoPropList(liveVideoProp);
		return getDataTable( list );
	}

	/**
	 * 导出送礼物列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:export')" )
	@Log( title = "送礼物", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(LiveVideoProp liveVideoProp) {
		List<LiveVideoProp>      list = liveVideoPropService.selectLiveVideoPropList(liveVideoProp);
		ExcelUtil<LiveVideoProp> util = new ExcelUtil<LiveVideoProp>(LiveVideoProp. class);
		return util.exportExcel( list, "liveVideoProp" );
	}

	/**
	 * 获取送礼物详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( liveVideoPropService.selectLiveVideoPropById(id) );
	}

	/**
	 * 新增送礼物
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:add')" )
	@Log( title = "送礼物", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveVideoProp liveVideoProp) {
		return toAjax( liveVideoPropService.insertLiveVideoProp(liveVideoProp) );
	}

	/**
	 * 修改送礼物
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:edit')" )
	@Log( title = "送礼物", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveVideoProp liveVideoProp) {
		return toAjax( liveVideoPropService.updateLiveVideoProp(liveVideoProp) );
	}

	/**
	 * 删除送礼物
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:remove')" )
	@Log( title = "送礼物", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( liveVideoPropService.deleteLiveVideoPropByIds( ids ) );
	}
}
