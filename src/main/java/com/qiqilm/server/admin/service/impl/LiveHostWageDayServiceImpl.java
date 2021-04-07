package com.qiqilm.server.admin.service.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.domain.LiveHostWageDay;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageDayFamily;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageDayList;
import com.qiqilm.server.admin.mapper.LiveHostWageDayMapper;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveHostWageDayMapper;
import com.qiqilm.server.admin.domain.LiveHostWageDay;
import com.qiqilm.server.admin.service.ILiveHostWageDayService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-29
 */
@Service
public class LiveHostWageDayServiceImpl implements ILiveHostWageDayService {
    @Autowired
    private LiveHostWageDayMapper liveHostWageDayMapper;
    @Autowired
    private SysConfigCacheUtil sysConfigCacheUtil;

    /**
     * 查询主播时长
     *
     * @param id 主播时长ID
     * @return 主播时长
     */
    @Override
    public LiveHostWageDay selectLiveHostWageDayById(String id ) {
        return liveHostWageDayMapper.selectLiveHostWageDayById( id );
    }

    /**
     * 查询主播时长列表
     *
     * @param dto 主播时长
     * @return 主播时长
     */
    @Override
    public List<LiveHostWageDay> selectLiveHostWageDayList( LiveHostWageDay dto ) {
        this.setTime( dto );
        return liveHostWageDayMapper.selectLiveHostWageDayList( dto );
    }

    @Override
    public List<RspLiveHostWageDayFamily> familyPage(LiveHostWageDay dto ) throws ParseException {
        this.setTime( dto );
        //获取昨日日期
        if(StringUtils.isNotBlank(dto.getDateDay())) {
            dto.setDateDay(yesterday(dto.getDateDay()));
        }
//        BigDecimal ticketCattyRatio = sysConfigCacheUtil.getConfBd( "ticket_catty_ratio" );
        List<RspLiveHostWageDayFamily> liveHostWageDays = liveHostWageDayMapper.familyPage( dto );
        for ( RspLiveHostWageDayFamily liveHostWageDay : liveHostWageDays ) {
            if ( liveHostWageDay.getAllticket() != null ) {
//                BigDecimal allTicket = new BigDecimal( liveHostWageDay.getAllticket() );
                //判断是否是散户
                if (liveHostWageDay.getFamilyId() == 0) {
//                    liveHostWageDay.setAllticketRes( allTicket.multiply( dto.getSettlementRate() ).setScale( 2,
//                            BigDecimal.ROUND_HALF_UP ) );
//                    liveHostWageDay.setSettlementRate( dto.getSettlementRate() );
                    liveHostWageDay.setFamilyName("散户");
//                } else {
//                    liveHostWageDay.setAllticketRes( allTicket.multiply( ticketCattyRatio ).setScale( 2,
//                             BigDecimal.ROUND_HALF_UP ) );
//                    liveHostWageDay.setSettlementRate( ticketCattyRatio );
//                }
                }
            }
        }
        return liveHostWageDays;
    }

    @Override
    public List<RspLiveHostWageDayList> hostPage(LiveHostWageDay dto ) throws ParseException {
        this.setTime( dto );
//        BigDecimal ticketCattyRatio = sysConfigCacheUtil.getConfBd( "ticket_catty_ratio" );

        //获取昨日日期
        if(StringUtils.isNotBlank(dto.getDateDay())) {
            dto.setDateDay(yesterday(dto.getDateDay()));
        }
        List<RspLiveHostWageDayList> liveHostWageDays = liveHostWageDayMapper.hostPage(  dto );
        for ( RspLiveHostWageDayList liveHostWageDay : liveHostWageDays ) {
            if ( liveHostWageDay.getTicket() != null ) {
//                BigDecimal allTicket = new BigDecimal( liveHostWageDay.getTicket() );
                //判断是否是散户
                if ( liveHostWageDay.getFamilyId() == 0 ) {
//                    liveHostWageDay.setAllticketRes( allTicket.multiply( dto.getSettlementRate() ).setScale( 2,
//                            BigDecimal.ROUND_HALF_UP ) );
//                    liveHostWageDay.setSettlementRate( dto.getSettlementRate() );
                    liveHostWageDay.setFamilyName( "散户" );
//                } else {
//                    liveHostWageDay.setAllticketRes( allTicket.multiply( ticketCattyRatio ).setScale( 2,
//                            BigDecimal.ROUND_HALF_UP ) );
//                    liveHostWageDay.setSettlementRate( ticketCattyRatio );
//
                }
            }
        }
        return liveHostWageDays;
    }

    private void setTime( LiveHostWageDay dto ) {
        if ( dto.getDateDay() == null ) {
            Date d          = new Date();
            SimpleDateFormat sdf        = new SimpleDateFormat( "yyyy-MM-dd" );
            String           dateNowStr = sdf.format( d );
            dto.getSelectDate()[ 0 ] = dateNowStr;
            dto.getSelectDate()[ 1 ] = dateNowStr;
            dto.setStartTime(dto.getSelectDate()[ 0 ] + " 00:00:00");
            dto.setEndTime(dto.getSelectDate()[ 1 ] + " 23:59:59");
        } else {
            dto.setStartTime(dto.getDateDay() + " 00:00:00");
            dto.setEndTime(dto.getDateDay() + " 23:59:59");
        }
    }

    private String yesterday(String dateDay) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        long dif = df.parse(dateDay).getTime()-86400*1000;//减一天
        Date date=new Date();
        date.setTime(dif);
        return df.format(date);
    }
}