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
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.service.IMemberWithdrawLogService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/pay/memberWithdrawLog" )
public class MemberWithdrawLogController extends BaseController {
	@Autowired
	private IMemberWithdrawLogService memberWithdrawLogService;

	/**
	 * 查询【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(MemberWithdrawLog memberWithdrawLog) {
		startPage();
		List<MemberWithdrawLog> list = memberWithdrawLogService.selectMemberWithdrawLogList(memberWithdrawLog);
		return getDataTable( list );
	}
    
	/**
	 * 导出【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(MemberWithdrawLog memberWithdrawLog) {
		List<MemberWithdrawLog>      list = memberWithdrawLogService.selectMemberWithdrawLogList(memberWithdrawLog);
		ExcelUtil<MemberWithdrawLog> util = new ExcelUtil<MemberWithdrawLog>(MemberWithdrawLog. class);
		return util.exportExcel( list, "memberWithdrawLog" );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( memberWithdrawLogService.selectMemberWithdrawLogById(id) );
	}

	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody MemberWithdrawLog memberWithdrawLog) {
		return toAjax( memberWithdrawLogService.insertMemberWithdrawLog(memberWithdrawLog) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody MemberWithdrawLog memberWithdrawLog) {
		return toAjax( memberWithdrawLogService.updateMemberWithdrawLog(memberWithdrawLog) );
	}

	/**
	 * 删除【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( memberWithdrawLogService.deleteMemberWithdrawLogByIds( ids ) );
	}
}
