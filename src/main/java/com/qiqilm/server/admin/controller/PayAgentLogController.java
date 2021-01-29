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
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.service.IPayAgentLogService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 【代付下单日志】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/pay/payAgentLog" )
public class PayAgentLogController extends BaseController {
	@Autowired
	private IPayAgentLogService payAgentLogService;

	/**
	 * 查询【代付下单日志】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentLog:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(PayAgentLog payAgentLog) {
		startPage();
		List<PayAgentLog> list = payAgentLogService.selectPayAgentLogList(payAgentLog);
		return getDataTable( list );
	}
    
	/**
	 * 导出【代付下单日志】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentLog:export')" )
	@Log( title = "【代付下单日志】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(PayAgentLog payAgentLog) {
		List<PayAgentLog>      list = payAgentLogService.selectPayAgentLogList(payAgentLog);
		ExcelUtil<PayAgentLog> util = new ExcelUtil<PayAgentLog>(PayAgentLog. class);
		return util.exportExcel( list, "payAgentLog" );
	}

	/**
	 * 获取【代付下单日志】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentLog:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( payAgentLogService.selectPayAgentLogById(id) );
	}

	/**
	 * 新增【代付下单日志】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentLog:add')" )
	@Log( title = "【代付下单日志】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody PayAgentLog payAgentLog) {
		return toAjax( payAgentLogService.insertPayAgentLog(payAgentLog) );
	}

	/**
	 * 修改【代付下单日志】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentLog:edit')" )
	@Log( title = "【代付下单日志】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayAgentLog payAgentLog) {
		return toAjax( payAgentLogService.updatePayAgentLog(payAgentLog) );
	}

	/**
	 * 删除【代付下单日志】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentLog:remove')" )
	@Log( title = "【代付下单日志】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( payAgentLogService.deletePayAgentLogByIds( ids ) );
	}
}
