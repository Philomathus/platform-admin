package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberPayJour;
import com.qiqilm.server.admin.domain.req.DownLoadTime;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IMemberPayJourService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 线上充值信息Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping("/pay/memberPayJour")
public class MemberPayJourController extends BaseController {
    @Autowired
    private IMemberPayJourService memberPayJourService;

    /**
     * 查询线上充值信息列表
     */
    @PreAuthorize("@ss.hasPermi('pay:memberPayJour:list')")
    @GetMapping("/list")
    public TableDataInfo list(MemberPayJour memberPayJour) {
        startPage();
        List<MemberPayJour> list = memberPayJourService.selectMemberPayJourList(memberPayJour);
        return getDataTable(list);
    }

    /**
     * 查询线上充值信息统计信息列表
     */
    @PreAuthorize("@ss.hasPermi('pay:memberPayJour:list')")
    @GetMapping("/listCount")
    @Log(title = "线上通道报表报表", businessType = BusinessType.EXPORT)
    public Map listCount(MemberPayJour memberPayJour) {
        return memberPayJourService.listCount(memberPayJour);
    }

    /**
     * 导出线上充值信息列表
     */
    @PreAuthorize("@ss.hasPermi('pay:memberPayJour:export')")
    @Log(title = "线上充值信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(MemberPayJour memberPayJour, HttpServletResponse response) {
        List<MemberPayJour> list = memberPayJourService.selectMemberPayJourList(memberPayJour);
        if (list.size() <= DownLoadTime.downLoadLimit) {
            ExportExcelUtil.exportExcel(list, "线上充值", "线上充值表", MemberPayJour.class, response);
        }
    }

    /**
     * 获取线上充值信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:memberPayJour:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return AjaxResult.success(memberPayJourService.selectMemberPayJourById(id));
    }


}
