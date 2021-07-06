package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LotteryPrizepool;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILotteryPrizepoolService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 奖池配置Controller
 *
 * @author 77tv
 * @date 2021-03-18
 */
@RestController
@RequestMapping( "/admin/lotteryPrizepool" )
public class LotteryPrizepoolController extends BaseController {
	@Autowired
	private ILotteryPrizepoolService lotteryPrizepoolService;

	/**
	 * 查询奖池配置列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryPrizepool:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LotteryPrizepool lotteryPrizepool) {
		startPage();
		List<LotteryPrizepool> list = lotteryPrizepoolService.selectLotteryPrizepoolList(lotteryPrizepool);
		return getDataTable( list );
	}
    
	/**
	 * 导出奖池配置列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryPrizepool:export')" )
	@Log( title = "奖池配置", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(LotteryPrizepool lotteryPrizepool, HttpServletResponse response) {
		List<LotteryPrizepool>      list = lotteryPrizepoolService.selectLotteryPrizepoolList(lotteryPrizepool);
		ExportExcelUtil.exportExcel( list, "奖池配置", "奖池配置表", LotteryPrizepool.class, response );
	}

	/**
	 * 获取奖池配置详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryPrizepool:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( lotteryPrizepoolService.selectLotteryPrizepoolById(id) );
	}

	/**
	 * 新增奖池配置
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryPrizepool:add')" )
	@Log( title = "奖池配置", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LotteryPrizepool lotteryPrizepool) {
		return toAjax( lotteryPrizepoolService.insertLotteryPrizepool(lotteryPrizepool) );
	}

	/**
	 * 修改奖池配置
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryPrizepool:edit')" )
	@Log( title = "奖池配置", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LotteryPrizepool lotteryPrizepool) {
		return toAjax( lotteryPrizepoolService.updateLotteryPrizepool(lotteryPrizepool) );
	}

	/**
	 * 删除奖池配置
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryPrizepool:remove')" )
	@Log( title = "奖池配置", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( lotteryPrizepoolService.deleteLotteryPrizepoolByIds( ids ) );
	}
}
