package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberPayJour;
import com.qiqilm.server.admin.domain.req.ReqMemberRechargeLog;
import com.qiqilm.server.admin.domain.rsp.RspPayJour;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IMemberPayJourService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 线上充值信息Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/pay/memberPayList" )
public class MemberPayListController extends BaseController {
	@Autowired
	private IMemberPayJourService memberPayJourService;

	/**
	 * 导出线上充值信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberPayList:export')" )
	@Log( title = "线上充值信息", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( MemberPayJour memberPayJour, HttpServletResponse response ) {
		List<RspPayJour> list = memberPayJourService.selectMemberPayJourLists( memberPayJour );
		ExportExcelUtil.exportExcel( list, "线上充值", "线上充值表", RspPayJour.class, response );
	}


	/**
	 * 查询线上充值信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberPayList:lists')" )
	@GetMapping( "/lists" )
	public TableDataInfo lists( MemberPayJour memberPayJour ) {
		startPage();
		List<RspPayJour> list = memberPayJourService.selectMemberPayJourLists( memberPayJour );
		return getDataTable( list );
	}

	/**
	 * 列表统计
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberRechargeLog:list')" )
	@GetMapping( "/listCounts" )
	public Map listCounts(MemberPayJour req ) {
		return memberPayJourService.listCounts( req );
	}

}
