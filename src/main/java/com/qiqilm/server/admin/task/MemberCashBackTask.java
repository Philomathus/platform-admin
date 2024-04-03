package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.domain.ActivityCashBack;
import com.qiqilm.server.admin.domain.MemberBcode;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.vo.MemberSumRecharge;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.*;
import com.qiqilm.server.admin.service.ILogService;
import com.qiqilm.server.admin.utils.*;
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
    private MemberRechargeLogMapper memberRechargeLogMapper;
    @Resource
    private ActivityCashBackMapper  activityCashBackMapper;
    @Resource
    private MemberBcodeMapper       memberBcodeMapper;
    @Resource
    private MemberInfoMapper        memberInfoMapper;
    @Resource
    private LogMoneyMapper          logMoneyMapper;
    @Resource
    private SysConfigCacheUtil      sysConfigCacheUtil;
    @Resource
    private ILogService             logService;
    @Resource
    private RedisUtil               redisUtil;

    @Scheduled( cron = "0 58 15 * * ?" )// 每天15:58点执行一次
    @Scheduled( cron = "0 58 17 * * ?" )// 每天17:58点执行一次
    public void cashBackTask() {
        int cashBackSwitch = sysConfigCacheUtil.getConfInt( "cash_back_switch" );
        if ( cashBackSwitch <= 0 ) {
            return;
        }
        if ( !redisUtil.adminLock( EnumLock.adminTask, getClass().getSimpleName(), 1000 ) ) {
            return;
        }
        log.info( "开始执行充值返现活动任务" );

        //查询昨天公司入款金额
        List<MemberSumRecharge> memberRechargeLogs;
        if ( cashBackSwitch == 1 ) {
            memberRechargeLogs = memberRechargeLogMapper.bankRechargeSum();
        } else {
            memberRechargeLogs = memberRechargeLogMapper.allRechargeSum();
        }

        log.warn( "执行充值返现活动任务 - 昨日充值会员:{}", JsonUtil.object2Json( memberRechargeLogs ) );
        ActivityCashBack query = new ActivityCashBack();
        query.setStatus( "1" );
        List<ActivityCashBack> activityCashBackList = activityCashBackMapper.selectActivityCashBackList( query );

        long now = System.currentTimeMillis();
        for ( MemberSumRecharge sumRecharge : memberRechargeLogs ) {
            Long bycash = null;
            for ( ActivityCashBack activityCashBack : activityCashBackList ) {
                if ( new BigDecimal( activityCashBack.getDepositTotalMin() ).compareTo( sumRecharge.getMoney() ) <= 0
                        && new BigDecimal( activityCashBack.getDepositTotalMax() ).compareTo( sumRecharge.getMoney() ) > 0 ) {
                    bycash = activityCashBack.getRebate();
                }
            }
            if ( bycash == null ) {
                log.warn( "执行充值返现活动任务 - 未达到充值标准的会员:{}, 金额:{}", sumRecharge.getMemberId(), sumRecharge.getMoney() );
                continue;
            }
            String memberIdW_ = sumRecharge.getMemberId().replace( "_", "" );
            String dbNodes    = sumRecharge.getMemberId().substring( sumRecharge.getMemberId().length() - 1 );

            String orderId = "CZFX" + DateFormatUtils.formate( new Date(), DateFormatUtils.TIGHT_PATTERN_DATE ) + memberIdW_;
            if ( logMoneyMapper.findExist( dbNodes, orderId ) != null ) {
                log.error( "执行充值返现活动任务 - 存在充值记录的会员:{}, 金额:{}", sumRecharge.getMemberId(), sumRecharge.getMoney() );
                continue;
            }
            try {
                SpringUtils
                        .getAopProxy( this )
                        .updateMemberCharge( sumRecharge.getMemberId(), new BigDecimal( bycash ), EnumMoney.activity.getDes(),
                                orderId );
            } catch ( Exception e ) {
                log.error( sumRecharge.getMemberId() + "数据插入失败" + e.getMessage(), e );
                log.error( "执行充值返现活动任务 - 充值失败的会员:{}, 金额:{}", sumRecharge.getMemberId(), sumRecharge.getMoney() );
            }
        }
        redisUtil.strSet( getClass().getSimpleName(), "0", Duration.ofHours( 23 ) );
        log.info( "充值返现活动任务执行时间:{}ms", System.currentTimeMillis() - now );
    }

    @Transactional( rollbackFor = Exception.class )
    public void updateMemberCharge( String userId, BigDecimal money, String chargeType, String orderId ) {
        MemberBcode codeFlow = new MemberBcode();
        codeFlow.setId( orderId );
        codeFlow.setIncome( money );//
        codeFlow.setCreateTime( new Date() );
        codeFlow.setStatus( 0 );
        codeFlow.setCur( BigDecimal.ZERO );
        codeFlow.setUserId( userId );
        codeFlow.setDes( chargeType );
        MemberInfo memberInfo = memberInfoMapper.selectMemberInfoById( userId );
        logService.logMoneyAdd( orderId, userId, memberInfo.getUserName(), EnumMoney.activity, money,
                memberInfo.getTotalAccount(), "充值返现活动", null );
        int i = memberBcodeMapper.insertMemberBcode( codeFlow );
        int j = memberInfoMapper.updateMoneySelect( userId, money, null, money, null, null );
        if ( i <= 0 || j <= 0 ) {
            throw new BusinessException( "数据插入失败" );
        }
    }
}
