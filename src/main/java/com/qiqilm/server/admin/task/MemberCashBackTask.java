package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.domain.MemberBcode;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.MemberRechargeLog;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.ActivityCashBackMapper;
import com.qiqilm.server.admin.mapper.LogMoneyMapper;
import com.qiqilm.server.admin.mapper.MemberBcodeMapper;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.service.ILogService;
import com.qiqilm.server.admin.service.IMemberRechargeLogService;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.UuidUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Date;
import java.util.List;

/**
 * 充值返现活动
 */
@Log4j2
@Component
public class MemberCashBackTask {
    @Resource
    private IMemberRechargeLogService memberRechargeLogService;
    @Resource
    private ActivityCashBackMapper    activityCashBackMapper;
    @Resource
    private MemberBcodeMapper         memberBcodeMapper;
    @Resource
    private MemberInfoMapper          memberInfoMapper;
    @Resource
    private LogMoneyMapper            logMoneyMapper;
    @Resource
    private SysConfigCacheUtil        sysConfigCacheUtil;
    @Resource
    private ILogService               logService;
    @Resource
    private RedisUtil                 redisUtil;

    @Scheduled( cron = "0 0 16 * * ?" )// 每天16:00点执行一次
    public void cashBackTask() {
        if ( !sysConfigCacheUtil.getConfBool( "cash_back_switch" ) ) {
            return;
        }
        if ( !redisUtil.adminLock( EnumLock.adminTask, getClass().getSimpleName(), 100 )
                || redisUtil.exists( getClass().getSimpleName() ) ) {
            return;
        }
        //查询昨天公司入款金额
        List<MemberRechargeLog> memberRechargeLogs = memberRechargeLogService.memberRechargeLogLists();
        long                    now                = System.currentTimeMillis();
        for ( MemberRechargeLog memberRechargeLog : memberRechargeLogs ) {
            //要返现金额
            Integer bycash = activityCashBackMapper.selectActivityCashBackBycash( memberRechargeLog.getRechargeMoney() );
            if ( bycash != null ) {
                int count = logMoneyMapper.findExistActivityCashBack( memberRechargeLog.getMemberId(), memberRechargeLog
                        .getMemberId().substring( memberRechargeLog.getMemberId().length() - 1 ) );
                if ( count > 0 ) {
                    continue;
                }
                //会员返现
                try {
                    this.updateMemberCharge( memberRechargeLog.getMemberId(), new BigDecimal( bycash ),
                            EnumMoney.activity.getDes(), memberRechargeLog.getOrderNo() );
                } catch ( Exception e ) {
                    log.error( memberRechargeLog.getMemberId() + "数据插入失败" + e.getMessage(), e );
                }
            }
        }
        redisUtil.strSet( getClass().getSimpleName(), "0", Duration.ofHours( 23 ) );
        log.info( "充值返现活动任务执行时间:{}ms", System.currentTimeMillis() - now );
    }

    @Transactional( rollbackFor = Exception.class )
    public void updateMemberCharge( String userId, BigDecimal money, String chargeType, String orderNo ) {
        MemberBcode codeFlow = new MemberBcode();
        codeFlow.setId( UuidUtil.getRandomUuidWithoutSeparator() );
        codeFlow.setIncome( money );//
        codeFlow.setCreateTime( new Date() );
        codeFlow.setStatus( 0 );
        codeFlow.setCur( BigDecimal.ZERO );
        codeFlow.setUserId( userId );
        codeFlow.setDes( chargeType );
        MemberInfo memberInfo = memberInfoMapper.selectMemberInfoById( userId );
        if ( memberInfo == null ) {
            return;
        }
        //日志
        logService.logMoneyAdd( null, userId, memberInfo.getUserName(), EnumMoney.activity, money, memberInfo.getTotalAccount()
                , "充值返现活动", orderNo );
        int i = memberBcodeMapper.insertMemberBcode( codeFlow );
        int j = memberInfoMapper.updateMoneySelect( userId, money, null, money, null, null );
        if ( i <= 0 || j <= 0 ) {
            throw new BusinessException( "数据插入失败" );
        }
    }
}
