package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.PayPlatformNew;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IPayPlatformNewService;
import com.qiqilm.server.admin.service.IPayService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 支付平台Controller
 *
 * @author 77tv
 * @date 2021-01-27
 */
@RestController
@RequestMapping( "/pay/payPlatformNew" )
public class PayPlatformNewController extends BaseController {
	@Autowired
	private IPayPlatformNewService payPlatformNewService;
	@Autowired
	private IPayService            payService;

	/**
	 * 查询支付平台列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( PayPlatformNew payPlatformNew ) {
		startPage();
		List<PayPlatformNew> list = payPlatformNewService.selectPayPlatformNewList( payPlatformNew );
		return getDataTable( list );
	}

	/**
	 * 导出支付平台列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:export')" )
	@Log( title = "支付平台", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( PayPlatformNew payPlatformNew, HttpServletResponse response) {
		List<PayPlatformNew>      list = payPlatformNewService.selectPayPlatformNewList( payPlatformNew );
		ExportExcelUtil.exportExcel( list, "支付平台", "支付平台表", PayPlatformNew.class, response );
	}

	/**
	 * 获取支付平台详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		PayPlatformNew payPlatformNew = payPlatformNewService.selectPayPlatformNewById( id );
		String a = "**********";
		if(StringUtils.isNotBlank(payPlatformNew.getSignMd5())){
			payPlatformNew.setSignMd5(payPlatformNew.getSignMd5().substring(0,4) + a + payPlatformNew.getSignMd5().substring(14));
		}
		if(StringUtils.isNotBlank(payPlatformNew.getSignPrivateKey())){
			payPlatformNew.setSignPrivateKey(payPlatformNew.getSignPrivateKey().substring(0,4) + a + payPlatformNew.getSignPrivateKey().substring(14));
		}
		if(StringUtils.isNotBlank(payPlatformNew.getSignPublicKey())){
			payPlatformNew.setSignPublicKey(payPlatformNew.getSignPublicKey().substring(0,4) + a + payPlatformNew.getSignPublicKey().substring(14));
		}
		return AjaxResult.success(payPlatformNew);
	}

	/**
	 * 新增支付平台
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:add')" )
	@Log( title = "支付平台", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody PayPlatformNew payPlatformNew ) {
		if(StringUtils.isNotBlank(payPlatformNew.getOrgId())) {
			payPlatformNew.setOrgId(payPlatformNew.getOrgId().trim());
		}
		if(StringUtils.isNotBlank(payPlatformNew.getPlatPayUrl())) {
			payPlatformNew.setPlatPayUrl(payPlatformNew.getPlatPayUrl().trim());
		}
		if(StringUtils.isNotBlank(payPlatformNew.getPlatQueryUrl())) {
			payPlatformNew.setPlatQueryUrl(payPlatformNew.getPlatQueryUrl().trim());
		}
		if(StringUtils.isNotBlank(payPlatformNew.getSignMd5())) {
			payPlatformNew.setSignMd5(payPlatformNew.getSignMd5().trim());
		}
		if(StringUtils.isNotBlank(payPlatformNew.getSignPrivateKey())) {
			payPlatformNew.setSignPrivateKey(payPlatformNew.getSignPrivateKey().trim());
		}
		if(StringUtils.isNotBlank(payPlatformNew.getSignPublicKey())) {
		    payPlatformNew.setSignPublicKey(payPlatformNew.getSignPublicKey().trim());
     	}
		if(StringUtils.isNotBlank(payPlatformNew.getPlatWhiteIpList())) {
			payPlatformNew.setPlatWhiteIpList(payPlatformNew.getPlatWhiteIpList().
					trim().replaceAll(" ", "").replaceAll("，", ","));
		}
		return toAjax( payPlatformNewService.insertPayPlatformNew( payPlatformNew ) );
	}

	/**
	 * 修改支付平台
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:edit')" )
	@Log( title = "支付平台", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayPlatformNew payPlatformNew ) {
		if(StringUtils.isNotBlank(payPlatformNew.getOrgId())) {
			payPlatformNew.setOrgId(payPlatformNew.getOrgId().trim());
		}
		if(StringUtils.isNotBlank(payPlatformNew.getPlatPayUrl())) {
			payPlatformNew.setPlatPayUrl(payPlatformNew.getPlatPayUrl().trim());
		}
		if(StringUtils.isNotBlank(payPlatformNew.getPlatQueryUrl())) {
			payPlatformNew.setPlatQueryUrl(payPlatformNew.getPlatQueryUrl().trim());
		}
		String a = "**********";
		if(StringUtils.isNotBlank(payPlatformNew.getSignMd5()) && !payPlatformNew.getSignMd5().contains(a)) {
			payPlatformNew.setSignMd5(payPlatformNew.getSignMd5().trim());
		}
		if(StringUtils.isNotBlank(payPlatformNew.getSignPrivateKey()) && !payPlatformNew.getSignPrivateKey().contains(a)) {
			payPlatformNew.setSignPrivateKey(payPlatformNew.getSignPrivateKey().trim());
		}
		if(StringUtils.isNotBlank(payPlatformNew.getSignPublicKey()) && !payPlatformNew.getSignPublicKey().contains(a)) {
			payPlatformNew.setSignPublicKey(payPlatformNew.getSignPublicKey().trim());
		}
		if(StringUtils.isNotBlank(payPlatformNew.getPlatWhiteIpList())) {
			payPlatformNew.setPlatWhiteIpList(payPlatformNew.getPlatWhiteIpList().
					trim().replaceAll(" ", "").replaceAll("，", ","));
		}
		return toAjax( payPlatformNewService.updatePayPlatformNew( payPlatformNew ) );
	}

	/**
	 * 删除支付平台
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:remove')" )
	@Log( title = "支付平台", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( payPlatformNewService.deletePayPlatformNewByIds( ids ) );
	}

	@PreAuthorize( "@ss.hasPermi('pay:payPlatformNew:patchOrder')" )
	@Log( title = "线上支付人工补单", businessType = BusinessType.AUDIT )
	@PutMapping( value = "/payPatchOrder" )
	public AjaxResult payPatchOrder( @RequestBody Map<String, Object> requestMap ) throws Exception {
		return payService.payPatchOrder( requestMap );
	}
}
