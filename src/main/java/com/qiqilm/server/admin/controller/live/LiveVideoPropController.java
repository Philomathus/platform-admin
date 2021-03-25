package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveProplog;
import com.qiqilm.server.admin.domain.LiveVideoProp;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveVideoPropService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    	public TableDataInfo list(LiveVideoProp liveVideoProp) {
		startPage();
		List<LiveVideoProp> list = liveVideoPropService.selectLiveVideoPropList(liveVideoProp);
		return getDataTable( list );
	}

	/**
	 * 导出送礼物列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:export')" )
	@Log( title = "送礼物", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(LiveVideoProp liveVideoProp) {
		List<LiveVideoProp>      list = liveVideoPropService.selectLiveVideoPropList(liveVideoProp);
		ExcelUtil<LiveVideoProp> util = new ExcelUtil<LiveVideoProp>(LiveVideoProp. class);
		return util.exportExcel( list, "liveVideoProp" );
	}

	/**
	 * 统计礼物金额
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoProp:list')" )
	@GetMapping( "/getCount" )
	public AjaxResult getCount( LiveVideoProp liveVideoProp ) {
		LiveVideoProp liveVideoProp1;
		liveVideoProp1=liveVideoPropService.getCount( liveVideoProp );
		return AjaxResult.success(liveVideoProp1);
	}
}
