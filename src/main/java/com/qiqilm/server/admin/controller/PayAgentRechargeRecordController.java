package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.PayAgentRechargeRecord;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.IPayAgentRechargeRecordService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 【代充存提】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/pay/payAgentRechargeRecord" )
public class PayAgentRechargeRecordController extends BaseController {
	@Autowired
	private IPayAgentRechargeRecordService payAgentRechargeRecordService;
	@Autowired
	private RedisUtil redisUtil;

	/**
	 * 查询【代充存提】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeRecord:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(PayAgentRechargeRecord payAgentRechargeRecord) {
		startPage();
		List<PayAgentRechargeRecord> list = payAgentRechargeRecordService.selectPayAgentRechargeRecordList(payAgentRechargeRecord);
		return getDataTable( list );
	}

	/**
	 * 统计【代充存提】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeRecord:list')" )
	@GetMapping( "/getCount" )
	public AjaxResult getCount( PayAgentRechargeRecord payAgentRechargeRecord ) {
		PayAgentRechargeRecord payAgentRechargeRecord1;
		payAgentRechargeRecord1=payAgentRechargeRecordService.getCount( payAgentRechargeRecord );
		return AjaxResult.success(payAgentRechargeRecord1);
	}
    
	/**
	 * 导出【代充存提】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeRecord:export')" )
	@Log( title = "【代充存提】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(PayAgentRechargeRecord payAgentRechargeRecord, HttpServletResponse response) {
		List<PayAgentRechargeRecord>      list = payAgentRechargeRecordService.selectPayAgentRechargeRecordList(payAgentRechargeRecord);
		ExportExcelUtil.exportExcel( list, "代充存提", "代充存提表", PayAgentRechargeRecord.class, response );
	}

	/**
	 * 获取【代充存提】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeRecord:query')" )
	@GetMapping( value = "/{orderNo}" )
	public AjaxResult getInfo( @PathVariable( "orderNo" ) String orderNo) {
		return AjaxResult.success( payAgentRechargeRecordService.selectPayAgentRechargeRecordById(orderNo) );
	}

	/**
	 * 新增【代充存提】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeRecord:add')" )
	@Log( title = "【代充存提】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody PayAgentRechargeRecord payAgentRechargeRecord) {
		return toAjax( payAgentRechargeRecordService.insertPayAgentRechargeRecord(payAgentRechargeRecord) );
	}

	/**
	 * 修改【代充存提】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeRecord:edit')" )
	@Log( title = "【代充存提】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody PayAgentRechargeRecord payAgentRechargeRecord) {
		return toAjax( payAgentRechargeRecordService.updatePayAgentRechargeRecord(payAgentRechargeRecord) );
	}

	/**
	 * 删除【代充存提】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeRecord:remove')" )
	@Log( title = "【代充存提】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{orderNos}" )
	public AjaxResult remove( @PathVariable String[] orderNos ) {
		return toAjax( payAgentRechargeRecordService.deletePayAgentRechargeRecordByIds( orderNos ) );
	}

	/**
	 * 人工存入
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeRecord:deposit')" )
	@Log( title = "【人工存入】", businessType = BusinessType.OTHER )
	@PutMapping( "/deposit" )
	public AjaxResult deposit(PayAgentRechargeRecord payAgentRechargeRecord) throws Exception {
		if (!redisUtil.lock(EnumLock.member, payAgentRechargeRecord.getRechargeAcount(), "1", 10)) {
			return AjaxResult.error("请勿重复提交");
		}
		return payAgentRechargeRecordService.deposit(payAgentRechargeRecord) ;
	}

	/**
	 * 人工提出
	 */
	@PreAuthorize( "@ss.hasPermi('pay:payAgentRechargeRecord:proposed')" )
	@Log( title = "【人工提出】", businessType = BusinessType.OTHER )
	@PutMapping( "/proposed" )
	public AjaxResult proposed( PayAgentRechargeRecord payAgentRechargeRecord) throws Exception {
		if (!redisUtil.lock(EnumLock.member, payAgentRechargeRecord.getOrderNo(), "1", 10)) {
			return AjaxResult.error("请勿重复提交");
		}
		return  payAgentRechargeRecordService.proposed(payAgentRechargeRecord) ;
	}
}
