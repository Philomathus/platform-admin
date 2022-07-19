package com.qiqilm.server.admin.controller.member;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.MemberBcode;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.mapper.SysUserMapper;
import com.qiqilm.server.admin.service.IMemberBcodeService;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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
	@Autowired
	private TokenService tokenService;
	@Resource
	private SysUserMapper sysUserMapper;

	/**
	 * 查询会员打码数据列表
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberBcode:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( MemberBcode memberBcode ) {
		startPage();
		List<MemberBcode> list = memberBcodeService.selectMemberBcodeList( memberBcode );
		return getDataTable( list );
	}

	/**
	 * 统计会员打码数据
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberBcode:list')" )
	@GetMapping( "/getTotalData" )
	public AjaxResult getTotalData( MemberBcode memberBcode ) {
		return memberBcodeService.getTotalData( memberBcode );
	}

	/**
	 * 导出会员打码数据列表
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberBcode:export')" )
	@Log( title = "会员打码数据", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( MemberBcode memberBcode, HttpServletResponse response ) {
		List<MemberBcode> list = memberBcodeService.selectMemberBcodeList( memberBcode );
		ExportExcelUtil.exportExcel( list, "会员打码数据", "会员打码数据表", MemberBcode.class, response );
	}

	/**
	 * 获取会员打码数据详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberBcode:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id ) {
		return AjaxResult.success( memberBcodeService.selectMemberBcodeById( id ) );
	}

	/**
	 * 修改会员打码数据
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberBcode:edit')" )
	@Log( title = "修改会员打码数据", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit(@RequestBody MemberBcode memberBcode) throws Exception {
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String userName = loginUser.getUser().getUserName();

		if ( memberBcode.getGoogleAuthCode() == null ) {
			return AjaxResult.error( "请输入google验证码" );
		}

		String googleAuthSecret = sysUserMapper.selectGoogleAuthKeyByUserName( userName );
		if ( !StringUtils.hasText( googleAuthSecret ) ) {
			return AjaxResult.error( "未绑定google验证秘钥，无法审核" );
		}

		String googleAuthKey = RSACoder.decryptByPrivateKey( googleAuthSecret,
				AuthUtil.getSecurityKeyStr( "secretkey/googleAuthPrivateKey" ) );
		if ( !GoogleAuthUtil.verifyCode( googleAuthKey, memberBcode.getGoogleAuthCode() ) ) {
			return AjaxResult.error( "google验证码不正确，请检查" );
		}

		return toAjax( memberBcodeService.updateMemberBcode(memberBcode) );
	}

}
