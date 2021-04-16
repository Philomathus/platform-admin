package com.qiqilm.server.admin.controller;

import java.util.List;

import com.qiqilm.server.admin.utils.ExportExcelUtil;
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
import com.qiqilm.server.admin.domain.PayLog;
import com.qiqilm.server.admin.service.IPayLogService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 【支付日志】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/pay/payLog" )
public class PayLogController extends BaseController {
	@Autowired
	private IPayLogService payLogService;

	/**
	 * 查询【支付日志】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payLog:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(PayLog payLog) {
		startPage();
		List<PayLog> list = payLogService.selectPayLogList(payLog);
		return getDataTable( list );
	}

	/**
	 * 导出【支付日志】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payLog:export')" )
	@Log( title = "【支付日志】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(PayLog payLog, HttpServletResponse response) {
		List<PayLog>      list = payLogService.selectPayLogList(payLog);
		ExportExcelUtil.exportExcel( list, "支付日志", "支付日志表", PayLog.class, response );
	}

	/**
	 * 获取【支付日志】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payLog:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( payLogService.selectPayLogById(id) );
	}

	/**
	 * 新增【支付日志】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payLog:add')" )
	@Log( title = "【支付日志】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody PayLog payLog) {
		return toAjax( payLogService.insertPayLog(payLog) );
	}

	/**
	 * 修改【支付日志】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payLog:edit')" )
	@Log( title = "【支付日志】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayLog payLog) {
		return toAjax( payLogService.updatePayLog(payLog) );
	}

	/**
	 * 删除【支付日志】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payLog:remove')" )
	@Log( title = "【支付日志】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( payLogService.deletePayLogByIds( ids ) );
	}
}
