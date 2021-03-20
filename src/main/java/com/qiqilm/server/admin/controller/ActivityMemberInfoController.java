package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ActivityMemberInfo;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IActivityMemberInfoService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 会员推广管理Controller
 *
 * @author 77tv
 * @date 2021-03-19
 */
@RestController
@RequestMapping( "/activity/activityMemberInfo" )
public class ActivityMemberInfoController extends BaseController {
	@Autowired
	private IActivityMemberInfoService activityMemberInfoService;

	/**
	 * 查询会员推广管理列表
	 */
	@PreAuthorize( "@ss.hasPermi('activity:activityMemberInfo:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(ActivityMemberInfo activityMemberInfo) {
		startPage();
		List<ActivityMemberInfo> list = activityMemberInfoService.selectActivityMemberInfoList(activityMemberInfo);
		return getDataTable( list );
	}
	@PreAuthorize( "@ss.hasPermi('activity:activityMemberInfo:list')" )
	@GetMapping( "/ipList" )
    	public List<Map> ipList(ActivityMemberInfo activityMemberInfo) {
        return activityMemberInfoService.selectIpList(activityMemberInfo);
	}

	/**
	 * 导出会员推广管理列表
	 */
	@PreAuthorize( "@ss.hasPermi('activity:activityMemberInfo:export')" )
	@Log( title = "会员推广管理", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(ActivityMemberInfo activityMemberInfo, HttpServletResponse response) {
		List<ActivityMemberInfo>      list = activityMemberInfoService.selectActivityMemberInfoList(activityMemberInfo);
		ExportExcelUtil.exportExcel( list, "会员推广管理", "会员推广管理表", ActivityMemberInfo.class, response );
	}

	/**
	 * 获取会员推广管理详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('activity:activityMemberInfo:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( activityMemberInfoService.selectActivityMemberInfoById(id) );
	}

	/**
	 * 新增会员推广管理
	 */
	@PreAuthorize( "@ss.hasPermi('activity:activityMemberInfo:add')" )
	@Log( title = "会员推广管理", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ActivityMemberInfo activityMemberInfo) {
		return toAjax( activityMemberInfoService.insertActivityMemberInfo(activityMemberInfo) );
	}

	/**
	 * 修改会员推广管理
	 */
	@PreAuthorize( "@ss.hasPermi('activity:activityMemberInfo:edit')" )
	@Log( title = "会员推广管理", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ActivityMemberInfo activityMemberInfo) {
		return toAjax( activityMemberInfoService.updateActivityMemberInfo(activityMemberInfo) );
	}

	/**
	 * 删除会员推广管理
	 */
	@PreAuthorize( "@ss.hasPermi('activity:activityMemberInfo:remove')" )
	@Log( title = "会员推广管理", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( activityMemberInfoService.deleteActivityMemberInfoByIds( ids ) );
	}
}
