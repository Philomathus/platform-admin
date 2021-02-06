package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.LiveCacheUtil;
import com.qiqilm.server.admin.cache.RedisCacheUtil;
import com.qiqilm.server.admin.domain.*;
import com.qiqilm.server.admin.im.ImApi;
import com.qiqilm.server.admin.im.MessageType;
import com.qiqilm.server.admin.mapper.*;
import com.qiqilm.server.admin.service.ILiveVideoService;
import com.qiqilm.server.admin.utils.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 直播Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class LiveVideoServiceImpl implements ILiveVideoService {
	@Resource
	private LiveVideoMapper liveVideoMapper;

	public static final String                 REDIS_KEY_DETECT_PLAY = "live:liveVideo:detectPlay";

	@Autowired
	private             RedisUtil              redisUtil;

	@Autowired
	private LiveCacheUtil global;

	@Autowired
	private ImApi imApi;

	@Autowired
	private VideoCacheUtil videoCacheUtil;

	@Resource
	private LiveHostWageNoteMapper liveHostWageNoteMapper;

	@Resource
	private ServerLiveMapper serverLiveMapper;

	@Resource
	private LiveUserMapper     liveUserMapper;

	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	@Resource
	private LotteryBetMapper lotteryBetMapper;

	@Resource
	private LivePayLogMapper livePayLogMapper;

	/**
	 * 查询直播
	 *
	 * @param id 直播ID
	 * @return 直播
	 */
	@Override
	public LiveVideo selectLiveVideoById( Long id ) {
		return liveVideoMapper.selectLiveVideoById( id );
	}

	/**
	 * 查询直播列表
	 *
	 * @param liveVideo 直播
	 * @return 直播
	 */
	@Override
	public List<LiveVideo> selectLiveVideoList( LiveVideo liveVideo ) {
		List<LiveVideo>     liveVideos = liveVideoMapper.selectLiveVideoList( liveVideo );
		Map<Object, Object> failMap    = redisUtil.hGetAll( REDIS_KEY_DETECT_PLAY );
		if ( !CollectionUtils.isEmpty( failMap ) ) {
			liveVideos.forEach( video -> {
				failMap.forEach( ( key, value ) -> {
					if ( video.getId().toString().equals( key.toString() ) ) {
						video.setLiveStatus( value.toString() );
					}
				} );
			} );
		}
		return liveVideos;
	}

	/**
	 * 新增直播
	 *
	 * @param liveVideo 直播
	 * @return 结果
	 */
	@Override
	public int insertLiveVideo( LiveVideo liveVideo ) {
		//liveVideo.setCreateTime( DateUtils.getNowDate() );
		return liveVideoMapper.insertLiveVideo( liveVideo );
	}

	/**
	 * 修改直播
	 *
	 * @param liveVideo 直播
	 * @return 结果
	 */
	@Override
	public int updateLiveVideo( LiveVideo liveVideo ) {
		return liveVideoMapper.updateLiveVideo( liveVideo );
	}

	/**
	 * 批量删除直播
	 *
	 * @param ids 需要删除的直播ID
	 * @return 结果
	 */
	@Override
	public int deleteLiveVideoByIds( Long[] ids ) {
		return liveVideoMapper.deleteLiveVideoByIds( ids );
	}

	/**
	 * 删除直播信息
	 *
	 * @param id 直播ID
	 * @return 结果
	 */
	@Override
	public int deleteLiveVideoById( Long id ) {
		return liveVideoMapper.deleteLiveVideoById( id );
	}

	@Override
	public boolean close( Long id, String cause ) {
		LiveVideo video = liveVideoMapper.selectLiveVideoById( id );
		if ( "admin".equals( cause ) ) {
			//通知主播退出
			HashMap<String, Object> ext =new HashMap<>();
			ext.put( "type", 17 );
			ext.put( "room_id", id );
			ext.put( "desc", "违规直播，立即关闭直播" );
			MessageType message = MessageType.TIMCustomElem.setData( JsonUtil.object2Json( ext ) );
			try {
				imApi.sendMessage( global.getConf( "tim_identifier" ), video.getUserId().toString(), message );
			} catch ( Exception e ) {
				//log.error( this.toString() + "(m)close", e );
			}
		} else if ( "timeOut".equals( cause ) ) {
			//log.error( "直播心跳超时====>room_id" + id );
			//通知主播退出
			HashMap<String, Object> ext = new HashMap<>();
			ext.put( "type", 17 );
			ext.put( "room_id", id );
			ext.put( "desc", "违规直播，立即关闭直播" );
			MessageType message = MessageType.TIMCustomElem.setData( JsonUtil.object2Json( ext ) );
			try {
				imApi.sendMessage( global.getConf( "tim_identifier" ), video.getUserId().toString(), message );
			} catch ( Exception e ) {
				//log.error( this.toString() + "(m)close", e );
			}
		} else if ( "origin".equals( cause ) ) {
			//log.info( "直播源切换,关闭所有直播。当前正在关闭====>room_id" + id );
			//通知主播退出
			HashMap<String, Object> ext = new HashMap<>();
			ext.put( "type", 17 );
			ext.put( "room_id", id );
			ext.put( "desc", "直播源切换，立即关闭直播" );
			MessageType message = MessageType.TIMCustomElem.setData( JsonUtil.object2Json( ext ) );
			try {
				imApi.sendMessage( global.getConf( "tim_identifier" ), video.getUserId().toString(), message );
			} catch ( Exception e ) {
				//log.error( this.toString() + "(m)close", e );
			}
		}
		return close( id, false );
	}

	public boolean close( Long id, boolean isAborted ) {
		//关闭房间
		LiveVideo updateVideo = new LiveVideo();
		Date      now         = new Date();
		updateVideo.setLiveIn( 0 );
		updateVideo.setIsAborted( isAborted );
		updateVideo.setEndDate( now );
		updateVideo.setVideoVid( "" );
		updateVideo.setIsDelVod( true );
		updateVideo.setIsDelete( true );
		updateVideo.setId( id );
		if ( isAborted ) {
			Double monitorTimeLong = videoCacheUtil.getVideoMonitorTime(Integer.parseInt( ""+id) );
			if ( monitorTimeLong != null ) {
				updateVideo.setEndTime( new Date( monitorTimeLong.longValue() ) );
			} else {
				updateVideo.setEndTime( now );
			}
		} else {
			updateVideo.setEndTime( now );
		}

		// 设置在Redis中缓存的值
		videoCacheUtil.setVideoCache( updateVideo );

		// 更新主播直播印票
		BigDecimal voteNumber = videoCacheUtil.getVoteNumber( Integer.parseInt( ""+id)  );
		if ( voteNumber != null ) {
			LiveUser updateUser = new LiveUser();
			updateUser.setTicket( voteNumber );
			updateUser.setId( id );
			liveUserMapper.updateLiveUser( updateUser );
		}

		liveVideoMapper.updateLiveVideo( updateVideo );

		videoCacheUtil.clearVideoMonitorTime( Integer.parseInt( "" +id) );

		/*//连麦表更新
		LiveVideoLianmai lianmai = new LiveVideoLianmai();
		lianmai.setStopTime( ( int ) ( System.currentTimeMillis() / 1000 ) );
		ReqLiveVideoLianMai reqLiveVideoLianMai  = new ReqLiveVideoLianMai();
		ReqLiveVideoLianMai reqLiveVideoLianMai1 = reqLiveVideoLianMai;
		reqLiveVideoLianMai1.setVideo_id( id );
		reqLiveVideoLianMai1.setStop_time( 0 );
		lianmaiService.update( lianmai, reqLiveVideoLianMai1.getQueryWrapper() );*/

		LiveVideo video = liveVideoMapper.selectLiveVideoById( id );

		LiveUser liveUser = liveUserMapper.selectLiveUserById( video.getUserId() );

		this.saveHostWageNote( liveUser, video, isAborted );

		if ( !isAborted && video.getIsLivePay() ) {
			// 收费房间主动下播关闭会员收费记录，以便以后开播重新记录

			LivePayLog log = new LivePayLog();
			log.setIsHistory( true );
			log.setVideoId( id );
			int i = livePayLogMapper.updateToHistory( log );
		}

		ServerLive serverLive = serverLiveMapper.selectServerLiveById( video.getPaiId() );
		if ( serverLive != null && serverLive.getCountNum() > 0 ) {
			serverLive.setCountNum( serverLive.getCountNum() - 1 );
			serverLiveMapper.updateServerLive( serverLive );
		//	videoStreamUtil.setServerLive( serverLive );
		}

		this.closeVideoIMNotify( video, isAborted );

		RedisCacheUtil.me.clear( id, this.getClass() );
		return false;
	}

	private void saveHostWageNote( LiveUser liveUser, LiveVideo video, boolean isAborted ) {
		LiveHostWageNote oldHostWageNote = liveHostWageNoteMapper.beforeNote( video.getUserId() );
		String           videoBeginTime  = DateFormatUtils.formate( video.getBeginTime() );
		String           endTime         = DateFormatUtils.formate( video.getEndTime() );
		long              liveTimeSec     = 0;
		try {
			liveTimeSec = ( long ) ( DateFormatUtils.getIntervalTime( video.getBeginTime(),
					video.getMonitorTime() ) / 1000 );
		} catch ( Exception e ) {
			//log.error( e.getMessage(), e );
		}
		String remark = isAborted ? "异常下播" : "主动下播";

		if ( oldHostWageNote != null && oldHostWageNote.getStartTime().equals( videoBeginTime ) ) {
			LiveHostWageNote updateHostWageNote = new LiveHostWageNote();
			updateHostWageNote.setId( oldHostWageNote.getId() );
			updateHostWageNote.setEndTime( endTime );
			updateHostWageNote.setRemark( remark );
			updateHostWageNote.setLiveTimeSec( liveTimeSec );
			if ( oldHostWageNote.getBeforeTotalTicket() != null ) {
				updateHostWageNote.setTicket( video.getVoteNumber().subtract( oldHostWageNote.getBeforeTotalTicket() )
						.add( oldHostWageNote.getTicket() ) );
			}
			updateHostWageNote.setBeforeTotalTicket( video.getVoteNumber() );

			Map<String, Object> costMap = lotteryBetMapper.sumBatCostPrize( video.getUserId(),
					oldHostWageNote.getStartTime(), updateHostWageNote.getEndTime() );
			if ( !CollectionUtils.isEmpty( costMap ) ) {
				updateHostWageNote.setCpCost( new BigDecimal( costMap.getOrDefault( "cost", "0" ).toString() ) );
			} else {
				updateHostWageNote.setCpCost( BigDecimal.ZERO );
			}
			updateHostWageNote.setCpPrize( BigDecimal.ZERO );

			liveHostWageNoteMapper.updateLiveHostWageNote( updateHostWageNote );
		} else {
			LiveHostWageNote newHostWageNote = new LiveHostWageNote();
			newHostWageNote.setFamilyId( liveUser.getFamilyId() == null ? 0 : liveUser.getFamilyId() );
			newHostWageNote.setHostId( video.getUserId() );
			newHostWageNote.setEndTime( endTime );
			newHostWageNote.setRemark( remark );
			newHostWageNote.setBeforeTotalTicket( video.getVoteNumber() );

			//log.warn( "主播:{} redis礼物数:{} beforeTotalTicket:{}", video.getUserId(), video.getVoteNumber(),
			//		( oldHostWageNote == null ? 0 : oldHostWageNote.getBeforeTotalTicket() ) );
			if ( oldHostWageNote != null ) {
				//  直播间礼物数减去上次统计礼物数，就是本次礼物数
				newHostWageNote.setTicket( video.getVoteNumber().subtract( oldHostWageNote.getBeforeTotalTicket() ).setScale( 2,
						BigDecimal.ROUND_HALF_UP ) );
			} else {
				newHostWageNote.setTicket( video.getVoteNumber() );
			}
			newHostWageNote.setLiveTimeSec( liveTimeSec );
			newHostWageNote.setStartTime( videoBeginTime );

			newHostWageNote.setCreateTimes(DateFormatUtils.formate(new Date()));

			Map<String, Object> costMap = lotteryBetMapper.sumBatCostPrize( video.getUserId(),
					newHostWageNote.getStartTime(), newHostWageNote.getEndTime() );
			if ( !CollectionUtils.isEmpty( costMap ) ) {
				newHostWageNote.setCpCost( new BigDecimal( costMap.getOrDefault( "cost", "0" ).toString() ) );
			} else {
				newHostWageNote.setCpCost( BigDecimal.ZERO );
			}
			newHostWageNote.setCpPrize( BigDecimal.ZERO );

			liveHostWageNoteMapper.insertLiveHostWageNote( newHostWageNote );
		}
	}
	private void closeVideoIMNotify( LiveVideo video, boolean isAborted ) {
		if ( Strings.isNotBlank( video.getGroupId() ) && !isAborted ) {
			threadPoolTaskExecutor.execute( () -> {
				HashMap<String, Object> ext = new HashMap<>();
				ext.put( "type", 7 ); //0:普通消息;1:礼物;2:弹幕消息;3:主播退出;4:禁言;5:观众进入房间；6：观众退出房间；7:直播结束
				ext.put( "room_id", video.getId() ); //直播ID 也是room_id;只有与当前房间相同时，收到消息才响应
				ext.put( "show_num", video.getMaxWatchNumber() );  //观看人数
				ext.put( "fonts_color", "" ); //字体颜色
				ext.put( "desc", "直播结束" );  //弹幕消息;
				ext.put( "desc2", "直播结束" );  //弹幕消息;
				MessageType message = MessageType.TIMCustomElem.setData( JsonUtil.object2Json( ext ) );

				ext = new HashMap<>();
				ext.put( "type", 18 ); //18：直播结 束（全体推送的，用于更新用户列表状态）
				ext.put( "room_id", video.getId() );//直播ID 也是room_id;
				try {
					imApi.sendGroupMessage( video.getGroupId(), video.getUserId().toString(), message );
				} catch ( Exception e ) {
					//log.error( "房间号不存在或无法发送直播结束通知 - videoId:{};groupId:{}", video.getId(), video.getGroupId(), e );
				}
			} );
		}
	}

	@Override
	public String livePay( Long room_id, Integer live_fee, Integer live_pay_type ) {
		LiveVideo video = liveVideoMapper.selectLiveVideoById( room_id );

		if ( Objects.isNull( live_fee ) || Objects.isNull( live_pay_type ) ) {
			throw new RuntimeException( "参数错误" );
		}

		int payMin = live_pay_type == 0 ? global.getConfInt( "live_pay_min" ) : global.getConfInt( "live_pay_scene_min" );
		//付费最高
		int payMax = live_pay_type == 0 ? global.getConfInt( "live_pay_max" ) : global.getConfInt( "live_pay_scene_max" );
		//付费最低

		String coinName = global.getConf( "diamond_name" );

		if ( payMin != 0 && live_fee < payMin ) {
			throw new RuntimeException( "按" + ( live_pay_type == 0 ? "时" : "场" ) + "收费不能低于" + payMin + coinName );
		}

		if ( payMax != 0 && live_fee > payMax ) {
			throw new RuntimeException( "按" + ( live_pay_type == 0 ? "时" : "场" ) + "收费不能高于" + payMax + coinName );
		}

		String msg = "";
		if ( Objects.nonNull( room_id ) && live_fee > 0 ) {
			LiveVideo updateVideo = new LiveVideo();
			updateVideo.setId( video.getId() );

			Boolean isLivePay = video.getIsLivePay();
			if ( isLivePay ) {
				updateVideo.setLivePayTime( ( int ) ( System.currentTimeMillis() / 1000 ) );
				msg = "切换之按" + ( live_pay_type == 0 ? "时" : "场" ) + "收费;" + live_fee + coinName
						+ "/每" + ( live_pay_type == 0 ? "分钟" : "场" );
			} else {
				updateVideo.setIsLivePay( video.getIsLivePay() );
				msg = "按" + ( live_pay_type == 0 ? "时" : "场" ) + "收费开启成功;" + live_fee + coinName
						+ "/每" + ( live_pay_type == 0 ? "分钟" : "场" );
			}
			updateVideo.setLivePayType( live_pay_type );
			updateVideo.setLiveFee( live_fee );
			updateVideo.setLivePayTime( ( int ) ( System.currentTimeMillis() / 1000 ) );
			updateVideo.setCateId( 4L );// 设置主题ID为收费直播
			liveVideoMapper.updateLiveVideo( updateVideo );
			//im
			HashMap<String, Object> ext = new HashMap<>();
			ext.put( "type", live_pay_type == 0 ? 32 : 40 );
			ext.put( "room_id", room_id );
			ext.put( "live_fee", live_fee );
			imApi.sendGroupMessage( video.getGroupId(), room_id.toString(),
					MessageType.TIMCustomElem.setData( JsonUtil.object2Json( ext ) ) );
			return msg;
		}
		throw new RuntimeException( "切换失败" );
	}
}
