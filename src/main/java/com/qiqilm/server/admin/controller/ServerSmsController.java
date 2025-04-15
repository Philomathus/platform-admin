package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ServerSms;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IServerSmsService;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SMS短信服务配置Controller
 *
 * @author 77tv
 * @date 2021-01-27
 */
@RestController
@RequestMapping( "/server/sms" )
public class ServerSmsController extends BaseController {
	@Autowired
	private IServerSmsService serverSmsService;

	/**
	 * 查询SMS短信服务配置列表
	 */
	@PreAuthorize( "@ss.hasPermi('server:sms:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( ServerSms serverSms ) {
		startPage();
		List<ServerSms> list = serverSmsService.selectServerSmsList( serverSms );

		final int minMaskLength = 2;
		final int maxMaskLength = 8;
		list.forEach( s -> {
			s.setAppKey( StringUtils.mask( s.getAppKey(), minMaskLength, maxMaskLength ) );
			s.setAppAccess( StringUtils.mask( s.getAppAccess(), minMaskLength, maxMaskLength ) );
		} );

		return getDataTable( list );
	}

	/**
	 * 获取SMS短信服务配置详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('server:sms:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		final ServerSms serverSms = serverSmsService.selectServerSmsById( id );

		final int minMaskLength = 2;
		final int maxMaskLength = 8;
		serverSms.setAppKey( StringUtils.mask( serverSms.getAppKey(), minMaskLength, maxMaskLength ) );
		serverSms.setAppAccess( StringUtils.mask( serverSms.getAppAccess(), minMaskLength, maxMaskLength ) );

		return AjaxResult.success( serverSms );
	}

	/**
	 * 新增SMS短信服务配置
	 */
	@PreAuthorize( "@ss.hasPermi('server:sms:add')" )
	@Log( title = "SMS短信服务配置", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ServerSms serverSms ) {
		return toAjax( serverSmsService.insertServerSms( serverSms ) );
	}

	/**
	 * 修改SMS短信服务配置
	 */
	@PreAuthorize( "@ss.hasPermi('server:sms:edit')" )
	@Log( title = "SMS短信服务配置", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ServerSms serverSms ) {
		return toAjax( serverSmsService.updateServerSms( serverSms ) );
	}

	/**
	 * 删除SMS短信服务配置
	 */
	@PreAuthorize( "@ss.hasPermi('server:sms:remove')" )
	@Log( title = "SMS短信服务配置", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( serverSmsService.deleteServerSmsByIds( ids ) );
	}

	/**
	 * 激活SMS短信服务配置
	 */
	@PreAuthorize( "@ss.hasPermi('server:sms:effect')" )
	@Log( title = "SMS短信服务配置-激活", businessType = BusinessType.EFFECT )
	@PutMapping( "/effect/{id}" )
	public AjaxResult effect( @PathVariable long id ) {
		return toAjax( serverSmsService.effect( id ) );
	}

	/**
	 * 取消激活SMS短信服务配置
	 */
	@PreAuthorize( "@ss.hasPermi('server:sms:effect')" )
	@Log( title = "SMS短信服务配置-取消激活", businessType = BusinessType.EFFECT )
	@PutMapping( "/noEffect/{id}" )
	public AjaxResult noEffect( @PathVariable long id ) {
		return toAjax( serverSmsService.noEffect( id ) );
	}

	/**
	 * 测试SMS短信服务配置
	 */
	@PreAuthorize( "@ss.hasPermi('server:sms:smsTest')" )
	@Log( title = "SMS短信服务配置", businessType = BusinessType.OTHER )
	@PutMapping( "/smsTest/{id}/{mobile}" )
	public AjaxResult smsTest( @PathVariable long id, @PathVariable String mobile ) {
		return serverSmsService.smsTest( id, mobile );
	}
}
