package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.SystemIpWhite;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ISystemIpWhiteService;
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
 * IP白名单Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/admin/systemIpWhite" )
public class SystemIpWhiteController extends BaseController {
	@Autowired
	private ISystemIpWhiteService systemIpWhiteService;

	@Autowired
	private TokenService tokenService;

	/**
	 * 查询IP白名单列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:systemIpWhite:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( SystemIpWhite systemIpWhite ) {
		startPage();
		List<SystemIpWhite> list = systemIpWhiteService.selectSystemIpWhiteList( systemIpWhite );
		return getDataTable( list );
	}

	/**
	 * 导出IP白名单列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:systemIpWhite:export')" )
	@Log( title = "IP白名单", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( SystemIpWhite systemIpWhite, HttpServletResponse response ) {
		List<SystemIpWhite> list = systemIpWhiteService.selectSystemIpWhiteList( systemIpWhite );
		ExportExcelUtil.exportExcel( list, "IP白名单", "IP白名单表", SystemIpWhite.class, response );
	}

	/**
	 * 获取IP白名单详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:systemIpWhite:query')" )
	@GetMapping( value = "/{ipId}" )
	public AjaxResult getInfo( @PathVariable( "ipId" ) String ipId ) {
		return AjaxResult.success( systemIpWhiteService.selectSystemIpWhiteById( ipId ) );
	}

	/**
	 * 新增IP白名单
	 */
	@PreAuthorize( "@ss.hasPermi('admin:systemIpWhite:add')" )
	@Log( title = "IP白名单", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody SystemIpWhite systemIpWhite ) {
		//判断重复
		if ( systemIpWhiteService.exists( systemIpWhite.getIpAddress() ) > 0 ) {
			return AjaxResult.error( 0, "此IP已添加，请勿重复添加" );
		}
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    username  = loginUser.getUsername();
		systemIpWhite.setIpId( UuidUtil.getRandomUuidWithoutSeparator() );
		systemIpWhite.setIpAdmin( username );
		systemIpWhite.setIpStatus( "1" );
		return toAjax( systemIpWhiteService.insertSystemIpWhite( systemIpWhite ) );
	}

	/**
	 * 修改IP白名单
	 */
	@PreAuthorize( "@ss.hasPermi('admin:systemIpWhite:edit')" )
	@Log( title = "IP白名单", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody SystemIpWhite systemIpWhite ) {
		return toAjax( systemIpWhiteService.updateSystemIpWhite( systemIpWhite ) );
	}

	/**
	 * 删除IP白名单
	 */
	@PreAuthorize( "@ss.hasPermi('admin:systemIpWhite:remove')" )
	@Log( title = "IP白名单", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ipIds}" )
	public AjaxResult remove( @PathVariable String[] ipIds ) {
		return toAjax( systemIpWhiteService.deleteSystemIpWhiteByIds( ipIds ) );
	}

	/**
	 * 修改IP白名单启用状态
	 */
	@PreAuthorize( "@ss.hasPermi('admin:systemIpWhite:edit')" )
	@Log( title = "IP白名单", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeStatus" )
	public AjaxResult changeStatus( @RequestBody SystemIpWhite systemIpWhite ) {
		return toAjax( systemIpWhiteService.updateSystemIpWhite( systemIpWhite ) );
	}
}
