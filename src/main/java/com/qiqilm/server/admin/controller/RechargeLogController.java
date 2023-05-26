package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.domain.RechargeLog;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.RechargeLogService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/pay/rechargeLog")
public class RechargeLogController extends BaseController {

    @Autowired
    private RechargeLogService rechargeLogService;

    /**
     * 充值日志列表 - recharge log list controller
     *
     * @param rechargeLog  - list of recharge log
     * @return 返回充值日志列表 - ist of recharge log
     */
    @PreAuthorize( "@ss.hasPermi('pay:rechargeLog:list')" )
    @GetMapping("/list")
    public TableDataInfo list( RechargeLog rechargeLog ){
        System.out.println(rechargeLog);
        startPage();
        return getDataTable( rechargeLogService.selectAllRechargeLog( rechargeLog ) );
    }

    /**
     * 导出充值日志列表
     * export recharge log
     */
    @PreAuthorize( "@ss.hasPermi('pay:rechargeLog:export')" )
    @Log( title = "充值日志列表", businessType = BusinessType.EXPORT )
    @GetMapping( "/export" )
    public void export( RechargeLog rechargeLog, HttpServletResponse response ) {
        List<RechargeLog> list = rechargeLogService.selectAllRechargeLog( rechargeLog );
        ExportExcelUtil.exportExcel( list, "导出充值日志列表", "导出充值日志列表", RechargeLog.class, response );
    }


}
