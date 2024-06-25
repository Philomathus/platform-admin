package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LogMoney;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILogMoneyMonthlyService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping( "/pay/logMoneyMonthly" )
public class LogMoneyMonthlyController extends BaseController {

    @Autowired
    ILogMoneyMonthlyService logMoneyMonthlyService;

    @PreAuthorize( "@ss.hasPermi('pay:logMoneyMonthly:list')" )
    @GetMapping( "/list" )
    public TableDataInfo list( LogMoney logMoney ) {
        startPage();
        List<LogMoney> list = logMoneyMonthlyService.selectLogMoneyMonthlyList(logMoney);
        return getDataTable( list );
    }

    @PreAuthorize( "@ss.hasPermi('pay:logMoneyMonthly:list')" )
    @GetMapping( "/listCount" )
    public AjaxResult listCount(LogMoney logMoney ) {
        return logMoneyMonthlyService.listCount( logMoney );
    }

    @PreAuthorize( "@ss.hasPermi('pay:logMoneyMonthly:list')" )
    @GetMapping( "/totalCount" )
    public AjaxResult totalCount( LogMoney logMoney ) {
        return logMoneyMonthlyService.totalCount( logMoney );
    }

    @PreAuthorize( "@ss.hasPermi('pay:logMoneyMonthly:export')" )
    @Log( title = " 会员资金信息", businessType = BusinessType.EXPORT )
    @GetMapping( "/export" )
    public void export( LogMoney logMoney, HttpServletResponse response ) {
        List<LogMoney> list = logMoneyMonthlyService.selectLogMoneyMonthlyList( logMoney );
        if( !list.isEmpty() ) {
            ExportExcelUtil.exportExcel(list, "会员资金信息", "会员资金信息表", LogMoney.class, response);
        }
    }

}
