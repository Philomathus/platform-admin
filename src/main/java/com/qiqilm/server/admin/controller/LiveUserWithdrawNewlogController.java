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
import com.qiqilm.server.admin.domain.LiveUserWithdrawNewlog;
import com.qiqilm.server.admin.service.ILiveUserWithdrawNewlogService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 主播提现管理Controller
 *
 * @author 77tv
 * @date 2021-03-23
 */
@RestController
@RequestMapping( "/live-web/liveUserWithdrawNewlog" )
public class LiveUserWithdrawNewlogController extends BaseController {
	@Autowired
	private ILiveUserWithdrawNewlogService liveUserWithdrawNewlogService;

	/**
	 * 查询主播提现管理列表
	 */
	@PreAuthorize( "@ss.hasPermi('live-web:liveUserWithdrawNewlog:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LiveUserWithdrawNewlog liveUserWithdrawNewlog) {
		startPage();
		List<LiveUserWithdrawNewlog> list = liveUserWithdrawNewlogService.selectLiveUserWithdrawNewlogList(liveUserWithdrawNewlog);
		return getDataTable( list );
	}
    
	/**
	 * 导出主播提现管理列表
	 */
	@PreAuthorize( "@ss.hasPermi('live-web:liveUserWithdrawNewlog:export')" )
	@Log( title = "主播提现管理", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(LiveUserWithdrawNewlog liveUserWithdrawNewlog, HttpServletResponse response) {
		List<LiveUserWithdrawNewlog>      list = liveUserWithdrawNewlogService.selectLiveUserWithdrawNewlogList(liveUserWithdrawNewlog);
		ExportExcelUtil.exportExcel( list, "主播提现管理", "主播提现管理表", LiveUserWithdrawNewlog.class, response );
	}

	/**
	 * 获取主播提现管理详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('live-web:liveUserWithdrawNewlog:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( liveUserWithdrawNewlogService.selectLiveUserWithdrawNewlogById(id) );
	}

	/**
	 * 新增主播提现管理
	 */
	@PreAuthorize( "@ss.hasPermi('live-web:liveUserWithdrawNewlog:add')" )
	@Log( title = "主播提现管理", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveUserWithdrawNewlog liveUserWithdrawNewlog) {
		return toAjax( liveUserWithdrawNewlogService.insertLiveUserWithdrawNewlog(liveUserWithdrawNewlog) );
	}

	/**
	 * 修改主播提现管理
	 */
	@PreAuthorize( "@ss.hasPermi('live-web:liveUserWithdrawNewlog:edit')" )
	@Log( title = "主播提现管理", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveUserWithdrawNewlog liveUserWithdrawNewlog) {
		return toAjax( liveUserWithdrawNewlogService.updateLiveUserWithdrawNewlog(liveUserWithdrawNewlog) );
	}

	/**
	 * 删除主播提现管理
	 */
	@PreAuthorize( "@ss.hasPermi('live-web:liveUserWithdrawNewlog:remove')" )
	@Log( title = "主播提现管理", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( liveUserWithdrawNewlogService.deleteLiveUserWithdrawNewlogByIds( ids ) );
	}
}
