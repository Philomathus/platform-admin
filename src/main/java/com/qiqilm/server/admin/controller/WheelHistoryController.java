package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.WheelHistory;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IWheelHistoryService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 转盘中奖历史Controller
 *
 * @author 77tv
 * @date 2021-03-05
 */
@RestController
@RequestMapping( "/lottery/wheelHistory" )
public class WheelHistoryController extends BaseController {
	@Autowired
	private IWheelHistoryService wheelHistoryService;

	/**
	 * 查询转盘中奖历史列表
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelHistory:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( WheelHistory wheelHistory ) {
		startPage();
		List<WheelHistory> list = wheelHistoryService.selectWheelHistoryList( wheelHistory );
		return getDataTable( list );
	}

	/**
	 * 导出转盘中奖历史列表
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelHistory:export')" )
	@Log( title = "转盘中奖历史", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( WheelHistory wheelHistory, HttpServletResponse response ) {
		List<WheelHistory> list = wheelHistoryService.selectWheelHistoryList( wheelHistory );
		ExportExcelUtil.exportExcel( list, "转盘中奖历史", "转盘中奖历史表", WheelHistory.class, response );
	}

	/**
	 * 获取转盘中奖历史详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelHistory:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		return AjaxResult.success( wheelHistoryService.selectWheelHistoryById( id ) );
	}

	/**
	 * 新增转盘中奖历史
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelHistory:add')" )
	@Log( title = "转盘中奖历史", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody WheelHistory wheelHistory ) {
		return toAjax( wheelHistoryService.insertWheelHistory( wheelHistory ) );
	}

	/**
	 * 修改转盘中奖历史
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelHistory:edit')" )
	@Log( title = "转盘中奖历史", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody WheelHistory wheelHistory ) {
		return toAjax( wheelHistoryService.updateWheelHistory( wheelHistory ) );
	}

	/**
	 * 删除转盘中奖历史
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelHistory:remove')" )
	@Log( title = "转盘中奖历史", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( wheelHistoryService.deleteWheelHistoryByIds( ids ) );
	}
}
