package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveHostWageDay;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageDayFamily;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageDayList;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageDays;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveHostWageDayService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/admin/liveHostWageDay")
public class LiveHostWageDayController extends BaseController {
    @Autowired
    private ILiveHostWageDayService liveHostWageDayService;

    /**
     * 查询主播时长列表
     */
    @PreAuthorize("@ss.hasPermi('admin:liveHostWageNote:list')")
    @GetMapping("/list")
    public TableDataInfo list(LiveHostWageDay dto) {
        startPage();
        List<LiveHostWageDay> list = liveHostWageDayService.selectLiveHostWageDayList(dto);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('admin:liveHostWageNote:list')")
    @GetMapping("/familyPage")
    public TableDataInfo familyPage(LiveHostWageDay dto) throws ParseException {
        startPage();
        List<RspLiveHostWageDayFamily> list = liveHostWageDayService.familyPage(dto);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('admin:liveHostWageNote:list')")
    @GetMapping("/hostPage")
    public TableDataInfo getPage(LiveHostWageDay dto) throws ParseException {
        startPage();
        List<RspLiveHostWageDayList> list = liveHostWageDayService.hostPage(dto);
        return getDataTable(list);
    }

    /**
     * 导出家族直播时长列表
     */
    @PreAuthorize("@ss.hasPermi('admin:liveHostWageNote:export')")
    @Log(title = "家族直播时长", businessType = BusinessType.EXPORT)
    @GetMapping("/exportFamily")
    public void exportFamily(LiveHostWageDay dto, HttpServletResponse response) throws ParseException {
        List<RspLiveHostWageDayFamily> list = liveHostWageDayService.familyPage(dto);
        ExportExcelUtil.exportExcel(list, "家族直播时长", "家族直播时长", RspLiveHostWageDayFamily.class, response);
    }

    /**
     * 导出主播统计时长列表
     */
    @PreAuthorize("@ss.hasPermi('admin:liveHostWageNote:export')")
    @Log(title = "主播统计时长", businessType = BusinessType.EXPORT)
    @GetMapping("/exportHost")
    public void exportHost(LiveHostWageDay dto, HttpServletResponse response) throws ParseException {
        List<RspLiveHostWageDayList> list = liveHostWageDayService.hostPage(dto);
        ExportExcelUtil.exportExcel(list, "主播统计时长", "主播统计时长", RspLiveHostWageDayList.class, response);
    }

    /**
     * 获取主播时长详细信息
     */
    @PreAuthorize("@ss.hasPermi('admin:liveHostWageNote:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return AjaxResult.success(liveHostWageDayService.selectLiveHostWageDayById(id));
    }

    /**
     * 查询主播每日时长统计列表
     */
    @PreAuthorize("@ss.hasPermi('admin:liveHostWageNote:lists')")
    @GetMapping("/lists")
    public TableDataInfo lists(LiveHostWageDay dto) {
        startPage();
        List<RspLiveHostWageDays> list = liveHostWageDayService.liveHostWageDays(dto);
        return getDataTable(list);
    }

    /**
     * 导出主播统计每日时长统计列表
     */
    @PreAuthorize("@ss.hasPermi('admin:liveHostWageNote:exportHosts')")
    @Log(title = "主播时长礼物彩票总和", businessType = BusinessType.EXPORT)
    @GetMapping("/exportHosts")
    public void exportHosts(LiveHostWageDay dto, HttpServletResponse response) throws ParseException {
        List<RspLiveHostWageDays> list = liveHostWageDayService.liveHostWageDays(dto);
        ExportExcelUtil.exportExcel(list, "主播时长礼物彩票总和", "主播时长礼物彩票总和", RspLiveHostWageDays.class, response);
    }
}


