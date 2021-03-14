package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberRechargeLog;
import com.qiqilm.server.admin.domain.req.ReqMemberRechargeLog;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IMemberRechargeLogService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 公司入款信息Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/pay/memberRechargeLog" )
public class MemberRechargeLogController extends BaseController {
	@Autowired
	private IMemberRechargeLogService memberRechargeLogService;

	/**
	 * 查询公司入款信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberRechargeLog:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( ReqMemberRechargeLog req ) {
		startPage();
		List<MemberRechargeLog> list = memberRechargeLogService.selectMemberRechargeLogList( req );
		return getDataTable( list );
	}

	/**
	 * 查询公司入款信息列表统计
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberRechargeLog:list')" )
	@GetMapping( "/listCount" )
	public Map listCount( ReqMemberRechargeLog req ) {
		return memberRechargeLogService.listCount( req );
	}

	/**
	 * 导出公司入款信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberRechargeLog:export')" )
	@Log( title = "公司入款信息", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( ReqMemberRechargeLog req, HttpServletResponse response ) {
		List<MemberRechargeLog> list = memberRechargeLogService.selectMemberRechargeLogList( req );
		ExportExcelUtil.exportExcel( list, "公司入款", "公司入款信息表", MemberRechargeLog.class, response );
	}

	/**
	 * 获取公司入款信息详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberRechargeLog:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id ) {
		return AjaxResult.success( memberRechargeLogService.selectMemberRechargeLogById( id ) );
	}

	@PreAuthorize( "@ss.hasPermi('pay:memberRechargeLog:firstAudit')" )
	@Log( title = "公司入款信息初审", businessType = BusinessType.AUDIT )
	@PutMapping( "/firstAudit" )
	public AjaxResult firstAudit( @RequestBody ReqMemberRechargeLog req ) {
		return memberRechargeLogService.firstAudit( req );
	}

	@PreAuthorize( "@ss.hasPermi('pay:memberRechargeLog:finalAudit')" )
	@Log( title = "公司入款信息终审", businessType = BusinessType.AUDIT )
	@PutMapping( "/finalAudit" )
	public AjaxResult finalAudit( @RequestBody ReqMemberRechargeLog req ) {
		return memberRechargeLogService.finalAudit( req );
	}

	@PreAuthorize( "@ss.hasPermi('pay:memberRechargeLog:refusedAudit')" )
	@Log( title = "公司入款信息拒绝审核", businessType = BusinessType.AUDIT )
	@PutMapping( "/refusedAudit" )
	public AjaxResult refusedAudit( @RequestBody ReqMemberRechargeLog req ) {
		return memberRechargeLogService.refusedAudit( req );
	}

	@PreAuthorize( "@ss.hasPermi('pay:memberRechargeLog:recoverAudit')" )
	@Log( title = "公司入款信息恢复审核", businessType = BusinessType.AUDIT )
	@PutMapping( "/recoverAudit" )
	public AjaxResult recoverAudit( @RequestBody ReqMemberRechargeLog req ) {
		return memberRechargeLogService.recoverAudit( req );
	}
}
