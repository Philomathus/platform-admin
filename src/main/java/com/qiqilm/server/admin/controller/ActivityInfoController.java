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
import com.qiqilm.server.admin.domain.ActivityInfo;
import com.qiqilm.server.admin.service.IActivityInfoService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping("/admin/activityInfo")
public class ActivityInfoController extends BaseController {
    @Autowired
    private IActivityInfoService activityInfoService;

    /**
     * 查询【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('admin:activityInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(ActivityInfo activityInfo) {
        startPage();
        List<ActivityInfo> list = activityInfoService.selectActivityInfoList(activityInfo);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('admin:activityInfo:export')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(ActivityInfo activityInfo) {
        List<ActivityInfo> list = activityInfoService.selectActivityInfoList(activityInfo);
        ExcelUtil<ActivityInfo> util = new ExcelUtil<ActivityInfo>(ActivityInfo.class);
        return util.exportExcel(list, "activityInfo");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @PreAuthorize("@ss.hasPermi('admin:activityInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return AjaxResult.success(activityInfoService.selectActivityInfoById(id));
    }

    /**
     * 新增【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('admin:activityInfo:add')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ActivityInfo activityInfo) {
        activityInfo.setId(UuidUtil.getRandomUuidWithoutSeparator());
        activityInfo.setCtime(new Date());
        return toAjax(activityInfoService.insertActivityInfo(activityInfo));
    }

    /**
     * 修改【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('admin:activityInfo:edit')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ActivityInfo activityInfo) {
        return toAjax(activityInfoService.updateActivityInfo(activityInfo));
    }

    /**
     * 删除【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('admin:activityInfo:remove')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(activityInfoService.deleteActivityInfoByIds(ids));
    }
}
