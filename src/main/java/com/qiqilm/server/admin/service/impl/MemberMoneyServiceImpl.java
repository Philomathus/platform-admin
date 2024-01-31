package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.LogMoney;
import com.qiqilm.server.admin.domain.MemberBcode;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.MemberMoney;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.LogMoneyMapper;
import com.qiqilm.server.admin.mapper.MemberBcodeMapper;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.mapper.MemberMoneyMapper;
import com.qiqilm.server.admin.service.ILogService;
import com.qiqilm.server.admin.service.IMemberMoneyService;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 派送彩金暂存表Service业务层处理
 *
 * @author 77tv
 * @date 2022-02-09
 */
@Service
public class MemberMoneyServiceImpl implements IMemberMoneyService {
    @Autowired
    private MemberMoneyMapper memberMoneyMapper;
    @Autowired
    private TokenService      tokenService;
    @Autowired
    private LogMoneyMapper    logMoneyMapper;
    @Autowired
    private RedisUtil         redisUtil;
    @Autowired
    private MemberBcodeMapper codeFlowMapper;
    @Autowired
    private MemberInfoMapper  memberInfoMapper;
    @Autowired
    private ILogService       logService;

    /**
     * 查询派送彩金暂存表
     *
     * @param memberId 派送彩金暂存表ID
     *
     * @return 派送彩金暂存表
     */
    @Override
    public MemberMoney selectMemberMoneyById( String memberId ) {
        return memberMoneyMapper.selectMemberMoneyById( memberId );
    }

    /**
     * 查询派送彩金暂存表列表
     *
     * @param memberMoney 派送彩金暂存表
     *
     * @return 派送彩金暂存表
     */
    @Override
    public List<MemberMoney> selectMemberMoneyList( MemberMoney memberMoney ) {
        return memberMoneyMapper.selectMemberMoneyList( memberMoney );
    }

    /**
     * 新增派送彩金暂存表
     *
     * @param memberMoney 派送彩金暂存表
     *
     * @return 结果
     */
    @Override
    public int insertMemberMoney( MemberMoney memberMoney ) {
        return memberMoneyMapper.insertMemberMoney( memberMoney );
    }

    @Override
    public AjaxResult starSend( MemberMoney memberMoney ) throws Exception {
        if ( !redisUtil.lock( EnumLock.member, "paiSong" + memberMoney.getMoneydes(), "1", 50 ) ) {
            throw new BusinessException( "请勿重复提交" );
        }
        LoginUser         loginUser      = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String            admin_name     = loginUser.getUsername();
        MemberMoney       memberMoney1   = new MemberMoney();
        List<MemberMoney> list           = memberMoneyMapper.selectMemberMoneyList( memberMoney1 );
        String            startFirstTime = DateFormatUtils.formate( DateFormatUtils.getTodayMorning() );
        SimpleDateFormat  df             = new SimpleDateFormat( "yyyy-MM-dd" );//设置日期格式
        String            today          = df.format( new Date() );// new Date()为获取当前系统时间
        if ( list.size() > 0 ) {
            for ( MemberMoney li : list ) {
                if ( li.getMoney().compareTo( new BigDecimal( 10000 ) ) >= 0 ) {
                    throw new BusinessException( String.format( "会员%s派送金额超过一万, 派送金额:%s", li.getMemberId(), li.getMoney() ) );
                }
                MemberInfo memberInfo = memberInfoMapper.selectMemberInfoById( li.getMemberId() );
                if ( memberInfo == null ) {
                    redisUtil.unLock( EnumLock.member, "paiSong" + memberMoney.getMoneydes() );
                    throw new BusinessException( "会员id不存在:" + li.getMemberId() );
                }
                this.processMoney( li, memberInfo, memberMoney.getMoneydes(), startFirstTime, today, admin_name );
            }
        } else {
            redisUtil.unLock( EnumLock.member, "paiSong" + memberMoney.getMoneydes() );
            throw new BusinessException( "请先上传有数据的excel" );
        }
        //完成派送清除表中数据
        memberInfoMapper.clear();
        redisUtil.unLock( EnumLock.member, "paiSong" + memberMoney.getMoneydes() );
        return new AjaxResult( 0, "操作成功" );
    }

    @Transactional( rollbackFor = Exception.class )
    public void processMoney( MemberMoney li, MemberInfo memberInfo, String moneydes, String startFirstTime, String today,
                              String admin_name ) {
        String     userId = li.getMemberId();
        BigDecimal money  = li.getMoney();
        BigDecimal beat   = li.getBeat();
        //资金日志
        String         markorder = "CJ" + today + userId + "_" + money.setScale( 0, RoundingMode.HALF_UP ) + moneydes;
        List<LogMoney> markList  = null;
        if ( money.compareTo( BigDecimal.ZERO ) > 0 ) {
            markList = logMoneyMapper.findMarkStartTime( userId, markorder, money, null, userId.substring(
                    userId.length() - 1 ), startFirstTime );
        } else {
            BigDecimal negate = money.negate();
            markList = logMoneyMapper.findMarkStartTime( userId, markorder, null, negate, userId.substring(
                    userId.length() - 1 ), startFirstTime );
        }
        if ( markList.size() > 0 ) {
            redisUtil.unLock( EnumLock.member, "paiSong" + moneydes );
            throw new BusinessException(
                    "派送失败.请查看此笔金额是否今日已经入款过.如否请输入其他入款备注." + "会员id:" + userId + "入款金额" + money
                            + "入款备注" + moneydes );
        }
        BigDecimal total = memberInfo.getTotalAccount();
        BigDecimal now   = total.add( money );
        logService.logmarkMoneyPaiSong( userId, memberInfo.getUserName(), EnumMoney.wongive, now, total, moneydes,
                moneydes + ",操作人:" + admin_name, markorder );
        //打码
        BigDecimal beatMoney = money;
        if ( money.compareTo( BigDecimal.ZERO ) > 0 ) {
            MemberBcode codeFlow = new MemberBcode();
            codeFlow.setId( UuidUtil.getRandomUuidWithoutSeparator() );
            beatMoney = money.multiply( beat ).setScale( 2, RoundingMode.DOWN );
            codeFlow.setIncome( beatMoney );
            codeFlow.setCreateTime( new Date() );
            codeFlow.setStatus( 0 );
            codeFlow.setCur( BigDecimal.ZERO );
            codeFlow.setUserId( userId );
            codeFlow.setDes( "人工入款" );
            codeFlowMapper.insertMemberBcode( codeFlow );
        }
        //加钱
        memberInfoMapper.updateMoneySelect( userId, money, null, beatMoney, null, null );
    }

    /**
     * 修改派送彩金暂存表
     *
     * @param memberMoney 派送彩金暂存表
     *
     * @return 结果
     */
    @Override
    public int updateMemberMoney( MemberMoney memberMoney ) {
        return memberMoneyMapper.updateMemberMoney( memberMoney );
    }

    /**
     * 批量删除派送彩金暂存表
     *
     * @param memberIds 需要删除的派送彩金暂存表ID
     *
     * @return 结果
     */
    @Override
    public int deleteMemberMoneyByIds( String[] memberIds ) {
        return memberMoneyMapper.deleteMemberMoneyByIds( memberIds );
    }

    /**
     * 删除派送彩金暂存表信息
     *
     * @param memberId 派送彩金暂存表ID
     *
     * @return 结果
     */
    @Override
    public int deleteMemberMoneyById( String memberId ) {
        return memberMoneyMapper.deleteMemberMoneyById( memberId );
    }


    /**
     * 批量清理临时支付表 clear the temporary payout table in batches
     */
    @Override
    public Integer clear() {
        return memberInfoMapper.clear();
    }

    @Override
    public BigDecimal countMoney() {
        return memberMoneyMapper.countMoney();
    }
}
