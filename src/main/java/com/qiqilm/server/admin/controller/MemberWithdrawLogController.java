package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.MemberWithdrawLogShunWei;
import com.qiqilm.server.admin.domain.req.DownLoadTime;
import com.qiqilm.server.admin.domain.req.ReqMemberWithdrawLog;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IMemberWithdrawLogService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 会员提现信息Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Log4j2
@RestController
@RequestMapping( "/pay/memberWithdrawLog" )
public class MemberWithdrawLogController extends BaseController {
	@Autowired
	private IMemberWithdrawLogService memberWithdrawLogService;

	/**
	 * 查询会员提现信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( MemberWithdrawLog memberWithdrawLog ) {
		startPage();
		List<MemberWithdrawLog> list = memberWithdrawLogService.selectMemberWithdrawLogList( memberWithdrawLog );
		return getDataTable( list );
	}

	/**
	 * 获取会员提现信息详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id ) {
		return AjaxResult.success( memberWithdrawLogService.selectMemberWithdrawLogById( id ) );
	}

	/**
	 * 获取会员提现信息统计
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:query')" )
	@GetMapping( value = "/countTotal" )
	public AjaxResult getTotal( MemberWithdrawLog memberWithdrawLog ) {
		return memberWithdrawLogService.getTotal( memberWithdrawLog );
	}

	/**
	 * 获取会员提现信息详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:query')" )
	@GetMapping( value = "/report/{id}" )
	public AjaxResult getReport(@PathVariable( "id" ) String id ) {
		return memberWithdrawLogService.withdrawReport( id );
	}

	/**
	 * 导出会员提现信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:export')" )
	@Log( title = "会员提现信息", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( MemberWithdrawLog memberWithdrawLog, HttpServletResponse response ) {
		List<MemberWithdrawLog> list = memberWithdrawLogService.selectMemberWithdrawLogList( memberWithdrawLog );
        if (list.size() <= DownLoadTime.downLoadLimit) {
            ExportExcelUtil.exportExcel(list, "会员提现", "会员提现信息表", MemberWithdrawLog.class, response);
        }
	}

	/**
	 * 顺为代付格式导出会员提现信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:export')" )
	@Log( title = "顺为代付格式会员提现信息", businessType = BusinessType.EXPORT )
	@PostMapping( "/exportShunWei" )
	public void exportShunWei( @RequestBody ReqMemberWithdrawLog req, HttpServletResponse response ) {
		List<MemberWithdrawLogShunWei> list = memberWithdrawLogService.selectMemberWithdrawLogShunWeiList( req );
		ExportExcelUtil.exportExcel( list, null, "顺为格式会员提现表", MemberWithdrawLogShunWei.class, response );
	}

	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:refused')" )
	@Log( title = "会员提现拒绝", businessType = BusinessType.AUDIT )
	@PutMapping( "/refused" )
	public AjaxResult refused( @RequestBody ReqMemberWithdrawLog req ) {
		return memberWithdrawLogService.refused( req );
	}

	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:refused')" )
	@Log( title = "会员提现批量拒绝", businessType = BusinessType.AUDIT )
	@PutMapping( "/refuseds" )
	public AjaxResult refuseds( @RequestBody ReqMemberWithdrawLog req ) {
		return memberWithdrawLogService.refuseds( req );
	}

	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:lock')" )
	@Log( title = "会员提现批量锁定", businessType = BusinessType.AUDIT )
	@PutMapping( "/locks" )
	public AjaxResult locks( @RequestBody ReqMemberWithdrawLog req ) {
		return memberWithdrawLogService.locks( req );
	}

	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:back')" )
	@Log( title = "会员提现回退", businessType = BusinessType.AUDIT )
	@PutMapping( "/back" )
	public AjaxResult back( @RequestBody ReqMemberWithdrawLog req ) {
		return memberWithdrawLogService.back( req );
	}

	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:back')" )
	@Log( title = "会员提现代付失败回退", businessType = BusinessType.AUDIT )
	@PutMapping( "/failBack" )
	public AjaxResult failBack( @RequestBody ReqMemberWithdrawLog req ) {
		return memberWithdrawLogService.failBack( req );
	}

	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:queryStatus')" )
	@Log( title = "会员提现查询状态", businessType = BusinessType.AUDIT )
	@PutMapping( "/queryStatus" )
	public AjaxResult queryStatus( @RequestBody ReqMemberWithdrawLog req ) {
		return memberWithdrawLogService.queryStatus( req );
	}

	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:lock')" )
	@Log( title = "会员提现锁定", businessType = BusinessType.AUDIT )
	@PutMapping( "/lock" )
	public AjaxResult lock( @RequestBody ReqMemberWithdrawLog req ) {
		return memberWithdrawLogService.lock( req );
	}

	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:unlock')" )
	@Log( title = "会员提现解锁", businessType = BusinessType.AUDIT )
	@PutMapping( "/unlock" )
	public AjaxResult unlock( @RequestBody ReqMemberWithdrawLog req ) {
		return memberWithdrawLogService.unlock( req );
	}

	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:artificial')" )
	@Log( title = "会员提现人工出款", businessType = BusinessType.AUDIT )
	@PutMapping( "/artificial" )
	public AjaxResult artificial( @RequestBody ReqMemberWithdrawLog req ) {
		return memberWithdrawLogService.artificial( req );
	}

	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:abnormalWithdrawal')" )
	@Log( title = "会员出款异常", businessType = BusinessType.AUDIT )
	@PutMapping( "/abnormalWithdrawal" )
	public AjaxResult abnormalWithdrawal( @RequestBody ReqMemberWithdrawLog req ) {
		return memberWithdrawLogService.abnormalWithdrawal( req );
	}

	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:manualWithdrawal')" )
	@Log( title = "会员人工代付中", businessType = BusinessType.AUDIT )
	@PutMapping( "/manualWithdrawal" )
	public AjaxResult manualWithdrawal( @RequestBody ReqMemberWithdrawLog req ) {
		return memberWithdrawLogService.manualWithdrawal( req );
	}
}
