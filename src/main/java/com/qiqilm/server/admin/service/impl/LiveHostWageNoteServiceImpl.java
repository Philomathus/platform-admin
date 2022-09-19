package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.domain.LiveHostWageNote;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageNoteFamily;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageNoteList;
import com.qiqilm.server.admin.mapper.LiveHostWageNoteMapper;
import com.qiqilm.server.admin.service.ILiveHostWageNoteService;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 主播时长Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class LiveHostWageNoteServiceImpl implements ILiveHostWageNoteService {
	@Resource
	private LiveHostWageNoteMapper liveHostWageNoteMapper;
	@Resource
	private SysConfigCacheUtil     sysConfigCacheUtil;

	/**
	 * 查询主播时长
	 *
	 * @param id 主播时长ID
	 * @return 主播时长
	 */
	@Override
	public LiveHostWageNote selectLiveHostWageNoteById( Long id ) {
		return liveHostWageNoteMapper.selectLiveHostWageNoteById( id );
	}

	/**
	 * 查询主播时长列表
	 *
	 * @param dto 主播时长
	 * @return 主播时长
	 */
	@Override
	public List<LiveHostWageNote> selectLiveHostWageNoteList( LiveHostWageNote dto ) {
		this.setTime( dto );
		return liveHostWageNoteMapper.selectLiveHostWageNoteList( dto );
	}

	@Override
	public List<RspLiveHostWageNoteFamily> familyPage( LiveHostWageNote dto ) {
		this.setTime( dto );
		BigDecimal ticketCattyRatio = sysConfigCacheUtil.getConfBd( "ticket_catty_ratio" );
		List<RspLiveHostWageNoteFamily> liveHostWageNotes = liveHostWageNoteMapper.familyPage( dto.getSelectDate()[ 0 ] + " - "
				+ dto.getSelectDate()[ 1 ], dto );
		for ( RspLiveHostWageNoteFamily liveHostWageNote : liveHostWageNotes ) {
			if ( liveHostWageNote.getAllticket() != null ) {
				BigDecimal allTicket = new BigDecimal( liveHostWageNote.getAllticket() );
				//判断是否是散户
				if ( liveHostWageNote.getFamilyId() == 0 && dto.getSettlementRate() != null ) {
					liveHostWageNote.setAllticketRes( allTicket.multiply( dto.getSettlementRate() ).setScale( 2,
							BigDecimal.ROUND_HALF_UP ) );
					liveHostWageNote.setSettlementRate( dto.getSettlementRate() );
					liveHostWageNote.setFamilyName( "直播家族散户(未入家族)" );
				} else {
					liveHostWageNote.setAllticketRes( allTicket.multiply( ticketCattyRatio ).setScale( 2,
							BigDecimal.ROUND_HALF_UP ) );
					liveHostWageNote.setSettlementRate( ticketCattyRatio );
				}
			}
		}
		return liveHostWageNotes;
	}

	@Override
	public List<RspLiveHostWageNoteList> hostPage( LiveHostWageNote dto ) throws ParseException {
		this.setTime( dto );
		BigDecimal ticketCattyRatio = sysConfigCacheUtil.getConfBd( "ticket_catty_ratio" );

		//获取昨日日期
		if(StringUtils.isNotBlank(dto.getDateDay())) {
			dto.setDateDay(yesterday(dto.getDateDay()));
		}
		List<RspLiveHostWageNoteList> liveHostWageNotes = liveHostWageNoteMapper.hostPage( dto.getSelectDate()[ 0 ] + " - "
				+ dto.getSelectDate()[ 1 ], dto );
		for ( RspLiveHostWageNoteList liveHostWageNote : liveHostWageNotes ) {
			if ( liveHostWageNote.getAllticket() != null ) {
				BigDecimal allTicket = new BigDecimal( liveHostWageNote.getAllticket() );
				//判断是否是散户
				if ( liveHostWageNote.getFamilyId() == 0 && dto.getSettlementRate() != null ) {
					liveHostWageNote.setAllticketRes( allTicket.multiply( dto.getSettlementRate() ).setScale( 2,
							BigDecimal.ROUND_HALF_UP ) );
					liveHostWageNote.setSettlementRate( dto.getSettlementRate() );
					liveHostWageNote.setFamilyName( "直播家族散户(未入家族)" );
				} else {
					liveHostWageNote.setAllticketRes( allTicket.multiply( ticketCattyRatio ).setScale( 2,
							BigDecimal.ROUND_HALF_UP ) );
					liveHostWageNote.setSettlementRate( ticketCattyRatio );

				}
			}
		}
		return liveHostWageNotes;
	}

	private void setTime( LiveHostWageNote dto ) {
		if ( dto.getDateDay() == null ) {
			Date             d          = new Date();
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
