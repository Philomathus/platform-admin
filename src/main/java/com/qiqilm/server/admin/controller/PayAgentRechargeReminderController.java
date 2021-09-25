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
import com.qiqilm.server.admin.domain.PayAgentRechargeReminder;
import com.qiqilm.server.admin.service.IPayAgentRechargeReminderService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 代充银行提示语Controller
 *
 * @author 77tv
 * @date 2021-09-25
 */
@RestController
@RequestMapping( "/admin/payAgentRechargeReminder" )
public class PayAgentRechargeReminderController extends BaseController {
	@Autowired
	private IPayAgentRechargeReminderService payAgentRechargeReminderService;

	/**
	 * 查询代充银行提示语列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:payAgentRechargeReminder:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(PayAgentRechargeReminder payAgentRechargeReminder) {
		startPage();
		List<PayAgentRechargeReminder> list = payAgentRechargeReminderService.selectPayAgentRechargeReminderList(payAgentRechargeReminder);
		return getDataTable( list );
	}
    
	/**
	 * 导出代充银行提示语列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:payAgentRechargeReminder:export')" )
	@Log( title = "代充银行提示语", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(PayAgentRechargeReminder payAgentRechargeReminder, HttpServletResponse response) {
		List<PayAgentRechargeReminder>      list = payAgentRechargeReminderService.selectPayAgentRechargeReminderList(payAgentRechargeReminder);
		ExportExcelUtil.exportExcel( list, "代充银行提示语", "代充银行提示语表", PayAgentRechargeReminder.class, response );
	}

	/**
	 * 获取代充银行提示语详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:payAgentRechargeReminder:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( payAgentRechargeReminderService.selectPayAgentRechargeReminderById(id) );
	}

	/**
	 * 新增代充银行提示语
	 */
	@PreAuthorize( "@ss.hasPermi('admin:payAgentRechargeReminder:add')" )
	@Log( title = "代充银行提示语", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody PayAgentRechargeReminder payAgentRechargeReminder) {
		return toAjax( payAgentRechargeReminderService.insertPayAgentRechargeReminder(payAgentRechargeReminder) );
	}

	/**
	 * 修改代充银行提示语
	 */
	@PreAuthorize( "@ss.hasPermi('admin:payAgentRechargeReminder:edit')" )
	@Log( title = "代充银行提示语", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayAgentRechargeReminder payAgentRechargeReminder) {
		return toAjax( payAgentRechargeReminderService.updatePayAgentRechargeReminder(payAgentRechargeReminder) );
	}

	/**
	 * 删除代充银行提示语
	 */
	@PreAuthorize( "@ss.hasPermi('admin:payAgentRechargeReminder:remove')" )
	@Log( title = "代充银行提示语", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( payAgentRechargeReminderService.deletePayAgentRechargeReminderByIds( ids ) );
	}
}
