package com.qiqilm.server.admin.service.impl;

import com.google.common.collect.ImmutableMap;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LotteryBet0;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.LotteryBet0Mapper;
import com.qiqilm.server.admin.service.ILotteryBet0Service;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户投资行为Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-03
 */
@Service
public class LotteryBet0ServiceImpl implements ILotteryBet0Service {
	@Autowired
	private LotteryBet0Mapper lotteryBet0Mapper;


	/**
	 * 查询用户投资行为列表
	 *
	 * @param lotteryBet0 用户投资行为
	 * @return 用户投资行为
	 */
	@Override
	public List<LotteryBet0> selectLotteryBet0List( LotteryBet0 lotteryBet0 ) {
		if ( lotteryBet0.getSelectDate() != null && lotteryBet0.getSelectDate().length > 0 ) {
			lotteryBet0.setStartTime( lotteryBet0.getSelectDate()[ 0 ] );
			lotteryBet0.setEndTime( lotteryBet0.getSelectDate()[ 1 ] );
		}
		if ( lotteryBet0.isAbnormal() ) {
			if ( StringUtils.isBlank( lotteryBet0.getPuserId() ) ) {
				throw new BusinessException( "查询异常投注核对记录必须传入会员ID" );
			}
			String tableLast = lotteryBet0.getPuserId().substring( lotteryBet0.getPuserId().length() - 1 );
			lotteryBet0.setTableLast( tableLast );
			return lotteryBet0Mapper.selectLotteryBet0AbnormalList( lotteryBet0 );
		}
		if ( StringUtils.isNotBlank( lotteryBet0.getPuserId() ) ) {
			String tableLast = lotteryBet0.getPuserId().substring( lotteryBet0.getPuserId().length() - 1 );
			lotteryBet0.setTableLast( tableLast );
			return lotteryBet0Mapper.selectLotteryBet0SingleList( lotteryBet0 );
		}
		return lotteryBet0Mapper.selectLotteryBet0List( lotteryBet0 );
	}

	@Override
	public AjaxResult getCount( LotteryBet0 lotteryBet0 ) {
		LotteryBet0 lotteryBet01;
		if ( lotteryBet0.getSelectDate() != null && lotteryBet0.getSelectDate().length > 0 ) {
			lotteryBet0.setStartTime( lotteryBet0.getSelectDate()[ 0 ] );
			lotteryBet0.setEndTime( lotteryBet0.getSelectDate()[ 1 ] );
		}
		if ( lotteryBet0.isAbnormal() ) {
			if ( StringUtils.isBlank( lotteryBet0.getPuserId() ) ) {
				throw new BusinessException( "查询异常投注核对记录必须传入会员ID" );
			}
			String tableLast = lotteryBet0.getPuserId().substring( lotteryBet0.getPuserId().length() - 1 );
			lotteryBet0.setTableLast( tableLast );
			lotteryBet01 = lotteryBet0Mapper.getCountLotteryBet0AbnormalList( lotteryBet0 );
			return AjaxResult.success( lotteryBet01 );
		}
		if ( StringUtils.isNotBlank( lotteryBet0.getPuserId() ) ) {
			String tableLast = lotteryBet0.getPuserId().substring( lotteryBet0.getPuserId().length() - 1 );
			lotteryBet0.setTableLast( tableLast );
			lotteryBet01 = lotteryBet0Mapper.getCountLotteryBet0SingleList( lotteryBet0 );
			return AjaxResult.success( lotteryBet01 );
		}
		//lotteryBet01 = lotteryBet0Mapper.getCountLotteryBet0List(lotteryBet0);
		return AjaxResult.success( ImmutableMap.of( "totalCost", 0, "totalPrize", 0 ) );
	}
}
