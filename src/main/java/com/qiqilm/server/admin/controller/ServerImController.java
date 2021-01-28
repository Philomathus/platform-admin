package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ServerIm;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IServerImService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * IM即时通讯服务配置Controller
 *
 * @author 77tv
 * @date 2021-01-27
 */
@RestController
@RequestMapping( "/server/im" )
public class ServerImController extends BaseController {
	@Autowired
	private IServerImService serverImService;

	/**
	 * 查询IM即时通讯服务配置列表
	 */
	@PreAuthorize( "@ss.hasPermi('server:im:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( ServerIm serverIm ) {
		startPage();
		List<ServerIm> list = serverImService.selectServerImList( serverIm );
		return getDataTable( list );
	}

	/**
	 * 获取IM即时通讯服务配置详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('server:im:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		return AjaxResult.success( serverImService.selectServerImById( id ) );
	}

	/**
	 * 新增IM即时通讯服务配置
	 */
	@PreAuthorize( "@ss.hasPermi('server:im:add')" )
	@Log( title = "IM即时通讯服务配置", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ServerIm serverIm ) {
		return toAjax( serverImService.insertServerIm( serverIm ) );
	}

	/**
	 * 修改IM即时通讯服务配置
	 */
	@PreAuthorize( "@ss.hasPermi('server:im:edit')" )
	@Log( title = "IM即时通讯服务配置", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ServerIm serverIm ) {
		return toAjax( serverImService.updateServerIm( serverIm ) );
	}

	/**
	 * 删除IM即时通讯服务配置
	 */
	@PreAuthorize( "@ss.hasPermi('server:im:remove')" )
	@Log( title = "IM即时通讯服务配置", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( serverImService.deleteServerImByIds( ids ) );
	}
}
