package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.*;
import com.qiqilm.server.admin.config.LiveCenterConfig;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.*;
import com.qiqilm.server.admin.domain.req.ReqLotteryBat;
import com.qiqilm.server.admin.domain.rsp.RspLotteryBet;
import com.qiqilm.server.admin.domain.vo.PageVO;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.im.GroupType;
import com.qiqilm.server.admin.im.ImApi;
import com.qiqilm.server.admin.im.vo.api.ImInfo;
import com.qiqilm.server.admin.mapper.*;
import com.qiqilm.server.admin.service.ILiveUserService;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 主播用户信息Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Log4j2
@Service
public class LiveUserServiceImpl implements ILiveUserService {
    @Resource
    private LiveUserMapper        liveUserMapper;
    @Resource
    private LiveFamilyMapper      liveFamilyMapper;
    @Resource
    private ConfigDomainCacheUtil configDomainCacheUtil;
    @Resource
    private LiveVideoMapper       liveVideoMapper;
    @Resource
    private ImApi                 imApi;
    @Resource
    private LiveFamilyJoinMapper  liveFamilyJoinMapper;
    @Resource
    private VideoCacheUtil        videoCacheUtil;
    @Resource
    private LiveCacheUtil         liveCacheUtil;
    @Resource
    private BankListMapper        bankListMapper;

    @Resource
    private SysConfigCacheUtil sysConfigCacheUtil;

    /**
     * 查询主播用户信息
     *
     * @param id 主播用户信息ID
     * @return 主播用户信息
     */
    @Override
    public LiveUser selectLiveUserById( Long id ) {
        LiveUser liveUser = liveUserMapper.selectLiveUserById( id );
        if ( liveUser != null ) {
            if ( StringUtils.isNotBlank( liveUser.getMobile() ) ) {
                liveUser.setMobile( new StringBuilder( liveUser.getMobile() ).replace( 3, 7, "****" ).toString() );
            }
            String domainValue = configDomainCacheUtil.getValue( "domain.oss" );
            if ( StringUtils.isNotBlank( liveUser.getIdentifyHoldImage() ) && !liveUser.getIdentifyHoldImage()
                    .startsWith( "http" ) ) {
                liveUser.setIdentifyHoldImage( domainValue + liveUser.getIdentifyHoldImage() );
            }
            if ( StringUtils.isNotBlank( liveUser.getIdentifyNagativeImage() ) && !liveUser.getIdentifyNagativeImage()
                    .startsWith( "http" ) ) {
                liveUser.setIdentifyNagativeImage( domainValue + liveUser.getIdentifyNagativeImage() );
            }
            if ( StringUtils.isNotBlank( liveUser.getIdentifyPositiveImage() ) && !liveUser.getIdentifyPositiveImage()
                    .startsWith( "http" ) ) {
                liveUser.setIdentifyPositiveImage( domainValue + liveUser.getIdentifyPositiveImage() );
            }
        }
        return liveUser;
    }

    /**
     * 查询主播用户信息列表
     *
     * @param liveUser 主播用户信息
     * @return 主播用户信息
     */
    @Override
    public List<LiveUser> selectLiveUserList( LiveUser liveUser ) {
        List<LiveUser> liveUsers = liveUserMapper.selectLiveUserList( liveUser );

        String listHostList = sysConfigCacheUtil.getConf( "test_host_list" );

        liveUsers.forEach( user -> {

            boolean phone = listHostList.contains( user.getMobile() );
            user.setVirtualAnchor( phone ? 1 : 0 );

            if ( StringUtils.isNotBlank( user.getMobile() ) ) {
                user.setMobile( new StringBuilder( user.getMobile() ).replace( 3, 7, "****" ).toString() );
            }
        } );
        return liveUsers;
    }

    /**
     * 修改主播用户信息
     *
     * @param liveUser 主播用户信息
     * @return 结果
     */
    @Override
    public int updateLiveUser( LiveUser liveUser ) {
        liveUser.setUpdateTime( DateUtils.getNowDate() );
        int i = liveUserMapper.updateLiveUser( liveUser );
        if ( i > 0 ) {
            RedisCacheUtil.me.clear( liveUser.getId(), LiveUser.class );
            if ( liveUser.getIsBan() != null && liveUser.getIsBan() == 1 ) {
                liveCacheUtil.delHostToken( liveUser.getId() );
            }
        }
        return i;
    }

    @Override
    public AjaxResult updateFamilyID( Long familyID, Collection<String> userIds ) {
        if ( familyID != 0 ) {
            LiveFamily liveFamily = liveFamilyMapper.selectLiveFamilyById( familyID );
            if ( liveFamily == null ) {
                return AjaxResult.error( "家族不存在，请检查家族ID" );
            }
            if ( liveFamily.getStatus() != 1 ) {
                return AjaxResult.error( "家族状态异常,请检查" );
            }
        }
        for ( String s : userIds ) {
            long userId = Long.parseLong( s );
            if ( userId < 0 ) {
                return AjaxResult.error( "虚拟主播无法加入家族" );
            }
            LiveUser liveUser = liveUserMapper.selectLiveUserById( userId );
            if ( liveUser.getFamilyId() != 0 ) {
                return AjaxResult.error( "主播" + liveUser.getNickName() + "已有家族,无法加入家族" );
            }
        }
        for ( String s : userIds ) {
            long userId      = Long.parseLong( s );
            int  oldFamilyId = liveUserMapper.getFamilyId( userId );
            int  i           = liveUserMapper.updateFamilyID( familyID, userId );
            int  num         = liveUserMapper.getNumFamily( oldFamilyId );
            liveFamilyMapper.updateFamilyID( num, oldFamilyId );
            if ( familyID != 0 ) {
                int newnum = liveUserMapper.getNumFamily( familyID.intValue() );
                liveFamilyMapper.updateFamilyID( newnum, familyID.intValue() );
            }
            RedisCacheUtil.me.clear( userId, LiveUser.class );
        }
        return AjaxResult.success();
    }

    @Override
    public AjaxResult updateTicket( BigDecimal ticket, Long userId ) {
        liveUserMapper.updateTicket( ticket, userId );
        return AjaxResult.success();
    }

    @Override
    public List<RspLotteryBet> selectAnchorAward( ReqLotteryBat req ) {
        return liveUserMapper.selectAnchorAward( req );
    }

    @Override
    public AjaxResult insertLiveUser( LiveUser liveUser ) {
        if ( ValidatorUtil.isNumber11( liveUser.getMobile() ) ) {
            //	    查询手机号是否存在
            List<LiveUser> list = liveUserMapper.selectLiveUsersByMobile( liveUser.getMobile() );
            if ( list.isEmpty() ) {
                Long firstId = liveUserMapper.selectFirstId();
                if ( firstId == null || firstId > 0 ) {
                    firstId = -2L;
                } else {
                    firstId += -1;
                }
                liveUser.setId( firstId );
                liveUser.setCreateTime( new Date() );
                liveUser.setUpdateTime( liveUser.getCreateTime() );
                liveUser.setRoboter( 1 );
                liveUserMapper.insertLiveUser( liveUser );
                return AjaxResult.success( "添加成功" );
            } else {
                return AjaxResult.error( "手机号已存在" );
            }
        } else {
            return AjaxResult.error( "手机号格式错误" );
        }
    }

    public void imReg( LiveUser hostInfo ) {
        boolean regOk = false;
        if ( hostInfo.getExpiryAfter() == null || hostInfo.getExpiryAfter() < 0 ) {
            regOk = imApi.register( ImInfo.of( String.valueOf( hostInfo.getId() ) ) );
            if ( !regOk ) {
                log.error( "主播第一次注册IM失败hostId:{}", hostInfo.getId() );
                regOk = imApi.register( ImInfo.of( String.valueOf( hostInfo.getId() ) ) );
            }
            if ( !regOk ) {
                log.error( "主播第二次注册IM失败hostId:{}", hostInfo.getId() );
            }
            if ( regOk ) {//更新注册IM标识
                LiveUser update = new LiveUser();
                update.setId( hostInfo.getId() );
                update.setExpiryAfter( 1L );
                liveUserMapper.updateLiveUser( update );

                RedisCacheUtil.me.clear( hostInfo.getId(), LiveUser.class );
            }
        }
    }

    /**
     * 开播
     */
    @Override
    public AjaxResult openLive( Map map ) throws Exception {
        Integer id         = ( Integer ) map.get( "id" );
        String  title      = ( String ) map.get( "title" );
        String  flv        = ( String ) map.get( "flv" );
        String  liveImage  = "";
        Object  liveImage1 = map.get( "liveImage" );
        if ( liveImage1 != null ) {
            liveImage = ( String ) map.get( "liveImage" );
        }
        Integer   lotteryId   = ( Integer ) map.get( "lotteryId" );
        String    lotteryName = ( String ) map.get( "lotteryName" );
        LiveVideo liveVideo   = liveVideoMapper.selectLiveVideoById( id.longValue() );
        log.error( "虚拟主播开播map:{}", JsonUtil.object2Json( map ) );
        LiveUser hostInfo = liveUserMapper.selectLiveUserById( id.longValue() );
        imReg( hostInfo );

        if ( liveVideo != null ) {
            //修改
            liveVideo.setLiveIn( 1 );
            liveVideo.setBeginTime( new Date() );
            liveVideo.setEndTime( null );
            liveVideo.setEndDate( null );
            liveVideo.setTitle( title );
            liveVideo.setNPlayFlv( AesUtil.aesEncrypt( flv, "qwertyui12345678" ) );
            setIms( liveVideo, id, title );
            liveVideo.setCreateType( true );
            liveVideo.setLiveImage( liveImage );
            liveVideo.setHeadImage( hostInfo.getHeadImage() );
            liveVideo.setHostName( hostInfo.getNickName() );
            liveVideo.setNewPlayFlv( flv );
            liveVideo.setPlayUrl( flv );
            if ( lotteryId != null ) {
                liveVideo.setLotteryId( lotteryId );
                liveVideo.setLotteryName( lotteryName );
            } else {
                liveVideo.setLotteryId( 1002 );
                liveVideo.setLotteryName( "一分快三" );
            }
            liveVideoMapper.updateLiveVideo2( liveVideo );
        } else {
            //新增
            liveVideo = new LiveVideo();
            liveVideo.setId( id.longValue() );
            liveVideo.setLiveIn( 1 );
            liveVideo.setUserId( id );
            liveVideo.setBeginTime( new Date() );
            liveVideo.setEndTime( null );
            liveVideo.setHostName( hostInfo.getNickName() );
            liveVideo.setCateId( 2 );
            liveVideo.setEndDate( null );
            liveVideo.setCreateType( true );
            liveVideo.setTitle( title );
            liveVideo.setPaiId( -1L );
            liveVideo.setLiveImage( liveImage );
            liveVideo.setHeadImage( hostInfo.getHeadImage() );
            liveVideo.setNewPlayFlv( flv );
            liveVideo.setPlayUrl( flv );
            if ( lotteryId != null ) {
                liveVideo.setLotteryId( lotteryId );
                liveVideo.setLotteryName( lotteryName );
            } else {
                liveVideo.setLotteryId( 1002 );
                liveVideo.setLotteryName( "一分快三" );
            }
            setIms( liveVideo, id, title );
            liveVideo.setNPlayFlv( AesUtil.aesEncrypt( flv, "qwertyui12345678" ) );
            liveVideoMapper.insertLiveVideo( liveVideo, LiveCenterConfig.me.getProfileDbLive() );
        }
        RedisCacheUtil.me.clear( id, LiveVideo.class );
        return null;
    }

    /**
     * 设置ims
     *
     * @param liveVideo 视频直播
     * @param id        id
     * @param title     标题
     */
    private void setIms( LiveVideo liveVideo, Object id, String title ) {
        if ( !org.springframework.util.StringUtils.hasText( liveVideo.getGroupId() ) ) {
            //创建 im 聊天群
            String groupId = imApi.createGroup( null, GroupType.AV_CHART_ROOM, String.valueOf( liveVideo.getUserId() ) );
            if ( groupId == null ) {
                throw new BusinessException( "创建直播失败,请联系客服" );
            }
            log.info( "主播调用开播接口 - 开始创建群组 - userId:{};groupId:{}", id, groupId );
            liveVideo.setGroupId( groupId );
        } else {
            //im 连接测试
            try {
                imApi.getGroupUser( liveVideo.getGroupId(), PageVO.ofPage( 1, 1 ) );
            } catch ( Exception e ) {
                log.error( "主播调用开播接口 - 测试群组失败 - userId:{};groupId:{}", id, liveVideo.getGroupId(), e );
                //创建 im 聊天群
                String groupId = imApi.createGroup( null, GroupType.AV_CHART_ROOM,
                        String.valueOf( liveVideo.getUserId() ) );
                log.info( "主播调用开播接口 - 开始创建群组 - userId:{};groupId:{}", id, groupId );
                liveVideo.setGroupId( groupId );
            }
        }

        if ( liveVideo.getGroupId() != null ) {
            videoCacheUtil.putHostGroupId( liveVideo.getUserId(), liveVideo.getGroupId() );
        }
    }


    /**
     * 接近生活
     *
     * @param map 地图
     * @return {@link AjaxResult}
     */
    @Override
    public AjaxResult closeLive( Map map ) {
        LiveVideo liveVideo = new LiveVideo();
        liveVideo.setUserId( ( Integer ) map.get( "id" ) );
        List<LiveVideo> liveVideos = liveVideoMapper.selectLiveVideoList2( liveVideo );
        if ( !liveVideos.isEmpty() ) {
            liveVideo = liveVideos.get( 0 );
            liveVideo.setEndDate( new Date() );
            liveVideo.setEndTime( new Date() );
            liveVideo.setLiveIn( 0 );
            liveVideoMapper.updateLiveVideo( liveVideo, LiveCenterConfig.me.getProfileDbLive() );

            RedisCacheUtil.me.clear( liveVideo.getId(), LiveVideo.class );
            return AjaxResult.success( "关播成功" );
        } else {
            return AjaxResult.error( "直播不存在" );
        }
    }

    @Override
    public AjaxResult updateMobile( String newMobile, String oldMobile, String id ) {
        //校验旧手机号
        LiveUser liveUser = liveUserMapper.selectLiveUserById( Long.parseLong( id ) );
        //判断手机号是否存在
        Integer count = liveUserMapper.checkMobile( newMobile );
        if ( count == 0 ) {
            liveUser.setMobile( newMobile );
            liveUserMapper.updateLiveUser( liveUser );

            RedisCacheUtil.me.clear( id, LiveUser.class );
            return AjaxResult.success( "手机号修改成功" );
        } else {
            return AjaxResult.error( "手机号已存在" );
        }

    }

    @Override
    public List<LiveUser> selectLiveUserBankById( Integer userId ) {
        return liveUserMapper.selectLiveUserBankById( userId );
    }

    @Override
    public AjaxResult updateLiveUserBank( LiveUser liveUser ) {
        BankList bankList = bankListMapper.selectBankListByName( liveUser.getBankName() );
        if ( bankList == null ) {
            return AjaxResult.error( 100, "银行卡名称错误！" );
        }
        liveUser.setBankTypeId( bankList.getId() );
        liveUserMapper.updateLiveUserBank( liveUser );
        RedisCacheUtil.me.clear( liveUser.getId(), LiveUser.class );
        return AjaxResult.success();
    }

    @Override
    public int delLiveUserBankById( String bankAccount ) {
        return liveUserMapper.delLiveUserBankById( bankAccount );
    }

    /**
     * 踢出主播
     *
     * @param userIds
     * @return success
     * @desc 1：要判断主播是否在直播 live_video live_in字段
     * 2：要更新live_user 家族id清空 。。。
     * 3：家族成员要减少
     * 4: 家族成员配置表要删除改主播的信息
     * 5：要判断是否是家族长，家族长不能被踢出家族
     */
    @Override
    public AjaxResult kickOutLiveById( Collection<String> userIds ) {
        Map<Long, LiveUser> liveUserMap = new HashMap<>();
        for ( String id : userIds ) {
            long      userId    = Long.parseLong( id );
            LiveVideo liveVideo = liveVideoMapper.selectLiveVideoById( userId );
            if ( liveVideo != null && liveVideo.getLiveIn() == 1 ) {
                return AjaxResult.error( 100, "主播" + liveVideo.getHostName() + "在直播中,踢出家族失败！" );
            }
            LiveUser liveUser = liveUserMapper.selectLiveUserById( userId );
            if ( liveUser.getFamilyChieftain() != null && liveUser.getFamilyChieftain() == 1 ) {
                return AjaxResult.error( 100, "家族长" + liveUser.getNickName() + "不能被踢出家族,踢出家族失败！" );
            }
            LiveFamily family = liveFamilyMapper.selectLiveFamilyById( liveUser.getFamilyId() );
            if ( family == null ) {
                return AjaxResult.error( 100, "主播" + liveUser.getNickName() + "未加入家族,踢出家族失败！" );
            }
            liveUserMap.put( userId, liveUser );
        }

        this.updateFamily( liveUserMap );
        return AjaxResult.success();
    }

    @Transactional ( rollbackFor = Exception.class )
    public void updateFamily( Map<Long, LiveUser> liveUserMap ) {
        for ( Map.Entry<Long, LiveUser> entry : liveUserMap.entrySet() ) {
            long           userId         = entry.getKey();
            LiveUser       liveUser       = entry.getValue();
            LiveFamilyJoin liveFamilyJoin = new LiveFamilyJoin();
            liveFamilyJoin.setFamilyId( liveUser.getFamilyId() );
            liveFamilyJoin.setUserId( userId );
            liveFamilyJoin.setStatus( 3L );
            //修改申请表状态
            liveFamilyJoinMapper.updateLiveFamilyJoin( liveFamilyJoin );
            //修改家族成员数量
            liveFamilyMapper.updateFamilyCount( liveUser.getFamilyId() );
            LiveUser updateUser = new LiveUser();
            updateUser.setId( liveUser.getId() );
            updateUser.setFamilyId( 0L );
            updateUser.setFamilyChieftain( 0 );
            //修改主播状态
            liveUserMapper.updateLiveUser( updateUser );
        }
    }

    @Override
    public LiveUser selectMobileById( String id ) {
        return liveUserMapper.selectMobileById( id );
    }

    @Override
    public ArrayList selectLiveUserAuthList( LiveUser liveUser ) {
        List<LiveUser> authList = liveUserMapper.selectLiveUserAuthList( liveUser );
        Set<LiveUser>  hashSet  = new LinkedHashSet( authList );
        return new ArrayList( hashSet );
    }


}
