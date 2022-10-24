package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IPayAgentPlatformService;
import com.qiqilm.server.admin.service.IPayAgentService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 代付平台Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/pay/payAgentPlatform" )
public class PayAgentPlatformController extends BaseController {
	@Autowired
	private IPayAgentPlatformService payAgentPlatformService;
	@Autowired
	private IPayAgentService  payAgentService;

	/**
	 * 查询代付平台列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentPlatform:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( PayAgentPlatform payAgentPlatform ) {
		startPage();
		List<PayAgentPlatform> list = payAgentPlatformService.selectPayAgentPlatformList( payAgentPlatform );
		return getDataTable( list );
	}

	/**
	 * 代付平台选择列表
	 *
	 * @return
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentPlatform:list')" )
	@GetMapping( "/effect-pay-agents" )
	public AjaxResult findAgents() {
		PayAgentPlatform payAgentPlatform = new PayAgentPlatform();
		payAgentPlatform.setStatus( "1" );
		return AjaxResult.success( payAgentPlatformService.selectPayAgentPlatformList( payAgentPlatform ) );
	}

	/**
	 * 导出代付平台列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentPlatform:export')" )
	@Log( title = "代付平台", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( PayAgentPlatform payAgentPlatform, HttpServletResponse response ) {
		List<PayAgentPlatform> list = payAgentPlatformService.selectPayAgentPlatformList( payAgentPlatform );
		ExportExcelUtil.exportExcel( list, "代付平台", "代付平台表", PayAgentPlatform.class, response );
	}

	/**
	 * 获取代付平台详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentPlatform:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		return AjaxResult.success( payAgentPlatformService.selectPayAgentPlatformById( id ) );
	}

	/**
	 * 新增代付平台
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentPlatform:add')" )
	@Log( title = "代付平台", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody PayAgentPlatform payAgentPlatform ) {
		payAgentPlatform.setMerId( payAgentPlatform.getMerId().trim() );
		payAgentPlatform.setPayOrderAddr( payAgentPlatform.getPayOrderAddr().trim() );
		if ( StringUtils.isNotBlank( payAgentPlatform.getPayOrderQueryAddr() ) ) {
			payAgentPlatform.setPayOrderQueryAddr( payAgentPlatform.getPayOrderQueryAddr().trim() );
		}
		if ( StringUtils.isNotBlank( payAgentPlatform.getHeaderKey() ) ) {
			payAgentPlatform.setHeaderKey( payAgentPlatform.getHeaderKey().trim() );
		}
		if ( StringUtils.isNotBlank( payAgentPlatform.getSignMd5() ) ) {
			payAgentPlatform.setSignMd5( payAgentPlatform.getSignMd5().trim() );
		}
		if ( StringUtils.isNotBlank( payAgentPlatform.getSignPublicKey() ) ) {
			payAgentPlatform.setSignPublicKey( payAgentPlatform.getSignPublicKey().trim() );
		}
		if ( StringUtils.isNotBlank( payAgentPlatform.getSignPrivateKey() ) ) {
			payAgentPlatform.setSignPrivateKey( payAgentPlatform.getSignPrivateKey().trim() );
		}
		if ( StringUtils.isNotBlank( payAgentPlatform.getPlatWhiteIpList() ) ) {
			payAgentPlatform.setPlatWhiteIpList( payAgentPlatform.getPlatWhiteIpList().
					trim().replaceAll( " ", "" ).replaceAll( "，", "," ) );
		}
		return toAjax( payAgentPlatformService.insertPayAgentPlatform( payAgentPlatform ) );
	}

	/**
	 * 修改代付平台
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentPlatform:edit')" )
	@Log( title = "代付平台", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayAgentPlatform payAgentPlatform ) {
		payAgentPlatform.setMerId( payAgentPlatform.getMerId().trim() );
		payAgentPlatform.setPayOrderAddr( payAgentPlatform.getPayOrderAddr().trim() );
		if ( StringUtils.isNotBlank( payAgentPlatform.getPayOrderQueryAddr() ) ) {
			payAgentPlatform.setPayOrderQueryAddr( payAgentPlatform.getPayOrderQueryAddr().trim() );
		}
		if ( StringUtils.isNotBlank( payAgentPlatform.getHeaderKey() ) ) {
			payAgentPlatform.setHeaderKey( payAgentPlatform.getHeaderKey().trim() );
		}
		if ( StringUtils.isNotBlank( payAgentPlatform.getSignMd5() ) ) {
			payAgentPlatform.setSignMd5( payAgentPlatform.getSignMd5().trim() );
		}
		if ( StringUtils.isNotBlank( payAgentPlatform.getSignPublicKey() ) ) {
			payAgentPlatform.setSignPublicKey( payAgentPlatform.getSignPublicKey().trim() );
		}
		if ( StringUtils.isNotBlank( payAgentPlatform.getSignPrivateKey() ) ) {
			payAgentPlatform.setSignPrivateKey( payAgentPlatform.getSignPrivateKey().trim() );
		}
		if ( StringUtils.isNotBlank( payAgentPlatform.getPlatWhiteIpList() ) ) {
			payAgentPlatform.setPlatWhiteIpList( payAgentPlatform.getPlatWhiteIpList().
					trim().replaceAll( " ", "" ).replaceAll( "，", "," ) );
		}
		return toAjax( payAgentPlatformService.updatePayAgentPlatform( payAgentPlatform ) );
	}

	/**
	 * 删除代付平台
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentPlatform:remove')" )
	@Log( title = "代付平台", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( payAgentPlatformService.deletePayAgentPlatformByIds( ids ) );
	}

	/**
	 * 代付平台状态修改
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentPlatform:edit')" )
	@Log( title = "代付平台状态修改", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeStatus" )
	public AjaxResult changeStatus( @RequestBody PayAgentPlatform payAgentPlatform ) {
		return toAjax( payAgentPlatformService.updatePayAgentPlatform( payAgentPlatform ) );
	}

	/**
	 * 代付下单
	 *
	 * @return
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentPlatform:order')" )
	@Log( title = "代付下单", businessType = BusinessType.ORDER )
	@PostMapping( "/payAgentOrder" )
	public AjaxResult payAgentOrder( @RequestBody ReqPayAgent reqPayAgent ) throws Exception {
		return payAgentService.payAgentOrder( reqPayAgent );
	}

	/**
	 * 批量代付下单
	 *
	 * @return
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentPlatform:order')" )
	@Log( title = "批量代付下单", businessType = BusinessType.ORDER )
	@PostMapping( "/payAgentOrders" )
	public AjaxResult payAgentOrders( @RequestBody ReqPayAgent reqPayAgent ) throws Exception {
		return payAgentService.payAgentOrders( reqPayAgent );
	}
}
