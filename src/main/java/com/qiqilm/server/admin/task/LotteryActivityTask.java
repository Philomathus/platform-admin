package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.domain.LotteryDiceUser;
import com.qiqilm.server.admin.domain.MemberRechargeLog;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.LotteryDiceConfigMapper;
import com.qiqilm.server.admin.mapper.LotteryDiceUserMapper;
import com.qiqilm.server.admin.service.IMemberRechargeLogService;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

/**
 * 红包雨活动
 */
@Log4j2
@Component
public class LotteryActivityTask {
    @Resource
    private IMemberRechargeLogService memberRechargeLogService;
    @Resource
    private LotteryDiceConfigMapper   lotteryDiceConfigMapper;
    @Resource
    private SysConfigCacheUtil        sysConfigCacheUtil;
    @Resource
    private LotteryDiceUserMapper     lotteryDiceUserMapper;
    @Resource
    private RedisUtil                 redisUtil;

    @Scheduled( cron = "0 0 1 * * ?" )// 每天1:00点执行一次
    public void cashBackTask() {
        if ( !sysConfigCacheUtil.getConfBool( "lottery_activity_switch" ) ) {
            return;
        }
        if ( !redisUtil.adminLock( EnumLock.adminTask, getClass().getSimpleName(), 100 )
                || redisUtil.exists( getClass().getSimpleName() ) ) {
            return;
        }
        //查询昨天公司入款金额
        List<MemberRechargeLog> memberRechargeLogs = memberRechargeLogService.memberRechargeLogLists();
        if ( memberRechargeLogs == null || memberRechargeLogs.size() == 0 ) {
            log.warn( "昨日没充值数据" );
            return;
        }
        long now = System.currentTimeMillis();
        for ( MemberRechargeLog memberRechargeLog : memberRechargeLogs ) {
            //抽奖次数
            Integer diceBycash = lotteryDiceConfigMapper.selectWheelDiceBycash( memberRechargeLog.getRechargeMoney() );
            if ( diceBycash != null ) {
                //抽奖次数
                try {
                    this.updateLotteryTimes( memberRechargeLog.getMemberId(), diceBycash, memberRechargeLog.getRechargeMoney() );
                } catch ( Exception e ) {
                    log.error( memberRechargeLog.getMemberId() + "数据插入失败" + e.getMessage(), e );
                }
            }
        }
        redisUtil.strSet( getClass().getSimpleName(), "0", Duration.ofHours( 23 ) );
        log.info( "红包雨任务执行时间:{}ms", System.currentTimeMillis() - now );
    }

    private void updateLotteryTimes( String userId, Integer times, BigDecimal rechargeMoney ) {
        LotteryDiceUser lotteryDiceUser = lotteryDiceUserMapper.selectLotteryDiceUserById( userId );
        int             i;
        if ( lotteryDiceUser == null ) {
            LotteryDiceUser diceUser = new LotteryDiceUser();
            diceUser.setId( userId );
            diceUser.setType( 1 );
            diceUser.setRechargeMoney( rechargeMoney );
            diceUser.setTimes( times );
            i = lotteryDiceUserMapper.insertLotteryDiceUser( diceUser );
        } else {
            lotteryDiceUser.setTimes( times );
            lotteryDiceUser.setRechargeMoney( rechargeMoney );
            i = lotteryDiceUserMapper.updateLotteryDiceUser( lotteryDiceUser );
        }
        if (i <= 0){
            throw new BusinessException( "数据插入失败" );
        }
    }

}
