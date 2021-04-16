package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveVideo;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveVideoService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

/**
 * 直播Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Log4j2
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
	public TableDataInfo list( LiveVideo liveVideo ) {
		startPage();
		List<LiveVideo> list = liveVideoService.selectLiveVideoList( liveVideo );
		return getDataTable( list );
	}

	/**
	 * 获取直播详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideo:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		return AjaxResult.success( liveVideoService.selectLiveVideoById( id ) );
	}

	/**
	 * 导出直播列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideo:export')" )
	@Log( title = "直播", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( LiveVideo liveVideo, HttpServletResponse response ) {
		List<LiveVideo>      list = liveVideoService.selectLiveVideoList( liveVideo );
		ExportExcelUtil.exportExcel( list, "直播", "直播息表", LiveVideo.class, response );
	}

	/**
	 * 关播
	 */
	@Log( title = "关播", businessType = BusinessType.CLOSE )
	@GetMapping( value = "close/{ids}" )
	public AjaxResult close( @PathVariable( "ids" ) String ids ) {
		String[] allId = ids.split( "," );
		for ( String id : allId ) {
			liveVideoService.close( Long.valueOf( id ), "admin" );
		}
		return AjaxResult.success();
	}

	/**
	 * 开启直播付费
	 */
	@Log( title = "直播付费", businessType = BusinessType.LIVE_PAY )
	@PutMapping( "/livePay/{userId}" )
	public AjaxResult livePay( @PathVariable long userId, Integer liveFee ) {
		if ( Objects.nonNull( liveFee ) && liveFee > 0 ) {
			try {
				return AjaxResult.success( liveVideoService.livePay( userId, liveFee, 1 ) );
			} catch ( Exception e ) {
				log.error( e.getMessage(), e );
			}
		}
		return AjaxResult.error();
	}

	/**
	 * 设置排序值 固定定位、取消固定定位、推荐、取消推荐、置底、取消置底
	 */
	@PutMapping( "/updateVideoSort" )
	public AjaxResult updateVideoSort( @RequestBody LiveVideo liveVideo ) {
		return liveVideoService.updateVideoSort( liveVideo );
	}
}
