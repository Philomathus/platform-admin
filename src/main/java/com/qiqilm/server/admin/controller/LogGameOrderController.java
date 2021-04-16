package com.qiqilm.server.admin.controller;

import java.util.List;

import com.qiqilm.server.admin.utils.ExportExcelUtil;
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
import com.qiqilm.server.admin.domain.LogGameOrder;
import com.qiqilm.server.admin.service.ILogGameOrderService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 会员上下分Controller
 *
 * @author 77tv
 * @date 2021-01-29
 */
@RestController
@RequestMapping( "/member/logGameOrder" )
public class LogGameOrderController extends BaseController {
	@Autowired
	private ILogGameOrderService logGameOrderService;

	/**
	 * 查询会员上下分列表
	 */
	@PreAuthorize( "@ss.hasPermi('member:logGameOrder:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LogGameOrder logGameOrder) {
		startPage();
		List<LogGameOrder> list = logGameOrderService.selectLogGameOrderList(logGameOrder);
		return getDataTable( list );
	}
    
	/**
	 * 导出会员上下分列表
	 */
	@PreAuthorize( "@ss.hasPermi('member:logGameOrder:export')" )
	@Log( title = "会员上下分", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(LogGameOrder logGameOrder, HttpServletResponse response) {
		List<LogGameOrder>      list = logGameOrderService.selectLogGameOrderList(logGameOrder);
		ExportExcelUtil.exportExcel( list, "会员上下分", "会员上下分表", LogGameOrder.class, response );
	}

	/**
	 * 获取会员上下分详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('member:logGameOrder:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( logGameOrderService.selectLogGameOrderById(id) );
	}

	/**
	 * 新增会员上下分
	 */
	@PreAuthorize( "@ss.hasPermi('member:logGameOrder:add')" )
	@Log( title = "会员上下分", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LogGameOrder logGameOrder) {
		return toAjax( logGameOrderService.insertLogGameOrder(logGameOrder) );
	}

	/**
	 * 修改会员上下分
	 */
	@PreAuthorize( "@ss.hasPermi('member:logGameOrder:edit')" )
	@Log( title = "会员上下分", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LogGameOrder logGameOrder) {
		return toAjax( logGameOrderService.updateLogGameOrder(logGameOrder) );
	}

	/**
	 * 删除会员上下分
	 */
	@PreAuthorize( "@ss.hasPermi('member:logGameOrder:remove')" )
	@Log( title = "会员上下分", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( logGameOrderService.deleteLogGameOrderByIds( ids ) );
	}
}
