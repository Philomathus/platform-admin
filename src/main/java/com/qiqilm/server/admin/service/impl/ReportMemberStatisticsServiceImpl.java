package com.qiqilm.server.admin.service.impl;


import com.qiqilm.server.admin.domain.req.ReqReportMemberStatistics;
import com.qiqilm.server.admin.im.vo.RechargeStatsDto;
import com.qiqilm.server.admin.im.vo.RspMemberStats;
import com.qiqilm.server.admin.mapper.ReportMemberStatisticsMapper;
import com.qiqilm.server.admin.service.ReportMemberStatisticsService;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.LocalDateTimeUtils;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Log4j2
@Service
public class ReportMemberStatisticsServiceImpl implements ReportMemberStatisticsService {

    public static final String MEMBER_REPORT_STATS = "member:report:stats:";

    @Resource
    private  ReportMemberStatisticsMapper reportMemberStatisticsMapper;

    @Resource
    private RedisUtil redisUtils;

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

    @Override
    public RspMemberStats getMemberStats( ReqReportMemberStatistics req ) {

        setDefaults( req );

        String key = MEMBER_REPORT_STATS + req.getChannelCode() + ":" + req.getInclusive_date();

        if ( redisUtils.exists( key ) ) {
            return JsonUtil.json2Object( redisUtils.strGet( key ), RspMemberStats.class );
        }

        RechargeStatsDto rechargeStatsDto = reportMemberStatisticsMapper.getRechargeStats( req );
        RspMemberStats rspMemberStats = RspMemberStats.builder()
                .totalRegistration( reportMemberStatisticsMapper.getTotalRegistration( req ) )
                .dailyRechargeCount( rechargeStatsDto.getDailyRechargeCount() )
                .totalRechargeAmount( rechargeStatsDto.getTotalRecharge().setScale( 2, RoundingMode.HALF_DOWN ) )
                .dailyFirstRechargeCount( reportMemberStatisticsMapper.getDailyFirstRechargeCount( req ) )
                .totalWithdrawCount( reportMemberStatisticsMapper.getDailyWithdrawCount( req ) )
                .gift( reportMemberStatisticsMapper.getTotalGift( req ) )
                .userBalance( reportMemberStatisticsMapper.getUserBalance( req ) )
                .build();

        redisUtils.strSet( key, JsonUtil.object2Json( rspMemberStats ) );
        redisUtils.expireAt( key, LocalDateTimeUtils.getEndOfToday().toInstant( ZoneOffset.ofHoursMinutes( 3, 55 ) ) );
        redisUtils.expireAt( key, LocalDateTime.now().toInstant( ZoneOffset.ofHoursMinutes( 0, 1 ) ) );

        return rspMemberStats;
    }

}
