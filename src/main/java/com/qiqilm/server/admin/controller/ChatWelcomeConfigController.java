package com.qiqilm.server.admin.controller;

import java.util.Date;
import java.util.List;

import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.PayAgentRechargeAccount;
import com.qiqilm.server.admin.domain.PayType;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.mapper.PayAgentRechargeAccountMapper;
import com.qiqilm.server.admin.service.IPayAgentRechargeAccountService;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.StringUtils;
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
import com.qiqilm.server.admin.domain.ChatWelcomeConfig;
import com.qiqilm.server.admin.service.IChatWelcomeConfigService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 代充人欢迎语配置Controller
 *
 * @author 77tv
 * @date 2021-09-24
 */
@RestController
@RequestMapping( "/admin/chatWelcomeConfig" )
public class ChatWelcomeConfigController extends BaseController {
	@Autowired
	private IChatWelcomeConfigService chatWelcomeConfigService;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private PayAgentRechargeAccountMapper payAgentRechargeAccountMapper;
	@Autowired
	private MemberInfoMapper memberInfoMapper;

	/**
	 * 查询代充人欢迎语配置列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:chatWelcomeConfig:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(ChatWelcomeConfig chatWelcomeConfig) {
		startPage();
		List<ChatWelcomeConfig> list = chatWelcomeConfigService.selectChatWelcomeConfigList(chatWelcomeConfig);
		return getDataTable( list );
	}
    
	/**
	 * 导出代充人欢迎语配置列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:chatWelcomeConfig:export')" )
	@Log( title = "代充人欢迎语配置", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(ChatWelcomeConfig chatWelcomeConfig, HttpServletResponse response) {
		List<ChatWelcomeConfig>      list = chatWelcomeConfigService.selectChatWelcomeConfigList(chatWelcomeConfig);
		ExportExcelUtil.exportExcel( list, "代充人欢迎语配置", "代充人欢迎语配置表", ChatWelcomeConfig.class, response );
	}

	/**
	 * 获取代充人欢迎语配置详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:chatWelcomeConfig:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( chatWelcomeConfigService.selectChatWelcomeConfigById(id) );
	}

	/**
	 * 获取代充人账号列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:chatWelcomeConfig:list')" )
	@GetMapping( value = "/accounts" )
	public AjaxResult getInfoAccounts() {
		List<PayAgentRechargeAccount> payAgentRechargeAccounts = payAgentRechargeAccountMapper.selectPayAgentRechargeAccountAllList();
		for(PayAgentRechargeAccount pa:payAgentRechargeAccounts){
			pa.setNickName(pa.getAccount() + "-" + pa.getNickName());
		}
		return AjaxResult.success(payAgentRechargeAccounts);
	}


	/**
	 * 新增代充人欢迎语配置
	 */
	@PreAuthorize( "@ss.hasPermi('admin:chatWelcomeConfig:add')" )
	@Log( title = "代充人欢迎语配置", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ChatWelcomeConfig chatWelcomeConfig) {
		if(chatWelcomeConfig.getAccount() != null) {
			MemberInfo memberInfo = memberInfoMapper.selectMemberInfoById( chatWelcomeConfig.getAccount() );
			if ( memberInfo == null ) {
				return AjaxResult.error( 0,"该会员ID不存在" );
			}
			Integer id = payAgentRechargeAccountMapper.idSearchByMemberId( chatWelcomeConfig.getAccount() );
			if ( id == null ) {
				return AjaxResult.error( 0,"该代充人账号不存在," );
			} else {
				chatWelcomeConfig.setAgentId(Long.valueOf(id));
			}
			ChatWelcomeConfig chatWelcomeConfig1 = chatWelcomeConfigService.selectChatWelcomeConfigByAgentId(chatWelcomeConfig.getAgentId());
			if(chatWelcomeConfig1 != null){
				return AjaxResult.error(0,"此代充人已有欢迎语");
			}
			LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
			String    username  = loginUser.getUsername();
			chatWelcomeConfig.setCreateBy(username);
			chatWelcomeConfig.setCreateTime(new Date());
			chatWelcomeConfig.setStatus("0");
			return toAjax( chatWelcomeConfigService.insertChatWelcomeConfig(chatWelcomeConfig) );
		}
		return AjaxResult.error(0,"请填写代充人账号");
	}

	/**
	 * 修改代充人欢迎语配置
	 */
	@PreAuthorize( "@ss.hasPermi('admin:chatWelcomeConfig:edit')" )
	@Log( title = "代充人欢迎语配置", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ChatWelcomeConfig chatWelcomeConfig) {
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    username  = loginUser.getUsername();
		chatWelcomeConfig.setOperator(username);
		chatWelcomeConfig.setOperatorTime(new Date());
		return toAjax( chatWelcomeConfigService.updateChatWelcomeConfig(chatWelcomeConfig) );
	}

	/**
	 * 删除代充人欢迎语配置
	 */
	@PreAuthorize( "@ss.hasPermi('admin:chatWelcomeConfig:remove')" )
	@Log( title = "代充人欢迎语配置", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( chatWelcomeConfigService.deleteChatWelcomeConfigByIds( ids ) );
	}
}
