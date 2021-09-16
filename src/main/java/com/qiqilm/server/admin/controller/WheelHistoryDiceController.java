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
import com.qiqilm.server.admin.domain.WheelHistoryDice;
import com.qiqilm.server.admin.service.IWheelHistoryDiceService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 博饼中奖记录Controller
 *
 * @author 77tv
 * @date 2021-09-02
 */
@RestController
@RequestMapping( "/admin/wheelHistoryDice" )
public class WheelHistoryDiceController extends BaseController {
	@Autowired
	private IWheelHistoryDiceService wheelHistoryDiceService;

	/**
	 * 查询博饼中奖记录列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelHistoryDice:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(WheelHistoryDice wheelHistoryDice) {
		startPage();
		List<WheelHistoryDice> list = wheelHistoryDiceService.selectWheelHistoryDiceList(wheelHistoryDice);
		return getDataTable( list );
	}
    
	/**
	 * 导出博饼中奖记录列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelHistoryDice:export')" )
	@Log( title = "博饼中奖记录", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(WheelHistoryDice wheelHistoryDice, HttpServletResponse response) {
		List<WheelHistoryDice>      list = wheelHistoryDiceService.selectWheelHistoryDiceList(wheelHistoryDice);
		ExportExcelUtil.exportExcel( list, "博饼中奖记录", "博饼中奖记录表", WheelHistoryDice.class, response );
	}

	/**
	 * 获取博饼中奖记录详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelHistoryDice:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( wheelHistoryDiceService.selectWheelHistoryDiceById(id) );
	}

	/**
	 * 新增博饼中奖记录
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelHistoryDice:add')" )
	@Log( title = "博饼中奖记录", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody WheelHistoryDice wheelHistoryDice) {
		return toAjax( wheelHistoryDiceService.insertWheelHistoryDice(wheelHistoryDice) );
	}

	/**
	 * 修改博饼中奖记录
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelHistoryDice:edit')" )
	@Log( title = "博饼中奖记录", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody WheelHistoryDice wheelHistoryDice) {
		return toAjax( wheelHistoryDiceService.updateWheelHistoryDice(wheelHistoryDice) );
	}

	/**
	 * 删除博饼中奖记录
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelHistoryDice:remove')" )
	@Log( title = "博饼中奖记录", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( wheelHistoryDiceService.deleteWheelHistoryDiceByIds( ids ) );
	}
}
