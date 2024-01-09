package com.qiqilm.server.admin.service;


import com.qiqilm.server.admin.domain.req.ReqReportMemberStatistics;

import java.math.BigDecimal;

public interface ReportMemberStatisticsService {

    BigDecimal getTotalRecharge( ReqReportMemberStatistics req );

    BigDecimal getTotalWithdrawal( ReqReportMemberStatistics req );

    BigDecimal getUserBalance( ReqReportMemberStatistics req );

    Long getTotalRegistration( ReqReportMemberStatistics req );
    BigDecimal getTotalGift( ReqReportMemberStatistics req );

}
