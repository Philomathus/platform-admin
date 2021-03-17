package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveHostWageNote;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageNoteFamily;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageNoteList;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveHostWageNoteService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
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
	public TableDataInfo list( LiveHostWageNote liveHostWageNote ) {
		startPage();
		List<LiveHostWageNote> list = liveHostWageNoteService.selectLiveHostWageNoteList( liveHostWageNote );
		return getDataTable( list );
	}

	@PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:list')" )
	@GetMapping( "familyPage" )
	public TableDataInfo familyPage( LiveHostWageNote dto ) {
		if ( dto.getSelectDate() == null || dto.getSelectDate().length == 0 ) {
			Date             d          = new Date();
			SimpleDateFormat sdf        = new SimpleDateFormat( "yyyy-MM-dd" );
			String           dateNowStr = sdf.format( d );
			dto.getSelectDate()[ 0 ] = dateNowStr;
			dto.getSelectDate()[ 1 ] = dateNowStr;
		}
		dto.setStartTime( dto.getSelectDate()[ 0 ] + " 00:00:00" );
		dto.setEndTime( dto.getSelectDate()[ 1 ] + " 23:59:59" );
		startPage();
		List<RspLiveHostWageNoteFamily> list = liveHostWageNoteService.familyPage( dto );
		return getDataTable( list );
	}

	@PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:list')" )
	@GetMapping( "hostPage" )
	public TableDataInfo getPage( LiveHostWageNote dto ) {
		if ( dto.getSelectDate() == null || dto.getSelectDate().length == 0 ) {
			Date             d          = new Date();
			SimpleDateFormat sdf        = new SimpleDateFormat( "yyyy-MM-dd" );
			String           dateNowStr = sdf.format( d );
			dto.getSelectDate()[ 0 ] = dateNowStr;
			dto.getSelectDate()[ 1 ] = dateNowStr;
		}
		dto.setStartTime( dto.getSelectDate()[ 0 ] + " 00:00:00" );
		dto.setEndTime( dto.getSelectDate()[ 1 ] + " 23:59:59" );
		startPage();
		List<RspLiveHostWageNoteList> list = liveHostWageNoteService.hostPage( dto );
		return getDataTable( list );
	}

	/**
	 * 导出家族直播时长列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:export')" )
	@Log( title = "家族直播时长", businessType = BusinessType.EXPORT )
	@GetMapping( "/exportFamily" )
	public void exportFamily( LiveHostWageNote dto, HttpServletResponse response ) {
		if ( dto.getSelectDate() == null || dto.getSelectDate().length == 0 ) {
			Date             d          = new Date();
			SimpleDateFormat sdf        = new SimpleDateFormat( "yyyy-MM-dd" );
			String           dateNowStr = sdf.format( d );
			dto.getSelectDate()[ 0 ] = dateNowStr;
			dto.getSelectDate()[ 1 ] = dateNowStr;
		}
		dto.setStartTime( dto.getSelectDate()[ 0 ] + " 00:00:00" );
		dto.setEndTime( dto.getSelectDate()[ 1 ] + " 23:59:59" );
		List<RspLiveHostWageNoteFamily> list = liveHostWageNoteService.familyPage( dto );
		ExportExcelUtil.exportExcel( list, "家族直播时长", "家族直播时长", RspLiveHostWageNoteFamily.class, response );
	}

	/**
	 * 导出主播统计时长列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:export')" )
	@Log( title = "主播统计时长", businessType = BusinessType.EXPORT )
	@GetMapping( "/exportHost" )
	public void exportHost( LiveHostWageNote dto, HttpServletResponse response ) {
		if ( dto.getSelectDate() == null || dto.getSelectDate().length == 0 ) {
			Date             d          = new Date();
			SimpleDateFormat sdf        = new SimpleDateFormat( "yyyy-MM-dd" );
			String           dateNowStr = sdf.format( d );
			dto.getSelectDate()[ 0 ] = dateNowStr;
			dto.getSelectDate()[ 1 ] = dateNowStr;
		}
		dto.setStartTime( dto.getSelectDate()[ 0 ] + " 00:00:00" );
		dto.setEndTime( dto.getSelectDate()[ 1 ] + " 23:59:59" );
		List<RspLiveHostWageNoteList> list = liveHostWageNoteService.hostPage( dto );
		ExportExcelUtil.exportExcel( list, "主播统计时长", "主播统计时长", RspLiveHostWageNoteList.class, response );
	}

	/**
	 * 获取主播时长详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostWageNote:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		return AjaxResult.success( liveHostWageNoteService.selectLiveHostWageNoteById( id ) );
	}
}
