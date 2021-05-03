package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.LogMoney;
import com.qiqilm.server.admin.domain.MemberActionLogs;
import com.qiqilm.server.admin.enums.EnumAction;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.mapper.LogMoneyMapper;
import com.qiqilm.server.admin.mapper.MemberActionLogsMapper;
import com.qiqilm.server.admin.service.ILogService;
import com.qiqilm.server.admin.utils.UserDataUtil;
import com.qiqilm.server.admin.utils.UuidUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;

@Service
public class LogServiceImpl implements ILogService {
	@Resource
	private LogMoneyMapper         logMoneyMapper;
	@Resource
	private MemberActionLogsMapper actionLogsMapper;

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
		logMoneyMapper.insertLogMoney( log, log.getUserId().substring( log.getUserId().length() - 1 ) );
		if ( enumTrans == EnumMoney.chargegive || enumTrans == EnumMoney.gm
				|| enumTrans == EnumMoney.codeclean || enumTrans == EnumMoney.wongive ) {
			logMoneyMapper.insertLogMoney( log, "" );
		}
	}

	//备注行为enumTrans 现在金额totalNow   变动金额change  游戏agent  订单备注 name    变动订单号orderId
	@Override
	public void logMoneyAll( String userid, String username, EnumMoney enumTrans, BigDecimal totalNow, BigDecimal change,
							 String agent, String name, String orderId ) {
		int i = change.compareTo( BigDecimal.ZERO );
		if ( i == 0 ) {
			return;
		}
		if ( !StringUtils.hasText( orderId ) ) {
			orderId = UuidUtil.getRandomUuidWithoutSeparator();
		}
		LogMoney log = new LogMoney();
		log.setId( orderId );
		log.setUserId( userid );
		log.setUserName( username );
		log.setCreateTime( new Date() );
		log.setIncome( BigDecimal.ZERO );
		log.setPay( BigDecimal.ZERO );
		if ( i > 0 ) {
			log.setIncome( change );
		} else {
			log.setPay( change.negate() );
		}
		log.setTotal( totalNow );
		log.setTotalBefore( totalNow.subtract( change ) );
		log.setType( enumTrans.getType() );
		log.setDes( enumTrans.getDes() );
		log.setMark( name );
		log.setMarkorder( orderId );
		logMoneyMapper.insertLogMoney( log, log.getUserId().substring( log.getUserId().length() - 1 ) );
		if ( enumTrans == EnumMoney.chargegive || enumTrans == EnumMoney.gm
				|| enumTrans == EnumMoney.codeclean || enumTrans == EnumMoney.wongive ) {
			logMoneyMapper.insertLogMoney( log, "" );
		}
	}

	@Override
	public void logMoneyAdd( String businessId, String userid, String username, EnumMoney enumTrans, BigDecimal add,
							 BigDecimal old, String mark, String markorder ) {
		if ( businessId == null ) {
			businessId = UuidUtil.getRandomUuidWithoutSeparator();
		}
		LogMoney log = new LogMoney();
		log.setId( businessId );
		log.setUserId( userid );
		log.setUserName( username );
		log.setCreateTime( new Date() );
		log.setIncome( add );
		log.setPay( BigDecimal.ZERO );

		log.setTotal( old.add( add ) );
		log.setType( enumTrans.getType() );
		log.setDes( enumTrans.getDes() );
		log.setTotalBefore( old );
		log.setMark( mark );
		log.setMarkorder( markorder );
		logMoneyMapper.insertLogMoney( log, log.getUserId().substring( log.getUserId().length() - 1 ) );
		if ( enumTrans == EnumMoney.chargegive || enumTrans == EnumMoney.gm
				|| enumTrans == EnumMoney.codeclean || enumTrans == EnumMoney.wongive ) {
			logMoneyMapper.insertLogMoney( log, "" );
		}
	}

	@Override
	public void logMemberAction( HttpServletRequest request, String userid, String username, EnumAction enumAction,
								 String params1, String params2,
								 String params3, String params4 ) {
		MemberActionLogs log = new MemberActionLogs();
		log.setId( UuidUtil.getRandomUuidWithoutSeparator() );
		log.setUserId( userid );
		log.setUserName( username );
		log.setType( enumAction.getType() );
		log.setDes( enumAction.getDes() );
		log.setParam1( params1 );
		log.setParam2( params2 );
		log.setParam3( params3 );
		log.setParam4( params4 );
		log.setParamIp( UserDataUtil.getIp( request ) );
		log.setcTime( new Date() );
		actionLogsMapper.insertMemberActionLogs( log );
	}
}
