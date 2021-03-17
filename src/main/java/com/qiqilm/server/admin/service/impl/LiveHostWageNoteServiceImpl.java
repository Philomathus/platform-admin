package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.domain.LiveHostWageNote;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageNoteFamily;
import com.qiqilm.server.admin.domain.rsp.RspLiveHostWageNoteList;
import com.qiqilm.server.admin.mapper.LiveHostWageNoteMapper;
import com.qiqilm.server.admin.service.ILiveHostWageNoteService;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 主播时长Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class LiveHostWageNoteServiceImpl implements ILiveHostWageNoteService {
	@Autowired
	private LiveHostWageNoteMapper liveHostWageNoteMapper;
	@Autowired
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
	 * @param liveHostWageNote 主播时长
	 * @return 主播时长
	 */
	@Override
	public List<LiveHostWageNote> selectLiveHostWageNoteList( LiveHostWageNote liveHostWageNote ) {
		return liveHostWageNoteMapper.selectLiveHostWageNoteList( liveHostWageNote );
	}

	@Override
	public List<RspLiveHostWageNoteFamily> familyPage( LiveHostWageNote dto ) {
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
	public List<RspLiveHostWageNoteList> hostPage( LiveHostWageNote dto ) {
		BigDecimal                ticketCattyRatio  = sysConfigCacheUtil.getConfBd( "ticket_catty_ratio" );
		List<RspLiveHostWageNoteList>    liveHostWageNotes = liveHostWageNoteMapper.hostPage( dto.getSelectDate()[ 0 ] + " - "
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
}
