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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 主播时长Controller
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
	 * 查询主播时长列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LiveHostWageNote liveHostWageNote) {
		startPage();
		List<LiveHostWageNote> list = liveHostWageNoteService.selectLiveHostWageNoteList(liveHostWageNote);
		return getDataTable( list );
	}

    @PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:list')" )
    @GetMapping( "familyPage" )
    public TableDataInfo familyPage(LiveHostWageNote dto ) {
        if(dto.getSelectDate()==null||dto.getSelectDate().length==0||dto.getSelectDate()[0]==null){
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateNowStr = sdf.format(d);
            dto.getSelectDate()[0] = dateNowStr;
            dto.getSelectDate()[1] = dateNowStr;
        }
        dto.setStartTime(dto.getSelectDate()[0]);
        dto.setEndTime(dto.getSelectDate()[1]);
//        startPage();
        List<LiveHostWageNote> list = liveHostWageNoteService.familyPage(dto);
        return getDataTable( list );
    }

    @PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:list')" )
    @GetMapping( "getPage" )
    public TableDataInfo getPage(LiveHostWageNote dto ) {
        if(StringUtils.isEmpty(dto.getStartTime())){
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateNowStr = sdf.format(d);
            dto.setStartTime(dateNowStr);
            dto.setEndTime(dateNowStr);
        }
        startPage();
        List<LiveHostWageNote> list = liveHostWageNoteService.getPage(dto);
        return getDataTable( list );
    }

	/**
	 * 导出主播时长列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:export')" )
	@Log( title = "主播时长", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(LiveHostWageNote liveHostWageNote) {
		List<LiveHostWageNote>      list = liveHostWageNoteService.selectLiveHostWageNoteList(liveHostWageNote);
		ExcelUtil<LiveHostWageNote> util = new ExcelUtil<LiveHostWageNote>(LiveHostWageNote. class);
		return util.exportExcel( list, "liveHostWageNote" );
	}

	/**
	 * 获取主播时长详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
        return AjaxResult.success( liveHostWageNoteService.selectLiveHostWageNoteById(id) );
	}

	/**
	 * 新增主播时长
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:add')" )
	@Log( title = "主播时长", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveHostWageNote liveHostWageNote) {
		return toAjax( liveHostWageNoteService.insertLiveHostWageNote(liveHostWageNote) );
	}

	/**
	 * 修改主播时长
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:edit')" )
	@Log( title = "主播时长", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveHostWageNote liveHostWageNote) {
		return toAjax( liveHostWageNoteService.updateLiveHostWageNote(liveHostWageNote) );
	}

	/**
	 * 删除主播时长
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:remove')" )
	@Log( title = "主播时长", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( liveHostWageNoteService.deleteLiveHostWageNoteByIds( ids ) );
	}
}
