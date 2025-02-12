package com.qiqilm.server.admin.service;


import com.qiqilm.server.admin.domain.req.ReqReportMemberStatistics;
import com.qiqilm.server.admin.im.vo.RspMemberStats;

import java.math.BigDecimal;

public interface ReportMemberStatisticsService {

    BigDecimal getTotalRecharge( ReqReportMemberStatistics req );

    BigDecimal getTotalWithdrawal( ReqReportMemberStatistics req );

    BigDecimal getUserBalance( ReqReportMemberStatistics req );

    Long getTotalRegistration( ReqReportMemberStatistics req );

    BigDecimal getTotalGift( ReqReportMemberStatistics req );

    Long getDailyRechargeCount( ReqReportMemberStatistics req );

    Long getDailyFirstRechargeCount( ReqReportMemberStatistics req );

    RspMemberStats getMemberStats( ReqReportMemberStatistics req );
}
