package com.qiqilm.server.admin.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.qiqilm.server.admin.cache.MemberCacheManager;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.*;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.mapper.*;
import com.qiqilm.server.admin.service.ILogService;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.service.IPayUsdtRechargeService;
import org.springframework.transaction.annotation.Transactional;

/**
 * USDT充值提交记录Service业务层处理
 *
 * @author 77tv
 * @date 2021-09-14
 */

@Log4j2
@Service
public class PayUsdtRechargeServiceImpl implements IPayUsdtRechargeService {
    @Autowired
    private PayUsdtRechargeMapper payUsdtRechargeMapper;
    @Autowired
    private TokenService       tokenService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MemberInfoMapper memberInfoMapper;
    @Autowired
    private ILogService logService;
    @Autowired
    private ConfigRecommendMapper configRecommendMapper;
    @Autowired
    private MemberRecommendMapper recommendMapper;
    @Autowired
    private MemberCacheManager memberCacheManager;
    @Autowired
    private MemberBcodeMapper memberBcodeMapper;

    /**
     * 查询USDT充值提交记录
     *
     * @param id USDT充值提交记录ID
     * @return USDT充值提交记录
     */
    @Override
    public PayUsdtRecharge selectPayUsdtRechargeById(Long id) {
        return payUsdtRechargeMapper.selectPayUsdtRechargeById(id);
    }

    /**
     * 查询USDT充值提交记录列表
     *
     * @param payUsdtRecharge USDT充值提交记录
     * @return USDT充值提交记录
     */
    @Override
    public List<PayUsdtRecharge> selectPayUsdtRechargeList(PayUsdtRecharge payUsdtRecharge) {
        String[] selectDate = payUsdtRecharge.getSelectDate();
        if ( selectDate != null && selectDate.length > 0 ) {
            payUsdtRecharge.setSelectStartDate( selectDate[ 0 ]+ " 00:00:00");
            payUsdtRecharge.setSelectEndDate( selectDate[ 1 ] + " 23:59:59" );
        }
        return payUsdtRechargeMapper.selectPayUsdtRechargeList(payUsdtRecharge);
    }

    /**
     * 新增USDT充值提交记录
     *
     * @param payUsdtRecharge USDT充值提交记录
     * @return 结果
     */
    @Override
    public int insertPayUsdtRecharge(PayUsdtRecharge payUsdtRecharge) {
        payUsdtRecharge.setCreateTime(DateUtils.getNowDate());
        return payUsdtRechargeMapper.insertPayUsdtRecharge(payUsdtRecharge);
    }

    /**
     * 修改USDT充值提交记录
     *
     * @param payUsdtRecharge USDT充值提交记录
     * @return 结果
     */
    @Override
    @Transactional( rollbackFor = Exception.class )
    public AjaxResult updatePayUsdtRecharge(PayUsdtRecharge payUsdtRecharge) {
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        PayUsdtRecharge payUsdtRecharge1 = this.selectPayUsdtRechargeById(payUsdtRecharge.getId());
        payUsdtRecharge1.setOpName(userName);
        payUsdtRecharge1.setUpdateTime(new Date());
        payUsdtRecharge1.setRemark(payUsdtRecharge.getRemark());
        payUsdtRecharge1.setStatus(payUsdtRecharge.getStatus());
        //驳回
        if(StringUtils.isNotBlank(payUsdtRecharge.getStatus()) && "2".equals(payUsdtRecharge.getStatus())){
            return AjaxResult.success(payUsdtRechargeMapper.updatePayUsdtRecharge(payUsdtRecharge1));
        }
        //通过
        if ( payUsdtRecharge1 == null ) {
            return AjaxResult.error( "该充值记录不存在" );
        }
        if ( !"0".equals(payUsdtRecharge1.getStatus()) ) {
            return AjaxResult.error( "该充值记录状态有误，请刷新数据后重试" );
        }
        if ( !redisUtil.lock( EnumLock.usdt, payUsdtRecharge1.getMemberId(), "1", 5 ) ) {
            return AjaxResult.error( "请勿重复提交" );
        }
        try {
            boolean isAudit = this.updatePayUsdtRechargeLogic( payUsdtRecharge1 );
            return isAudit ? AjaxResult.success( "审核通过成功" ) : AjaxResult.error( "审核通过失败" );
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
            return AjaxResult.error( "订单审核通过有误" );
        } finally {
            redisUtil.unLock( EnumLock.usdt, payUsdtRecharge1.getMemberId() );
        }
    }

    @Transactional( rollbackFor = Exception.class )
    public boolean updatePayUsdtRechargeLogic(PayUsdtRecharge payUsdtRecharge){
        MemberInfo memberInfo = memberInfoMapper.selectMemberInfoById( payUsdtRecharge.getMemberId() );

        BigDecimal chargeGive = payUsdtRecharge.getDiscountBill().multiply( payUsdtRecharge.getRechargeMoney() )
                .setScale( 2, BigDecimal.ROUND_HALF_UP ); // 充值彩金

        //套利号无优惠
        if(memberInfo.getStatus()==4){
            chargeGive = BigDecimal.ZERO;
        }

        //优惠钱+充值金额
        BigDecimal add = payUsdtRecharge.getRechargeMoney().add( chargeGive );

        //充值彩金日志
        if ( chargeGive.compareTo( BigDecimal.ZERO ) > 0 ) {
            logService.logMoneyAdd( null, memberInfo.getId(), memberInfo.getUserName(), EnumMoney.chargegive, chargeGive
                    , memberInfo.getTotalAccount().add( payUsdtRecharge.getRechargeMoney() ), payUsdtRecharge.getRemark(), payUsdtRecharge.getTransactionId() );
        }

        //充值日志
        logService.logMoneyAdd( payUsdtRecharge.getTransactionId(), payUsdtRecharge.getMemberId(), memberInfo.getUserName(), EnumMoney.usdt,
                payUsdtRecharge.getRechargeMoney(), memberInfo.getTotalAccount(), payUsdtRecharge.getRemark(), payUsdtRecharge.getTransactionId() );

        //更新usdt充值记录表状态
        payUsdtRechargeMapper.updatePayUsdtRecharge(payUsdtRecharge);

         //新增佣金记录
        this.recommendProcess( payUsdtRecharge, memberInfo );
        try {
            memberCacheManager.bankChargeMount(payUsdtRecharge.getMemberId());
            if(memberInfo.getLevelIntegral().compareTo(BigDecimal.ZERO)==0||memberInfo.getLevelIntegral().compareTo(memberInfo.getInviteMoney())<=0){
                memberCacheManager.checkFirstChargeaddWheelTimes(payUsdtRecharge.getMemberId());
            }
        }catch (Exception e){
            log.error("首充报错",e);
        }

        //更新用户账户余额
        return this.updateMemberCharge( memberInfo.getId(), add, "线下存款" );
    }

    private void recommendProcess(PayUsdtRecharge payUsdtRecharge, MemberInfo memberInfo ) {
        //新增佣金记录
        if ( org.apache.commons.lang3.StringUtils.isNotBlank( memberInfo.getInviterCode() ) ) {
            Map<Integer, ConfigRecommend> billMap = configRecommendMapper.selectConfigRecommendList( null )
                    .stream()
                    .collect( Collectors.toMap( ConfigRecommend::getLevel, Function.identity() ) );

            MemberInfo rd1 = memberInfoMapper.findRecommendByInviterCode( memberInfo.getInviterCode() );
            MemberInfo rd2 = null;
            if ( rd1 != null ) {
                BigDecimal      commission       = payUsdtRecharge.getRechargeMoney().multiply( billMap.get( 1 ).getBill() );
                MemberRecommend recommendUserLog = new MemberRecommend();
                recommendUserLog.setId( UuidUtil.getRandomUuidWithoutSeparator() );
                recommendUserLog.setLevel( 1 );
                recommendUserLog.setMemberId( payUsdtRecharge.getMemberId() );
                recommendUserLog.setMemberName( memberInfo.getUserName() );
                recommendUserLog.setInviterId( rd1.getId() );
                recommendUserLog.setInviter( rd1.getUserName() );
                recommendUserLog.setCreateTime( new Date() );
                recommendUserLog.setCommission( commission );
                recommendUserLog.setStatus( 0 );
                recommendUserLog.setCode( memberInfo.getMemberCode() );
                recommendUserLog.setOrderMoney( payUsdtRecharge.getRechargeMoney() );
                recommendMapper.insertMemberRecommend( recommendUserLog );
                if ( org.apache.commons.lang3.StringUtils.isNotBlank( rd1.getInviterCode() ) ) {
                    rd2 = memberInfoMapper.findRecommendByInviterCode( rd1.getInviterCode() );
                }
            }

            if ( rd2 != null ) {
                BigDecimal      commission       = payUsdtRecharge.getRechargeMoney().multiply( billMap.get( 2 ).getBill() );
                MemberRecommend recommendUserLog = new MemberRecommend();
                recommendUserLog.setId( UuidUtil.getRandomUuidWithoutSeparator() );
                recommendUserLog.setLevel( 2 );
                recommendUserLog.setMemberId( payUsdtRecharge.getMemberId() );
                recommendUserLog.setMemberName( memberInfo.getUserName() );
                recommendUserLog.setInviterId( rd2.getId() );
                recommendUserLog.setInviter( rd2.getUserName() );
                recommendUserLog.setCreateTime( new Date() );
                recommendUserLog.setCommission( commission );
                recommendUserLog.setStatus( 0 );
                recommendUserLog.setCode( memberInfo.getMemberCode() );
                recommendUserLog.setOrderMoney( payUsdtRecharge.getRechargeMoney() );
                recommendMapper.insertMemberRecommend( recommendUserLog );
            }
        }
    }

    private boolean updateMemberCharge( String userId, BigDecimal money, String chargeType ) {
        MemberBcode codeFlow = new MemberBcode();
        codeFlow.setId( UuidUtil.getRandomUuidWithoutSeparator() );
        codeFlow.setIncome( money );//
        codeFlow.setCreateTime( new Date() );
        codeFlow.setStatus( 0 );
        codeFlow.setCur( BigDecimal.ZERO );
        codeFlow.setUserId( userId );
        codeFlow.setDes( chargeType );
        return memberBcodeMapper.insertMemberBcode( codeFlow ) > 0
                && memberInfoMapper.updateMoneySelect( userId, money, null, money, null, null ) > 0;
    }

    /**
     * 批量删除USDT充值提交记录
     *
     * @param ids 需要删除的USDT充值提交记录ID
     * @return 结果
     */
    @Override
    public int deletePayUsdtRechargeByIds(Long[] ids) {
        return payUsdtRechargeMapper.deletePayUsdtRechargeByIds(ids);
    }

    /**
     * 删除USDT充值提交记录信息
     *
     * @param id USDT充值提交记录ID
     * @return 结果
     */
    @Override
    public int deletePayUsdtRechargeById(Long id) {
        return payUsdtRechargeMapper.deletePayUsdtRechargeById(id);
    }
}
