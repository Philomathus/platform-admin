package com.qiqilm.server.admin.controller.member.pay;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberPayJour;
import com.qiqilm.server.admin.domain.PayLog;
import com.qiqilm.server.admin.domain.req.DownLoadTime;
import com.qiqilm.server.admin.domain.req.MemberPayJourReq;
import com.qiqilm.server.admin.domain.rsp.MemberPayJourRsp;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IMemberPayJourService;
import com.qiqilm.server.admin.service.IPayLogService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private IPayLogService payLogService;

    @PostMapping("/all-list")
    public AjaxResult list(@RequestBody MemberPayJourReq req) {
        List<MemberPayJourRsp> list = memberPayJourService.selectMemberPayJourRspList(req.getCount());
        return AjaxResult.success(list);
    }

    @PostMapping("/error-list")
    public AjaxResult errorList() {
        List<PayLog> list = payLogService.selectPayLogErrorList();
        return AjaxResult.success(list);
    }

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
