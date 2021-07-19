package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.req.ReqMemberRechargeLog;
import com.qiqilm.server.admin.domain.rsp.RspBankRecharge;
import com.qiqilm.server.admin.domain.rsp.RspMemberChannel;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IMemberInfoService;
import com.qiqilm.server.admin.service.IMemberRechargeLogService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 网易异常统计Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/report-member/memberChannel" )
public class MemberChannelController extends BaseController {
	@Autowired
	private IMemberInfoService memberInfoService;

	/**
	 * 网易异常统计
	 */
	@PreAuthorize( "@ss.hasPermi('report-member:memberChannel:export')" )
	@Log( title = "网易异常统计", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(MemberInfo memberInfo, HttpServletResponse response ) {
		List<RspMemberChannel> memberstatistics = memberInfoService.memberstatistics(memberInfo);
		ExportExcelUtil.exportExcel( memberstatistics, "网易异常统计", "网易异常统计表", RspMemberChannel.class, response );
	}


	/**
	 * 网易异常统计
	 */
	@PreAuthorize( "@ss.hasPermi('report-member:memberChannel:lists')" )
	@GetMapping( "/lists" )
	public TableDataInfo lists(  MemberInfo memberInfo ) {
		startPage();
		List<RspMemberChannel> memberstatistics = memberInfoService.memberstatistics(memberInfo);
		return getDataTable( memberstatistics );
	}

}
