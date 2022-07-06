//package com.qiqilm.server.admin.controller;
//
//import com.qiqilm.server.admin.annotation.Log;
//import com.qiqilm.server.admin.core.controller.BaseController;
//import com.qiqilm.server.admin.core.page.TableDataInfo;
//import com.qiqilm.server.admin.domain.MemberPayJour;
//import com.qiqilm.server.admin.domain.req.ReqMemberRechargeLog;
//import com.qiqilm.server.admin.domain.rsp.RspBankRecharge;
//import com.qiqilm.server.admin.domain.rsp.RspPayJour;
//import com.qiqilm.server.admin.enums.BusinessType;
//import com.qiqilm.server.admin.service.IMemberPayJourService;
//import com.qiqilm.server.admin.service.IMemberRechargeLogService;
//import com.qiqilm.server.admin.utils.ExportExcelUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletResponse;
//import java.util.List;
//import java.util.Map;
//
///**
// * 线上充值信息Controller
// *
// * @author 77tv
// * @date 2021-01-26
// */
//@RestController
//@RequestMapping( "/pay/memberBankRecharge" )
//public class MemberBankRechargeController extends BaseController {
//	@Autowired
//	private IMemberRechargeLogService memberRechargeLogService;
//
//	/**
//	 * 导出线上充值信息列表
//	 */
//	@PreAuthorize( "@ss.hasPermi('pay:memberBankRecharge:export')" )
//	@Log( title = "线下充值报表", businessType = BusinessType.EXPORT )
//	@GetMapping( "/export" )
//	public void export(ReqMemberRechargeLog req, HttpServletResponse response ) {
//		List<RspBankRecharge> list = memberRechargeLogService.selectMemberBankRecharge(req);
//		ExportExcelUtil.exportExcel( list, "线上充值", "线上充值表", RspBankRecharge.class, response );
//	}
//
//
//	/**
//	 * 查询线上充值信息列表
//	 */
//	@PreAuthorize( "@ss.hasPermi('pay:memberBankRecharge:lists')" )
//	@GetMapping( "/lists" )
//	public TableDataInfo lists( ReqMemberRechargeLog req ) {
//		startPage();
//		List<RspBankRecharge> list = memberRechargeLogService.selectMemberBankRecharge(req);
//		return getDataTable( list );
//	}
//	/**
//	 * 列表统计
//	 */
//	@PreAuthorize( "@ss.hasPermi('pay:memberRechargeLog:list')" )
//	@GetMapping( "/listCounts" )
//	public Map listCounts(ReqMemberRechargeLog req ) {
//		return memberRechargeLogService.listCounts( req );
//	}
//}
