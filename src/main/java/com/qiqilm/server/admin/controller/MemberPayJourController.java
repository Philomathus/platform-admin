package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.req.ReqPayJour;
import com.qiqilm.server.admin.domain.rsp.RspPayJour;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IMemberPayJourService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 线上充值信息Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/pay/memberPayJour" )
public class MemberPayJourController extends BaseController {
	@Autowired
	private IMemberPayJourService memberPayJourService;

	/**
	 * 查询线上充值信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberPayJour:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( ReqPayJour memberPayJour ) {
		startPage();
		List<RspPayJour> list = memberPayJourService.findList( memberPayJour );
		return getDataTable( list );
	}

	/**
	 * 查询线上充值信息统计信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberPayJour:list')" )
	@GetMapping( "/listCount" )
	public Map listCount( ReqPayJour memberPayJour ) {
		return memberPayJourService.listCount( memberPayJour );
	}

	/**
	 * 导出线上充值信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberPayJour:export')" )
	@Log( title = "线上充值信息", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export( ReqPayJour memberPayJour ) {
		List<RspPayJour>      list = memberPayJourService.findList( memberPayJour );
		ExcelUtil<RspPayJour> util = new ExcelUtil<>( RspPayJour.class );
		return util.exportExcel( list, "memberPayJour" );
	}

	/**
	 * 获取线上充值信息详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:memberPayJour:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id ) {
		return AjaxResult.success( memberPayJourService.selectById( id ) );
	}


}
