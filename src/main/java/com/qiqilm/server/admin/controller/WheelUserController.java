package com.qiqilm.server.admin.controller;

import java.util.List;

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
import com.qiqilm.server.admin.domain.WheelUser;
import com.qiqilm.server.admin.service.IWheelUserService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 转盘用户Controller
 *
 * @author 77tv
 * @date 2021-03-08
 */
@RestController
@RequestMapping( "/wheel/wheelUser" )
public class WheelUserController extends BaseController {
	@Autowired
	private IWheelUserService wheelUserService;

	/**
	 * 查询转盘用户列表
	 */
	@PreAuthorize( "@ss.hasPermi('wheel:wheelUser:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(WheelUser wheelUser) {
		startPage();
		List<WheelUser> list = wheelUserService.selectWheelUserList(wheelUser);
		return getDataTable( list );
	}
    
	/**
	 * 导出转盘用户列表
	 */
	@PreAuthorize( "@ss.hasPermi('wheel:wheelUser:export')" )
	@Log( title = "转盘用户", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(WheelUser wheelUser) {
		List<WheelUser>      list = wheelUserService.selectWheelUserList(wheelUser);
		ExcelUtil<WheelUser> util = new ExcelUtil<>(WheelUser.class);
		return util.exportExcel( list, "wheelUser" );
	}

	/**
	 * 获取转盘用户详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('wheel:wheelUser:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( wheelUserService.selectWheelUserById(id) );
	}

	/**
	 * 新增转盘用户
	 */
	@PreAuthorize( "@ss.hasPermi('wheel:wheelUser:add')" )
	@Log( title = "转盘用户", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody WheelUser wheelUser) {
		return toAjax( wheelUserService.insertWheelUser(wheelUser) );
	}

	/**
	 * 修改转盘用户
	 */
	@PreAuthorize( "@ss.hasPermi('wheel:wheelUser:edit')" )
	@Log( title = "转盘用户", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody WheelUser wheelUser) {
		return toAjax( wheelUserService.updateWheelUser(wheelUser) );
	}

	/**
	 * 删除转盘用户
	 */
	@PreAuthorize( "@ss.hasPermi('wheel:wheelUser:remove')" )
	@Log( title = "转盘用户", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( wheelUserService.deleteWheelUserByIds( ids ) );
	}
}
