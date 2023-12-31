package com.qiqilm.server.admin.service.impl;



import com.qiqilm.server.admin.domain.req.ReqReportMemberStatistics;
import com.qiqilm.server.admin.mapper.ReportMemberStatisticsMapper;
import com.qiqilm.server.admin.service.ReportMemberStatisticsService;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Service
public class ReportMemberStatisticsServiceImpl implements ReportMemberStatisticsService {

    @Resource
    private  ReportMemberStatisticsMapper reportMemberStatisticsMapper;


    @Override
    public BigDecimal getTotalRecharge( ReqReportMemberStatistics req ) {
//        if( req.getInclusive_date().isEmpty()){
//            req.setInclusive_date( DateFormatUtils.formate( new Date(), DateFormatUtils.SPLIT_PATTERN_DATE ) );
//        }
        return reportMemberStatisticsMapper.getTotalRecharge(req) ;
    }

    @Override
    public BigDecimal getTotalWithdrawal( ReqReportMemberStatistics req ) {
        if( req.getInclusive_date().isEmpty()){
            req.setInclusive_date( DateFormatUtils.formate( new Date(), DateFormatUtils.SPLIT_PATTERN_DATE ) );
        }
        return reportMemberStatisticsMapper.getTotalWithdrawal(req);
    }

    @Override
    public BigDecimal getUserBalance( ReqReportMemberStatistics req ) {
        if( req.getInclusive_date().isEmpty()){
            req.setInclusive_date( DateFormatUtils.formate( new Date(), DateFormatUtils.SPLIT_PATTERN_DATE ) );
        }
        return reportMemberStatisticsMapper.getUserBalance(req);
    }

    @Override
    public Long getTotalRegistration( ReqReportMemberStatistics req ) {
        if( req.getInclusive_date().isEmpty()){
            req.setInclusive_date( DateFormatUtils.formate( new Date(), DateFormatUtils.SPLIT_PATTERN_DATE ) );
        }
        return reportMemberStatisticsMapper.getTotalRegistration(req);
    }

}
