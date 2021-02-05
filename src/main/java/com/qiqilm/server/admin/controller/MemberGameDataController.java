package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberGameData;
import com.qiqilm.server.admin.domain.rsp.RspLotteryBetLog;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IMemberGameDataService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    	public TableDataInfo list(MemberGameData memberGameData) {
        if (memberGameData.getSelectDate() != null) {
            memberGameData.setStartTime(memberGameData.getSelectDate()[0] + " 00:00:00");
            memberGameData.setEndTime(memberGameData.getSelectDate()[1] + " 23:59:59");
        }
        startPage();
        List<MemberGameData> list = memberGameDataService.selectMemberGameDataList(memberGameData);
		return getDataTable( list );
	}
	@PutMapping(value = "/getLotteryBetData")
	public AjaxResult getBetData( MemberGameData memberGameData ) {
		return memberGameDataService.getBetData( memberGameData);
	}
	/**
	 * 查询会员注单数据统计
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberGameData:list')" )
	@GetMapping( "/getCount" )
    	public AjaxResult getCount(MemberGameData memberGameData) {
        if (memberGameData.getSelectDate() != null) {
            memberGameData.setStartTime(memberGameData.getSelectDate()[0] + " 00:00:00");
            memberGameData.setEndTime(memberGameData.getSelectDate()[1] + " 23:59:59");
        }
        return memberGameDataService.getCount(memberGameData);
	}

	/**
	 * 导出会员注单数据列表
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberGameData:export')" )
	@Log( title = "会员注单数据", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(MemberGameData memberGameData) {
		List<MemberGameData>      list = memberGameDataService.selectMemberGameDataList(memberGameData);
		ExcelUtil<MemberGameData> util = new ExcelUtil<MemberGameData>(MemberGameData. class);
		return util.exportExcel( list, "memberGameData" );
	}

	/**
	 * 获取会员注单数据详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberGameData:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( memberGameDataService.selectMemberGameDataById(id) );
	}

	/**
	 * 新增会员注单数据
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberGameData:add')" )
	@Log( title = "会员注单数据", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody MemberGameData memberGameData) {
		return toAjax( memberGameDataService.insertMemberGameData(memberGameData) );
	}

	/**
	 * 修改会员注单数据
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberGameData:edit')" )
	@Log( title = "会员注单数据", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody MemberGameData memberGameData) {
		return toAjax( memberGameDataService.updateMemberGameData(memberGameData) );
	}

	/**
	 * 删除会员注单数据
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberGameData:remove')" )
	@Log( title = "会员注单数据", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( memberGameDataService.deleteMemberGameDataByIds( ids ) );
	}
}
