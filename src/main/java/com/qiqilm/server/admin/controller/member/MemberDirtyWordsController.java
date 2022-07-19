package com.qiqilm.server.admin.controller.member;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberDirtyWords;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IMemberDirtyWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/admin/memberDirtyWords" )
public class MemberDirtyWordsController extends BaseController {
	@Autowired
	private IMemberDirtyWordsService memberDirtyWordsService;


	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:memberDirtyWords:query')" )
	@GetMapping( value = "/list" )
	public AjaxResult getInfo() {
		return AjaxResult.success( memberDirtyWordsService.selectMemberDirtyWordsById() );
	}


	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:memberDirtyWords:edit')" )
	@Log( title = "编辑脏字昵称", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody MemberDirtyWords memberDirtyWords ) {
		return toAjax( memberDirtyWordsService.updateMemberDirtyWords( memberDirtyWords ) );
	}


}