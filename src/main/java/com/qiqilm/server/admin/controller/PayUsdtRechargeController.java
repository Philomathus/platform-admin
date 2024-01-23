package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.ConfigUsdtRecharge;
import com.qiqilm.server.admin.domain.PayUsdtRecharge;
import com.qiqilm.server.admin.domain.req.ReqPayUsdtRecharge;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IConfigUsdtRechargeService;
import com.qiqilm.server.admin.service.IPayUsdtRechargeService;
import com.qiqilm.server.admin.service.ISysUserService;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * USDT充值提交记录Controller
 *
 * @author 77tv
 * @date 2021-09-14
 */
@RestController
@RequestMapping( "/admin/payUsdtRecharge" )
public class PayUsdtRechargeController extends BaseController {
	@Autowired
	private IPayUsdtRechargeService payUsdtRechargeService;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private IConfigUsdtRechargeService configUsdtRechargeService;

	/**
	 * 查询USDT充值提交记录列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:payUsdtRecharge:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(ReqPayUsdtRecharge reqPayUsdtRecharge) {
		startPage();
		List<PayUsdtRecharge> list = payUsdtRechargeService.selectPayUsdtRechargeList(reqPayUsdtRecharge);
		return getDataTable( list );
	}

	/**
	 * 查询USDT充值提交记录列表统计
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberRechargeLog:list')" )
	@GetMapping( "/listCount" )
	public Map listCount(ReqPayUsdtRecharge req ) {
		return payUsdtRechargeService.listCount( req );
	}

	/**
	 * 渠道名称选择列表
	 *
	 * @return
	 */
	@PreAuthorize( "@ss.hasPermi('admin:payUsdtRecharge:list')" )
	@GetMapping( "/channelNames" )
	public AjaxResult channelNames() {
		ConfigUsdtRecharge configUsdtRecharge = new ConfigUsdtRecharge();
		List<ConfigUsdtRecharge> data           = configUsdtRechargeService.selectConfigUsdtRechargeList( configUsdtRecharge );
		if ( StringUtils.isNull( data ) ) {
			data = new ArrayList<>();
		}
		return AjaxResult.success( data );
	}

	/**
	 * 导出USDT充值提交记录列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:payUsdtRecharge:export')" )
	@Log( title = "USDT充值提交记录", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(ReqPayUsdtRecharge reqPayUsdtRecharge, HttpServletResponse response) {
		List<PayUsdtRecharge>      list = payUsdtRechargeService.selectPayUsdtRechargeList(reqPayUsdtRecharge);
		ExportExcelUtil.exportExcel( list, "USDT充值提交记录", "USDT充值提交记录表", PayUsdtRecharge.class, response );
	}

	/**
	 * 获取USDT充值提交记录详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:payUsdtRecharge:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( payUsdtRechargeService.selectPayUsdtRechargeById(id) );
	}

//	/**
//	 * 新增USDT充值提交记录
//	 */
//	@PreAuthorize( "@ss.hasPermi('admin:payUsdtRecharge:add')" )
//	@Log( title = "新增USDT充值提交记录", businessType = BusinessType.INSERT )
//	@PostMapping
//	public AjaxResult add( @RequestBody PayUsdtRecharge payUsdtRecharge) {
//		return toAjax( payUsdtRechargeService.insertPayUsdtRecharge(payUsdtRecharge) );
//	}

	/**
	 * 锁定USDT充值提交记录
	 */
	@PreAuthorize( "@ss.hasPermi('admin:payUsdtRecharge:edit')" )
	@Log( title = "锁定USDT入款申请", businessType = BusinessType.UPDATE )
	@GetMapping( value = "/lock/{id}" )
	public int lock( @PathVariable( "id" ) Long id) {
		return payUsdtRechargeService.lock(id);
	}

	/**
	 * 解锁USDT充值提交记录
	 */
	@PreAuthorize( "@ss.hasPermi('admin:payUsdtRecharge:edit')" )
	@Log( title = "解锁USDT入款申请", businessType = BusinessType.UPDATE )
	@GetMapping( value = "/unLock/{id}" )
	public AjaxResult unLock( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( payUsdtRechargeService.unLock(id) );
	}

	/**
	 * 通过USDT充值提交记录
	 */
	@PreAuthorize( "@ss.hasPermi('admin:payUsdtRecharge:edit')" )
	@Log( title = "通过USDT充值提交记录", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayUsdtRecharge payUsdtRecharge) throws Exception{
		if (payUsdtRecharge.getGoogleAuthCode() == null) {
			return AjaxResult.error(1,"请输入google验证码");
		}
		LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
		String googleAuthSecret = sysUserService.selectGoogleAuthKeyByUserName(loginUser.getUsername());
		if (!org.springframework.util.StringUtils.hasText(googleAuthSecret)) {
			return AjaxResult.error(1,"未绑定google验证秘钥，无法审核");
		}
		if (googleAuthSecret.length() == 32) {
			return AjaxResult.error(1,"google验证秘钥未加密，请重新登录");
		}
		String googleAuthKey = RSACoder.decryptByPrivateKey(googleAuthSecret, AuthUtil.getSecurityKeyStr("secretkey" +
				"/googleAuthPrivateKey"));
		if (!GoogleAuthUtil.verifyCode(googleAuthKey, payUsdtRecharge.getGoogleAuthCode())) {
			return AjaxResult.error(1,"google验证码不正确，请检查");
		}
		return payUsdtRechargeService.updatePayUsdtRecharge(payUsdtRecharge);
	}

	/**
	 * 拒绝USDT充值提交记录
	 */
	@PreAuthorize( "@ss.hasPermi('admin:payUsdtRecharge:edit')" )
	@Log( title = "拒绝USDT充值提交记录", businessType = BusinessType.UPDATE )
	@PutMapping("/refuse")
	public int refuse( @RequestBody PayUsdtRecharge payUsdtRecharge) {
		return payUsdtRechargeService.refusePayUsdtRecharge(payUsdtRecharge);
	}

//	/**
//	 * 删除USDT充值提交记录
//	 */
//	@PreAuthorize( "@ss.hasPermi('admin:payUsdtRecharge:remove')" )
//	@Log( title = "删除USDT充值提交记录", businessType = BusinessType.DELETE )
//	@DeleteMapping( "/{ids}" )
//	public AjaxResult remove( @PathVariable Long[] ids ) {
//		return toAjax( payUsdtRechargeService.deletePayUsdtRechargeByIds( ids ) );
//	}
}
