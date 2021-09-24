package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveVideoProp;
import com.qiqilm.server.admin.domain.rsp.RspTestAccountProp;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveVideoPropService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
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
@RestController
@RequestMapping( "/admin/liveVideoProp" )
public class LiveVideoPropController extends BaseController {
	@Autowired
	private ILiveVideoPropService liveVideoPropService;

	/**
	 * 查询送礼物列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( LiveVideoProp liveVideoProp ) {
		startPage();
		List<LiveVideoProp> list = liveVideoPropService.selectLiveVideoPropList( liveVideoProp );
		return getDataTable( list );
	}

	/**
	 * 导出送礼物列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:export')" )
	@Log( title = "送礼物", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( LiveVideoProp liveVideoProp, HttpServletResponse response ) {
		List<LiveVideoProp> list = liveVideoPropService.selectLiveVideoPropList( liveVideoProp );
		ExportExcelUtil.exportExcel( list, "送礼物", "送礼物表", LiveVideoProp.class, response );
	}

	/**
	 * 统计礼物金额
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:list')" )
	@GetMapping( "/getCount" )
	public AjaxResult getCount( LiveVideoProp liveVideoProp ) {
		LiveVideoProp liveVideoProp1 = liveVideoPropService.getCount( liveVideoProp );
		return AjaxResult.success( liveVideoProp1 );
	}

	/**
	 * 查询送礼物列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:list')" )
	@GetMapping( "/testAccountPorpList" )
	public TableDataInfo testAccountPorpList( LiveVideoProp liveVideoProp ) {
		startPage();
		List<RspTestAccountProp> list = liveVideoPropService.testAccountPorpList( liveVideoProp );
		return getDataTable( list );
	}

	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:export')" )
	@Log( title = "送礼物", businessType = BusinessType.EXPORT )
	@GetMapping( "/exportTestAccountProplog" )
	public void exportTestAccountProplog( LiveVideoProp liveVideoProp, HttpServletResponse response ) {
		List<RspTestAccountProp> list = liveVideoPropService.testAccountPorpList( liveVideoProp );
		ExportExcelUtil.exportExcel( list, "送礼物", "送礼物表", RspTestAccountProp.class, response );
	}

	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:list')" )
	@GetMapping( "/testAccountCount" )
	public AjaxResult testAccountCount( LiveVideoProp liveVideoProp ) {
		RspTestAccountProp liveVideoProp1 = liveVideoPropService.testAccountCount( liveVideoProp );
		return AjaxResult.success( liveVideoProp1 );
	}
}
