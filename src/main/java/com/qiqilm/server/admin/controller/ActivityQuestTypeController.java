package com.qiqilm.server.admin.controller;

import java.util.List;

import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.ServletUtil;
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
import com.qiqilm.server.admin.domain.ActivityQuestType;
import com.qiqilm.server.admin.service.IActivityQuestTypeService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 任务类型Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping( "/admin/activityQuestType" )
public class ActivityQuestTypeController extends BaseController {
	@Autowired
	private IActivityQuestTypeService activityQuestTypeService;

	@Autowired
    private TokenService tokenService;

	/**
	 * 查询任务类型列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityQuestType:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(ActivityQuestType activityQuestType) {
		startPage();
		List<ActivityQuestType> list = activityQuestTypeService.selectActivityQuestTypeList(activityQuestType);
		return getDataTable( list );
	}
    
	/**
	 * 导出任务类型列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityQuestType:export')" )
	@Log( title = "任务类型", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(ActivityQuestType activityQuestType) {
		List<ActivityQuestType>      list = activityQuestTypeService.selectActivityQuestTypeList(activityQuestType);
		ExcelUtil<ActivityQuestType> util = new ExcelUtil<ActivityQuestType>(ActivityQuestType. class);
		return util.exportExcel( list, "activityQuestType" );
	}

	/**
	 * 获取任务类型详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityQuestType:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( activityQuestTypeService.selectActivityQuestTypeById(id) );
	}

	/**
	 * 新增任务类型
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityQuestType:add')" )
	@Log( title = "任务类型", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ActivityQuestType activityQuestType) {
		activityQuestType.setId(UuidUtil.getRandomUuidWithoutSeparator());
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String username = loginUser.getUser().getUserName();
		activityQuestType.setCreateBy(username);
		return toAjax( activityQuestTypeService.insertActivityQuestType(activityQuestType) );
	}

	/**
	 * 修改任务类型
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityQuestType:edit')" )
	@Log( title = "任务类型", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ActivityQuestType activityQuestType) {
		return toAjax( activityQuestTypeService.updateActivityQuestType(activityQuestType) );
	}

	/**
	 * 删除任务类型
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityQuestType:remove')" )
	@Log( title = "任务类型", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( activityQuestTypeService.deleteActivityQuestTypeByIds( ids ) );
	}
}
