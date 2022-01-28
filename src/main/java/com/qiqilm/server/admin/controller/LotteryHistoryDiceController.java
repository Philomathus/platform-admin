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
import com.qiqilm.server.admin.domain.LotteryHistoryDice;
import com.qiqilm.server.admin.service.ILotteryHistoryDiceService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 抽奖结果Controller
 *
 * @author 77tv
 * @date 2022-01-27
 */
@RestController
@RequestMapping( "/admin/lotteryHistoryDice" )
public class LotteryHistoryDiceController extends BaseController {
	@Autowired
	private ILotteryHistoryDiceService lotteryHistoryDiceService;

	/**
	 * 查询【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryHistoryDice:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LotteryHistoryDice lotteryHistoryDice) {
		startPage();
		List<LotteryHistoryDice> list = lotteryHistoryDiceService.selectLotteryHistoryDiceList(lotteryHistoryDice);
		return getDataTable( list );
	}
    
	/**
	 * 导出【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryHistoryDice:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(LotteryHistoryDice lotteryHistoryDice, HttpServletResponse response) {
		List<LotteryHistoryDice>      list = lotteryHistoryDiceService.selectLotteryHistoryDiceList(lotteryHistoryDice);
		ExportExcelUtil.exportExcel( list, "【请填写功能名称】", "【请填写功能名称】表", LotteryHistoryDice.class, response );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryHistoryDice:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( lotteryHistoryDiceService.selectLotteryHistoryDiceById(id) );
	}

	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryHistoryDice:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LotteryHistoryDice lotteryHistoryDice) {
		return toAjax( lotteryHistoryDiceService.insertLotteryHistoryDice(lotteryHistoryDice) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryHistoryDice:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LotteryHistoryDice lotteryHistoryDice) {
		return toAjax( lotteryHistoryDiceService.updateLotteryHistoryDice(lotteryHistoryDice) );
	}

	/**
	 * 删除【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryHistoryDice:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( lotteryHistoryDiceService.deleteLotteryHistoryDiceByIds( ids ) );
	}
}
