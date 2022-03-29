package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveVideoProp;
import com.qiqilm.server.admin.domain.rsp.RspTestAccountProp;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveVideoPropService;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.utils.ServletUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 送礼物Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Log4j2
@RestController
@RequestMapping( "/admin/liveVideoProp" )
public class LiveVideoPropController extends BaseController {
	@Autowired
	private ILiveVideoPropService liveVideoPropService;
	@Autowired
	private TokenService tokenService;

	/**
	 * 查询送礼物列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( LiveVideoProp liveVideoProp ) {
		//startPage();
		//List<LiveVideoProp> list = liveVideoPropService.selectLiveVideoPropList( liveVideoProp );
		//return getDataTable( list );
		log.error(tokenService.getLoginUser(ServletUtil.getHttpServletRequest()).getUsername());
		return null;
	}

	/**
	 * 导出送礼物列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:export')" )
	@Log( title = "送礼物", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( LiveVideoProp liveVideoProp, HttpServletResponse response ) {
		//List<LiveVideoProp> list = liveVideoPropService.selectLiveVideoPropList( liveVideoProp );
		//ExportExcelUtil.exportExcel( list, "送礼物", "送礼物表", LiveVideoProp.class, response );
		log.error(tokenService.getLoginUser(ServletUtil.getHttpServletRequest()).getUsername());
	}

	/**
	 * 统计礼物金额
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:list')" )
	@GetMapping( "/getCount" )
	public AjaxResult getCount( LiveVideoProp liveVideoProp ) {
		//LiveVideoProp liveVideoProp1 = liveVideoPropService.getCount( liveVideoProp );
		//return AjaxResult.success( liveVideoProp1 );
		return null;
	}

	/**
	 * 查询送礼物列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:list')" )
	@GetMapping( "/testAccountPorpList" )
	public TableDataInfo testAccountPorpList( LiveVideoProp liveVideoProp ) {
		//startPage();
		//List<RspTestAccountProp> list = liveVideoPropService.testAccountPorpList( liveVideoProp );
		//return getDataTable( list );
		return null;
	}

	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:export')" )
	@Log( title = "送礼物", businessType = BusinessType.EXPORT )
	@GetMapping( "/exportTestAccountProplog" )
	public void exportTestAccountProplog( LiveVideoProp liveVideoProp, HttpServletResponse response ) {
		//List<RspTestAccountProp> list = liveVideoPropService.testAccountPorpList( liveVideoProp );
		//ExportExcelUtil.exportExcel( list, "送礼物", "送礼物表", RspTestAccountProp.class, response );
	}

	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:list')" )
	@GetMapping( "/testAccountCount" )
	public AjaxResult testAccountCount( LiveVideoProp liveVideoProp ) {
		//RspTestAccountProp liveVideoProp1 = liveVideoPropService.testAccountCount( liveVideoProp );
		//return AjaxResult.success( liveVideoProp1 );
		log.error(tokenService.getLoginUser(ServletUtil.getHttpServletRequest()).getUsername());
		return null;
	}
}
