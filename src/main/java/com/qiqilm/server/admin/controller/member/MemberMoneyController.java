package com.qiqilm.server.admin.controller.member;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.MemberMoney;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IMemberMoneyService;
import com.qiqilm.server.admin.service.ISysUserService;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 派送彩金暂存表Controller
 *
 * @author 77tv
 * @date 2022-02-09
 */
@RestController
@RequestMapping( "/admin/memberMoney" )
public class MemberMoneyController extends BaseController {
	@Autowired
	private IMemberMoneyService memberMoneyService;

	@Autowired
	private TokenService tokenService;
	@Autowired
	private ISysUserService sysUserService;

	/**
	 * 查询派送彩金暂存表列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:memberMoney:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(MemberMoney memberMoney) {
		startPage();
		List<MemberMoney> list = memberMoneyService.selectMemberMoneyList(memberMoney);
		return getDataTable( list );
	}

	@PreAuthorize( "@ss.hasPermi('admin:memberMoney:remove')" )
	@GetMapping( "/handleClean" )
	public AjaxResult handleClean() {
		//清除表中数据
		memberMoneyService.clear();
		return AjaxResult.success();
	}
    
	/**
	 * 导出派送彩金暂存表列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:memberMoney:export')" )
	@Log( title = "派送彩金暂存表", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(MemberMoney memberMoney, HttpServletResponse response) {
		List<MemberMoney>      list = memberMoneyService.selectMemberMoneyList(memberMoney);
		ExportExcelUtil.exportExcel( list, "派送彩金暂存表", "派送彩金暂存表表", MemberMoney.class, response );
	}

	/**
	 * 获取派送彩金暂存表详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:memberMoney:query')" )
	@GetMapping( value = "/{memberId}" )
	public AjaxResult getInfo( @PathVariable( "memberId" ) String memberId) {
		return AjaxResult.success( memberMoneyService.selectMemberMoneyById(memberId) );
	}

	/**
	 * 开始派送彩金
	 */
	@PreAuthorize( "@ss.hasPermi('admin:memberMoney:edit')" )
	@Log( title = "开始派送彩金", businessType = BusinessType.INSERT )
	@PostMapping("/starSend")
	public AjaxResult starSend( @RequestBody MemberMoney memberMoney) throws Exception{
		if (memberMoney.getGoogleAuthCode() == null) {
			return AjaxResult.error("请输入google验证码");
		}
		LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
		String googleAuthSecret = sysUserService.selectGoogleAuthKeyByUserName(loginUser.getUsername());

		if (!org.springframework.util.StringUtils.hasText(googleAuthSecret)) {
			return AjaxResult.error("未绑定google验证秘钥，无法审核");
		}
		if (googleAuthSecret.length() == 32) {
			return AjaxResult.error("google验证秘钥未加密，请重新登录");
		}
		String googleAuthKey = RSACoder.decryptByPrivateKey(googleAuthSecret, AuthUtil.getSecurityKeyStr("secretkey" +
				"/googleAuthPrivateKey"));

		if (!GoogleAuthUtil.verifyCode(googleAuthKey, memberMoney.getGoogleAuthCode())) {
			return AjaxResult.error("google验证码不正确，请检查");
		}
		return memberMoneyService.starSend(memberMoney);
	}

	/**
	 * 新增派送彩金暂存表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:memberMoney:add')" )
	@Log( title = "派送彩金暂存表", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody MemberMoney memberMoney) {
		return toAjax( memberMoneyService.insertMemberMoney(memberMoney) );
	}

	/**
	 * 修改派送彩金暂存表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:memberMoney:edit')" )
	@Log( title = "派送彩金暂存表", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody MemberMoney memberMoney) {
		return toAjax( memberMoneyService.updateMemberMoney(memberMoney) );
	}

	/**
	 * 删除派送彩金暂存表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:memberMoney:remove')" )
	@Log( title = "派送彩金暂存表", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{memberIds}" )
	public AjaxResult remove( @PathVariable String[] memberIds ) {
		return toAjax( memberMoneyService.deleteMemberMoneyByIds( memberIds ) );
	}

	@GetMapping("/count")
	public AjaxResult count(){
		return AjaxResult.success(memberMoneyService.countMoney());
	}
}
