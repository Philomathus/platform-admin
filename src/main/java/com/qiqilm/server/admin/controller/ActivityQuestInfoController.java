package com.qiqilm.server.admin.controller;

import java.util.Date;
import java.util.List;

import com.qiqilm.server.admin.utils.UuidUtil;
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
import com.qiqilm.server.admin.domain.ActivityQuestInfo;
import com.qiqilm.server.admin.service.IActivityQuestInfoService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 任务信息列表Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping( "/admin/activityQuestInfo" )
public class ActivityQuestInfoController extends BaseController {
	@Autowired
	private IActivityQuestInfoService activityQuestInfoService;

	/**
	 * 查询任务信息列表列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityQuestInfo:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(ActivityQuestInfo activityQuestInfo) {
		startPage();
		List<ActivityQuestInfo> list = activityQuestInfoService.selectActivityQuestInfoList(activityQuestInfo);
		return getDataTable( list );
	}
    
	/**
	 * 导出任务信息列表列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityQuestInfo:export')" )
	@Log( title = "任务信息列表", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(ActivityQuestInfo activityQuestInfo) {
		List<ActivityQuestInfo>      list = activityQuestInfoService.selectActivityQuestInfoList(activityQuestInfo);
		ExcelUtil<ActivityQuestInfo> util = new ExcelUtil<ActivityQuestInfo>(ActivityQuestInfo. class);
		return util.exportExcel( list, "activityQuestInfo" );
	}

	/**
	 * 获取任务信息列表详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityQuestInfo:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( activityQuestInfoService.selectActivityQuestInfoById(id) );
	}

	/**
	 * 新增任务信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityQuestInfo:add')" )
	@Log( title = "任务信息列表", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ActivityQuestInfo activityQuestInfo) {
		activityQuestInfo.setId(UuidUtil.getRandomUuidWithoutSeparator());
		activityQuestInfo.setCtime(new Date());
		return toAjax( activityQuestInfoService.insertActivityQuestInfo(activityQuestInfo) );
	}

	/**
	 * 修改任务信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityQuestInfo:edit')" )
	@Log( title = "任务信息列表", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ActivityQuestInfo activityQuestInfo) {
		return toAjax( activityQuestInfoService.updateActivityQuestInfo(activityQuestInfo) );
	}

	/**
	 * 删除任务信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityQuestInfo:remove')" )
	@Log( title = "任务信息列表", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( activityQuestInfoService.deleteActivityQuestInfoByIds( ids ) );
	}
}
