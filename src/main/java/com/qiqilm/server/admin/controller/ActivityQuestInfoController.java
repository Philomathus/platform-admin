package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ActivityQuestInfo;
import com.qiqilm.server.admin.domain.ActivityQuestType;
import com.qiqilm.server.admin.domain.GameInfo;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IActivityQuestInfoService;
import com.qiqilm.server.admin.service.IActivityQuestTypeService;
import com.qiqilm.server.admin.service.IGameInfoService;
import com.qiqilm.server.admin.service.IGamePlatformService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

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

	@Autowired
	private IActivityQuestTypeService activityQuestTypeService;

	@Autowired
	private IGameInfoService gameInfoService;

	@Autowired
	private IGamePlatformService gamePlatformService;

	/**
	 * 查询任务信息列表列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityQuestInfo:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( ActivityQuestInfo activityQuestInfo ) {
		startPage();
		List<ActivityQuestInfo> list = activityQuestInfoService.selectActivityQuestInfoList( activityQuestInfo );
		return getDataTable( list );
	}

	/**
	 * 导出任务信息列表列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityQuestInfo:export')" )
	@Log( title = "任务信息列表", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( ActivityQuestInfo activityQuestInfo, HttpServletResponse response ) {
		List<ActivityQuestInfo> list = activityQuestInfoService.selectActivityQuestInfoList( activityQuestInfo );
		ExportExcelUtil.exportExcel( list, "任务信息", "任务信息表", ActivityQuestInfo.class, response );
	}

	/**
	 * 获取任务信息列表详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityQuestInfo:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id ) {
		return AjaxResult.success( activityQuestInfoService.selectActivityQuestInfoById( id ) );
	}

	/**
	 * 新增任务信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityQuestInfo:add')" )
	@Log( title = "任务信息列表", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ActivityQuestInfo activityQuestInfo ) {
		activityQuestInfo.setId( UuidUtil.getRandomUuidWithoutSeparator() );
		activityQuestInfo.setCtime( new Date() );
		return toAjax( activityQuestInfoService.insertActivityQuestInfo( activityQuestInfo ) );
	}

	/**
	 * 修改任务信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:activityQuestInfo:edit')" )
	@Log( title = "任务信息列表", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ActivityQuestInfo activityQuestInfo ) {
		return toAjax( activityQuestInfoService.updateActivityQuestInfo( activityQuestInfo ) );
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

	/**
	 * 任务类型下拉框
	 *
	 * @return
	 */
	@GetMapping( "/activityQuestType" )
	public AjaxResult findActivityQuestType() {
		List<ActivityQuestType> activityQuestType = activityQuestTypeService.selectActivityQuestType();
		return AjaxResult.success( activityQuestType );
	}

	/**
	 * 所属游戏下拉框
	 *
	 * @return
	 */
	@GetMapping( "/gameInfo" )
	public AjaxResult findgameInfo() {
		List<GameInfo> gameInfo = gameInfoService.selectGameInfo();
		return AjaxResult.success( gameInfo );
	}

	/**
	 * 平台游戏类型下拉框
	 *
	 * @return
	 */
	@GetMapping( "/kindIdSelect" )
	public AjaxResult kindIdSelect() {
		List<GameInfo> gameInfo = gameInfoService.kindIdSelect();
		return AjaxResult.success( gameInfo );
	}

	/**
	 * 平台类型下拉框
	 *
	 * @return
	 */
	@GetMapping( "/platformIdSelect" )
	public AjaxResult platformIdSelect() {
		List<GamePlatform> gameInfo = gamePlatformService.platformIdSelect();
		return AjaxResult.success( gameInfo );
	}

	@GetMapping( "/nameSelect" )
	public AjaxResult nameSelect() {
		List<ActivityQuestType> activityQuestTypes = activityQuestTypeService.nameSelect();
		return AjaxResult.success( activityQuestTypes );
	}
}
