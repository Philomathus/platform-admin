package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.domain.*;
import com.qiqilm.server.admin.domain.vo.LiveVideoPropVo;
import com.qiqilm.server.admin.mapper.*;
import com.qiqilm.server.admin.service.IGameDataLogService;
import com.qiqilm.server.admin.utils.LocalDateTimeUtils;
import com.qiqilm.server.admin.utils.RobotMessage;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 总代理游戏注单Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-17
 */
@Service
@Log4j2
public class GameDataLogServiceImpl implements IGameDataLogService {
    @Resource
    private GameDataRecordMapper gameDataRecordMapper;

    @Resource
    private MemberBcodeMapper memberBcodeMapper;

    @Resource
    private MemberInfoMapper memberInfoMapper;

    @Resource
    private ActivityQuestInfoMapper questInfoMapper;

    @Resource
    private MemberQuestMapper memberQuestMapper;

    @Resource
    private LotteryBetMapper lotteryBetMapper;

    @Resource
    private LiveVideoPropMapper liveVideoPropMapper;

    @Resource
    private SqlSessionTemplate sqlSessionTemplate;
    @Resource
    private RobotMessage       robotMessage;

    @Resource
    private SysConfigCacheUtil sysConfigCacheUtil;

    @Value( "${spring.profiles.active}" )
    private String profile;

    private static final String TABLE_PREFIX = "game_data_record_";

    @Override
    public void beatGameCodeAgent( Map<Integer, String> platformType, Map<Integer, BigDecimal> beatRateMap, String start,
                                   String end, String account, Long platformId ) {
        String day = end.substring( 0, 10 ).replace( "-", "" );
        List<GameDataRecord> gameDataRecords = gameDataRecordMapper.selectGameDataRecordAgentList(
                TABLE_PREFIX + day, start, end, profile, account, getDataRemoteByEnum( platformId ) );

        if ( gameDataRecords.isEmpty() ) {
            log.warn( "拉单条数为0, 开始时间:{} 结束时间:{}", start, end );
            return;
        }
        log.info( "拉单条数:{}, 开始时间:{} 结束时间:{}", gameDataRecords.size(), start, end );
        Map<String, BigDecimal> willCodeMap  = new HashMap<>();
        List<MemberGameData>    willCodeList = new ArrayList<>();
        SqlSession              session      = sqlSessionTemplate.getSqlSessionFactory().openSession( ExecutorType.BATCH, false );
        MemberGameDataMapper    mapper       = session.getMapper( MemberGameDataMapper.class );
        for ( GameDataRecord og : gameDataRecords ) {
            if ( mapper.findExist( og.getAccount().substring( og.getAccount().length() - 1 ), og.getId() ) != null ) {
                continue;
            }
            Integer        enumByDataRemote = getEnumByDataRemote( og.getPlatformId().intValue() );
            MemberGameData gameDataLog      = new MemberGameData();
            gameDataLog.setId( og.getId() );
            gameDataLog.setGameId( og.getGameId() );
            gameDataLog.setGameRound( og.getGameRound() );
            gameDataLog.setAccount( og.getAccount().toLowerCase() );
            gameDataLog.setKindId( og.getKindId() );
            gameDataLog.setCellScore( og.getCellScore() );
            gameDataLog.setAllBet( og.getAllBet() );
            gameDataLog.setProfit( og.getProfit() );
            gameDataLog.setGameStartTime( og.getGameStartTime() );
            gameDataLog.setGameEndTime( og.getGameEndTime() );
            gameDataLog.setAgent( og.getGameAgent() );
            gameDataLog.setStatus( 0 );
            gameDataLog.setPlatformType( platformType.get( enumByDataRemote ) );
            gameDataLog.setPlatformId( enumByDataRemote );
            gameDataLog.setRevenue( og.getRevenue() );
            willCodeList.add( gameDataLog );

            if ( new BigDecimal( gameDataLog.getProfit() ).compareTo( BigDecimal.ZERO ) == 0 ) {
                continue;
            }
            if ( beatRateMap.containsKey( enumByDataRemote ) ) {
                BigDecimal beatAdd = new BigDecimal( og.getCellScore() ).multiply( beatRateMap.get( enumByDataRemote ) )
                                                                        .setScale( 4, RoundingMode.HALF_UP );
                willCodeMap.putIfAbsent( og.getAccount(), BigDecimal.ZERO );
                willCodeMap.put( og.getAccount(), willCodeMap.get( og.getAccount() ).add( beatAdd ) );
            }
        }
        log.warn( "准备处理条数:{}, 开始时间:{} 结束时间:{}", willCodeList.size(), start, end );
        insertBatch( session, mapper, willCodeList );
        doBeatCode( willCodeMap );
        deQuestCheck( willCodeList );
        log.info( "新拉单拉取条数：{},实际插入:{}", gameDataRecords.size(), willCodeList.size() );
    }

    public static Integer getDataRemoteByEnum( Long platformId ) {
        if ( platformId == null ) {
            return null;
        }
        Integer pid;
        switch ( platformId.toString() ) {
        case "1":
            pid = 39;
            break;
        case "2":
            pid = 12;
            break;
        case "5":
            pid = 8;
            break;
        case "6":
            pid = 9;
            break;
        case "7":
            pid = 11;
            break;
        case "8":
        case "9":
        case "10":
        case "11":
            pid = 16;
            break;
        case "12":
            pid = 40;
            break;
        case "14":
            pid = 3;
            break;
        case "15":
            pid = 38;
            break;
        case "17":
            pid = 13;
            break;
        case "50":
            pid = 1;
            break;
        case "51":
            pid = 21;
            break;
        default:
            pid = null;
        }
        return pid;
    }

    public static Integer getEnumByDataRemote( Integer platformId ) {
        Integer pid;
        switch ( platformId ) {
        case 1:
            pid = 50;
            break;
        case 3:
            pid = 14;
            break;
        case 8:
            pid = 5;
            break;
        case 9:
            pid = 6;
            break;
        case 11:
            pid = 7;
            break;
        case 12:
            pid = 2;
            break;
        case 13:
            pid = 17;
            break;
        case 16:
            pid = 8;
            break;
        case 21:
            pid = 51;
            break;
        case 38:
            pid = 15;
            break;
        case 39:
            pid = 1;
            break;
        case 40:
            pid = 12;
            break;
        case 41:
            pid = 41;
            break;
        default:
            pid = platformId;
        }
        return pid;
    }

    //批量插入
    public void insertBatch( SqlSession session, MemberGameDataMapper mapper, List<MemberGameData> willCodeList ) {
        int count = 0;
        for ( MemberGameData in : willCodeList ) {
            try {
                mapper.insertMemberGameData( in, in.getAccount().substring( in.getAccount().length() - 1 ) );
                count += 1;
                if ( count >= 500 ) {
                    session.commit();
                    count = 0;
                }

            } catch ( Exception e ) {
                log.error( in.getId() );
            }

        }
        if ( count > 0 ) {
            session.commit();

        }
        session.close();
    }

    //实际打码
    @Async
    public void doBeatCode( Map<String, BigDecimal> willCodeMap ) {
        Map<String, BigDecimal> codeAccountMap = new HashMap<>();
        MemberBcode             query          = new MemberBcode();
        query.setStatus( 0 );
        //遍历有注单的会员
        for ( String user_id : willCodeMap.keySet() ) {
            //记录此会员新的打码量
            BigDecimal beatVal = BigDecimal.ZERO;
            BigDecimal codeVal = willCodeMap.get( user_id );
            //查询到此人需要打码的充值记录
            query.setUserId( user_id );
            List<MemberBcode> codeFlowlist = memberBcodeMapper.selectWillBcodeList( query );
            for ( MemberBcode codeFlow : codeFlowlist ) {
                if ( codeVal.compareTo( BigDecimal.ZERO ) <= 0 ) {
                    continue;
                }
                //此纪录最初打码量
                BigDecimal oldCur  = codeFlow.getCur();
                BigDecimal addCode = codeVal.add( oldCur );
                if ( addCode.compareTo( codeFlow.getIncome() ) > 0 ) {
                    codeFlow.setCur( codeFlow.getIncome() );
                    codeFlow.setStatus( 1 );
                    //codeFlow.setCreate_time(new Date());
                } else if ( addCode.compareTo( codeFlow.getIncome() ) == 0 ) {
                    codeFlow.setCur( codeFlow.getIncome() );
                    codeFlow.setStatus( 1 );
                    //codeFlow.setCreate_time(new Date());
                } else {
                    codeFlow.setCur( addCode );
                }
                beatVal = beatVal.add( codeFlow.getCur().subtract( oldCur ) );

                codeVal = codeVal.subtract( codeFlow.getCur().subtract( oldCur ) );
                memberBcodeMapper.updateMemberBcode( codeFlow );
            }
            if ( codeAccountMap.containsKey( user_id ) ) {
                codeAccountMap.put( user_id, codeAccountMap.get( user_id ).add( beatVal )
                                                           .setScale( 4, BigDecimal.ROUND_HALF_UP ) );
            } else {
                codeAccountMap.put( user_id, beatVal );
            }
        }

        for ( String userId : willCodeMap.keySet() ) {
            BigDecimal c = codeAccountMap.get( userId );
            if ( c == null ) {
                c = BigDecimal.ZERO;
            } else {
                c = c.setScale( 2, BigDecimal.ROUND_DOWN );
            }
            BigDecimal w = willCodeMap.get( userId );
            if ( w == null ) {
                w = BigDecimal.ZERO;
            } else {
                w = w.setScale( 2, BigDecimal.ROUND_DOWN );
            }
            try {
                memberInfoMapper.updateBeatCode( userId, c, w );
            } catch ( Exception e ) {
                log.error( "打码异常userId:{},code_account：{},code_total:{}", userId, c, w, e );
            }

        }
    }

    //做任务
    @Async
    public void deQuestCheck( final List<MemberGameData> list ) {
        //查找全部任务
        List<ActivityQuestInfo> listConfQuet = questInfoMapper.selectAllQuestList();
        Set<Integer>            questSet     = listConfQuet.stream().map( ActivityQuestInfo::getPlatformId )
                                                           .collect( Collectors.toSet() );
        for ( MemberGameData data : list ) {
            //过滤没参加活动的游戏平台
            if ( !questSet.contains( data.getPlatformId() ) ) {
                continue;
            }
            // 过滤百家乐和局庄闲下注，不计入打码和任务
            if ( new BigDecimal( data.getProfit() ).compareTo( BigDecimal.ZERO ) == 0 && data.getKindId().equals( "2001" ) ) {
                continue;
            }
            int add = new BigDecimal( data.getCellScore() ).intValue();
            for ( ActivityQuestInfo confQuest : listConfQuet ) {
                if ( !Objects.equals( confQuest.getPlatformId(), data.getPlatformId() ) ) {
                    continue;
                }
                if ( !confQuest.getKindId().equals( "0" ) && !confQuest.getKindId().equals( data.getKindId() ) ) {
                    continue;
                }
                MemberQuest memberQuest = memberQuestMapper.selectMemberQuestById( data.getAccount().concat( "_" )
                                                                                       .concat( confQuest.getId() ) );
                if ( memberQuest == null ) {
                    memberQuest = new MemberQuest();
                    memberQuest.setMemberId( data.getAccount() );
                    memberQuest.setQuestId( confQuest.getId() );
                    memberQuest.setId( data.getAccount().concat( "_" ).concat( confQuest.getId() ) );
                    memberQuest.setStatus( 0 );
                    memberQuest.setCurnum( add );
                    if ( memberQuest.getCurnum() >= confQuest.getTarget() ) {
                        memberQuest.setCurnum( confQuest.getTarget() );
                        memberQuest.setStatus( 1 );
                    }
                    memberQuest.setTaskMode( confQuest.getTaskMode() );
                    memberQuestMapper.insertMemberQuest( memberQuest );
                } else if ( memberQuest.getStatus() == 0 ) {
                    memberQuest.setCurnum( memberQuest.getCurnum() + add );
                    if ( memberQuest.getCurnum() >= confQuest.getTarget() ) {
                        memberQuest.setCurnum( confQuest.getTarget() );
                        memberQuest.setStatus( 1 );
                    }
                    memberQuestMapper.updateMemberQuest( memberQuest );
                }

            }
        }

    }

    @Override
    public void beatLotteryCode( String platformTypeId, BigDecimal beatRate, String start, String end ) {
        List<LotteryBet> list = lotteryBetMapper.selectLotteryBetList( start, end );
        if ( list.size() == 0 ) {
            return;
        }
        log.warn( "彩票拉取注单数量" + list.size() );
        Map<String, BigDecimal> willCodeMap  = new HashMap<>();
        List<MemberGameData>    willCodeList = new ArrayList<>();
        SqlSession              session      = sqlSessionTemplate.getSqlSessionFactory().openSession( ExecutorType.BATCH, false );
        MemberGameDataMapper    mapper       = session.getMapper( MemberGameDataMapper.class );
        for ( LotteryBet og : list ) {
            if ( mapper.findExist( og.getPuserId().substring( og.getPuserId().length() - 1 ), og.getId() ) != null ) {
                continue;
            }
            MemberGameData gameDataLog = new MemberGameData();
            gameDataLog.setId( og.getId() );
            gameDataLog.setGameId( og.getId() );
            gameDataLog.setAccount( og.getPuserId() );
            gameDataLog.setKindId( og.getLotteryId() );
            gameDataLog.setCellScore( String.valueOf( og.getCost() ) );
            gameDataLog.setAllBet( gameDataLog.getCellScore() );
            gameDataLog.setProfit( String.valueOf( og.getPrize().subtract( og.getCost() ) ) );
            gameDataLog.setGameStartTime( og.getBetTime() );
            gameDataLog.setGameEndTime( og.getUpdateTime() );
            gameDataLog.setAgent( og.getAnchor() > 0 ? "80000" : "10000" );
            gameDataLog.setStatus( 0 );
            gameDataLog.setPlatformType( platformTypeId );
            gameDataLog.setPlatformId( 4 );

            // 百家乐和局中庄闲下注退款不计打码
            if ( !( og.getLotteryId().equals( "2001" )
                            && new BigDecimal( gameDataLog.getProfit() ).compareTo( BigDecimal.ZERO ) == 0 ) ) {
                BigDecimal beatAdd = og.getCost().multiply( beatRate ).setScale( 4 );
                willCodeMap.putIfAbsent( og.getPuserId(), BigDecimal.ZERO );
                willCodeMap.put( og.getPuserId(), willCodeMap.get( og.getPuserId() ).add( beatAdd ) );
            }

            willCodeList.add( gameDataLog );
        }

        insertBatch( session, mapper, willCodeList );

        doBeatCode( willCodeMap );

        deQuestCheck( willCodeList );

        String lottery_telegram = sysConfigCacheUtil.getConf( "lottery_telegram" );

        log.info( "纸飞机2id" + lottery_telegram );
        if ( lottery_telegram != null ) {
            noticeRobotMessage( lottery_telegram, willCodeList );
        }
    }


    @Override
    public void beatLotteryCode2( String platformTypeId, BigDecimal beatRate, LotteryBet0 lotteryBet0 ) {
        String     tableLast  = lotteryBet0.getPuserId().substring( lotteryBet0.getPuserId().length() - 1 );
        LotteryBet lotteryBet = lotteryBetMapper.selectLotteryBetById( lotteryBet0.getId(), tableLast );
        if ( lotteryBet == null ) {
            return;
        }
        Map<String, BigDecimal> willCodeMap  = new HashMap<>();
        List<MemberGameData>    willCodeList = new ArrayList<>();
        SqlSession              session      = sqlSessionTemplate.getSqlSessionFactory().openSession( ExecutorType.BATCH, false );
        MemberGameDataMapper    mapper       = session.getMapper( MemberGameDataMapper.class );

        if ( mapper.findExist( lotteryBet.getPuserId().substring( lotteryBet.getPuserId().length() - 1 ), lotteryBet.getId() )
                == null ) {
            MemberGameData gameDataLog = new MemberGameData();
            gameDataLog.setId( lotteryBet.getId() );
            gameDataLog.setGameId( lotteryBet.getId() );
            gameDataLog.setAccount( lotteryBet.getPuserId() );
            gameDataLog.setKindId( lotteryBet.getLotteryId() );
            gameDataLog.setCellScore( String.valueOf( lotteryBet.getCost() ) );
            gameDataLog.setAllBet( gameDataLog.getCellScore() );
            gameDataLog.setProfit( String.valueOf( lotteryBet.getPrize().subtract( lotteryBet.getCost() ) ) );
            gameDataLog.setGameStartTime( lotteryBet.getBetTime() );
            gameDataLog.setGameEndTime( lotteryBet.getUpdateTime() );
            gameDataLog.setAgent( lotteryBet.getAnchor() > 0 ? "80000" : "10000" );
            gameDataLog.setStatus( 0 );
            gameDataLog.setPlatformType( platformTypeId );
            gameDataLog.setPlatformId( 4 );

            // 百家乐和局中庄闲下注退款不计打码
            if ( !( lotteryBet.getLotteryId().equals( "2001" )
                            && new BigDecimal( gameDataLog.getProfit() ).compareTo( BigDecimal.ZERO ) == 0 ) ) {
                BigDecimal beatAdd = lotteryBet.getCost().multiply( beatRate ).setScale( 4 );
                willCodeMap.putIfAbsent( lotteryBet.getPuserId(), BigDecimal.ZERO );
                willCodeMap.put( lotteryBet.getPuserId(), willCodeMap.get( lotteryBet.getPuserId() ).add( beatAdd ) );
            }

            willCodeList.add( gameDataLog );

            insertBatch( session, mapper, willCodeList );

            doBeatCode( willCodeMap );

            deQuestCheck( willCodeList );
        }
    }


    @Async
    public void noticeRobotMessage( String lottery_telegram, List<MemberGameData> willCodeList ) {
        BigDecimal temProfit = new BigDecimal( 1000 );
        for ( MemberGameData og : willCodeList ) {
            try {
                if ( new BigDecimal( og.getProfit() ).compareTo( temProfit ) < 0 ) {
                    continue;
                }
                log.info( "纸飞机1id" + lottery_telegram );
                robotMessage.sendByChatId(
                        og.getAccount() + ( og.getAgent().equals( "80000" ) ? "在直播间内" : "在直播间外" ) + og.getKindId()
                                + "盈利了:" + og.getProfit() + "元", lottery_telegram );
            } catch ( Exception e ) {
                log.error( "彩票消息推送异常" + e.getMessage() );
            }

        }
    }


    @Override
    public void beatLiveProp( String platformTypeId, BigDecimal beatRate, long start, long end ) {
        List<LiveVideoPropVo> list = liveVideoPropMapper.findVideoPropList( start, end );
        if ( list.size() == 0 ) {
            return;
        }
        Map<String, BigDecimal> willCodeMap = new HashMap<>();
        List<LogMoneyLive>      logList     = new ArrayList<>();
        SqlSession              session     = sqlSessionTemplate.getSqlSessionFactory().openSession( ExecutorType.BATCH, false );
        LogMoneyMapper          mapper      = session.getMapper( LogMoneyMapper.class );
        for ( LiveVideoPropVo og : list ) {
            if ( mapper.findExist( og.getP_user_id().substring( og.getP_user_id().length() - 1 ), og.getId() ) != null ) {
                continue;
            }

            LogMoneyLive log = new LogMoneyLive();
            log.setId( og.getId() );
            log.setUserId( og.getP_user_id() );
            log.setCreateTime( LocalDateTimeUtils.getFormatTimeSecond( og.getCreate_time() ) );
            log.setIncome( BigDecimal.ZERO );
            log.setPay( BigDecimal.ZERO );
            BigDecimal beatAdd = null;
            if ( og.getTotal_diamonds().compareTo( BigDecimal.ZERO ) > 0 ) {
                log.setPay( og.getTotal_diamonds() );
                beatAdd = log.getPay();
            } else {
                log.setIncome( og.getTotal_diamonds().negate() );

            }
            log.setTotal( og.getCurrent_diamonds() );
            log.setTotalBefore( og.getCurrent_diamonds().add( og.getTotal_diamonds() ) );

            log.setType( 0 );
            if ( og.getProp_id().compareTo( "0" ) > 0 ) {
                log.setDes( og.getProp_name().concat( "礼物" ) );
            } else {
                log.setDes( og.getProp_name() );
            }


            if ( beatAdd != null ) {
                beatAdd = beatAdd.multiply( beatRate ).setScale( 4 );
                willCodeMap.putIfAbsent( og.getP_user_id(), BigDecimal.ZERO );
                willCodeMap.put( og.getP_user_id(), willCodeMap.get( og.getP_user_id() ).add( beatAdd ) );
            }

            logList.add( log );

        }

        int count = 0;
        for ( LogMoneyLive in : logList ) {
            try {
                mapper.insertLogMoneyPop( in, in.getUserId().substring( in.getUserId().length() - 1 ) );
                count += 1;
                if ( count >= 500 ) {
                    session.commit();
                    count = 0;
                }

            } catch ( Exception e ) {
                e.printStackTrace();
            }

        }
        if ( count > 0 ) {
            session.commit();

        }
        session.close();

        doBeatCode( willCodeMap );

    }

}
