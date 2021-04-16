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
import com.qiqilm.server.admin.domain.WheelSkin;
import com.qiqilm.server.admin.service.IWheelSkinService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 转盘皮肤列Controller
 *
 * @author 77tv
 * @date 2021-02-26
 */
@RestController
@RequestMapping( "/lottery/wheelSkin" )
public class WheelSkinController extends BaseController {
	@Autowired
	private IWheelSkinService wheelSkinService;

	/**
	 * 查询转盘皮肤列列表
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelSkin:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(WheelSkin wheelSkin) {
		startPage();
		List<WheelSkin> list = wheelSkinService.selectWheelSkinList(wheelSkin);
		return getDataTable( list );
	}
    
	/**
	 * 导出转盘皮肤列列表
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelSkin:export')" )
	@Log( title = "转盘皮肤", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(WheelSkin wheelSkin, HttpServletResponse response) {
		List<WheelSkin>      list = wheelSkinService.selectWheelSkinList(wheelSkin);
		ExportExcelUtil.exportExcel( list, "转盘皮肤", "转盘皮肤表", WheelSkin.class, response );
	}

	/**
	 * 获取转盘皮肤列详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelSkin:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( wheelSkinService.selectWheelSkinById(id) );
	}

	/**
	 * 新增转盘皮肤列
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelSkin:add')" )
	@Log( title = "转盘皮肤列", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody WheelSkin wheelSkin) {
		return toAjax( wheelSkinService.insertWheelSkin(wheelSkin) );
	}

	/**
	 * 修改转盘皮肤列
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelSkin:edit')" )
	@Log( title = "转盘皮肤列", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody WheelSkin wheelSkin) {
		return toAjax( wheelSkinService.updateWheelSkin(wheelSkin) );
	}

	/**
	 * 删除转盘皮肤列
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelSkin:remove')" )
	@Log( title = "转盘皮肤列", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( wheelSkinService.deleteWheelSkinByIds( ids ) );
	}
}
