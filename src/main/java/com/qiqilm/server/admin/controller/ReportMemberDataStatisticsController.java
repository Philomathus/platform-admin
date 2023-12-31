package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.req.ReqReportMemberStatistics;
import com.qiqilm.server.admin.service.ReportMemberStatisticsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping( "/report/memberData" )
public class ReportMemberDataStatisticsController {

    @Resource
     private ReportMemberStatisticsService reportMemberStatisticsService;


    @PostMapping( "/recharge" )
    public AjaxResult getTotalRecharge( @RequestBody ReqReportMemberStatistics req) {
        return AjaxResult.success( reportMemberStatisticsService.getTotalRecharge(req) );
    }


    @PostMapping( "/withdrawal" )
    public AjaxResult getTotalWithdrawal(@RequestBody ReqReportMemberStatistics req) {
        return AjaxResult.success( reportMemberStatisticsService.getTotalWithdrawal(req));
    }


    @PostMapping( "/balance" )
    public AjaxResult getUserBalance(@RequestBody ReqReportMemberStatistics req) {
        return AjaxResult.success( reportMemberStatisticsService.getUserBalance(req) );
    }


    @PostMapping( "/registration" )
    public AjaxResult getTotalRegistration( @RequestBody ReqReportMemberStatistics req) {
        return AjaxResult.success(reportMemberStatisticsService.getTotalRegistration(req));
    }

//    @PostMapping("totalCode")

}
