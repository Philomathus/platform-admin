package com.qiqilm.server.admin.service.impl;


import com.qiqilm.server.admin.domain.req.ReqReportMemberStatistics;
import com.qiqilm.server.admin.mapper.ReportMemberStatisticsMapper;
import com.qiqilm.server.admin.service.ReportMemberStatisticsService;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Log4j2
@Service
public class ReportMemberStatisticsServiceImpl implements ReportMemberStatisticsService {

    @Resource
    private  ReportMemberStatisticsMapper reportMemberStatisticsMapper;


    @Override
    public BigDecimal getTotalRecharge( ReqReportMemberStatistics req ) {
        if( StringUtils.isEmpty( req.getInclusive_date() )){
            req.setInclusive_date( DateFormatUtils.formate( new Date(), DateFormatUtils.SPLIT_PATTERN_DATE ) );
        }
        return reportMemberStatisticsMapper.getTotalRecharge(req) ;
    }

    @Override
    public BigDecimal getTotalWithdrawal( ReqReportMemberStatistics req ) {
        if( StringUtils.isEmpty(req.getInclusive_date() )){
            req.setInclusive_date( DateFormatUtils.formate( new Date(), DateFormatUtils.SPLIT_PATTERN_DATE ) );
        }
        return reportMemberStatisticsMapper.getTotalWithdrawal(req);
    }

    @Override
    public BigDecimal getUserBalance( ReqReportMemberStatistics req ) {
        if( StringUtils.isEmpty( req.getInclusive_date()  ) ){
            req.setInclusive_date( DateFormatUtils.formate( new Date(), DateFormatUtils.SPLIT_PATTERN_DATE ) );
        }
        return reportMemberStatisticsMapper.getUserBalance(req);
    }

    @Override
    public Long getTotalRegistration( ReqReportMemberStatistics req ) {
        if( StringUtils.isEmpty(req.getInclusive_date() ) ){
            req.setInclusive_date( DateFormatUtils.formate( new Date(), DateFormatUtils.SPLIT_PATTERN_DATE ) );
        }
        return reportMemberStatisticsMapper.getTotalRegistration(req);
    }


    @Override
    public BigDecimal getTotalGift(ReqReportMemberStatistics req) {
        if( StringUtils.isEmpty( req.getInclusive_date() ) ){
            req.setInclusive_date( DateFormatUtils.formate( new Date(), DateFormatUtils.SPLIT_PATTERN_DATE ) );
        }
        BigDecimal result =  reportMemberStatisticsMapper.getTotalGift(req);
        log.info("result of getTotalGift: {}", result);
        return result;
    }

    private void setDefaults(ReqReportMemberStatistics reqReportMemberStatistics) {
        if( StringUtils.isBlank(reqReportMemberStatistics.getInclusive_date()) ){
//            reqReportMemberStatistics.setInclusive_date( LocalDateTimeUtils.format( LocalDate.now() ) );
        }
    }

    @Override
    public Long getDailyRechargeCount( ReqReportMemberStatistics req ) {
        setDefaults(req);
        return reportMemberStatisticsMapper.getDailyRechargeCount(req) ;
    }

    @Override
    public Long getDailyFirstRechargeCount( ReqReportMemberStatistics req ) {
        setDefaults(req);
        return reportMemberStatisticsMapper.getDailyFirstRechargeCount(req) ;
    }
}
