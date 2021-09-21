package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveMount;
import com.qiqilm.server.admin.domain.LiveUserMount;
import com.qiqilm.server.admin.domain.PayPlatformNew;
import com.qiqilm.server.admin.domain.rsp.RspPayPlatformNew;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveMountService;
import com.qiqilm.server.admin.service.ILiveUserMountService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

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
	@Autowired
	private ILiveMountService liveMountService;

	/**
	 * 查询直播间会员坐骑列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUserMount:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( LiveUserMount liveUserMount ) {
		startPage();
		List<LiveUserMount> list = liveUserMountService.selectLiveUserMountList( liveUserMount );
		return getDataTable( list );
	}

	/**
	 * 坐骑名称下拉列表
	 *
	 * @return
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUserMount:list')" )
	@GetMapping( "/mountNames" )
	public AjaxResult mountNames() {
		LiveMount liveMount  = new LiveMount();
		List<LiveMount> data           = liveMountService.selectLiveMountList(liveMount);
		if ( StringUtils.isNull( data ) ) {
			data = new ArrayList<>();
		}
		return AjaxResult.success( data );
	}

	/**
	 * 导出直播间会员坐骑列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUserMount:export')" )
	@Log( title = "直播间会员坐骑", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( LiveUserMount liveUserMount, HttpServletResponse response ) {
		List<LiveUserMount> list = liveUserMountService.selectLiveUserMountList( liveUserMount );
		ExportExcelUtil.exportExcel( list, "直播间会员坐骑", "直播间会员坐骑表", LiveUserMount.class, response );
	}

	/**
	 * 获取直播间会员坐骑详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUserMount:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		return AjaxResult.success( liveUserMountService.selectLiveUserMountById( id ) );
	}

	/**
	 * 新增直播间会员坐骑
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUserMount:add')" )
	@Log( title = "直播间会员坐骑", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveUserMount liveUserMount ) {
		return toAjax( liveUserMountService.insertLiveUserMount( liveUserMount ) );
	}

	/**
	 * 修改直播间会员坐骑
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUserMount:edit')" )
	@Log( title = "直播间会员坐骑", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveUserMount liveUserMount ) {
		return toAjax( liveUserMountService.updateLiveUserMount( liveUserMount ) );
	}

	/**
	 * 删除直播间会员坐骑
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUserMount:remove')" )
	@Log( title = "直播间会员坐骑", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( liveUserMountService.deleteLiveUserMountByIds( ids ) );
	}
}
