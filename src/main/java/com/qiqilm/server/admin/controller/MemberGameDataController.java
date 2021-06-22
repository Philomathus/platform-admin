package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.AccessLimit;
import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.req.ReqMemberGameData;
import com.qiqilm.server.admin.domain.rsp.RspMemberGameData;
import com.qiqilm.server.admin.domain.MemberGameData;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IMemberGameDataService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 会员注单数据Controller
 *
 * @author 77tv
 * @date 2021-01-29
 */
@RestController
@RequestMapping( "/member/memberGameData" )
public class MemberGameDataController extends BaseController {
	@Autowired
	private IMemberGameDataService memberGameDataService;

	/**
	 * 查询会员注单数据列表
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberGameData:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( ReqMemberGameData reqMemberGameData ) {
		startPage();
		List<RspMemberGameData> list = memberGameDataService.selectMemberGameDataList( reqMemberGameData );
		return getDataTable( list );
	}

	@PutMapping( value = "/getLotteryBetData" )
	public AjaxResult getBetData( MemberGameData memberGameData ) {
		return memberGameDataService.getBetData( memberGameData );
	}

	/**
	 * 查询会员注单数据统计
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberGameData:list')" )
	@GetMapping( "/getCount" )
	public AjaxResult getCount( ReqMemberGameData reqMemberGameData ) {
		RspMemberGameData rspMemberGameData = memberGameDataService.getCount( reqMemberGameData );
		return AjaxResult.success(rspMemberGameData);
	}

	/**
	 * 导出会员注单数据列表
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberGameData:export')" )
	@Log( title = "会员注单数据", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( ReqMemberGameData reqMemberGameData, HttpServletResponse response) {
		List<RspMemberGameData>      list = memberGameDataService.selectMemberGameDataList( reqMemberGameData );
		ExportExcelUtil.exportExcel( list, "会员注单数据", "会员注单数据表", RspMemberGameData.class, response );
	}

	@AccessLimit(seconds = 5, maxCount = 1)
	@PreAuthorize( "@ss.hasPermi('member:memberGameData:recordList')" )
	@GetMapping( value = "/recordList" )
	public AjaxResult getGameRecordList( MemberGameData memberGameData ) {
		return memberGameDataService.getGameBetRecordData( memberGameData );
	}

	@AccessLimit(seconds = 5, maxCount = 1)
	@PreAuthorize( "@ss.hasPermi('member:memberGameData:detailList')" )
	@GetMapping( value = "/detailList" )
	public AjaxResult getGameDetailList( MemberGameData memberGameData ) {
		return memberGameDataService.getGameBetDetailData( memberGameData );
	}
}
