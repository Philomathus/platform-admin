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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
		Date             d          = new Date();
		SimpleDateFormat sdf        = new SimpleDateFormat( "yyyy-MM-dd" );
		String           dateNowStr = sdf.format( d );

		Calendar beforeTime = Calendar.getInstance();
		beforeTime.add(Calendar.MINUTE, -5);// 5分钟之前的时间
		Date beforeD = beforeTime.getTime();
		List<ReportPlamGames> allList = reportPlamGamesMapper.selectReportPlamGamesList( reportPlamGames );
		if (allList.size()==0&&reportPlamGames.getBegindate().equals( dateNowStr)){
			storage(dateNowStr,reportPlamGames);
		}
		if (allList.size()!=0&&reportPlamGames.getBegindate().equals( dateNowStr)){
			Date updateTime=allList.get(0).getUpdateTime();
			if (updateTime.getTime()<=beforeD.getTime()){
				storage(dateNowStr,reportPlamGames);
			}
		}
		return allList;

	}

	@Override
	public ReportPlamGames countBetData( ReportPlamGames reportPlamGames ) {

		return reportPlamGamesMapper.countBetData( reportPlamGames );
	}

	public  List<ReportPlamGames> storage( String dateNowStr,ReportPlamGames reportPlamGames ) {
		threadPoolTaskExecutor.execute(() -> {
			redisUtil.strSet( "admin-reportPlamGames", "0", Duration.ofMinutes( 1 ) );
			String result=reportPlamGamesMapper.calldataProrepPlamcom( dateNowStr );
			if ( StringUtils.hasText( result ) && redisUtil.exists( "admin-reportPlamGames" ) ) {
				redisUtil.strIncrement("admin-reportPlamGames");
			}
		});
		List<ReportPlamGames> allList1 = reportPlamGamesMapper.selectReportPlamGamesList( reportPlamGames );
		return allList1;
	}


}