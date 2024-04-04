package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.RedisCacheUtil;
import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.cache.VideoCacheUtil;
import com.qiqilm.server.admin.config.LiveCenterConfig;
import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveHostWageDay;
import com.qiqilm.server.admin.domain.LiveUser;
import com.qiqilm.server.admin.domain.LiveVideo;
import com.qiqilm.server.admin.domain.ServerLive;
import com.qiqilm.server.admin.domain.vo.HostPropDayVo;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.im.ImApi;
import com.qiqilm.server.admin.imserver.ImServerUtils;
import com.qiqilm.server.admin.mapper.*;
import com.qiqilm.server.admin.service.ILiveVideoService;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 直播Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
@Log4j2
public class LiveVideoServiceImpl implements ILiveVideoService {
    public static final String REDIS_KEY_DETECT_PLAY = "live:liveVideo:detectPlay";

    @Autowired
    private RedisUtil          redisUtil;
    @Autowired
    private ImApi              imApi;
    @Autowired
    private VideoCacheUtil     videoCacheUtil;
    @Autowired
    private SysConfigCacheUtil sysConfigCacheUtil;

    @Resource
    private ServerLiveMapper    serverLiveMapper;
    @Resource
    private LiveUserMapper      liveUserMapper;
    @Resource
    private LiveVideoMapper     liveVideoMapper;
    @Resource
    private LiveVideoPropMapper liveVideoPropMapper;

    @Resource
    private LiveHostWageDayMapper liveHostWageDayMapper;
    @Resource
    private HelpNoticeUtil        helpNoticeUtil;
    @Resource
    private ImServerUtils         imServerUtils;

    @Value( "${live.encrypt.privateKey}" )
    private String liveRsaPrivateKey;

    @Value( "${spring.profiles.active}" )
    private String profile;

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 查询直播
     *
     * @param id 直播ID
     *
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
     *
     * @return 直播
     */
    @Override
    public List<LiveVideo> selectLiveVideoList( LiveVideo liveVideo ) {
        List<LiveVideo> liveVideos   = liveVideoMapper.selectLiveVideoList( liveVideo );
        Set<Long>       liveVideoIds = liveVideos.stream().map( LiveVideo::getId ).collect( Collectors.toSet() );
        if ( !CollectionUtils.isEmpty( liveVideoIds ) ) {
            List<LiveUser> liveUsers = liveUserMapper.selectLiveUserInId( liveVideoIds );
            for ( LiveUser liveUser : liveUsers ) {
                for ( LiveVideo video : liveVideos ) {
                    if ( Objects.equals( video.getId(), liveUser.getId() ) ) {
                        video.setOpenPay( liveUser.getOpenPay().toString() );
                    }
                }
            }
        }

        Map<Object, Object> failMap = redisUtil.hGetAll( REDIS_KEY_DETECT_PLAY );
        liveVideos.forEach( video -> {
            failMap.forEach( ( key, value ) -> {
                if ( video.getId().toString().equals( key.toString() ) ) {
                    video.setLiveStatus( value.toString() );
                }
            } );
            if ( video.getLineStatus() == null ) {
                video.setLineStatus( 0 );
            }
        } );
        threadPoolTaskExecutor.execute( () -> this.detectPlay( liveVideos ) );
        return liveVideos;
    }

    public void detectPlay( List<LiveVideo> liveVideos ) {
        if ( CollectionUtils.isEmpty( liveVideos ) ) {
            return;
        }
        liveVideos.forEach( video -> {
            if ( StringUtils.isBlank( video.getLiveStatus() ) && !redisUtil.hExists( Constants.REDIS_KEY_DETECT_PLAY, video
                    .getId().toString() ) ) {
                if ( !HttpHelper.isConnServerByHttp( video.getPlayUrl() ) ) {
                    redisUtil.hSet( Constants.REDIS_KEY_DETECT_PLAY, video.getId().toString(), "0" );
                    video.setLiveStatus( "0" );
                } else {
                    redisUtil.hSet( Constants.REDIS_KEY_DETECT_PLAY, video.getId().toString(), "1" );
                    video.setLiveStatus( "1" );
                }
            }
        } );
    }

    @Override
    public boolean close( Long id, String cause ) {
        String why = "主播下播";
        switch ( cause ) {
        case "admin":
            why = "管理员关播";
            break;
        case "timeOut":
            why = "当前网络不稳定，请重新开播";
            break;
        case "origin":
            break;
        default:
            why = "直播源切换";
            break;
        }
        return close( id, false, why );
    }

    public boolean close( Long id, boolean isAborted, String why ) {
        //关闭房间
        LiveVideo updateVideo = new LiveVideo();
        Date      now         = new Date();
        updateVideo.setLiveIn( 0 );
        updateVideo.setIsAborted( isAborted );
        updateVideo.setEndDate( now );
        updateVideo.setConcatVideo( 0 );
        updateVideo.setId( id );
        if ( isAborted ) {
            Double monitorTimeLong = videoCacheUtil.getVideoMonitorTime( Integer.parseInt( "" + id ) );
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

        LiveUser updateUser = new LiveUser();
        updateUser.setId( id );
        // 更新主播直播印票
        BigDecimal voteNumber = videoCacheUtil.getVoteNumber( Integer.parseInt( "" + id ) );
        if ( voteNumber != null ) {
            updateUser.setTicket( voteNumber );
        }
        updateUser.setOpenPay( 0 );
        liveUserMapper.updateLiveUser( updateUser );

        liveVideoMapper.updateLiveVideo( updateVideo, LiveCenterConfig.me.getProfileDbLive() );

        try {
            this.processVideoSort();
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }

        videoCacheUtil.clearVideoMonitorTime( Integer.parseInt( "" + id ) );

        LiveVideo video = liveVideoMapper.selectLiveVideoById( id );

        LiveUser liveUser = liveUserMapper.selectLiveUserById( id );

        this.saveHostWageNote( liveUser, video );

        ServerLive serverLive = serverLiveMapper.selectServerLiveById( video.getPaiId() );
        if ( serverLive != null && serverLive.getCountNum() > 0 ) {
            serverLive.setCountNum( serverLive.getCountNum() - 1 );
            serverLiveMapper.updateServerLive( serverLive );
            //	videoStreamUtil.setServerLive( serverLive );
        }
        this.closeVideoIMNotify( video, isAborted, why );
        RedisCacheUtil.me.clear( id, LiveVideo.class );

        if ( !Objects.isNull( LiveCenterConfig.me.getLiveSubAgents() ) ) {
            for ( String liveSubAgent : LiveCenterConfig.me.getLiveSubAgents() ) {
                liveVideoMapper.updateLiveVideo( updateVideo, LiveCenterConfig.me.getLiveSubAgentDbLive( liveSubAgent ) );
            }
        }

        return false;
    }


    private void saveHostWageNote( LiveUser liveUser, LiveVideo video ) {

        String          dayTime       = LocalDate.now().toString();
        String          hostLiveDayId = dayTime.concat( "-" ).concat( String.valueOf( liveUser.getId() ) );
        LiveHostWageDay hostLiveDay   = liveHostWageDayMapper.selectLiveHostWageDayById( hostLiveDayId );
        if ( hostLiveDay == null ) {
            hostLiveDay = new LiveHostWageDay();
            hostLiveDay.setHostId( liveUser.getId() );
            hostLiveDay.setId( hostLiveDayId );
            hostLiveDay.setStartTime( DateFormatUtils.formate( new Date() ) );
            hostLiveDay.setEndTime( hostLiveDay.getStartTime() );
            hostLiveDay.setFamilyId( liveUser.getFamilyId() );
            hostLiveDay.setLiveTimeSec( 0 );
            hostLiveDay.setTimes( 1 );
            liveHostWageDayMapper.insertLiveHostWageDay( hostLiveDay, LiveCenterConfig.me.getProfileDbLive() );
        } else {
            LiveHostWageDay updateLiveDay = new LiveHostWageDay();
            updateLiveDay.setId( hostLiveDayId );
            updateLiveDay.setTicket( liveVideoPropMapper.sumHostPropDay( video.getUserId(), dayTime ) );
            liveHostWageDayMapper.updateLiveHostWageDay( updateLiveDay, LiveCenterConfig.me.getProfileDbLive() );
        }
    }

    private void closeVideoIMNotify( LiveVideo video, boolean isAborted, String why ) {
        if ( Strings.isNotBlank( video.getGroupId() ) && !isAborted ) {
            HashMap<String, Object> ext = new HashMap<>();
            ext.put( "type", 7 ); //0:普通消息;1:礼物;2:弹幕消息;3:主播退出;4:禁言;5:观众进入房间；6：观众退出房间；7:直播结束
            ext.put( "room_id", video.getId() ); //直播ID 也是room_id;只有与当前房间相同时，收到消息才响应
            if ( video.getMaxWatchNumber() == null ) {
                video.setMaxWatchNumber( 0L );
            }
            ext.put( "show_num", video.getMaxWatchNumber() );  //观看人数
            ext.put( "fonts_color", "" ); //字体颜色
            ext.put( "desc", why );  //弹幕消息;
            ext.put( "desc2", "直播结束" );  //弹幕消息;
            try {
                long time = System.currentTimeMillis();
                ext.put( "systemtime", time );
                String signData = video.getId().toString() + video.getMaxWatchNumber().toString() + why + time;
                log.warn( signData );
                ext.put( "userinfomat", RSACoder.signSha1Rsa( signData, liveRsaPrivateKey ) );

                String value = JsonUtil.object2Json( ext );

                log.warn( "关播通知：{}", value );

                //MessageType message = MessageType.setMsgEnmu(MessageEnum.TIMCustomElem).setData(JsonUtil.object2Json(ext));
                //imApi.sendGroupMessage(video.getGroupId(), message);
                imApi.sendSystemNotify( video.getGroupId(), value );
                imApi.sendSystemNotify( video.getGroupId(), value );
                imServerUtils.sendGroupMessage( String.valueOf( video.getId() ), ext );
            } catch ( Exception e ) {
                log.error( "房间号不存在或无法发送直播结束通知 - videoId:{};groupId:{}", video.getId(), video.getGroupId(), e );
            }
        }
    }

    @Override
    @Transactional( rollbackFor = Exception.class )
    public String livePay( Long room_id, Integer live_fee, Integer live_pay_type ) {
        LiveVideo video = liveVideoMapper.selectLiveVideoById( room_id );

        if ( Objects.isNull( live_fee ) || Objects.isNull( live_pay_type ) ) {
            throw new RuntimeException( "参数错误" );
        }

        int payMin = live_pay_type
                == 0 ? sysConfigCacheUtil.getConfInt( "live_pay_min" ) : sysConfigCacheUtil.getConfInt( "live_pay_scene_min" );
        //付费最高
        int payMax = live_pay_type
                == 0 ? sysConfigCacheUtil.getConfInt( "live_pay_max" ) : sysConfigCacheUtil.getConfInt( "live_pay_scene_max" );
        //付费最低

        String coinName = sysConfigCacheUtil.getConf( "diamond_name" );

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
                msg = "切换之按" + ( live_pay_type == 0 ? "时" : "场" ) + "收费;" + live_fee + coinName + "/每" + (
                        live_pay_type == 0 ? "分钟" : "场" );
            } else {
                updateVideo.setIsLivePay( video.getIsLivePay() );
                msg = "按" + ( live_pay_type == 0 ? "时" : "场" ) + "收费开启成功;" + live_fee + coinName + "/每" + (
                        live_pay_type == 0 ? "分钟" : "场" );
            }
            updateVideo.setLivePayType( live_pay_type );
            updateVideo.setLiveFee( live_fee );
            updateVideo.setLivePayTime( ( int ) ( System.currentTimeMillis() / 1000 ) );
            updateVideo.setCateId( 4 );// 设置主题ID为收费直播
            updateVideo.setIsLivePay( true );
            this.updateVideo( updateVideo );
            //im
            HashMap<String, Object> ext = new HashMap<>();
            ext.put( "type", live_pay_type == 0 ? 32 : 40 );
            ext.put( "room_id", room_id );
            ext.put( "live_fee", live_fee );
            try {
                long time = System.currentTimeMillis();
                ext.put( "systemtime", time );
                String signData = room_id.toString() + live_fee + time;
                ext.put( "userinfomat", RSACoder.signSha1Rsa( signData, liveRsaPrivateKey ) );
            } catch ( Exception e ) {
                e.printStackTrace();
            }

            //MessageType message = MessageType.setMsgEnmu(MessageEnum.TIMCustomElem).setData(JsonUtil.object2Json(ext));
            //imApi.sendGroupMessage(video.getGroupId(), message);
            String value = JsonUtil.object2Json( ext );
            imApi.sendSystemNotify( video.getGroupId(), value );
            imApi.sendSystemNotify( video.getGroupId(), value );
            imServerUtils.sendGroupMessage( String.valueOf( video.getId() ), ext );
            return msg;
        }
        throw new RuntimeException( "切换失败" );
    }

    @Override
    public void updateVideo( LiveVideo entity ) {
        int updateCount = liveVideoMapper.updateLiveVideo( entity, LiveCenterConfig.me.getProfileDbLive() );
        if ( updateCount > 0 ) {
            RedisCacheUtil.me.clear( entity.getId(), LiveVideo.class );
        }

        if ( !Objects.isNull( LiveCenterConfig.me.getLiveSubAgents() ) ) {
            for ( String liveSubAgent : LiveCenterConfig.me.getLiveSubAgents() ) {
                liveVideoMapper.updateLiveVideo( entity, LiveCenterConfig.me.getLiveSubAgentDbLive( liveSubAgent ) );
            }
        }
    }

    @Override
    public AjaxResult updateVideoSort( LiveVideo liveVideo ) {
        if ( !redisUtil.strSetIfAbsent( "admin:videoSort:" + liveVideo.getId(), "1", Duration.ofSeconds( 5 ) ) ) {
            return AjaxResult.error( "已有管理员正在设置此主播，请稍后重试" );
        }
        LiveVideo newLiveVideo = liveVideoMapper.selectLiveVideoSortById( liveVideo.getId(),
                LiveCenterConfig.me.getProfileDbLive() );
        if ( newLiveVideo.getLiveIn() == 0 ) {
            redisUtil.unlink( "admin:videoSort:" + liveVideo.getId() );
            return AjaxResult.error( "主播已下播，更新失败" );
        }
        if ( liveVideo.getSort() != null && liveVideo.getSort() != 9999999 ) {
            if ( liveVideo.getSort() <= 0 || liveVideo.getSort() >= 999 ) {
                redisUtil.unlink( "admin:videoSort:" + liveVideo.getId() );
                return AjaxResult.error( "固定位大小有误，请输入大于0小于100的整数值" );
            }
            if ( newLiveVideo.getIsRecommend() == 1 ) {
                redisUtil.unlink( "admin:videoSort:" + liveVideo.getId() );
                return AjaxResult.error( "当前主播是推荐位，无法设置固定位，请取消推荐位后重试" );
            }
            if ( liveVideo.getStick() != null && newLiveVideo.getStick() ) {
                redisUtil.unlink( "admin:videoSort:" + liveVideo.getId() );
                return AjaxResult.error( "当前主播是置底位，无法设置固定位，请取消置底位后重试" );
            }
            long count = liveVideoMapper.countLiveInSort( liveVideo.getSort() );
            if ( count > 0 ) {
                redisUtil.unlink( "admin:videoSort:" + liveVideo.getId() );
                return AjaxResult.error( "固定位{}已存在，请重新设置固定位值", liveVideo.getSort() );
            }
            liveVideo.setIsRecommend( 0L );
            liveVideo.setStick( false );
        }
        if ( liveVideo.getIsRecommend() != null && liveVideo.getIsRecommend() == 1 ) {
            if ( newLiveVideo.getSort() < 9999000 ) {
                redisUtil.unlink( "admin:videoSort:" + liveVideo.getId() );
                return AjaxResult.error( "当前主播是固定位，无法设置推荐位，请取消固定位后重试" );
            }
            if ( liveVideo.getStick() != null && newLiveVideo.getStick() ) {
                redisUtil.unlink( "admin:videoSort:" + liveVideo.getId() );
                return AjaxResult.error( "当前主播是置底位，无法设置推荐位，请取消置底位后重试" );
            }
        }
        if ( liveVideo.getStick() != null && liveVideo.getStick() ) {
            if ( newLiveVideo.getSort() < 9999000 ) {
                redisUtil.unlink( "admin:videoSort:" + liveVideo.getId() );
                return AjaxResult.error( "当前主播是固定位，无法设置置底位，请取消固定位后重试" );
            }
            if ( newLiveVideo.getIsRecommend() == 1 ) {
                redisUtil.unlink( "admin:videoSort:" + liveVideo.getId() );
                return AjaxResult.error( "当前主播是推荐位，无法设置置底位，请取消推荐位后重试" );
            }
        }
        int i = liveVideoMapper.updateLiveVideo( liveVideo, LiveCenterConfig.me.getProfileDbLive() );
        redisUtil.unlink( "admin:videoSort:" + liveVideo.getId() );
        if ( i > 0 ) {
            if ( liveVideo.getStick() == null ) {
                liveVideo.setStick( false );
            }
            if ( liveVideo.getIsRecommend() == null ) {
                liveVideo.setIsRecommend( 0L );
            }
            if ( ( liveVideo.getIsRecommend() == 1 || BooleanUtils.isTrue( liveVideo.getStick() ) ) && liveVideo.getSort() == null
                    && LiveCenterConfig.me.isLiveCenter() && !Objects.isNull( LiveCenterConfig.me.getLiveSubAgents() )
                    && !"7704".equals( LiveCenterConfig.me.getProfile() ) // 04不需要此功能
                    && !"7708".equals( LiveCenterConfig.me.getProfile() ) // 08不需要此功能
            ) {
                for ( String liveSubAgent : LiveCenterConfig.me.getLiveSubAgents() ) {
                    LiveVideo subLiveVideo = liveVideoMapper.selectLiveVideoSortById( liveVideo.getId(),
                            LiveCenterConfig.me.getLiveSubAgentDbLive( liveSubAgent ) );
                    if ( subLiveVideo != null && subLiveVideo.getSort() == 9999999 ) {
                        liveVideoMapper.updateLiveVideo( liveVideo, LiveCenterConfig.me.getLiveSubAgentDbLive( liveSubAgent ) );
                    }
                }
            }
            this.processVideoSort();
            LiveVideo liveVideo1 = liveVideoMapper.selectLiveVideoById( liveVideo.getId() );
            Long      sort       = liveVideo1.getSort();
            if ( sort != null && sort <= 20 ) {
                String msg     = sysConfigCacheUtil.getConf( "first_twenty_notice" );
                String groupId = liveVideo1.getGroupId();
                if ( StringUtils.isNotEmpty( msg ) && StringUtils.isNotEmpty( groupId ) ) {
                    helpNoticeUtil.sendMsg( msg, groupId, String.valueOf( liveVideo.getId() ) );
                }
            }
        }
        return AjaxResult.success( "更新成功" );
    }

    @Override
    public void processVideoSort() {
        List<LiveVideo> liveVideos = liveVideoMapper.selectLiveInVideoSort();
        if ( CollectionUtils.isEmpty( liveVideos ) ) {
            return;
        }

        // 固定位
        Map<Integer, Long> sortHostMap = new TreeMap<>();
        // 推荐位
        List<Long> recommendHostList = new ArrayList<>();
        // 正常位
        List<Long> normalHostList = new ArrayList<>();
        // 置底位
        List<Long> stickHostList = new ArrayList<>();

        liveVideos.forEach( liveVideo -> {
            if ( liveVideo.getSort() < 9999000 ) {
                sortHostMap.put( liveVideo.getSort().intValue(), liveVideo.getId() );
            } else if ( liveVideo.getIsRecommend() == 1 ) {
                recommendHostList.add( liveVideo.getId() );
            } else if ( liveVideo.getIsRecommend() == 0 && !liveVideo.getStick() ) {
                normalHostList.add( liveVideo.getId() );
            } else if ( liveVideo.getStick() ) {
                stickHostList.add( liveVideo.getId() );
            }
        } );

        List<Long> resultList = new ArrayList<>();
        for ( int i = 1; i <= liveVideos.size(); i++ ) {
            Long sortHostId = sortHostMap.get( i );
            if ( sortHostId != null ) {
                resultList.add( sortHostId );
                sortHostMap.remove( i );
            } else if ( !CollectionUtils.isEmpty( recommendHostList ) ) {
                // 加个锁  阻止定时器短时间内又乱序
                if ( !redisUtil.lock( "host:shuffle" + profile, 120 ) ) {
                    Collections.shuffle( recommendHostList );
                }
                resultList.add( recommendHostList.get( 0 ) );
                recommendHostList.remove( 0 );
            } else if ( !CollectionUtils.isEmpty( normalHostList ) ) {
                resultList.add( normalHostList.get( 0 ) );
                normalHostList.remove( 0 );
            }
        }

        if ( !CollectionUtils.isEmpty( sortHostMap ) ) {
            resultList.addAll( sortHostMap.values() );
        }
        if ( !CollectionUtils.isEmpty( stickHostList ) ) {
            resultList.addAll( stickHostList );
        }

        for ( int i = 0; i < resultList.size(); i++ ) {
            LiveVideo update = new LiveVideo();
            update.setId( resultList.get( i ) );
            update.setSortInit( i );
            liveVideoMapper.updateLiveVideo( update, LiveCenterConfig.me.getProfileDbLive() );
        }
    }

    @Override
    public List<LiveVideo> selectOnlineLiveGroups() {
        return liveVideoMapper.selectOnlineLiveGroups();
    }

    @Override
    public void updateNowLine() {
        for ( ServerLive serverLive : serverLiveMapper.selectServerLiveList( null ) ) {
            int num = liveVideoMapper.countLineCount( serverLive.getId() );
            if ( num != serverLive.getCountNum() ) {
                ServerLive update = new ServerLive();
                update.setCountNum( num );
                update.setId( serverLive.getId() );
                serverLiveMapper.updateServerLive( update );
            }
        }
    }

    @Override
    public void countHostGift() {
        long s = System.currentTimeMillis();
        log.info( "开始执行主播礼物计算,彩票投注" );
        String dayTime = LocalDate.now().plusDays( -1 ).toString();
        List<HostPropDayVo> propDayVos = liveVideoPropMapper.sumHostPropDayList( dayTime,
                LiveCenterConfig.me.getProfileDbMain(), LiveCenterConfig.me.getProfileDbLive() );
        log.info( "收礼物主播数：{}", propDayVos.size() );
        String begin = dayTime.concat( " 00:00:00" );
        String end   = dayTime.concat( " 23:59:59" );

        List<HostPropDayVo> lotteryDayVos = liveVideoPropMapper.sumHostLotteryDayList( begin, end,
                LiveCenterConfig.me.getProfileDbMain(), LiveCenterConfig.me.getProfileDbLottery() );
        log.info( "投注主播数：{}", lotteryDayVos.size() );


        Map<String, LiveHostWageDay> updateMap = new HashMap<>();

        for ( HostPropDayVo v : propDayVos ) {
            LiveHostWageDay updateLiveDay = new LiveHostWageDay();
            updateLiveDay.setId( dayTime.concat( "-" ).concat( String.valueOf( v.getHostId() ) ) );
            updateLiveDay.setTicket( v.getSumHostProp() );
            updateMap.put( updateLiveDay.getId(), updateLiveDay );
        }
        String id;
        for ( HostPropDayVo v : lotteryDayVos ) {
            id = dayTime.concat( "-" ).concat( String.valueOf( v.getHostId() ) );
            LiveHostWageDay updateLiveDay = updateMap.get( id );
            if ( updateLiveDay == null ) {
                updateLiveDay = new LiveHostWageDay();
                updateLiveDay.setId( id );
                updateMap.put( updateLiveDay.getId(), updateLiveDay );
            }
            updateLiveDay.setLotteryCost( v.getSumHostProp() );
        }

        log.error( JsonUtil.object2Json( updateMap ) );

        if ( !Objects.isNull( LiveCenterConfig.me.getLiveSubAgents() ) ) {
            for ( String liveSubAgent : LiveCenterConfig.me.getLiveSubAgents() ) {
                Map<String, LiveHostWageDay> updateLiveSubMap = new HashMap<>();
                propDayVos = liveVideoPropMapper.sumHostPropDayList( dayTime,
                        LiveCenterConfig.me.getLiveSubAgentDbMain( liveSubAgent ),
                        LiveCenterConfig.me.getLiveSubAgentDbLive( liveSubAgent ) );

                for ( HostPropDayVo v : propDayVos ) {
                    id = dayTime.concat( "-" ).concat( String.valueOf( v.getHostId() ) );
                    LiveHostWageDay updateLiveDay = updateLiveSubMap.get( id );
                    if ( updateLiveDay == null ) {
                        updateLiveDay = new LiveHostWageDay();
                        updateLiveDay.setId( id );
                        updateLiveSubMap.put( updateLiveDay.getId(), updateLiveDay );
                    }
                    updateLiveDay.setTicket( updateLiveDay.getTicket().add( v.getSumHostProp() ) );

                }
                log.error( liveSubAgent + "主播收礼物数：{}", propDayVos.size() );

                lotteryDayVos = liveVideoPropMapper.sumHostLotteryDayList( begin, end,
                        LiveCenterConfig.me.getLiveSubAgentDbMain( liveSubAgent ),
                        LiveCenterConfig.me.getLiveSubAgentDbLottery( liveSubAgent ) );
                for ( HostPropDayVo v : lotteryDayVos ) {
                    id = dayTime.concat( "-" ).concat( String.valueOf( v.getHostId() ) );
                    LiveHostWageDay updateLiveDay = updateLiveSubMap.get( id );
                    if ( updateLiveDay == null ) {
                        updateLiveDay = new LiveHostWageDay();
                        updateLiveDay.setId( id );
                        updateLiveSubMap.put( updateLiveDay.getId(), updateLiveDay );
                    }
                    updateLiveDay.setLotteryCost( updateLiveDay.getLotteryCost().add( v.getSumHostProp() ) );

                }
                log.error( liveSubAgent + "主播投注数：{}", lotteryDayVos.size() );
                for ( LiveHostWageDay updateLiveDay : updateLiveSubMap.values() ) {
                    LiveHostWageDay db = liveHostWageDayMapper.selectLiveHostWageDayById( updateLiveDay.getId() );
                    if ( db == null ) {
                        continue;
                    }
                    updateLiveDay.setHostId( db.getHostId() );
                    updateLiveDay.setStartTime( db.getStartTime() );
                    updateLiveDay.setEndTime( db.getEndTime() );
                    updateLiveDay.setFamilyId( db.getFamilyId() );
                    updateLiveDay.setLiveTimeSec( db.getLiveTimeSec() );
                    updateLiveDay.setTimes( db.getTimes() );

                    String liveSubAgentDbLive = LiveCenterConfig.me.getLiveSubAgentDbLive( liveSubAgent );

                    if ( liveHostWageDayMapper.countId( updateLiveDay.getId(), liveSubAgentDbLive ) > 0 ) {
                        liveHostWageDayMapper.updateLiveHostWageDay( updateLiveDay, liveSubAgentDbLive );
                    } else {
                        liveHostWageDayMapper.insertLiveHostWageDay( updateLiveDay, liveSubAgentDbLive );
                    }
                }
                for ( LiveHostWageDay v : updateLiveSubMap.values() ) {
                    if ( updateMap.containsKey( v.getId() ) ) {
                        LiveHostWageDay tem = updateMap.get( v.getId() );
                        tem.setTicket( tem.getTicket().add( v.getTicket() ) );
                        tem.setLotteryCost( tem.getLotteryCost().add( v.getLotteryCost() ) );
                        updateMap.put( v.getId(), tem );
                    } else {
                        updateMap.put( v.getId(), v );
                    }
                }
            }
        }

        log.error( JsonUtil.object2Json( updateMap ) );

        for ( LiveHostWageDay updateLiveDay : updateMap.values() ) {
            liveHostWageDayMapper.updateLiveHostWageDay( updateLiveDay, LiveCenterConfig.me.getProfileDbLive() );
        }


        log.info( "结束执行主播礼物计算,执行时间：{}ms", System.currentTimeMillis() - s );

    }

    @Override
    public LiveVideo liveInStatus( Long userId ) {
        return liveVideoMapper.liveInStatus( userId );
    }

    @Override
    public AjaxResult syncMainLiveSort() {
        if ( LiveCenterConfig.me.isLiveCenter() ) {
            return AjaxResult.error( "主台无需同步" );
        }

        // redis 加锁,不可以频繁同步
        if ( !redisUtil.adminLock( EnumLock.adminTask, "syncMainLiveSort", 60 ) ) {
            return AjaxResult.error(
                    "请勿频繁同步,需等待" + redisUtil.getExpire( Constants.ADMIN_LOCK.concat( EnumLock.adminTask.getKey() )
                                                                                     .concat( "syncMainLiveSort" ) ) + "秒" );
        }

        List<LiveVideo> liveVideos = liveVideoMapper.selectMainLiveInVideoList( LiveCenterConfig.me.getLiveCenterDbLive() );
        for ( LiveVideo liveVideo : liveVideos ) {
            if ( liveVideoMapper.countLiveVideo( liveVideo.getId() ) > 0 ) {
                liveVideoMapper.updateLiveVideoSort( liveVideo );
            } else {
                liveVideoMapper.insertLiveVideo( liveVideo, LiveCenterConfig.me.getProfileDbLive() );
            }
        }
        return AjaxResult.success();
    }
}
