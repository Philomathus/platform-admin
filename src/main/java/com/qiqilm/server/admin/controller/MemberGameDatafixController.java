package com.qiqilm.server.admin.controller;

import java.util.List;

import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.service.IMemberInfoService;
import com.qiqilm.server.admin.utils.UuidUtil;
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
 * 游戏补单Controller
 *
 * @author 77tv
 * @date 2021-01-29
 */
@RestController
@RequestMapping( "/game/memberGameDatafix" )
public class MemberGameDatafixController extends BaseController {
	@Autowired
	private IMemberGameDatafixService memberGameDatafixService;
	@Autowired
	private IMemberInfoService memberInfoService;

	/**
	 * 查询游戏补单列表
	 */
	@PreAuthorize( "@ss.hasPermi('game:memberGameDatafix:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(MemberGameDatafix memberGameDatafix) {
		startPage();
		List<MemberGameDatafix> list = memberGameDatafixService.selectMemberGameDatafixList(memberGameDatafix);
		return getDataTable( list );
	}


	/**
	 * 新增游戏补单
	 */
	@PreAuthorize( "@ss.hasPermi('game:memberGameDatafix:add')" )
	@Log( title = "新增补单", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody MemberGameDatafix memberGameDatafix) {
		memberGameDatafix.setId(UuidUtil.getRandomUuidWithoutSeparator());
		memberGameDatafix.setStatus(0);
		if (memberGameDatafix.getUserId()!=null){
			MemberInfo memberInfo = memberInfoService.selectMemberInfoById(memberGameDatafix.getUserId());
			if (memberInfo==null)return AjaxResult.error( "用户不存在" );
		}
		return toAjax( memberGameDatafixService.insertMemberGameDatafix(memberGameDatafix) );
	}


}