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
import com.qiqilm.server.admin.domain.LivePayLog;
import com.qiqilm.server.admin.service.ILivePayLogService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * //付费直播记录Controller
 *
 * @author 77tv
 * @date 2021-02-03
 */
@RestController
@RequestMapping( "/admin/livePayLog" )
public class LivePayLogController extends BaseController {
	@Autowired
	private ILivePayLogService livePayLogService;

	/**
	 * 查询//付费直播记录列表
	 */
	//@PreAuthorize( "@ss.hasPermi('admin:livePayLog:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LivePayLog livePayLog) {
		startPage();
		List<LivePayLog> list = livePayLogService.selectLivePayLogList(livePayLog);
		return getDataTable( list );
	}
    
	/**
	 * 导出//付费直播记录列表
	 */
	//@PreAuthorize( "@ss.hasPermi('admin:livePayLog:export')" )
	@Log( title = "//付费直播记录", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(LivePayLog livePayLog) {
		List<LivePayLog>      list = livePayLogService.selectLivePayLogList(livePayLog);
		ExcelUtil<LivePayLog> util = new ExcelUtil<>(LivePayLog.class);
		return util.exportExcel( list, "livePayLog" );
	}

	/**
	 * 获取//付费直播记录详细信息
	 */
	//@PreAuthorize( "@ss.hasPermi('admin:livePayLog:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( livePayLogService.selectLivePayLogById(id) );
	}

	/**
	 * 新增//付费直播记录
	 */
	//@PreAuthorize( "@ss.hasPermi('admin:livePayLog:add')" )
	@Log( title = "//付费直播记录", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LivePayLog livePayLog) {
		return toAjax( livePayLogService.insertLivePayLog(livePayLog) );
	}

	/**
	 * 修改//付费直播记录
	 */
	//@PreAuthorize( "@ss.hasPermi('admin:livePayLog:edit')" )
	@Log( title = "//付费直播记录", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LivePayLog livePayLog) {
		return toAjax( livePayLogService.updateLivePayLog(livePayLog) );
	}

	/**
	 * 删除//付费直播记录
	 */
	//@PreAuthorize( "@ss.hasPermi('admin:livePayLog:remove')" )
	@Log( title = "//付费直播记录", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( livePayLogService.deleteLivePayLogByIds( ids ) );
	}
}
