package com.qiqilm.server.admin.controller.member;

import java.util.List;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.domain.MemberOnline;
import com.qiqilm.server.admin.service.IMemberOnlineService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 在线会员列表Controller
 *
 * @author 77tv
 * @date 2021-03-22
 */
@RestController
@RequestMapping( "/admin/memberOnline" )
public class MemberOnlineController extends BaseController {
	@Autowired
	private IMemberOnlineService memberOnlineService;

	/**
	 * 查询在线会员列表列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:memberOnline:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(MemberOnline memberOnline) {
		startPage();
		List<MemberOnline> list = memberOnlineService.selectMemberOnlineList(memberOnline);
		return getDataTable( list );
	}

	/**
	 * 统计在线会员
	 */
	@PreAuthorize( "@ss.hasPermi('admin:memberOnline:list')" )
	@GetMapping( "/countTotal" )
	public AjaxResult count() {
		MemberOnline memberOnline = memberOnlineService.selectMemberOnlineListCountTotal();
		return AjaxResult.success(memberOnline);
	}

	/**
	 * 导出在线会员列表列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:memberOnline:export')" )
	@Log( title = "在线会员列表", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(MemberOnline memberOnline, HttpServletResponse response) {
		List<MemberOnline>      list = memberOnlineService.selectMemberOnlineList(memberOnline);
		ExportExcelUtil.exportExcel( list, "在线会员列表", "在线会员列表表", MemberOnline.class, response );
	}
}
