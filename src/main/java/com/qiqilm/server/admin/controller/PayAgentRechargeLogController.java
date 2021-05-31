package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.PayAgentRechargeLog;
import com.qiqilm.server.admin.domain.req.DownLoadTime;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IPayAgentRechargeLogService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 【代充信息日志】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping("/pay/payAgentRechargeLog")
public class PayAgentRechargeLogController extends BaseController {
    @Autowired
    private IPayAgentRechargeLogService payAgentRechargeLogService;

    /**
     * 查询【代充信息日志】列表
     */
    @PreAuthorize("@ss.hasPermi('pay:payAgentRechargeLog:list')")
    @GetMapping("/list")
    public TableDataInfo list(PayAgentRechargeLog payAgentRechargeLog) {
        startPage();
        List<PayAgentRechargeLog> list = payAgentRechargeLogService.selectPayAgentRechargeLogList(payAgentRechargeLog);
        return getDataTable(list);
    }

    /**
     * 导出【代充信息日志】列表
     */
    @PreAuthorize("@ss.hasPermi('pay:payAgentRechargeLog:export')")
    @Log(title = "【代充信息日志】", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(PayAgentRechargeLog payAgentRechargeLog, HttpServletResponse response ) {
        List<PayAgentRechargeLog> list = payAgentRechargeLogService.selectPayAgentRechargeLogList(payAgentRechargeLog);
        if (list.size() <= DownLoadTime.downLoadLimit) {
            ExportExcelUtil.exportExcel(list, "代充日志", "代充日志表", PayAgentRechargeLog.class, response);
        }
    }

    /**
     * 获取【代充信息日志】详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:payAgentRechargeLog:query')")
    @GetMapping(value = "/{orderNo}")
    public AjaxResult getInfo(@PathVariable("orderNo") String orderNo) {
        return AjaxResult.success(payAgentRechargeLogService.selectPayAgentRechargeLogById(orderNo));
    }

    /**
     * 新增【代充信息日志】
     */
    @PreAuthorize("@ss.hasPermi('pay:payAgentRechargeLog:add')")
    @Log(title = "【代充信息日志】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PayAgentRechargeLog payAgentRechargeLog) {
        return toAjax(payAgentRechargeLogService.insertPayAgentRechargeLog(payAgentRechargeLog));
    }

    /**
     * 修改【代充信息日志】
     */
    @PreAuthorize("@ss.hasPermi('pay:payAgentRechargeLog:edit')")
    @Log(title = "【代充信息日志】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PayAgentRechargeLog payAgentRechargeLog) {
        return toAjax(payAgentRechargeLogService.updatePayAgentRechargeLog(payAgentRechargeLog));
    }

    /**
     * 删除【代充信息日志】
     */
    @PreAuthorize("@ss.hasPermi('pay:payAgentRechargeLog:remove')")
    @Log(title = "【代充信息日志】", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderNos}")
    public AjaxResult remove(@PathVariable String[] orderNos) {
        return toAjax(payAgentRechargeLogService.deletePayAgentRechargeLogByIds(orderNos));
    }

    /**
     * 统计按钮【代充信息日志】
     */
    @PreAuthorize("@ss.hasPermi('pay:payAgentRechargeLog:list')")
    @GetMapping("/count")
    public AjaxResult count(PayAgentRechargeLog payAgentRechargeLog) {
        PayAgentRechargeLog payAgentRechargeLogtwo = payAgentRechargeLogService.count(payAgentRechargeLog);
        return AjaxResult.success(payAgentRechargeLogtwo);
    }
}
