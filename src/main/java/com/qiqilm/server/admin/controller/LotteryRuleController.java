package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LotteryRule;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILotteryRuleService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 开奖规则说明Controller
 *
 * @author 77tv
 * @date 2021-02-26
 */
@RestController
@RequestMapping( "/admin/lotteryRule" )
public class LotteryRuleController extends BaseController {
	@Autowired
	private ILotteryRuleService lotteryRuleService;

	/**
	 * 查询开奖规则说明列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryRule:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( LotteryRule lotteryRule ) {
		startPage();
		List<LotteryRule> list = lotteryRuleService.selectLotteryRuleList( lotteryRule );
		return getDataTable( list );
	}

	/**
	 * 导出开奖规则说明列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryRule:export')" )
	@Log( title = "开奖规则说明", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( LotteryRule lotteryRule, HttpServletResponse response ) {
		List<LotteryRule> list = lotteryRuleService.selectLotteryRuleList( lotteryRule );
		ExportExcelUtil.exportExcel( list, "开奖规则说明", "开奖规则说明表", LotteryRule.class, response );
	}

	/**
	 * 获取开奖规则说明详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryRule:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		return AjaxResult.success( lotteryRuleService.selectLotteryRuleById( id ) );
	}

	/**
	 * 新增开奖规则说明
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryRule:add')" )
	@Log( title = "开奖规则说明", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LotteryRule lotteryRule ) {
		return toAjax( lotteryRuleService.insertLotteryRule( lotteryRule ) );
	}

	/**
	 * 修改开奖规则说明
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryRule:edit')" )
	@Log( title = "开奖规则说明", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LotteryRule lotteryRule ) {
		return toAjax( lotteryRuleService.updateLotteryRule( lotteryRule ) );
	}

	/**
	 * 删除开奖规则说明
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryRule:remove')" )
	@Log( title = "开奖规则说明", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( lotteryRuleService.deleteLotteryRuleByIds( ids ) );
	}


}
