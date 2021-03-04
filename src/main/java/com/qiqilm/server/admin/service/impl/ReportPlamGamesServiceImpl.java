package com.qiqilm.server.admin.service.impl;


import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ReportPlamGames;
import com.qiqilm.server.admin.mapper.ReportPlamGamesMapper;
import com.qiqilm.server.admin.service.IReportPlamGamesService;
import com.qiqilm.server.admin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class ReportPlamGamesServiceImpl implements IReportPlamGamesService {
	@Autowired
	private ReportPlamGamesMapper  reportPlamGamesMapper;
	@Autowired
	private RedisUtil              redisUtil;
	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param reportPlamGames 【请填写功能名称】
	 * @return 【请填写功能名称】
	 */
	@Override
	public Object selectReportPlamGamesList( ReportPlamGames reportPlamGames ) {

		Date             d          = new Date();
		SimpleDateFormat sdf        = new SimpleDateFormat( "yyyy-MM-dd" );
		String           dateNowStr = sdf.format( d );

		Calendar beforeTime = Calendar.getInstance();
		beforeTime.add( Calendar.MINUTE, -5 );// 5分钟之前的时间
		Date                  beforeD = beforeTime.getTime();
		List<ReportPlamGames> allList = reportPlamGamesMapper.selectReportPlamGamesList( reportPlamGames );
		if ( allList.size() == 0 && reportPlamGames.getBegindate().equals( dateNowStr ) ) {
			storage( dateNowStr, reportPlamGames );
			return new AjaxResult( 900, "报表正在生成，请稍后..." );
		}
		if ( allList.size() != 0 && reportPlamGames.getBegindate().equals( dateNowStr ) ) {
			Date updateTime = allList.get( 0 ).getUpdateTime();
			if ( updateTime.getTime() <= beforeD.getTime() ) {
				storage( dateNowStr, reportPlamGames );
				return new AjaxResult( 900, "报表正在生成，请稍后..." );
			} else if ( "0".equals( redisUtil.strGet( "admin-reportPlamGames" ) ) ) {
				return new AjaxResult( 900, "报表正在生成，请稍后..." );
			}
		}
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put( "rows", allList );
		return resultMap;

	}

	@Override
	public ReportPlamGames countBetData( ReportPlamGames reportPlamGames ) {

		return reportPlamGamesMapper.countBetData( reportPlamGames );
	}


	public void storage( String dateNowStr, ReportPlamGames reportPlamGames ) {
		String keyVal = redisUtil.strGet( "admin-reportPlamGames" );
		if ( !"0".equals( keyVal ) ) {
			synchronized ( this ) {
				redisUtil.strSet( "admin-reportPlamGames", "0", Duration.ofMinutes( 4) );
				threadPoolTaskExecutor.execute( () -> {
					String result = reportPlamGamesMapper.calldataProrepPlamcom( dateNowStr );
					if ( StringUtils.hasText( result ) && redisUtil.exists( "admin-reportPlamGames" ) ) {
						redisUtil.strIncrement( "admin-reportPlamGames" );
					}
				} );
			}
		}
	}

	@Override
	public List<ReportPlamGames> exportPlamGamesList( ReportPlamGames reportPlamGames ) {
		List<ReportPlamGames> allList = reportPlamGamesMapper.selectReportPlamGamesList( reportPlamGames );
		return allList;
	}

}