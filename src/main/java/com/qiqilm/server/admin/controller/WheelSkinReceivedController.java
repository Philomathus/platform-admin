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
import com.qiqilm.server.admin.domain.WheelSkinReceived;
import com.qiqilm.server.admin.service.IWheelSkinReceivedService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 转盘皮肤领取Controller
 *
 * @author 77tv
 * @date 2021-02-24
 */
@RestController
@RequestMapping( "/lottery/wheelSkinReceived" )
public class WheelSkinReceivedController extends BaseController {
	@Autowired
	private IWheelSkinReceivedService wheelSkinReceivedService;

	/**
	 * 查询转盘皮肤领取列表
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelSkinReceived:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(WheelSkinReceived wheelSkinReceived) {
		startPage();
		List<WheelSkinReceived> list = wheelSkinReceivedService.selectWheelSkinReceivedList(wheelSkinReceived);
		return getDataTable( list );
	}
    
	/**
	 * 导出转盘皮肤领取列表
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelSkinReceived:export')" )
	@Log( title = "转盘皮肤领取", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(WheelSkinReceived wheelSkinReceived) {
		List<WheelSkinReceived>      list = wheelSkinReceivedService.selectWheelSkinReceivedList(wheelSkinReceived);
		ExcelUtil<WheelSkinReceived> util = new ExcelUtil<>(WheelSkinReceived.class);
		return util.exportExcel( list, "wheelSkinReceived" );
	}

	/**
	 * 获取转盘皮肤领取详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelSkinReceived:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( wheelSkinReceivedService.selectWheelSkinReceivedById(id) );
	}

	/**
	 * 新增转盘皮肤领取
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelSkinReceived:add')" )
	@Log( title = "转盘皮肤领取", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody WheelSkinReceived wheelSkinReceived) {
		return toAjax( wheelSkinReceivedService.insertWheelSkinReceived(wheelSkinReceived) );
	}

	/**
	 * 修改转盘皮肤领取
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelSkinReceived:edit')" )
	@Log( title = "转盘皮肤领取", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody WheelSkinReceived wheelSkinReceived) {
		return toAjax( wheelSkinReceivedService.updateWheelSkinReceived(wheelSkinReceived) );
	}

	/**
	 * 删除转盘皮肤领取
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelSkinReceived:remove')" )
	@Log( title = "转盘皮肤领取", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( wheelSkinReceivedService.deleteWheelSkinReceivedByIds( ids ) );
	}
}
