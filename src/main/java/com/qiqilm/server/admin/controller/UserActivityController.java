package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.UserActivity;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.UserActivityService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 活动大厅申请Controller
 *
 * @author Rajesh
 * @date 2023-01-17
 */
@Slf4j
@RestController
@RequestMapping("/activity/userActivity")
public class UserActivityController extends BaseController {

    @Autowired
    private UserActivityService userActivityService;


    /**
     * 查询用户活动信息列表
     */
    @PreAuthorize( "@ss.hasPermi('activity:userActivity:list')" )
    @GetMapping("/list")
    public TableDataInfo list( UserActivity userActivity ){
        startPage();
        List<UserActivity> activityList =  userActivityService.selectAllUserActivity(userActivity);
        return getDataTable( activityList );
    }


    /**
     * 获取用户事件信息详情
     */
    @PreAuthorize( "@ss.hasPermi('activity:userActivity:query')" )
    @GetMapping("/{id}")
    public AjaxResult getQuery( @PathVariable String id ){
        return AjaxResult.success(userActivityService.selectByUserId(id));
    }


    /**
     * 导出用户活动信息列表
     */
    @PreAuthorize("@ss.hasPermi('activity:userActivity:export')")
    @Log(title = "用户事件导出", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export( UserActivity userActivity, HttpServletResponse response) {
        List<UserActivity> list = userActivityService.selectAllUserActivity(userActivity);
        ExportExcelUtil.exportExcel( list, "活动大厅申请", "用户事件信息表", UserActivity.class, response);
    }


}
