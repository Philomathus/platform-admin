package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveLimitMsg;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveLimitMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * //昵称限制Controller
 *
 * @author 77tv
 * @date 2021-01-27
 */
@RestController
@RequestMapping( "/admin/liveLimitMsg" )
public class LiveLimitMsgController extends BaseController {
	@Autowired
	private ILiveLimitMsgService liveLimitMsgService;

	/**
	 * 获取//昵称限制详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveLimitMsg:query')" )
	@GetMapping( value = "/list" )
	public AjaxResult getInfo( ) {
		return AjaxResult.success( liveLimitMsgService.selectLiveLimitMsgById() );
	}


	/**
	 * 修改//昵称限制
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveLimitMsg:edit')" )
	@Log( title = "//昵称限制", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveLimitMsg liveLimitMsg) {
		return toAjax( liveLimitMsgService.updateLiveLimitMsg(liveLimitMsg) );
	}


}