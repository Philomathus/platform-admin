package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.PayAgentRechargeAccountLog;
import com.qiqilm.server.admin.domain.req.ReqPayAgentRechargeAccountLog;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IPayAgentRechargeAccountLogService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 【代充人入款】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/pay/payAgentRechargeAccountLog" )
public class PayAgentRechargeAccountLogController extends BaseController {
	@Autowired
	private IPayAgentRechargeAccountLogService payAgentRechargeAccountLogService;

	/**
	 * 查询【代充人入款】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccountLog:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( PayAgentRechargeAccountLog payAgentRechargeAccountLog ) {
		startPage();
		List<PayAgentRechargeAccountLog> list =
				payAgentRechargeAccountLogService.selectPayAgentRechargeAccountLogList( payAgentRechargeAccountLog );
		return getDataTable( list );
	}

	/**
	 * 导出【代充人入款】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccountLog:export')" )
	@Log( title = "代充人入款", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( PayAgentRechargeAccountLog payAgentRechargeAccountLog, HttpServletResponse response ) {
		List<PayAgentRechargeAccountLog> list =
				payAgentRechargeAccountLogService.selectPayAgentRechargeAccountLogList( payAgentRechargeAccountLog );
		ExportExcelUtil.exportExcel( list, "代充人入款", "代充人入款信息表", PayAgentRechargeAccountLog.class, response );
	}

	/**
	 * 获取【代充人入款】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccountLog:query')" )
	@GetMapping( value = "/{orderNo}" )
	public AjaxResult getInfo( @PathVariable( "orderNo" ) String orderNo ) {
		return AjaxResult.success( payAgentRechargeAccountLogService.selectPayAgentRechargeAccountLogById( orderNo ) );
	}

	/**
	 * 新增【代充人入款】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccountLog:add')" )
	@Log( title = "【代充人入款】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody PayAgentRechargeAccountLog payAgentRechargeAccountLog ) {
		return toAjax( payAgentRechargeAccountLogService.insertPayAgentRechargeAccountLog( payAgentRechargeAccountLog ) );
	}

	/**
	 * 修改【代充人入款】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccountLog:edit')" )
	@Log( title = "【代充人入款】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayAgentRechargeAccountLog payAgentRechargeAccountLog ) {
		return toAjax( payAgentRechargeAccountLogService.updatePayAgentRechargeAccountLog( payAgentRechargeAccountLog ) );
	}

	/**
	 * 删除【代充人入款】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccountLog:remove')" )
	@Log( title = "【代充人入款】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{orderNos}" )
	public AjaxResult remove( @PathVariable String[] orderNos ) {
		return toAjax( payAgentRechargeAccountLogService.deletePayAgentRechargeAccountLogByIds( orderNos ) );
	}

	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccountLog:refused')" )
	@Log( title = "代充人入款拒绝", businessType = BusinessType.AUDIT )
	@PutMapping( "/refused" )
	public AjaxResult refused( @RequestBody ReqPayAgentRechargeAccountLog req ) {
		return payAgentRechargeAccountLogService.refused( req );
	}

	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccountLog:lock')" )
	@Log( title = "代充人入款锁定", businessType = BusinessType.AUDIT )
	@PutMapping( "/lock" )
	public AjaxResult lock( @RequestBody ReqPayAgentRechargeAccountLog req ) {
		return payAgentRechargeAccountLogService.lock( req );
	}

	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccountLog:unlock')" )
	@Log( title = "代充人入款解锁", businessType = BusinessType.AUDIT )
	@PutMapping( "/unlock" )
	public AjaxResult unlock( @RequestBody ReqPayAgentRechargeAccountLog req ) {
		return payAgentRechargeAccountLogService.unlock( req );
	}

	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccountLog:artificial')" )
	@Log( title = "代充人入款存入", businessType = BusinessType.AUDIT )
	@PutMapping( "/artificial" )
	public AjaxResult artificial( @RequestBody ReqPayAgentRechargeAccountLog req ) {
		return payAgentRechargeAccountLogService.artificial( req );
	}

	/**
	 * 统计按钮【代充人入款】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeAccountLog:list')" )
	@GetMapping( "/statistic" )
	public AjaxResult count( PayAgentRechargeAccountLog payAgentRechargeAccountLog ) {
		return payAgentRechargeAccountLogService.statistic( payAgentRechargeAccountLog );
	}

}
