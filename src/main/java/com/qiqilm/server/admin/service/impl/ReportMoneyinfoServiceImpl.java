package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ReportMoneyinfo;
import com.qiqilm.server.admin.mapper.ReportMoneyinfoMapper;
import com.qiqilm.server.admin.service.IReportMoneyinfoService;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;


/**
 * 平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class ReportMoneyinfoServiceImpl implements IReportMoneyinfoService {
	@Autowired
	private ReportMoneyinfoMapper  reportMoneyinfoMapper;
	@Autowired
	private RedisUtil              redisUtil;
	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	/**
	 * 查询平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额列表
	 *
	 * @param reportMoneyinfo 平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额
	 * @return 平台资金报，记录平台每日收入及支出总额，预估当前会员的积分余额
	 */
	@Override
	public Object selectReportMoneyinfoList( ReportMoneyinfo reportMoneyinfo ) throws ParseException {
		String dateNowStr = dateNowStr();//获取当天时间字符串
		setSelectTime( dateNowStr, reportMoneyinfo );//首次进入查询7天的数据
		String beginTime     = ( String ) reportMoneyinfo.getParams().get( "beginTime" );
		String endTime     = ( String ) reportMoneyinfo.getParams().get( "endTime" );
		Map<String, Object> resultMap = new HashMap<>();
//		Date   beginTimeDate = DateFormatUtils.parse( beginTime, DateFormatUtils.SPLIT_PATTERN_DATE );
//		Date   endTimeDate = DateFormatUtils.parse( endTime, DateFormatUtils.SPLIT_PATTERN_DATE );
//		Date toDay = DateFormatUtils.parse( dateNowStr, DateFormatUtils.SPLIT_PATTERN_DATE );
//		if ( beginTimeDate.compareTo( toDay ) <= 0 && endTimeDate.compareTo( toDay ) >= 0 ) {
//			// 调用存储过程
//			if(!redisUtil.exists( "admin-reportMoneyInfo" )){
//				storage( dateNowStr );
//				return new AjaxResult( 900, "报表正在生成，请稍后..." );
//			}
//			if("0".equals( redisUtil.strGet( "admin-reportMoneyInfo" ) )){
//				return new AjaxResult( 900, "报表正在生成，请稍后..." );
//			}
//		}
		if(dateNowStr.equals(beginTime) || dateNowStr.equals(endTime)){
			if(!redisUtil.exists( "admin-reportMoneyInfo" )){
				storage( dateNowStr );
			}
		}
		List<ReportMoneyinfo> allList = reportMoneyinfoMapper.selectReportMoneyinfoList( reportMoneyinfo );
		resultMap.put( "rows", allList );
		return resultMap;
	}

	public void storage( String dateNowStr ) {
//		if ( !redisUtil.exists( "admin-reportMoneyInfo" ) &&
//				redisUtil.strSetIfAbsent( "admin-reportMoneyInfo", "0", Duration.ofMinutes( 5 ) ) ) {
//			redisUtil.strSet( "admin-reportMoneyInfo", "0", Duration.ofMinutes( 5 ) );
//			threadPoolTaskExecutor.execute( () -> {
//				String result = reportMoneyinfoMapper.calldataProrepPlamcom( dateNowStr, dateNowStr );
//				if ( StringUtils.hasText( result ) && redisUtil.exists( "admin-reportMoneyInfo" ) ) {
//					redisUtil.strIncrement( "admin-reportMoneyInfo" );
//				}
//			} );
//		}
		if ( !redisUtil.exists( "admin-reportMoneyInfo" )){
			String result = reportMoneyinfoMapper.calldataProrepPlamcom( dateNowStr, dateNowStr );
			redisUtil.strSet( "admin-reportMoneyInfo", "0", Duration.ofMinutes( 5 ) );
		}
	}

	//统计表头数据
	@Override
	public ReportMoneyinfo countMoneyData( ReportMoneyinfo reportMoneyinfo ) throws ParseException {
		String dateNowStr = dateNowStr();//获取当天时间字符串
		setSelectTime( dateNowStr, reportMoneyinfo );//首次进入查询7天的数据
		String           beginTime        = ( String ) reportMoneyinfo.getParams().get( "beginTime" );
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
		Date             date             = simpleDateFormat.parse( beginTime );
		boolean          flag             = date.before( new Date() );
		if ( !flag ) {
			reportMoneyinfo.setPaymentAmount( BigDecimal.ZERO );
			reportMoneyinfo.setOutMoney( BigDecimal.ZERO );
			reportMoneyinfo.setCountMoney( BigDecimal.ZERO );
			reportMoneyinfo.setTotalAccountGifts( BigDecimal.ZERO );
			return reportMoneyinfo;
		}
		ReportMoneyinfo reportMoneyinfo1 = reportMoneyinfoMapper.countMoneyInfoData( reportMoneyinfo );
		if ( !ObjectUtils.isEmpty( reportMoneyinfo1 ) ) {
			BigDecimal paymentAmount = reportMoneyinfo1.getPaymentAmount();//入款总金额
			BigDecimal outMoney      = reportMoneyinfo1.getOutMoney();//出款总金额
			reportMoneyinfo1.setCountMoney( paymentAmount.subtract( outMoney ) );
			return reportMoneyinfo1;
		} else {
			reportMoneyinfo.setPaymentAmount( BigDecimal.ZERO );
			reportMoneyinfo.setOutMoney( BigDecimal.ZERO );
			reportMoneyinfo.setCountMoney( BigDecimal.ZERO );
			reportMoneyinfo.setTotalAccountGifts( BigDecimal.ZERO );
			return reportMoneyinfo;
		}
	}

	@Override
	public List<ReportMoneyinfo> exportMoneyinfoList( ReportMoneyinfo reportMoneyinfo ) {
		List<ReportMoneyinfo> allList = reportMoneyinfoMapper.selectReportMoneyinfoList( reportMoneyinfo );
		return allList;
	}

	private void setSelectTime( String dateNowStr, ReportMoneyinfo reportMoneyinfo ) {
		if ( null == reportMoneyinfo.getParams() || reportMoneyinfo.getParams().size() == 0 ||
				reportMoneyinfo.getParams().get( "beginTime" ) == "" ) {
			HashMap m = new HashMap<>();
			m.put( "beginTime", getPastDate( 7 ) );
			m.put( "endTime", dateNowStr );
			reportMoneyinfo.setParams( m );
		}
	}

	private String dateNowStr() {
		Date             d          = new Date();
		SimpleDateFormat sdf        = new SimpleDateFormat( "yyyy-MM-dd" );
		String           dateNowStr = sdf.format( d );
		return dateNowStr;
	}

	private String getPastDate( int past ) {
		Calendar calendar = Calendar.getInstance();
		calendar.set( Calendar.DAY_OF_YEAR, calendar.get( Calendar.DAY_OF_YEAR ) - past );
		Date             today  = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );
		String           result = format.format( today );
		return result;
	}

}