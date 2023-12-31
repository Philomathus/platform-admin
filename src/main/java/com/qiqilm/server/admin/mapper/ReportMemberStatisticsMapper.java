package com.qiqilm.server.admin.mapper;


import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.RspBase;
import com.qiqilm.server.admin.domain.req.ReqReportMemberStatistics;
import com.qiqilm.server.admin.domain.rsp.RspReportMemberStatistics;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ReportMemberStatisticsMapper {

    BigDecimal getTotalRecharge( @Param("req") ReqReportMemberStatistics req);

    BigDecimal getTotalWithdrawal(@Param("req") ReqReportMemberStatistics req);

    BigDecimal getUserBalance(@Param("req") ReqReportMemberStatistics req);

    Long getTotalRegistration(@Param("req") ReqReportMemberStatistics req);
}
