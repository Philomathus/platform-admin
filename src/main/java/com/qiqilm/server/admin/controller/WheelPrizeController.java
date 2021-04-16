package com.qiqilm.server.admin.controller;

import java.util.List;

import com.qiqilm.server.admin.utils.ExportExcelUtil;
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
import com.qiqilm.server.admin.domain.WheelPrize;
import com.qiqilm.server.admin.service.IWheelPrizeService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 转盘奖励Controller
 *
 * @author 77tv
 * @date 2021-02-26
 */
@RestController
@RequestMapping( "/lottery/wheelPrize" )
public class WheelPrizeController extends BaseController {
	@Autowired
	private IWheelPrizeService wheelPrizeService;

	/**
	 * 查询转盘奖励列表
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelPrize:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(WheelPrize wheelPrize) {
		startPage();
		List<WheelPrize> list = wheelPrizeService.selectWheelPrizeList(wheelPrize);
		return getDataTable( list );
	}
    
	/**
	 * 导出转盘奖励列表
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelPrize:export')" )
	@Log( title = "转盘奖励", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(WheelPrize wheelPrize, HttpServletResponse response) {
		List<WheelPrize>      list = wheelPrizeService.selectWheelPrizeList(wheelPrize);
		ExportExcelUtil.exportExcel( list, "转盘奖励", "转盘奖励表", WheelPrize.class, response );
	}

	/**
	 * 获取转盘奖励详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelPrize:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( wheelPrizeService.selectWheelPrizeById(id) );
	}

	/**
	 * 新增转盘奖励
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelPrize:add')" )
	@Log( title = "转盘奖励", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody WheelPrize wheelPrize) {
		return toAjax( wheelPrizeService.insertWheelPrize(wheelPrize) );
	}

	/**
	 * 修改转盘奖励
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelPrize:edit')" )
	@Log( title = "转盘奖励", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody WheelPrize wheelPrize) {
		return toAjax( wheelPrizeService.updateWheelPrize(wheelPrize) );
	}

	/**
	 * 删除转盘奖励
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelPrize:remove')" )
	@Log( title = "转盘奖励", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( wheelPrizeService.deleteWheelPrizeByIds( ids ) );
	}
}
