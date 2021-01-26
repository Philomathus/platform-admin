package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.LogMoney;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.mapper.LogMoneyMapper;
import com.qiqilm.server.admin.service.ILogService;
import com.qiqilm.server.admin.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class LogServiceImpl implements ILogService {
	@Autowired
	private LogMoneyMapper logMoneyMapper;

	@Override
	public void logmarkMoney( String userid, String username, EnumMoney enumTrans, BigDecimal totalNow, BigDecimal totalold,
							  String mark, String markorder ) {
		BigDecimal trade = totalNow.subtract( totalold );
		int        i     = trade.compareTo( BigDecimal.ZERO );
		if ( i == 0 ) {
			return;
		}

		LogMoney log = new LogMoney();
		log.setId( UuidUtil.getRandomUuidWithoutSeparator() );
		log.setUserId( userid );
		log.setUserName( username );
		log.setCreateTime( new Date() );
		log.setIncome( BigDecimal.ZERO );
		log.setPay( BigDecimal.ZERO );
		if ( i > 0 ) {
			log.setIncome( trade );
		} else if ( i < 0 ) {
			log.setPay( trade.negate() );
		}
		log.setTotalBefore( totalold );
		log.setTotal( totalNow );
		log.setType( enumTrans.getType() );
		log.setDes( enumTrans.getDes() );
		log.setMark( mark );
		log.setMarkorder( markorder );
		logMoneyMapper.insertLogMoney( log );
	}
}
