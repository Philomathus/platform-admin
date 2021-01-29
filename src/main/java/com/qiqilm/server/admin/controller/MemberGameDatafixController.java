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
import com.qiqilm.server.admin.domain.MemberGameDatafix;
import com.qiqilm.server.admin.service.IMemberGameDatafixService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-29
 */
@RestController
@RequestMapping( "/admin/memberGameDatafix" )
public class MemberGameDatafixController extends BaseController {
	@Autowired
	private IMemberGameDatafixService memberGameDatafixService;

	/**
	 * 查询【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:memberGameDatafix:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(MemberGameDatafix memberGameDatafix) {
		startPage();
		List<MemberGameDatafix> list = memberGameDatafixService.selectMemberGameDatafixList(memberGameDatafix);
		return getDataTable( list );
	}
    
	/**
	 * 导出【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:memberGameDatafix:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(MemberGameDatafix memberGameDatafix) {
		List<MemberGameDatafix>      list = memberGameDatafixService.selectMemberGameDatafixList(memberGameDatafix);
		ExcelUtil<MemberGameDatafix> util = new ExcelUtil<MemberGameDatafix>(MemberGameDatafix. class);
		return util.exportExcel( list, "memberGameDatafix" );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:memberGameDatafix:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( memberGameDatafixService.selectMemberGameDatafixById(id) );
	}

	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:memberGameDatafix:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody MemberGameDatafix memberGameDatafix) {
		return toAjax( memberGameDatafixService.insertMemberGameDatafix(memberGameDatafix) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:memberGameDatafix:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody MemberGameDatafix memberGameDatafix) {
		return toAjax( memberGameDatafixService.updateMemberGameDatafix(memberGameDatafix) );
	}

	/**
	 * 删除【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:memberGameDatafix:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( memberGameDatafixService.deleteMemberGameDatafixByIds( ids ) );
	}
}