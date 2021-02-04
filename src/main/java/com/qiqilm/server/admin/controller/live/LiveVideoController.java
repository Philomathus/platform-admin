package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveVideo;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveVideoService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 直播Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping( "/admin/liveVideo" )
public class LiveVideoController extends BaseController {
	@Autowired
	private ILiveVideoService liveVideoService;

/**
 * 查询直播列表
 */
@PreAuthorize( "@ss.hasPermi('admin:liveVideo:list')" )
@GetMapping( "/list" )
    	public TableDataInfo list(LiveVideo liveVideo) {
		startPage();
		List<LiveVideo> list = liveVideoService.selectLiveVideoList(liveVideo);
		return getDataTable( list );
	}

	/**
	 * 导出直播列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideo:export')" )
	@Log( title = "直播", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(LiveVideo liveVideo) {
		List<LiveVideo>      list = liveVideoService.selectLiveVideoList(liveVideo);
		ExcelUtil<LiveVideo> util = new ExcelUtil<LiveVideo>(LiveVideo. class);
		return util.exportExcel( list, "liveVideo" );
	}

	/**
	 * 获取直播详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideo:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( liveVideoService.selectLiveVideoById(id) );
	}

	@GetMapping( value = "close/{id}" )
	public AjaxResult close(@PathVariable( "id" ) Long id){
		return AjaxResult.success( liveVideoService.close(id,"admin") );
	}

	/**
	 * 新增直播
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideo:add')" )
	@Log( title = "直播", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveVideo liveVideo) {
		return toAjax( liveVideoService.insertLiveVideo(liveVideo) );
	}

	/**
	 * 修改直播
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideo:edit')" )
	@Log( title = "直播", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveVideo liveVideo) {
		return toAjax( liveVideoService.updateLiveVideo(liveVideo) );
	}

	/**
	 * 删除直播
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideo:remove')" )
	@Log( title = "直播", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( liveVideoService.deleteLiveVideoByIds( ids ) );
	}


}
