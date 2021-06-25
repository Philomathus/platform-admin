package com.qiqilm.server.admin.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.qiqilm.server.admin.domain.ActivityType;
import com.qiqilm.server.admin.domain.ConfigBank;
import com.qiqilm.server.admin.domain.PayPlatformNew;
import com.qiqilm.server.admin.service.IActivityTypeService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.utils.StringUtils;
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
import com.qiqilm.server.admin.domain.ActivityInfo;
import com.qiqilm.server.admin.service.IActivityInfoService;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 活动信息Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping("/admin/activityInfo")
public class ActivityInfoController extends BaseController {
    @Autowired
    private IActivityInfoService activityInfoService;

    @Autowired
    private IActivityTypeService activityTypeService;

    /**
     * 查询活动信息列表
     */
    @PreAuthorize("@ss.hasPermi('admin:activityInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(ActivityInfo activityInfo) {
        startPage();
        List<ActivityInfo> list = activityInfoService.selectActivityInfoList(activityInfo);
        return getDataTable(list);
    }

    /**
     * 导出活动信息列表
     */
    @PreAuthorize("@ss.hasPermi('admin:activityInfo:export')")
    @Log(title = "活动信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(ActivityInfo activityInfo, HttpServletResponse response) {
        List<ActivityInfo> list = activityInfoService.selectActivityInfoList(activityInfo);
        ExportExcelUtil.exportExcel( list, "活动信息", "活动信息表", ActivityInfo.class, response);
    }

    /**
     * 获取活动信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('admin:activityInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return AjaxResult.success(activityInfoService.selectActivityInfoById(id));
    }

    /**
     * 新增活动信息
     */
    @PreAuthorize("@ss.hasPermi('admin:activityInfo:add')")
    @Log(title = "活动信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ActivityInfo activityInfo) {
        activityInfo.setId(UuidUtil.getRandomUuidWithoutSeparator());
        activityInfo.setCtime(new Date());
        return toAjax(activityInfoService.insertActivityInfo(activityInfo));
    }

    /**
     * 修改活动信息
     */
    @PreAuthorize("@ss.hasPermi('admin:activityInfo:edit')")
    @Log(title = "活动信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ActivityInfo activityInfo) {
        activityInfo.setCtime(new Date());
        return toAjax(activityInfoService.updateActivityInfo(activityInfo));
    }

    /**
     * 删除活动信息
     */
    @PreAuthorize("@ss.hasPermi('admin:activityInfo:remove')")
    @Log(title = "活动信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(activityInfoService.deleteActivityInfoByIds(ids));
    }

    /**
     * 活动类型下拉框
     *
     * @return
     */
    @GetMapping("/activityType")
    public AjaxResult findActivityType() {
        List<ActivityType> activityType = activityTypeService.selectActivityType();
        return AjaxResult.success(activityType);
    }

    /**
     * 活动信息状态修改
     */
    @PreAuthorize( "@ss.hasPermi('pay:configBank:edit')" )
    @Log( title = "活动信息状态", businessType = BusinessType.UPDATE )
    @PutMapping( "/changeStatus" )
    public AjaxResult changeStatus( @RequestBody ActivityInfo activityInfo ) {
        return toAjax(activityInfoService.updateActivityInfo(activityInfo));
    }

}
