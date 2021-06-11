package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberGameDatafix;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IMemberGameDatafixService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 游戏注单修复Controller
 *
 * @author 77tv
 * @date 2021-06-11
 */
@RestController
@RequestMapping( "/game/memberGameDatafix" )
public class MemberGameDatafixController extends BaseController {
	@Autowired
	private IMemberGameDatafixService memberGameDatafixService;

	/**
	 * 查询游戏注单修复列表
	 */
	@PreAuthorize( "@ss.hasPermi('game:memberGameDatafix:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list(MemberGameDatafix memberGameDatafix) {
		startPage();
		List<MemberGameDatafix> list = memberGameDatafixService.selectMemberGameDatafixList(memberGameDatafix);
		return getDataTable( list );
	}

	/**
	 * 导出游戏注单修复列表
	 */
	@PreAuthorize( "@ss.hasPermi('game:memberGameDatafix:export')" )
	@Log( title = "游戏注单修复", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(MemberGameDatafix memberGameDatafix, HttpServletResponse response) {
		List<MemberGameDatafix>      list = memberGameDatafixService.selectMemberGameDatafixList(memberGameDatafix);
		ExportExcelUtil.exportExcel( list, "游戏注单修复", "游戏注单修复表", MemberGameDatafix.class, response );
	}

	/**
	 * 获取游戏注单修复详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('game:memberGameDatafix:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( memberGameDatafixService.selectMemberGameDatafixById(id) );
	}

	/**
	 * 新增游戏注单修复
	 */
	@PreAuthorize( "@ss.hasPermi('game:memberGameDatafix:add')" )
	@Log( title = "游戏注单修复", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody MemberGameDatafix memberGameDatafix) {
		memberGameDatafix.setId(UuidUtil.getRandomUuidWithoutSeparator());
		memberGameDatafix.setStatus(0);
		return toAjax( memberGameDatafixService.insertMemberGameDatafix(memberGameDatafix) );
	}



	/**
	 * 删除游戏注单修复
	 */
	@PreAuthorize( "@ss.hasPermi('game:memberGameDatafix:remove')" )
	@Log( title = "游戏注单修复", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( memberGameDatafixService.deleteMemberGameDatafixByIds( ids ) );
	}
}
