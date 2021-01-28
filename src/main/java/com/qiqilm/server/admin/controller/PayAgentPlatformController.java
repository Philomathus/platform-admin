package com.qiqilm.server.admin.controller;

import java.util.ArrayList;
import java.util.List;

import com.qiqilm.server.admin.domain.PayType;
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
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.service.IPayAgentPlatformService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 【代付平台】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/pay/payAgentPlatform" )
public class PayAgentPlatformController extends BaseController {
	@Autowired
	private IPayAgentPlatformService payAgentPlatformService;

	/**
	 * 查询【代付平台】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentPlatform:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(PayAgentPlatform payAgentPlatform) {
		startPage();
		List<PayAgentPlatform> list = payAgentPlatformService.selectPayAgentPlatformList(payAgentPlatform);
		for ( PayAgentPlatform agentPlatform : list ) {
			agentPlatform.setSignMd5( "*********" );
			agentPlatform.setHeaderKey( "*********" );
			agentPlatform.setSignPublicKey( "*********" );
			agentPlatform.setSignPrivateKey( "*********" );
		}
		return getDataTable( list );
	}
    
	/**
	 * 导出【代付平台】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentPlatform:export')" )
	@Log( title = "【代付平台】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(PayAgentPlatform payAgentPlatform) {
		List<PayAgentPlatform>      list = payAgentPlatformService.selectPayAgentPlatformList(payAgentPlatform);
		ExcelUtil<PayAgentPlatform> util = new ExcelUtil<PayAgentPlatform>(PayAgentPlatform. class);
		return util.exportExcel( list, "payAgentPlatform" );
	}

	/**
	 * 获取【代付平台】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentPlatform:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( payAgentPlatformService.selectPayAgentPlatformById(id) );
	}

	/**
	 * 新增【代付平台】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentPlatform:add')" )
	@Log( title = "【代付平台】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody PayAgentPlatform payAgentPlatform) {
		return toAjax( payAgentPlatformService.insertPayAgentPlatform(payAgentPlatform) );
	}

	/**
	 * 修改【代付平台】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentPlatform:edit')" )
	@Log( title = "【代付平台】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayAgentPlatform payAgentPlatform) {
		return toAjax( payAgentPlatformService.updatePayAgentPlatform(payAgentPlatform) );
	}

	/**
	 * 删除【代付平台】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentPlatform:remove')" )
	@Log( title = "【代付平台】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( payAgentPlatformService.deletePayAgentPlatformByIds( ids ) );
	}

	/**
	 * 代付平台状态修改
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payType:edit')" )
	@Log( title = "代付平台状态修改", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeStatus" )
	public AjaxResult changeStatus( @RequestBody PayAgentPlatform payAgentPlatform) {
		return toAjax( payAgentPlatformService.updatePayAgentPlatform(payAgentPlatform));
	}

	/**
	 * 代付平台选择列表
	 *
	 * @return
	 */
	@GetMapping("/effect-pay-agents")
	public AjaxResult findAgents()
	{
		PayAgentPlatform payAgentPlatform=new PayAgentPlatform();
		List<PayAgentPlatform> data = payAgentPlatformService.selectPayAgentPlatformList(payAgentPlatform);
		if ( StringUtils.isNull( data ) ) {
			data = new ArrayList<>();
		}
		return AjaxResult.success(data);
	}

}
