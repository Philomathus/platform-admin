package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.WheelSkinReceived;
import com.qiqilm.server.admin.domain.dto.WheelSkinReceivedExcel;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IWheelSkinReceivedService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
	 * 查询转盘皮肤领取支出统计
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelSkinReceived:list')" )
	@GetMapping( "/getTotal" )
    	public AjaxResult getTotal(WheelSkinReceived wheelSkinReceived) {
        return AjaxResult.success(wheelSkinReceivedService.getTotal(wheelSkinReceived));
	}

	/**
	 * 导出转盘皮肤领取列表
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelSkinReceived:export')" )
	@Log( title = "转盘皮肤领取", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(WheelSkinReceived wheelSkinReceived, HttpServletResponse response) {
        List<WheelSkinReceivedExcel> list = wheelSkinReceivedService.selectWheelSkinReceivedList2(wheelSkinReceived);
		ExportExcelUtil.exportExcel( list, "转盘皮肤领取", "转盘皮肤领取表", WheelSkinReceivedExcel.class, response );
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
