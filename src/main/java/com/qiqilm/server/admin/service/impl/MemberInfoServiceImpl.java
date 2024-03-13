package com.qiqilm.server.admin.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.ImmutableMap;
import com.qiqilm.server.admin.cache.MemberCacheManager;
import com.qiqilm.server.admin.cache.MemberForbidUtil;
import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.core.vo.RspBase;
import com.qiqilm.server.admin.domain.*;
import com.qiqilm.server.admin.domain.req.ReqSmallFeatures;
import com.qiqilm.server.admin.domain.rsp.RspMemberChannel;
import com.qiqilm.server.admin.domain.vo.PageBO;
import com.qiqilm.server.admin.domain.vo.ReqAddScore;
import com.qiqilm.server.admin.domain.vo.WithdrawReport;
import com.qiqilm.server.admin.enums.EnumAction;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.*;
import com.qiqilm.server.admin.service.IConfigEnvironmentService;
import com.qiqilm.server.admin.service.ILogService;
import com.qiqilm.server.admin.service.IMemberInfoService;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * 会员信息 Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Slf4j
@Service
public class MemberInfoServiceImpl implements IMemberInfoService {
    @Autowired
    private MemberInfoMapper       memberInfoMapper;
    @Autowired
    private MemberActionLogsMapper actionLogsMapper;
    @Autowired
    private LogMoneyMapper         logMoneyMapper;
    @Autowired
    private MemberBcodeMapper      codeFlowMapper;
    @Resource
    private MemberGameMoneyMapper  gameMoneyMapper;
    @Resource
    private LogGameOrderMapper     logGameOrderMapper;
    @Autowired
    private ILogService            logService;
    @Autowired
    private MemberCardMapper       memberCardMapper;
    @Autowired
    private MemberCacheManager     memberCacheManager;
    @Autowired
    private MemberForbidUtil       memberForbidUtil;
    @Autowired
    private MemberBcodeMapper      memberBcodeMapper;
    @Autowired
    private LiveUserMapper         liveUserMapper;
    @Autowired
    private NameUtil               nameUtil;
    @Autowired
    private RedisUtil              redisUtil;

    @Resource
    private ForkJoinPool forkJoinPool;

    @Autowired
    private IConfigEnvironmentService configEnvironmentService;

    /**
     * 查询会员信息
     *
     * @param id 会员信息 ID
     *
     * @return 会员信息
     */
    @Override
    public MemberInfo selectMemberInfoById( String id ) {
        return memberInfoMapper.selectMemberInfoById( id );
    }

    /**
     * 查询会员信息 列表
     *
     * @param memberInfo 会员信息
     *
     * @return 会员信息
     */
    @Override
    public List<MemberInfo> selectMemberInfoList( MemberInfo memberInfo ) {
        if ( StringUtils.hasText( memberInfo.getSearchValue() ) || StringUtils.hasText( memberInfo.getLoginIp() )
                || StringUtils.hasText( memberInfo.getPhone() ) || StringUtils.hasText( memberInfo.getNickName() ) ) {
            memberInfo.setParams( null );
        }
        List<MemberInfo> memberInfos = memberInfoMapper.selectMemberInfoList( memberInfo );
        if ( memberInfos.size() > 0 && !CollectionUtils.isEmpty( memberInfos ) ) {
            for ( MemberInfo me : memberInfos ) {
                if ( StringUtils.hasText( me.getPhone() ) ) {
                    me.setPhone( me.getPhone().substring( 0, 3 ) + "****" + me.getPhone().substring( 7, 11 ) );
                }
            }
        }
        return memberInfos;
    }

    /**
     * 新增会员信息
     *
     * @param memberInfo 会员信息
     *
     * @return 结果
     */
    @Override
    public AjaxResult insertMemberInfo( MemberInfo memberInfo ) {
        //校验是不是手机号
        if ( !ValidatorUtil.isNumber11( memberInfo.getPhone() ) ) {
            return AjaxResult.error( "手机号必须是11位数字" );
        }
        if ( memberInfoMapper.countByPhone( memberInfo.getPhone() ) > 0 ) {
            return AjaxResult.error( "此手机号已经存在" );
        }
        MemberInfo member = memberCacheManager.createMember();
        if ( StringUtils.isEmpty( member.getId() ) ) {
            return AjaxResult.error( "注册redis存在问题，请联系管理员" );
        }

        member.setIsOnline( 0 );
        member.setVip( 1 );//默认vip1
        member.setStatus( 2 );
        member.setTotalAccount( BigDecimal.ZERO );
        member.setPassword( memberInfo.getPassword() );
        member.setUserName( member.getMemberCode() );
        member.setPhone( memberInfo.getPhone() );
        member.setRegTime( new Date() );
        member.setLevelIntegral( BigDecimal.ZERO );
        member.setBoxAccount( BigDecimal.ZERO );
        member.setCodeAccount( BigDecimal.ZERO );
        member.setCodeTotal( BigDecimal.ZERO );
        member.setInviteMoney( memberInfo.getInviteMoney() );
        member.setInviterCode( memberInfo.getInviterCode() );
        member.setNickName( nameUtil.nickNameRandom() );
        member.setLoginNum( 0 );
        if ( memberInfoMapper.insertMemberInfo( member ) > 0 ) {
            return AjaxResult.success( "添加成功" );
        } else {
            return AjaxResult.success( "添加失败" );
        }
    }

    /**
     * 修改会员信息
     *
     * @param memberInfo 会员信息
     *
     * @return 结果
     */
    @Override
    public int updateMemberInfo( MemberInfo memberInfo ) {
        return memberInfoMapper.updateMemberInfo( memberInfo );
    }

    @Override
    @Transactional( rollbackFor = Exception.class )
    public RspBase addMemberMoneyOnly( String ip, LoginUser loginUser, ReqAddScore req ) {
        String     userId        = req.getId();
        BigDecimal money         = req.getScore();
        BigDecimal beatNum       = req.getBeatNum();
        String     Mk            = req.getMk() + ",操作人:" + loginUser.getUser().getUserName();
        String     markorder     = req.getOrdermk();
        String     admin_name    = loginUser.getUsername();
        RspBase    rspBase       = new RspBase();
        MemberInfo oldmemberInfo = this.selectMemberInfoById( userId );
        BigDecimal total         = oldmemberInfo.getTotalAccount();

        if ( money.compareTo( BigDecimal.ZERO ) > 0 ) {
            ConfigEnvironment environment = configEnvironmentService.selectConfigEnvironmentById( "addmoney" );
            String            environmentAmount;
            if ( environment == null ) {
                environmentAmount = "1000000";
            } else {
                environmentAmount = environment.getEnvValue();
            }
            if ( money.compareTo( new BigDecimal( Integer.parseInt( environmentAmount ) ) ) > 0 ) {
                rspBase.setMsg( "最大金额为" + Integer.parseInt( environmentAmount ) );
                rspBase.setCode( 1 );
                return rspBase;
            }

        } else if ( money.compareTo( BigDecimal.ZERO ) < 0 ) {
            BigDecimal lat = total.add( money );
            if ( lat.compareTo( BigDecimal.ZERO ) < 0 ) {
                rspBase.setMsg( "余额" + money + "不足扣除" );
                rspBase.setCode( 1 );
                return rspBase;
            }
            beatNum = new BigDecimal( 0 );
        }

        if ( !"0".equals( markorder ) ) {
            List<LogMoney> markList = null;
            if ( money.compareTo( BigDecimal.ZERO ) > 0 ) {
                markList = logMoneyMapper.findMark( userId, markorder, money, null, userId.substring( userId.length() - 1 ) );
            } else {
                BigDecimal negate = money.negate();
                markList = logMoneyMapper.findMark( userId, markorder, null, negate, userId.substring( userId.length() - 1 ) );
            }
            if ( markList.size() > 0 ) {
                rspBase.setMsg( "请查看此笔金额是否已经入款过，如否请输入其他订单备注" );
                rspBase.setCode( 1 );
                return rspBase;
            }
        }

        if ( total != null ) {
            BigDecimal now = total.add( money );
            if ( beatNum != null && beatNum.compareTo( BigDecimal.ZERO ) > 0 ) {
                MemberBcode codeFlow = new MemberBcode();
                codeFlow.setId( UuidUtil.getRandomUuidWithoutSeparator() );
                codeFlow.setIncome( money.multiply( beatNum ).setScale( 2, RoundingMode.HALF_UP ) );
                codeFlow.setCreateTime( new Date() );
                codeFlow.setStatus( 0 );
                codeFlow.setCur( BigDecimal.ZERO );
                codeFlow.setUserId( userId );
                codeFlow.setDes( "人工入款" );
                codeFlowMapper.insertMemberBcode( codeFlow );
            } else {
                beatNum = new BigDecimal( 0 );
            }
            memberInfoMapper.updateMoneySelect( userId, money, null, money
                    .multiply( beatNum )
                    .setScale( 2, RoundingMode.HALF_UP ), null, null );
            MemberActionLogs log = new MemberActionLogs();
            log.setId( UuidUtil.getRandomUuidWithoutSeparator() );
            log.setUserId( userId );
            log.setUserName( oldmemberInfo.getUserName() );
            log.setcTime( new Date() );
            log.setType( EnumAction.gm.getType() );
            log.setDes( EnumAction.gm.getDes() );
            log.setParam1( "人工入款：" + money );
            log.setParam2( "剩余资金：" + now );
            log.setParam3( "操作人：" + admin_name );
            log.setParam4( "备注：" + Mk );
            log.setParamIp( ip );
            actionLogsMapper.insertMemberActionLogs( log );
            logService.logmarkMoney( userId, oldmemberInfo.getUserName(), EnumMoney.gm, now, total, Mk, markorder );
        } else {
            rspBase.setMsg( "该成员redis未初始化金额，或者您输入的金额有误 " );
            rspBase.setCode( 1 );
            return rspBase;
        }

        //人工加分日志
        //		MemberDepositLog memberDepositLog = new MemberDepositLog();
        //		memberDepositLog.setMemberId(req.getId());
        //		String user_name = req.getId().substring(5);
        //		memberDepositLog.setUserName(user_name);
        //        memberDepositLog.setMoney(req.getScore());
        //        memberDepositLog.setRemark(req.getMk());
        //        memberDepositLog.setMoneydes(req.getMoneydes());
        //        String beatNumTwo = String.valueOf(beatNum.setScale(0,BigDecimal.ROUND_HALF_UP));
        //		memberDepositLog.setBeatNum(Integer.valueOf(beatNumTwo));
        //		memberDepositLog.setRemarkPay(req.getRemarkPay());
        //		memberDepositLog.setOrderRemark(req.getOrdermk());
        //		memberDepositLog.setOpName(admin_name);
        //		memberDepositLog.setOpTime(new Date());
        //		memberDepositLog.setIp(ip);
        //		memberDepositLogMapper.insertMemberDepositLog(memberDepositLog);
        return rspBase;
    }

    @Override
    public PageBO<WithdrawReport> withdrawReport( String memberId, Integer pageNum, Integer pageSize ) {
        //		memberInfoMapper.call_pro_useranalysis( memberid );
        PageBO<WithdrawReport> pageBO = new PageBO<>();
        pageNum  = 1;
        pageSize = 100;
        Page                 page            = PageHelper.startPage( pageNum, pageSize, true );
        List<WithdrawReport> withdrawReports = memberInfoMapper.userWithdrawReportList();
        String               remark          = memberInfoMapper.findBanRemark( memberId );
        WithdrawReport       withdrawReport  = new WithdrawReport();
        withdrawReport.setClass_twoname( "禁言禁用备注" );
        withdrawReport.setT_value( remark );
        withdrawReports.add( withdrawReport );
        pageBO.setData( withdrawReports );
        pageBO.setCount( page.getTotal() );
        return pageBO;
    }


    @Override
    public PageBO<MemberCard> findMemberCardPage( String memberid, Integer pageNum, Integer pageSize, String orderBy ) {
        PageBO<MemberCard> pageBO = new PageBO<>();
        Page               page   = PageHelper.startPage( pageNum, pageSize, orderBy );
        pageBO.setData( memberCardMapper.findList( memberid ) );
        pageBO.setCount( page.getTotal() );
        return pageBO;
    }

    @Override
    public void outGameFail( String orderId, String userId, Integer platformId ) {
        MemberGameMoney myGameMoney = new MemberGameMoney();
        myGameMoney.setId( userId + "_" + platformId );
        myGameMoney.setStatus( 2 );
        myGameMoney.setOderSn( "" );
        gameMoneyMapper.updateMemberGameMoney( myGameMoney );

        LogGameOrder logOrder = new LogGameOrder();
        logOrder.setId( orderId );
        logOrder.setStatus( 1 );
        logOrder.setETime( new Date() );
        logGameOrderMapper.updateLogGameOrder( logOrder );
    }

    @Override
    public void outGMGameSucess( String orderId, String userId, Integer platformId, BigDecimal money, String account ) {
        MemberGameMoney myGameMoney = new MemberGameMoney();
        myGameMoney.setId( userId + "_" + platformId );
        myGameMoney.setStatus( 0 );
        myGameMoney.setOderSn( "" );
        myGameMoney.setMoney( BigDecimal.ZERO );
        int i = gameMoneyMapper.updateMemberGameMoney( myGameMoney );

        Date         date     = new Date();
        LogGameOrder logOrder = new LogGameOrder();
        logOrder.setId( orderId );
        logOrder.setBTime( date );
        logOrder.setETime( date );
        logOrder.setMemberId( userId );
        logOrder.setMoney( money );
        logOrder.setStatus( 2 );
        logOrder.setType( 2 );
        logOrder.setUserName( account );
        logOrder.setPlatformId( platformId );
        int i1 = logGameOrderMapper.insertLogGameOrder( logOrder );

        if ( money.compareTo( BigDecimal.ZERO ) > 0 && i > 0 && i1 > 0 ) {
            memberInfoMapper.updateMoneySelect( userId, money, null, null, null, null );
        }
    }

    @Override
    public int changeSpeak( MemberInfo memberInfo ) {
        if ( "0".equals( memberInfo.getSpeak() ) ) {
            memberInfo.setSpeak( "0" );
            memberInfoMapper.updateMemberInfo( memberInfo );
            memberForbidUtil.setPlatformUserSpeak( memberInfo.getId(), false );
        } else {
            memberInfo.setSpeak( "1" );
            memberInfoMapper.updateMemberInfo( memberInfo );
            memberForbidUtil.setPlatformUserSpeak( memberInfo.getId(), true );
        }
        return 1;
    }

    @Override
    public AjaxResult updatePhones( ReqSmallFeatures req ) {
        if ( StringUtils.hasText( req.getPhones() ) && StringUtils.hasText( req.getPassword() ) ) {
            if ( req.getPhones().contains( "\n" ) ) {
                try {
                    String[]      phones = req.getPhones().split( "\n" );
                    StringBuilder phone  = new StringBuilder();
                    for ( int i = 0; i < phones.length; i++ ) {
                        phone.append( "\"" ).append( phones[ i ] ).append( "\"" ).append( "," );
                    }
                    phone = new StringBuilder( phone.substring( 0, phone.length() - 1 ) );
                    req.setPhones( phone.toString() );
                } catch ( Exception e ) {
                    return AjaxResult.error( 0, "分割手机号出错,请检查格式" );
                }
            } else {
                req.setPhones( "\"" + req.getPhones() + "\"" );
            }
            memberInfoMapper.updatePhones( req );
            return AjaxResult.success();
        }
        return AjaxResult.error( 0, "请输入批量手机号和密码" );
    }

    @Override
    public AjaxResult queryPhones( ReqSmallFeatures req ) {
        if ( StringUtils.hasText( req.getUserIds() ) ) {
            if ( req.getUserIds().contains( "\n" ) ) {
                try {
                    String[]      userIds = req.getUserIds().split( "\n" );
                    StringBuilder userId  = new StringBuilder();
                    for ( int i = 0; i < userIds.length; i++ ) {
                        userId.append( "\"" ).append( userIds[ i ] ).append( "\"" ).append( "," );
                    }
                    userId = new StringBuilder( userId.substring( 0, userId.length() - 1 ) );
                    req.setUserIds( userId.toString() );
                } catch ( Exception e ) {
                    return AjaxResult.error( 0, "分割会员ID出错,请检查格式" );
                }
            } else {
                req.setUserIds( "\"" + req.getUserIds() + "\"" );
            }
            List<ReqSmallFeatures> phonesAndUserId = memberInfoMapper.queryPhones( req );
            List<String>           phonesByIds     = new ArrayList<>();
            for ( ReqSmallFeatures ph : phonesAndUserId ) {
                phonesByIds.add( ph.getUserIds() + ":" + ph.getPhonesByIds() );
            }
            return AjaxResult.success( phonesByIds );
        }
        return AjaxResult.error( 0, "请输入批量会员ID" );
    }

    @Override
    @Transactional( rollbackFor = Exception.class )
    public AjaxResult commitMoney( ReqSmallFeatures req ) {
        if ( StringUtils.hasText( req.getMemberIds() ) ) {
            String[] userIds = null;
            if ( req.getMemberIds().contains( "\n" ) ) {
                try {
                    userIds = req.getMemberIds().split( "\n" );
                    StringBuilder userId = new StringBuilder();
                    for ( int i = 0; i < userIds.length; i++ ) {
                        userId
                                .append( "\"" )
                                .append( userIds[ i ] )
                                .append( "\"" )
                                .append( "," )
                                .append( req.getMoney() )
                                .append( "," )
                                .append( req.getMoney() )
                                .append( "),(" );
                    }
                    userId = new StringBuilder( userId.substring( 0, userId.length() - 3 ) );
                    req.setUserIds( userId.toString() );
                } catch ( Exception e ) {
                    return AjaxResult.error( 0, "分割会员ID出错,请检查格式" );
                }
            } else {
                req.setUserIds( "\"" + req.getMemberIds() + "\"" + "," + req.getMoney() + "," + req.getMoney() );
            }
            //清除表中数据
            memberInfoMapper.clear();
            memberInfoMapper.insertPaiSong( req.getUserIds() );
            return AjaxResult.success();
        }
        return AjaxResult.error( 0, "请输入批量会员ID" );
    }

    @Override
    public AjaxResult unbindCard( MemberCard member ) {
        String           id             = member.getId();
        String           memberId       = member.getMemberId();
        List<MemberCard> memberCardList = memberCardMapper.memberCardList( memberId );
        MemberCard       memberCard     = memberCardMapper.selectMemberCardById( id );
        if ( Objects.isNull( memberCard ) ) {
            return AjaxResult.success( "卡号不存在" );
        }
        if ( memberCardList.size() > 1 && memberCard.getDv() == 1 ) {
            return AjaxResult.success( "请先解绑副卡" );
        }
        memberCardMapper.deleteMemberCardById( id );
        return AjaxResult.success( "解绑成功" );
    }

    @Override
    public AjaxResult changeBank( MemberCard member ) {
        String id = member.getId();
        //判断用户是否已经绑定该银行卡
        MemberCard memberCard1 = new MemberCard();
        memberCard1.setBankAccount( member.getBankAccount() );
        memberCard1.setMemberId( member.getMemberId() );

        List<MemberCard> memberCardList = memberCardMapper.findAllByBankAccount( member );
        List<MemberCard> memberCardListFiltered = memberCardList
                .stream()
                .filter( ( mc ) -> !mc.getId().equals( member.getId() ) && mc.getBankAccount().equals( member.getBankAccount() ) )
                .collect( Collectors.toList() );
        if ( !memberCardListFiltered.isEmpty() && memberCardListFiltered.get( 0 ) != null ) {
            return AjaxResult.error( "卡已绑定帐号" + memberCardListFiltered.get( 0 ).getMemberId() );
        }

        MemberCard memberCard = memberCardMapper.selectMemberCardById( id );
        memberCard.setRealName( member.getRealName().trim() );
        memberCard.setBankAddress( member.getBankAddress().trim() );
        memberCard.setBankAccount( member.getBankAccount().trim() );
        memberCard.setBankId( member.getBankId() );
        memberCardMapper.updateMemberCard( memberCard );
        return AjaxResult.success( "修改银行卡信息成功" );
    }

    @Override
    public void repairMemberBcode( String memberId ) {
        //        int count=memberBcodeMapper.countMemberBcodeStatus(memberId);
        //        if (count>0){
        //            return;
        //        }
        memberBcodeMapper.updateMemberBcodeStatus( memberId );
        memberBcodeMapper.repairMemberInfo( memberId );
    }

    @Override
    public void updateVip( String memberId, Integer vip, String nickName ) {
        memberBcodeMapper.updateVip( memberId, vip, nickName );
        //更新用户登录缓存
        String token = redisUtil.strGet( Constants.USER_TOKEN_KEY + memberId );
        if ( StringUtils.hasText( token ) ) {
            redisUtil.hSet( Constants.TOKEN_USER_KEY + token, "vip", vip.toString() );
        }
    }

    @Override
    public AjaxResult updateInviterCode( String inviterCode, String memberId ) {
        memberInfoMapper.updateInviterCode( memberId, inviterCode );
        return AjaxResult.success( "修改成功" );
    }

    @Override
    public AjaxResult changeEmail( MemberInfo memberInfo ) {
        memberInfoMapper.changeEmail( memberInfo );
        return AjaxResult.success( "修改成功" );
    }

    @Override
    public String getMemberLoginAddress( String id ) {
        return memberInfoMapper.selectMemberInfoAddressById( id );
    }

    @Override
    public String getHistoryRecharge( String id ) {
        return memberInfoMapper.selectMemberInfoHistoryRechargeById( id );
    }

    @Override
    public List<RspMemberChannel> memberstatistics( MemberInfo memberInfo ) {
        return memberInfoMapper.memberstatistics( memberInfo );
    }

    @Override
    public void updataStatus( MemberInfo memberInfo ) {
        if ( memberInfo.getBanSpeakTime() == 0 ) {
            memberForbidUtil.setPlatformUserSpeak( memberInfo.getId(), false );
            memberInfo.setSpeak( "0" );
            memberInfoMapper.updateMemberInfo( memberInfo );
        }
        if ( memberInfo.getBanSpeakTime() > 0 ) {
            memberForbidUtil.setPlatformUserSpeak( memberInfo.getId(), true );
            memberInfo.setSpeak( "1" );
            memberInfoMapper.updateMemberInfo( memberInfo );
        }
    }

    @Override
    public Map listCount( MemberInfo memberInfo ) {
        return memberInfoMapper.listCount( memberInfo );
    }

    @Override
    public AjaxResult findMemberFollowList( String id ) {
        Set<String> liveHostSet = redisUtil.sMembers( "live:user-new-follow:" + id );
        if ( !liveHostSet.isEmpty() ) {
            List<LiveUser> liveUserList = liveUserMapper.selectLiveUserInId( liveHostSet
                    .stream()
                    .map( Long::parseLong )
                    .collect( Collectors.toSet() ) );
            return AjaxResult.success( liveUserList );
        }
        return AjaxResult.success();
    }

    @Override
    public int banStatus( MemberInfo memberInfo ) {
        List<MemberInfo> memberInfos = memberInfoMapper.selectMemberInfoByIp( memberInfo.getLoginIp() );
        List<String>     loginIp     = new ArrayList<>();
        for ( MemberInfo info : memberInfos ) {
            memberCacheManager.delToken( info.getId() );
            loginIp.add( info.getId() );
        }
        if ( CollectionUtils.isEmpty( loginIp ) ) {
            throw new BusinessException( "未查询到需要禁用的会员" );
        }
        return memberInfoMapper.banStatus( loginIp, memberInfo.getRealName() );
    }

    @Override
    public int unBlockStatus( MemberInfo memberInfo ) {
        List<MemberInfo> memberInfos = memberInfoMapper.selectMemberInfoByIp( memberInfo.getLoginIp() );
        List<String>     loginIp     = new ArrayList<>();
        for ( MemberInfo info : memberInfos ) {
            memberCacheManager.delToken( info.getId() );
            loginIp.add( info.getId() );
        }
        if ( CollectionUtils.isEmpty( loginIp ) ) {
            throw new BusinessException( "未查询到需要解禁的会员" );
        }
        return memberInfoMapper.unBlockStatus( loginIp, memberInfo.getRealName() );
    }

    @Override
    public List<LiveGuardUser> selectLiveGuard( LiveGuardUser liveGuardUser ) {
        return memberInfoMapper.selectLiveGuard( liveGuardUser );
    }

    @Override
    public int withdrawStatus( MemberInfo memberInfo ) {
        return memberInfoMapper.withdrawStatus( memberInfo );
    }

    @Override
    public AjaxResult personalReport( String startTime, String endTime, String memberId ) {

        List<Callable<Map<String, Object>>> forkJoinTasks = new ArrayList<>();

        // 线下充值 Offline recharge
        forkJoinTasks.add( () -> ImmutableMap.of( "personalRecharge", memberInfoMapper.personalRecharge( startTime, endTime,
                memberId ) ) );
        // 线上充值 online recharge
        forkJoinTasks.add( () -> ImmutableMap.of( "personalOnlineRecharge", memberInfoMapper.personalOnlineRecharge( startTime,
                endTime, memberId ) ) );
        // 线上充值2 online recharge 2
        forkJoinTasks.add( () -> ImmutableMap.of( "personalAgentRecharge", memberInfoMapper.personalAgentRecharge( startTime,
                endTime, memberId ) ) );
        // 线上充值3 online recharge 3
        forkJoinTasks.add( () -> ImmutableMap.of( "personalUsdtRecharge", memberInfoMapper.personalUsdtRecharge( startTime,
                endTime, memberId ) ) );
        // 提款 withdrawal
        forkJoinTasks.add( () -> ImmutableMap.of( "personalWithdrawRecharge",
                memberInfoMapper.personalWithdrawRecharge( startTime, endTime, memberId ) ) );


        forkJoinTasks.add( () -> ImmutableMap.of( "totalAccount",
                memberInfoMapper.totalAccount( startTime, endTime, memberId ) ) );

        // 送礼 gift
        forkJoinTasks.add( () -> ImmutableMap.of( "personalLiverVideoProp", memberInfoMapper.personalLiverVideoProp( startTime,
                endTime, memberId ) ) );


        List<Future<Map<String, Object>>> futureList = forkJoinPool.invokeAll( forkJoinTasks );
        if ( futureList.isEmpty() ) {
            System.out.println( "error is here" );
        }

        Set<Map<String, Object>> resultSet = futureList.stream().map( t -> {
            try {
                return t.get();
            } catch ( InterruptedException | ExecutionException e ) {
                throw new IllegalStateException( e );
            }
        } ).filter( Objects::nonNull ).collect( Collectors.toSet() );
        resultSet.add( ImmutableMap.of( "memberId", memberId ) );

        Map<String, Object> resultMap = resultSet
                .stream()
                .map( Map::entrySet )
                .flatMap( Set::stream )
                .collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue ) );

        List<Map> mapList = memberInfoMapper.personalGameData( startTime, endTime, memberId, memberId.substring(
                memberId.length() - 1 ) );

        resultMap.put( "bCodeList", mapList );

        return AjaxResult.success( resultMap );
    }

    @Transactional( rollbackFor = Exception.class )
    @Override
    public RspBase<?> boxDish( String memberId ) {
        MemberInfo memberInfo   = memberInfoMapper.selectMemberInfoById( memberId );
        BigDecimal totalAccount = memberInfo.getTotalAccount();
        BigDecimal boxAccount   = memberInfo.getBoxAccount();

        BigDecimal totalNow = totalAccount.add( boxAccount );
        String     name     = "保险箱存入:" + boxAccount.negate() + "现保险箱余额:0";
        logService.logMoneyAll( memberId, memberInfo.getUserName(), EnumMoney.safebox, totalNow, boxAccount, null, name, null );

        int i = memberInfoMapper.boxDish( memberId );
        if ( i <= 0 ) {
            throw new BusinessException( "保险箱余额提出失败" );
        }
        return RspBase.ok();
    }


    @Override
    public int updateCodeTotalVipLevel( MemberInfo memberInfo ) {
        if ( com.qiqilm.server.admin.utils.StringUtils.isBlank( memberInfo.getId() ) || memberInfo.getCodeTotal() == null ) {
            throw new BusinessException( "输入有误" );
        }
        return memberInfoMapper.updateCodeTotalVipLevel( memberInfo );
    }

}
