package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveHostWageNote;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveHostWageNoteService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-27
 */
@RestController
@RequestMapping( "/admin/liveHostWageNote" )
public class LiveHostWageNoteController extends BaseController {
	@Autowired
	private ILiveHostWageNoteService liveHostWageNoteService;

	/**
	 * 查询【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LiveHostWageNote liveHostWageNote) {
		startPage();
		List<LiveHostWageNote> list = liveHostWageNoteService.selectLiveHostWageNoteList(liveHostWageNote);
		return getDataTable( list );
	}

	/**
	 * 导出【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(LiveHostWageNote liveHostWageNote) {
		List<LiveHostWageNote>      list = liveHostWageNoteService.selectLiveHostWageNoteList(liveHostWageNote);
		ExcelUtil<LiveHostWageNote> util = new ExcelUtil<LiveHostWageNote>(LiveHostWageNote. class);
		return util.exportExcel( list, "liveHostWageNote" );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
        return AjaxResult.success( liveHostWageNoteService.selectLiveHostWageNoteById(id) );
	}

	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveHostWageNote liveHostWageNote) {
		return toAjax( liveHostWageNoteService.insertLiveHostWageNote(liveHostWageNote) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveHostWageNote liveHostWageNote) {
		return toAjax( liveHostWageNoteService.updateLiveHostWageNote(liveHostWageNote) );
	}

	/**
	 * 删除【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( liveHostWageNoteService.deleteLiveHostWageNoteByIds( ids ) );
	}
}
