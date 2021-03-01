package com.qiqilm.server.admin.service.impl;


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
import java.util.Date;
import java.util.List;

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
	public List<ReportPlamGames> selectReportPlamGamesList( ReportPlamGames reportPlamGames ) {
		List<ReportPlamGames> allList = reportPlamGamesMapper.selectReportPlamGamesList( reportPlamGames );

		return allList;
	}

	@Override
	public ReportPlamGames countBetData( ReportPlamGames reportPlamGames ) {

		return reportPlamGamesMapper.countBetData( reportPlamGames );
	}

	@Override
	public void storage( ReportPlamGames reportPlamGames ) {
		if ( !redisUtil.strSetIfAbsent( "admin-reportPlamGames", "0", Duration.ofMinutes( 10 ) ) ) {
			return;
		}
		// 判断锁是否释放
		// 如果是否，则return
		// 如果是则执行
		Date             d          = new Date();
		SimpleDateFormat sdf        = new SimpleDateFormat( "yyyy-MM-dd" );
		String           dateNowStr = sdf.format( d );
		reportPlamGames.setBegindate( dateNowStr );
		if ( reportPlamGames.getBegindate() == null || reportPlamGames.getBegindate().equals( dateNowStr ) ) {
			threadPoolTaskExecutor.execute( () -> {
				String result = reportPlamGamesMapper.calldataProrepPlamcom( dateNowStr );
				if ( StringUtils.hasText( result ) && redisUtil.exists( "admin-reportPlamGames" ) ) {
					redisUtil.strSet( "admin-reportPlamGames", "1" );
				}
			} );
		}
	}


}