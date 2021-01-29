package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberBcode;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IMemberBcodeService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员打码数据Controller
 *
 * @author 77tv
 * @date 2021-01-29
 */
@RestController
@RequestMapping( "/member/memberBcode" )
public class MemberBcodeController extends BaseController {
	@Autowired
	private IMemberBcodeService memberBcodeService;

	/**
	 * 查询会员打码数据列表
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberBcode:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(MemberBcode memberBcode) {
		startPage();
		List<MemberBcode> list = memberBcodeService.selectMemberBcodeList(memberBcode);
		return getDataTable( list );
	}

	/**
	 * 查询会员打码数据列表
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberBcode:list')" )
	@GetMapping( "/getTotalData" )
    	public AjaxResult getTotalData() {
		return memberBcodeService.getTotalData();
	}

	/**
	 * 导出会员打码数据列表
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberBcode:export')" )
	@Log( title = "会员打码数据", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(MemberBcode memberBcode) {
		List<MemberBcode>      list = memberBcodeService.selectMemberBcodeList(memberBcode);
		ExcelUtil<MemberBcode> util = new ExcelUtil<MemberBcode>(MemberBcode. class);
		return util.exportExcel( list, "memberBcode" );
	}

	/**
	 * 获取会员打码数据详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberBcode:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( memberBcodeService.selectMemberBcodeById(id) );
	}

	/**
	 * 新增会员打码数据
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberBcode:add')" )
	@Log( title = "会员打码数据", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody MemberBcode memberBcode) {
		return toAjax( memberBcodeService.insertMemberBcode(memberBcode) );
	}

	/**
	 * 修改会员打码数据
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberBcode:edit')" )
	@Log( title = "会员打码数据", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody MemberBcode memberBcode) {
		return toAjax( memberBcodeService.updateMemberBcode(memberBcode) );
	}

	/**
	 * 删除会员打码数据
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberBcode:remove')" )
	@Log( title = "会员打码数据", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( memberBcodeService.deleteMemberBcodeByIds( ids ) );
	}
}
