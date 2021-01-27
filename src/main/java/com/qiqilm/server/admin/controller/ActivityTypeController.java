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
import com.qiqilm.server.admin.domain.ActivityType;
import com.qiqilm.server.admin.service.IActivityTypeService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping( "/admin/activityType" )
public class ActivityTypeController extends BaseController {
	@Autowired
	private IActivityTypeService activityTypeService;

	@Autowired
	private TokenService tokenService;

	/**
	 * ��ѯ【请填写功能名称】�б�
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityType:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(ActivityType activityType) {
		startPage();
		List<ActivityType> list = activityTypeService.selectActivityTypeList(activityType);
		return getDataTable( list );
	}
    
	/**
	 * ����【请填写功能名称】�б�
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityType:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(ActivityType activityType) {
		List<ActivityType>      list = activityTypeService.selectActivityTypeList(activityType);
		ExcelUtil<ActivityType> util = new ExcelUtil<ActivityType>(ActivityType. class);
		return util.exportExcel( list, "activityType" );
	}

	/**
	 * ��ȡ【请填写功能名称】��ϸ��Ϣ
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityType:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( activityTypeService.selectActivityTypeById(id) );
	}

	/**
	 * ����【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityType:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ActivityType activityType) {
		activityType.setId(UuidUtil.getRandomUuidWithoutSeparator());
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String username = loginUser.getUser().getUserName();
		activityType.setName(username);
		return toAjax( activityTypeService.insertActivityType(activityType) );
	}

	/**
	 * �޸�【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityType:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ActivityType activityType) {
		return toAjax( activityTypeService.updateActivityType(activityType) );
	}

	/**
	 * ɾ��【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityType:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( activityTypeService.deleteActivityTypeByIds( ids ) );
	}
}
