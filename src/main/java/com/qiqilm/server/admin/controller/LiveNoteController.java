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
import com.qiqilm.server.admin.domain.LiveNote;
import com.qiqilm.server.admin.service.ILiveNoteService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 彩票注单Controller
 *
 * @author 77tv
 * @date 2021-03-19
 */
@RestController
@RequestMapping( "/admin/liveNote" )
public class LiveNoteController extends BaseController {
	@Autowired
	private ILiveNoteService liveNoteService;

	/**
	 * 查询彩票注单列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveNote:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LiveNote liveNote) {
		startPage();
		List<LiveNote> list = liveNoteService.selectLiveNoteList(liveNote);
		return getDataTable( list );
	}
    
	/**
	 * 导出彩票注单列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveNote:export')" )
	@Log( title = "彩票注单", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(LiveNote liveNote, HttpServletResponse response) {
		List<LiveNote>      list = liveNoteService.selectLiveNoteList(liveNote);
		ExportExcelUtil.exportExcel( list, "彩票注单", "彩票注单表", LiveNote.class, response );
	}

	/**
	 * 获取彩票注单详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveNote:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( liveNoteService.selectLiveNoteById(id) );
	}

	/**
	 * 新增彩票注单
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveNote:add')" )
	@Log( title = "彩票注单", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveNote liveNote) {
		return toAjax( liveNoteService.insertLiveNote(liveNote) );
	}

	/**
	 * 修改彩票注单
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveNote:edit')" )
	@Log( title = "彩票注单", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveNote liveNote) {
		return toAjax( liveNoteService.updateLiveNote(liveNote) );
	}

	/**
	 * 删除彩票注单
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveNote:remove')" )
	@Log( title = "彩票注单", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( liveNoteService.deleteLiveNoteByIds( ids ) );
	}
}
