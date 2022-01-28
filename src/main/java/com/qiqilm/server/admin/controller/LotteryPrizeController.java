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
import com.qiqilm.server.admin.domain.LotteryPrize;
import com.qiqilm.server.admin.service.ILotteryPrizeService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 奖项设置Controller
 *
 * @author 77tv
 * @date 2022-01-27
 */
@RestController
@RequestMapping( "/admin/lotteryPrize" )
public class LotteryPrizeController extends BaseController {
	@Autowired
	private ILotteryPrizeService lotteryPrizeService;

	/**
	 * 查询【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryPrize:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LotteryPrize lotteryPrize) {
		startPage();
		List<LotteryPrize> list = lotteryPrizeService.selectLotteryPrizeList(lotteryPrize);
		return getDataTable( list );
	}
    
	/**
	 * 导出【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryPrize:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(LotteryPrize lotteryPrize, HttpServletResponse response) {
		List<LotteryPrize>      list = lotteryPrizeService.selectLotteryPrizeList(lotteryPrize);
		ExportExcelUtil.exportExcel( list, "【请填写功能名称】", "【请填写功能名称】表", LotteryPrize.class, response );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryPrize:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( lotteryPrizeService.selectLotteryPrizeById(id) );
	}

	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryPrize:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LotteryPrize lotteryPrize) {
		return toAjax( lotteryPrizeService.insertLotteryPrize(lotteryPrize) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryPrize:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LotteryPrize lotteryPrize) {
		return toAjax( lotteryPrizeService.updateLotteryPrize(lotteryPrize) );
	}

	/**
	 * 删除【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryPrize:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( lotteryPrizeService.deleteLotteryPrizeByIds( ids ) );
	}
}
