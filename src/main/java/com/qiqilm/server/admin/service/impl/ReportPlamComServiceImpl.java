package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ReportPlamCom;
import com.qiqilm.server.admin.mapper.ReportPlamComMapper;
import com.qiqilm.server.admin.service.IReportPlamComService;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

/**
 * 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Log4j2
@Service
public class ReportPlamComServiceImpl implements IReportPlamComService {
	@Autowired
	private ReportPlamComMapper    reportPlamComMapper;
	@Autowired
	private RedisUtil              redisUtil;
	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	public static void main( String[] args ) {
		System.out.println( DateFormatUtils.formate( new Date() ) );
		Calendar beforeTime = Calendar.getInstance();
		beforeTime.add( Calendar.MINUTE, -5 );
		System.out.println( DateFormatUtils.formate( beforeTime.getTime() ) );
	}

	/**
	 * 查询综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间列表
	 *
	 * @param reportPlamCom 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
	 * @return 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
	 */
	@Override
	public Object selectReportPlamComList( ReportPlamCom reportPlamCom ) {
		Date             d          = new Date();
		SimpleDateFormat sdf        = new SimpleDateFormat( "yyyy-MM-dd" );
		String           dateNowStr = sdf.format( d );
		if ( Strings.isBlank( reportPlamCom.getReporttime() ) ) {
			reportPlamCom.setReporttime( dateNowStr );
		}
		Calendar beforeTime = Calendar.getInstance();
		beforeTime.add( Calendar.MINUTE, -5 );// 5分钟之前的时间
		Date                beforeD = beforeTime.getTime();
		List<ReportPlamCom> allList = reportPlamComMapper.selectReportPlamComList( reportPlamCom );
		if ( allList.size() == 0 && reportPlamCom.getReporttime().equals( dateNowStr ) ) {
			storage( dateNowStr );
			return new AjaxResult( 900, "报表正在生成，请稍后..." );
		}
		Map<String, Object> resultMap = new HashMap<>();
		if ( allList.size() != 0 && reportPlamCom.getReporttime().equals( dateNowStr ) ) {
			Date   updateTime  = allList.get( 0 ).getUpdateTime();
			String reportCache = redisUtil.strGet( "admin-reportPlamCom" );
			if ( "1".equals( reportCache ) ) {
				resultMap.put( "rows", allList );
			} else if ( "0".equals( reportCache ) ) {
				return new AjaxResult( 900, "报表正在生成，请稍后..." );
			} else if ( updateTime.getTime() <= beforeD.getTime() ) {
				storage( dateNowStr );
				return new AjaxResult( 900, "报表正在生成，请稍后..." );
			}
		} else {
			resultMap.put( "rows", allList );
		}
		return resultMap;

	}

	@Override
	public List<ReportPlamCom> exportPlamComList( ReportPlamCom reportPlamCom ) {
		List<ReportPlamCom> allList = reportPlamComMapper.selectReportPlamComList( reportPlamCom );
		return allList;
	}

	public void storage( String dateNowStr ) {
		if ( !redisUtil.exists( "admin-reportPlamCom" )
				&& redisUtil.strSetIfAbsent( "admin-reportPlamCom", "0", Duration.ofMinutes( 5 ) ) ) {
			threadPoolTaskExecutor.execute( () -> {
				String result = reportPlamComMapper.calldataProrepPlamcom( dateNowStr );
				if ( StringUtils.hasText( result ) && redisUtil.exists( "admin-reportPlamCom" ) ) {
					redisUtil.strIncrement( "admin-reportPlamCom" );
				}
			} );
		}
	}

}