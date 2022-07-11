//package com.qiqilm.server.admin.controller;
//
//import com.qiqilm.server.admin.annotation.Log;
//import com.qiqilm.server.admin.core.controller.BaseController;
//import com.qiqilm.server.admin.core.page.TableDataInfo;
//import com.qiqilm.server.admin.core.vo.AjaxResult;
//import com.qiqilm.server.admin.domain.MemberGameDatafix;
//import com.qiqilm.server.admin.domain.MemberInfo;
//import com.qiqilm.server.admin.enums.BusinessType;
//import com.qiqilm.server.admin.service.IMemberGameDatafixService;
//import com.qiqilm.server.admin.service.IMemberInfoService;
//import com.qiqilm.server.admin.utils.ExportExcelUtil;
//import com.qiqilm.server.admin.utils.StringUtils;
//import com.qiqilm.server.admin.utils.UuidUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletResponse;
//import java.util.List;
//
///**
// * 游戏补单Controller
// *
// * @author 77tv
// * @date 2021-01-29
// */
//@RestController
//@RequestMapping( "/game/memberGameDatafix" )
//public class MemberGameDatafixController extends BaseController {
//	@Autowired
//	private IMemberGameDatafixService memberGameDatafixService;
//	@Autowired
//	private IMemberInfoService        memberInfoService;
//
//	/**
//	 * 查询游戏补单列表
//	 */
//	@PreAuthorize( "@ss.hasPermi('game:memberGameDatafix:list')" )
//	@GetMapping( "/list" )
//	public TableDataInfo list( MemberGameDatafix memberGameDatafix ) {
//		startPage();
//		List<MemberGameDatafix> list = memberGameDatafixService.selectMemberGameDatafixList( memberGameDatafix );
//		return getDataTable( list );
//	}
//
//
//	/**
//	 * 新增游戏补单
//	 */
//	@PreAuthorize( "@ss.hasPermi('game:memberGameDatafix:add')" )
//	@Log( title = "新增补单", businessType = BusinessType.INSERT )
//	@PostMapping
//	public AjaxResult add( @RequestBody MemberGameDatafix memberGameDatafix ) {
//		memberGameDatafix.setId( UuidUtil.getRandomUuidWithoutSeparator() );
//		memberGameDatafix.setStatus( 0 );
//		if (!StringUtils.isEmpty(memberGameDatafix.getUserId())) {
//			MemberInfo memberInfo = memberInfoService.selectMemberInfoById( memberGameDatafix.getUserId() );
//			if ( memberInfo == null )
//				return AjaxResult.error( "用户不存在" );
//		}
//		return toAjax( memberGameDatafixService.insertMemberGameDatafix( memberGameDatafix ) );
//	}
//
//	/**
//	 * 导出游戏注单修复列表
//	 */
//	@PreAuthorize( "@ss.hasPermi('game:memberGameDatafix:export')" )
//	@Log( title = "游戏注单修复", businessType = BusinessType.EXPORT )
//	@GetMapping( "/export" )
//	public void export( MemberGameDatafix memberGameDatafix, HttpServletResponse response ) {
//		List<MemberGameDatafix> list = memberGameDatafixService.selectMemberGameDatafixList( memberGameDatafix );
//		ExportExcelUtil.exportExcel( list, "游戏注单修复", "游戏注单修复表", MemberGameDatafix.class, response );
//	}
//
//	/**
//	 * 删除游戏注单修复
//	 */
//	@PreAuthorize( "@ss.hasPermi('game:memberGameDatafix:remove')" )
//	@Log( title = "游戏注单修复", businessType = BusinessType.DELETE )
//	@DeleteMapping( "/{ids}" )
//	public AjaxResult remove( @PathVariable String[] ids ) {
//		return toAjax( memberGameDatafixService.deleteMemberGameDatafixByIds( ids ) );
//	}
//
//}
