package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LotteryHistory;
import com.qiqilm.server.admin.domain.LotteryPrizeconfig;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILotteryHistoryService;
import com.qiqilm.server.admin.service.ILotteryPrizeconfigService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 开奖配置Controller
 *
 * @author 77tv
 * @date 2021-03-18
 */
@RestController
@RequestMapping( "/admin/lotteryPrizeconfig" )
public class LotteryPrizeconfigController extends BaseController {
	@Autowired
	private ILotteryPrizeconfigService lotteryPrizeconfigService;

	@Autowired
	private ILotteryHistoryService lotteryHistoryService;

	/**
	 * 查询开奖配置列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryPrizeconfig:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LotteryPrizeconfig lotteryPrizeconfig) {
		startPage();
		List<LotteryPrizeconfig> list = lotteryPrizeconfigService.selectLotteryPrizeconfigList(lotteryPrizeconfig);
		return getDataTable( list );
	}

	/**
	 * 查询全部彩种
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryHistory:list')" )
	@GetMapping( "/lotteryName" )
	public AjaxResult lotteryName() {
		List<LotteryHistory> list = lotteryHistoryService.selectLotteryHistoryList();
		return AjaxResult.success( list );
	}

	/**
	 * 导出开奖配置列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryPrizeconfig:export')" )
	@Log( title = "开奖配置", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(LotteryPrizeconfig lotteryPrizeconfig, HttpServletResponse response) {
		List<LotteryPrizeconfig>      list = lotteryPrizeconfigService.selectLotteryPrizeconfigList(lotteryPrizeconfig);
		ExportExcelUtil.exportExcel( list, "开奖配置", "开奖配置表", LotteryPrizeconfig.class, response );
	}

	/**
	 * 获取开奖配置详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryPrizeconfig:query')" )
	@GetMapping( value = "/{lotteryId}" )
	public AjaxResult getInfo( @PathVariable( "lotteryId" ) String lotteryId) {
		return AjaxResult.success( lotteryPrizeconfigService.selectLotteryPrizeconfigById(lotteryId) );
	}

	/**
	 * 新增开奖配置
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryPrizeconfig:add')" )
	@Log( title = "开奖配置", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LotteryPrizeconfig lotteryPrizeconfig) {
		return toAjax( lotteryPrizeconfigService.insertLotteryPrizeconfig(lotteryPrizeconfig) );
	}

	/**
	 * 修改开奖配置
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryPrizeconfig:edit')" )
	@Log( title = "开奖配置", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LotteryPrizeconfig lotteryPrizeconfig) {
		return toAjax( lotteryPrizeconfigService.updateLotteryPrizeconfig(lotteryPrizeconfig) );
	}

	/**
	 * 删除开奖配置
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryPrizeconfig:remove')" )
	@Log( title = "开奖配置", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{lotteryIds}" )
	public AjaxResult remove( @PathVariable String[] lotteryIds ) {
		return toAjax( lotteryPrizeconfigService.deleteLotteryPrizeconfigByIds( lotteryIds ) );
	}
}
