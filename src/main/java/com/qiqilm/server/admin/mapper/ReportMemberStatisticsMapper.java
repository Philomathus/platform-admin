package com.qiqilm.server.admin.mapper;


import com.qiqilm.server.admin.domain.req.ReqReportMemberStatistics;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface ReportMemberStatisticsMapper {

    BigDecimal getTotalRecharge( @Param("req") ReqReportMemberStatistics req);

    BigDecimal getTotalWithdrawal(@Param("req") ReqReportMemberStatistics req);

    BigDecimal getUserBalance(@Param("req") ReqReportMemberStatistics req);

    Long getTotalRegistration(@Param("req") ReqReportMemberStatistics req);

    BigDecimal getTotalGift(@Param("req") ReqReportMemberStatistics req);

    Long getDailyRechargeCount( ReqReportMemberStatistics req );

    Long getDailyFirstRechargeCount( @Param("req")  ReqReportMemberStatistics req );
}
