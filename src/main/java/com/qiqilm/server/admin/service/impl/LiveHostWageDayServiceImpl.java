package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.LiveHostWageDay;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageDayFamily;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageDayList;
import com.qiqilm.server.admin.mapper.LiveHostWageDayMapper;
import com.qiqilm.server.admin.service.ILiveHostWageDayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-29
 */
@Service
public class LiveHostWageDayServiceImpl implements ILiveHostWageDayService {
    @Resource
    private LiveHostWageDayMapper liveHostWageDayMapper;
    @Value( "${spring.profiles.active}" )
    private String profile;

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
        List<RspLiveHostWageDayFamily> liveHostWageDays;
        if(profile.equals("7706")){
            liveHostWageDays = liveHostWageDayMapper.familyPage_7706(dto);
        }else {
            liveHostWageDays = liveHostWageDayMapper.familyPage( dto );
        }
        if (liveHostWageDays.size()>0 && liveHostWageDays!=null){
            for ( RspLiveHostWageDayFamily liveHostWageDay : liveHostWageDays ) {
                if ( liveHostWageDay.getLiwu() != null ) {
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
        }
        return liveHostWageDays;
    }

    @Override
    public List<RspLiveHostWageDayList> hostPage(LiveHostWageDay dto ) throws ParseException {
        this.setTime( dto );
//        BigDecimal ticketCattyRatio = sysConfigCacheUtil.getConfBd( "ticket_catty_ratio" );

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
}