package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.BankList;
import com.qiqilm.server.admin.domain.ConfigBank;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.mapper.SysUserMapper;
import com.qiqilm.server.admin.service.IBankListService;
import com.qiqilm.server.admin.service.IConfigBankService;
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
 * 【公司入款银行列表】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/pay/configBank" )
public class ConfigBankController extends BaseController {
	@Autowired
	private IConfigBankService configBankService;
	@Autowired
	private IBankListService   bankListService;
	@Autowired
	private TokenService       tokenService;
	@Resource
	private SysUserMapper      sysUserMapper;

	/**
	 * 查询【公司入款银行列表】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configBank:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( ConfigBank configBank ) {
		startPage();
		List<ConfigBank> list = configBankService.selectConfigBankList( configBank );
		return getDataTable( list );
	}

	/**
	 * 导出【公司入款银行列表】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configBank:export')" )
	@Log( title = "【公司入款银行列表】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( ConfigBank configBank, HttpServletResponse response ) {
		List<ConfigBank> list = configBankService.selectConfigBankList( configBank );
		ExportExcelUtil.exportExcel( list, "公司入款银行列表", "公司入款银行表", ConfigBank.class, response );
	}

	/**
	 * 获取【公司入款银行列表】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configBank:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id ) {
		return AjaxResult.success( configBankService.selectConfigBankById( id ) );
	}

	/**
	 * 新增【公司入款银行列表】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configBank:add')" )
	@Log( title = "【公司入款银行列表】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ConfigBank configBank ) throws Exception {
		if ( configBank.getGoogleAuthCode() == null ) {
			return AjaxResult.error( "请输入google验证码" );
		}
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    userName  = loginUser.getUser().getUserName();

		String googleAuthSecret = sysUserMapper.selectGoogleAuthKeyByUserName( userName );

		if ( !StringUtils.hasText( googleAuthSecret ) ) {
			return AjaxResult.error( "未绑定google验证秘钥，无法审核" );
		}
		String googleAuthKey = RSACoder.decryptByPrivateKey( googleAuthSecret, AuthUtil.getSecurityKeyStr(
				"secretkey/googleAuthPrivateKey" ) );

		if ( !GoogleAuthUtil.verifyCode( googleAuthKey, configBank.getGoogleAuthCode() ) ) {
			return AjaxResult.error( "google验证码不正确，请检查" );
		}
		if((configBank.getOpenLevel() != null && configBank.getOpenLevel() < 1) || (configBank.getOpenLevel() != null && configBank.getOpenLevel() > 50)){
			return AjaxResult.error( "开放层级最小为1，最大为50" );
		}
		if((configBank.getOpenLevelMax() != null && configBank.getOpenLevelMax() < 1) || (configBank.getOpenLevelMax() != null && configBank.getOpenLevelMax() > 50)){
			return AjaxResult.error( "开放层级最小为1，最大为50" );
		}
		if(configBank.getOpenLevel() != null && configBank.getOpenLevelMax() != null && configBank.getOpenLevel() > configBank.getOpenLevelMax()){
			return AjaxResult.error( "最小开放层级和最大开放层级写反了" );
		}
		configBank.setId( UuidUtil.getRandomUuidWithoutSeparator() );
		configBank.setAccountName(configBank.getAccountName().trim());
		BankList bankList = new BankList();
		bankList.setBankName( configBank.getName() );
		List<BankList> bankLists = bankListService.selectBankListList( bankList );
		if ( bankList != null ) {
			configBank.setIcon( bankLists.get( 0 ).getBankIcon() );
			configBank.setCode( bankLists.get( 0 ).getBankCode() );
		}
		return toAjax( configBankService.insertConfigBank( configBank ) );
	}

	/**
	 * 银行列表下拉框
	 *
	 * @return
	 */
	@GetMapping( "/bankLists" )
	public AjaxResult bankLists() {
		BankList       bankList  = new BankList();
		List<BankList> bankLists = bankListService.selectBankListList( bankList );
		return AjaxResult.success( bankLists );
	}

	/**
	 * 修改【公司入款银行列表】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configBank:edit')" )
	@Log( title = "【公司入款银行列表】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ConfigBank configBank ) throws Exception {
		if ( configBank.getGoogleAuthCode() == null ) {
			return AjaxResult.error( "请输入google验证码" );
		}
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    userName  = loginUser.getUser().getUserName();

		String googleAuthSecret = sysUserMapper.selectGoogleAuthKeyByUserName( userName );

		if ( !StringUtils.hasText( googleAuthSecret ) ) {
			return AjaxResult.error( "未绑定google验证秘钥，无法审核" );
		}
		String googleAuthKey = RSACoder.decryptByPrivateKey( googleAuthSecret, AuthUtil.getSecurityKeyStr(
				"secretkey/googleAuthPrivateKey" ) );

		if ( !GoogleAuthUtil.verifyCode( googleAuthKey, configBank.getGoogleAuthCode() ) ) {
			return AjaxResult.error( "google验证码不正确，请检查" );
		}
		if(configBank.getOpenLevel() != null && (configBank.getOpenLevel() < 1 ||  configBank.getOpenLevel() > 50)){
			return AjaxResult.error( "开放层级最小为1，最大为50" );
		}
		if(configBank.getOpenLevelMax() != null && (configBank.getOpenLevelMax() < 1 || configBank.getOpenLevelMax() > 50)){
			return AjaxResult.error( "开放层级最小为1，最大为50" );
		}
		if(configBank.getOpenLevel() != null && configBank.getOpenLevelMax() != null && configBank.getOpenLevel() > configBank.getOpenLevelMax()){
			return AjaxResult.error( "最小开放层级和最大开放层级写反了" );
		}
		configBank.setAccountName(configBank.getAccountName().trim());
		return toAjax( configBankService.updateConfigBank( configBank ) );
	}

	/**
	 * 删除【公司入款银行列表】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configBank:remove')" )
	@Log( title = "【公司入款银行列表】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( configBankService.deleteConfigBankByIds( ids ) );
	}

	/**
	 * 支付状态修改
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configBank:edit')" )
	@Log( title = "支付类型", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeStatus" )
	public AjaxResult changeStatus( @RequestBody ConfigBank configBank ) {
		return toAjax( configBankService.updateConfigBank( configBank ) );
	}

	@PreAuthorize( "@ss.hasPermi('pay:configBank:edit')" )
	@Log( title = "支付类型", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeText" )
	public AjaxResult changeText( @RequestBody ConfigBank configBank ) {
		return toAjax( configBankService.updateConfigBank( configBank ) );
	}
}
