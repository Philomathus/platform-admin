package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.WheelLottery;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IWheelLotteryService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 转盘彩票Controller
 *
 * @author 77tv
 * @date 2021-03-01
 */
@RestController
@RequestMapping( "/lottery/wheelLottery" )
public class WheelLotteryController extends BaseController {
	@Autowired
	private IWheelLotteryService wheelLotteryService;

	/**
	 * 查询转盘彩票列表
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelLottery:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(WheelLottery wheelLottery) {
		startPage();
		List<WheelLottery> list = wheelLotteryService.selectWheelLotteryList(wheelLottery);
		return getDataTable( list );
	}

	/**
	 * 导出转盘彩票列表
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelLottery:export')" )
	@Log( title = "转盘彩票", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(WheelLottery wheelLottery, HttpServletResponse response) {
		List<WheelLottery>      list = wheelLotteryService.selectWheelLotteryList(wheelLottery);
		ExportExcelUtil.exportExcel( list, "转盘彩票", "转盘彩票表", WheelLottery.class, response );
	}

	/**
	 * 获取转盘彩票详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelLottery:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( wheelLotteryService.selectWheelLotteryById(id) );
	}

	/**
	 * 新增转盘彩票
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelLottery:add')" )
	@Log( title = "转盘彩票", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody WheelLottery wheelLottery) {
		return  wheelLotteryService.insertWheelLottery(wheelLottery)  > 0 ? AjaxResult.success() : AjaxResult.error("所加彩票已存在");
	}

	/**
	 * 修改转盘彩票
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelLottery:edit')" )
	@Log( title = "转盘彩票", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody WheelLottery wheelLottery) {
		return toAjax( wheelLotteryService.updateWheelLottery(wheelLottery) );
	}

	/**
	 * 删除转盘彩票
	 */
	@PreAuthorize( "@ss.hasPermi('lottery:wheelLottery:remove')" )
	@Log( title = "转盘彩票", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( wheelLotteryService.deleteWheelLotteryByIds( ids ) );
	}
}
