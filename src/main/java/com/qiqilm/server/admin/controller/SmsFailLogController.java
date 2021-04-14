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
import com.qiqilm.server.admin.domain.SmsFailLog;
import com.qiqilm.server.admin.service.ISmsFailLogService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 短信发送失败日志Controller
 *
 * @author 77tv
 * @date 2021-04-13
 */
@RestController
@RequestMapping( "/sms/smsFailLog" )
public class SmsFailLogController extends BaseController {
	@Autowired
	private ISmsFailLogService smsFailLogService;

	/**
	 * 查询短信发送失败日志列表
	 */
	@PreAuthorize( "@ss.hasPermi('sms:smsFailLog:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(SmsFailLog smsFailLog) {
		startPage();
		List<SmsFailLog> list = smsFailLogService.selectSmsFailLogList(smsFailLog);
		return getDataTable( list );
	}
    
	/**
	 * 导出短信发送失败日志列表
	 */
	@PreAuthorize( "@ss.hasPermi('sms:smsFailLog:export')" )
	@Log( title = "短信发送失败日志", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(SmsFailLog smsFailLog, HttpServletResponse response) {
		List<SmsFailLog>      list = smsFailLogService.selectSmsFailLogList(smsFailLog);
		ExportExcelUtil.exportExcel( list, "短信发送失败日志", "短信发送失败日志表", SmsFailLog.class, response );
	}

	/**
	 * 获取短信发送失败日志详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('sms:smsFailLog:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( smsFailLogService.selectSmsFailLogById(id) );
	}

	/**
	 * 新增短信发送失败日志
	 */
	@PreAuthorize( "@ss.hasPermi('sms:smsFailLog:add')" )
	@Log( title = "短信发送失败日志", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody SmsFailLog smsFailLog) {
		return toAjax( smsFailLogService.insertSmsFailLog(smsFailLog) );
	}

	/**
	 * 修改短信发送失败日志
	 */
	@PreAuthorize( "@ss.hasPermi('sms:smsFailLog:edit')" )
	@Log( title = "短信发送失败日志", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody SmsFailLog smsFailLog) {
		return toAjax( smsFailLogService.updateSmsFailLog(smsFailLog) );
	}

	/**
	 * 删除短信发送失败日志
	 */
	@PreAuthorize( "@ss.hasPermi('sms:smsFailLog:remove')" )
	@Log( title = "短信发送失败日志", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( smsFailLogService.deleteSmsFailLogByIds( ids ) );
	}
}
