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
import com.qiqilm.server.admin.domain.LotteryDiceConfig;
import com.qiqilm.server.admin.service.ILotteryDiceConfigService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 抽奖次数设置Controller
 *
 * @author 77tv
 * @date 2022-01-27
 */
@RestController
@RequestMapping( "/admin/lotteryDiceConfig" )
public class LotteryDiceConfigController extends BaseController {
	@Autowired
	private ILotteryDiceConfigService lotteryDiceConfigService;

	/**
	 * 查询【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryDiceConfig:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LotteryDiceConfig lotteryDiceConfig) {
		startPage();
		List<LotteryDiceConfig> list = lotteryDiceConfigService.selectLotteryDiceConfigList(lotteryDiceConfig);
		return getDataTable( list );
	}
    
	/**
	 * 导出【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryDiceConfig:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(LotteryDiceConfig lotteryDiceConfig, HttpServletResponse response) {
		List<LotteryDiceConfig>      list = lotteryDiceConfigService.selectLotteryDiceConfigList(lotteryDiceConfig);
		ExportExcelUtil.exportExcel( list, "【请填写功能名称】", "【请填写功能名称】表", LotteryDiceConfig.class, response );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryDiceConfig:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( lotteryDiceConfigService.selectLotteryDiceConfigById(id) );
	}

	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryDiceConfig:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LotteryDiceConfig lotteryDiceConfig) {
		return toAjax( lotteryDiceConfigService.insertLotteryDiceConfig(lotteryDiceConfig) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryDiceConfig:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LotteryDiceConfig lotteryDiceConfig) {
		return toAjax( lotteryDiceConfigService.updateLotteryDiceConfig(lotteryDiceConfig) );
	}

	/**
	 * 删除【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryDiceConfig:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( lotteryDiceConfigService.deleteLotteryDiceConfigByIds( ids ) );
	}

	/**
	 * 状态修改
	 */
	@PreAuthorize("@ss.hasPermi('admin:activityInfo:edit')")
	@Log( title = "活动信息状态", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeStatus" )
	public AjaxResult changeStatus( @RequestBody LotteryDiceConfig lotteryDiceConfig ) {
		return toAjax(lotteryDiceConfigService.updateLotteryDiceConfig(lotteryDiceConfig));
	}
}
