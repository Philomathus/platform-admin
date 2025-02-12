package com.qiqilm.server.admin.mapper;


import com.qiqilm.server.admin.domain.req.ReqReportMemberStatistics;
import com.qiqilm.server.admin.im.vo.RechargeStatsDto;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface ReportMemberStatisticsMapper {

    BigDecimal getTotalRecharge( ReqReportMemberStatistics req);

    BigDecimal getTotalWithdrawal( ReqReportMemberStatistics req);

    BigDecimal getUserBalance( ReqReportMemberStatistics req);

    Long getTotalRegistration( ReqReportMemberStatistics req);

    BigDecimal getTotalGift( ReqReportMemberStatistics req);

    Long getDailyRechargeCount( ReqReportMemberStatistics req );

    Long getDailyFirstRechargeCount(  ReqReportMemberStatistics req );

    Long getDailyWithdrawCount( @Param( "req" ) ReqReportMemberStatistics req );

    RechargeStatsDto getRechargeStats( @Param( "req" ) ReqReportMemberStatistics req );

}
