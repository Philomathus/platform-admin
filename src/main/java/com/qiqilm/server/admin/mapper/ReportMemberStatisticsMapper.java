package com.qiqilm.server.admin.mapper;


import com.qiqilm.server.admin.domain.req.ReqReportMemberStatistics;

import java.math.BigDecimal;

public interface ReportMemberStatisticsMapper {

    BigDecimal getTotalRecharge( ReqReportMemberStatistics req );

    BigDecimal getTotalWithdrawal( ReqReportMemberStatistics req );

    BigDecimal getUserBalance( ReqReportMemberStatistics req );

    Long getTotalRegistration( ReqReportMemberStatistics req );

    BigDecimal getTotalGift( ReqReportMemberStatistics req );

    Long getDailyRechargeCount( ReqReportMemberStatistics req );

    Long getDailyFirstRechargeCount( ReqReportMemberStatistics req );
}
