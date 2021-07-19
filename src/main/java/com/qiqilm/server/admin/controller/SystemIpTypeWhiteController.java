package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.SystemIpTypeWhite;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ISystemIpTypeWhiteService;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * IP黑名单或反作弊禁言Controller
 *
 * @author 77tv
 * @date 2021-07-12
 */
@RestController
@RequestMapping( "/admin/systemIpTypeWhite" )
public class SystemIpTypeWhiteController extends BaseController {
	@Autowired
	private ISystemIpTypeWhiteService systemIpTypeWhiteService;
	@Autowired
	private TokenService tokenService;

	/**
	 * 查询IP黑名单或反作弊禁言列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:systemIpTypeWhite:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(SystemIpTypeWhite systemIpTypeWhite) {
		startPage();
		List<SystemIpTypeWhite> list = systemIpTypeWhiteService.selectSystemIpTypeWhiteList(systemIpTypeWhite);
		return getDataTable( list );
	}
    
	/**
	 * 导出IP黑名单或反作弊禁言列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:systemIpTypeWhite:export')" )
	@Log( title = "IP黑名单或反作弊禁言", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(SystemIpTypeWhite systemIpTypeWhite, HttpServletResponse response) {
		List<SystemIpTypeWhite>      list = systemIpTypeWhiteService.selectSystemIpTypeWhiteList(systemIpTypeWhite);
		ExportExcelUtil.exportExcel( list, "IP黑名单或反作弊禁言", "IP黑名单或反作弊禁言表", SystemIpTypeWhite.class, response );
	}

	/**
	 * 获取IP黑名单或反作弊禁言详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:systemIpTypeWhite:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( systemIpTypeWhiteService.selectSystemIpTypeWhiteById(id) );
	}

	/**
	 * 新增IP黑名单或反作弊禁言
	 */
	@PreAuthorize( "@ss.hasPermi('admin:systemIpTypeWhite:add')" )
	@Log( title = "IP黑名单或反作弊禁言", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody SystemIpTypeWhite systemIpTypeWhite) {
		//判断重复
		if ( systemIpTypeWhiteService.exists( systemIpTypeWhite.getValue() ) > 0 ) {
			return AjaxResult.error( 0, "此参数值已添加，请勿重复添加" );
		}
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    username  = loginUser.getUsername();
		systemIpTypeWhite.setId( UuidUtil.getRandomUuidWithoutSeparator() );
		systemIpTypeWhite.setOpname( username );
		systemIpTypeWhite.setStatus( "1" );
		return toAjax( systemIpTypeWhiteService.insertSystemIpTypeWhite(systemIpTypeWhite) );
	}

	/**
	 * 修改IP黑名单或反作弊禁言
	 */
	@PreAuthorize( "@ss.hasPermi('admin:systemIpTypeWhite:edit')" )
	@Log( title = "IP黑名单或反作弊禁言", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody SystemIpTypeWhite systemIpTypeWhite) {
		return toAjax( systemIpTypeWhiteService.updateSystemIpTypeWhite(systemIpTypeWhite) );
	}

	/**
	 * 删除IP黑名单或反作弊禁言
	 */
	@PreAuthorize( "@ss.hasPermi('admin:systemIpTypeWhite:remove')" )
	@Log( title = "IP黑名单或反作弊禁言", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( systemIpTypeWhiteService.deleteSystemIpTypeWhiteByIds( ids ) );
	}

	/**
	 * 修改黑名单或反作弊禁言状态
	 */
	@PreAuthorize( "@ss.hasPermi('admin:systemIpWhite:edit')" )
	@Log( title = "IP白名单", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeStatus" )
	public AjaxResult changeStatus( @RequestBody SystemIpTypeWhite systemIpTypeWhite ) {
		return toAjax( systemIpTypeWhiteService.updateSystemIpTypeWhite( systemIpTypeWhite ) );
	}
}
