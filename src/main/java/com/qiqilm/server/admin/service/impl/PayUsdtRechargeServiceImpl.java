package com.qiqilm.server.admin.service.impl;

import com.google.common.io.CharStreams;
import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.*;
import com.qiqilm.server.admin.domain.req.ReqPayUsdtRecharge;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.*;
import com.qiqilm.server.admin.service.ILogService;
import com.qiqilm.server.admin.service.IPayUsdtRechargeService;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * USDT充值提交记录Service业务层处理
 *
 * @author 77tv
 * @date 2021-09-14
 */

@Log4j2
@Service
public class PayUsdtRechargeServiceImpl implements IPayUsdtRechargeService {
    @Resource
    private   PayUsdtRechargeMapper               payUsdtRechargeMapper;
    @Resource
    private   TokenService                        tokenService;
    @Resource
    private   RedisUtil                           redisUtil;
    @Resource
    private   MemberInfoMapper                    memberInfoMapper;
    @Resource
    private   ILogService                         logService;
    @Resource
    private   ConfigRecommendMapper               configRecommendMapper;
    @Resource
    private   MemberRecommendMapper               recommendMapper;
    @Resource
    private   MemberBcodeMapper                   memberBcodeMapper;
    @Resource
    private   ActivityCashBackFirstRechargeMapper cashBackFirstRechargeMapper;
    @Resource
    protected RestTemplate                        restTemplate;
    @Resource
    private   SysConfigCacheUtil                  sysConfigCacheUtil;

    /**
     * 查询USDT充值提交记录
     *
     * @param id USDT充值提交记录ID
     *
     * @return USDT充值提交记录
     */
    @Override
    public PayUsdtRecharge selectPayUsdtRechargeById( Long id ) {
        return payUsdtRechargeMapper.selectPayUsdtRechargeById( id );
    }

    /**
     * 查询USDT充值提交记录列表
     *
     * @param reqPayUsdtRecharge USDT充值提交记录
     *
     * @return USDT充值提交记录
     */
    @Override
    public List<PayUsdtRecharge> selectPayUsdtRechargeList( ReqPayUsdtRecharge reqPayUsdtRecharge ) {
        String[] selectDate = reqPayUsdtRecharge.getSelectDate();
        if ( selectDate != null && selectDate.length > 0 ) {
            reqPayUsdtRecharge.setSelectStartDate( selectDate[ 0 ] );
            reqPayUsdtRecharge.setSelectEndDate( selectDate[ 1 ] );
        }
        return payUsdtRechargeMapper.selectPayUsdtRechargeList( reqPayUsdtRecharge );
    }

    @Override
    public Map listCount( ReqPayUsdtRecharge req ) {
        String[] selectDate = req.getSelectDate();
        if ( selectDate != null && selectDate.length > 0 ) {
            req.setSelectStartDate( selectDate[ 0 ] );
            req.setSelectEndDate( selectDate[ 1 ] );
        }
        return payUsdtRechargeMapper.listCount( req );
    }

    /**
     * 新增USDT充值提交记录
     *
     * @param payUsdtRecharge USDT充值提交记录
     *
     * @return 结果
     */
    @Override
    public int insertPayUsdtRecharge( PayUsdtRecharge payUsdtRecharge ) {
        payUsdtRecharge.setCreateTime( DateUtils.getNowDate() );
        return payUsdtRechargeMapper.insertPayUsdtRecharge( payUsdtRecharge );
    }

    /**
     * 锁定USDT充值提交记录
     *
     * @param id
     *
     * @return 结果
     */
    @Override
    public int lock( Long id ) {
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();

        PayUsdtRecharge payUsdtRecharge = this.selectPayUsdtRechargeById( id );
        if ( !"1".equals( payUsdtRecharge.getStatus() ) ) {
            throw new BusinessException( "订单状态有误,请刷新数据" );
        }

        PayUsdtRecharge update = new PayUsdtRecharge();
        update.setId( id );
        update.setOpName( userName );
        update.setRemark( "锁定人:" + userName );
        update.setStatus( "0" );
        return payUsdtRechargeMapper.updatePayUsdtRecharge( update );
    }

    /**
     * 解锁USDT充值提交记录
     *
     * @param id
     *
     * @return 结果
     */
    @Override
    public AjaxResult unLock( Long id ) {
        LoginUser       loginUser       = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String          userName        = loginUser.getUser().getUserName();
        PayUsdtRecharge payUsdtRecharge = this.selectPayUsdtRechargeById( id );

        if ( !"0".equals( payUsdtRecharge.getStatus() ) ) {
            return AjaxResult.error( "订单状态有误,请刷新数据" );
        }

        List<SysRole> roles    = loginUser.getUser().getRoles();
        boolean       contains = roles.stream().anyMatch( m -> "common".equals( m.getRoleKey() ) );
        if ( !contains ) {
            if ( StringUtils.hasText( payUsdtRecharge.getOpName() ) && !userName.equals( payUsdtRecharge.getOpName() ) ) {
                return AjaxResult.error( "该订单只能由" + payUsdtRecharge.getOpName() + "处理" );
            }
        }
        payUsdtRecharge.setId( id );
        payUsdtRecharge.setOpName( userName );
        payUsdtRecharge.setRemark( "解锁人:" + userName );
        payUsdtRecharge.setStatus( "1" );
        return AjaxResult.success( payUsdtRechargeMapper.updatePayUsdtRecharge( payUsdtRecharge ) );
    }

    /**
     * 拒绝USDT充值提交记录
     *
     * @param payUsdtRecharge USDT充值提交记录
     *
     * @return 结果
     */
    @Override
    public int refusePayUsdtRecharge( PayUsdtRecharge payUsdtRecharge ) {
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        payUsdtRecharge.setOpName( userName );
        payUsdtRecharge.setUpdateTime( new Date() );
        payUsdtRecharge.setStatus( "2" );
        return payUsdtRechargeMapper.updatePayUsdtRecharge( payUsdtRecharge );
    }

    /**
     * 通过USDT充值提交记录
     *
     * @param payUsdtRecharge USDT充值提交记录
     *
     * @return 结果
     */
    @Override
    @Transactional( rollbackFor = Exception.class )
    public AjaxResult updatePayUsdtRecharge( PayUsdtRecharge payUsdtRecharge ) {
        LoginUser       loginUser        = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String          userName         = loginUser.getUser().getUserName();
        PayUsdtRecharge payUsdtRecharge1 = this.selectPayUsdtRechargeById( payUsdtRecharge.getId() );
        if ( payUsdtRecharge1 == null ) {
            return AjaxResult.error( "该充值记录不存在" );
        }
        if ( !"0".equals( payUsdtRecharge1.getStatus() ) ) {
            return AjaxResult.error( "该充值记录状态有误，请刷新数据后重试" );
        }
        if ( !redisUtil.lock( EnumLock.usdt, payUsdtRecharge1.getMemberId(), "1", 5 ) ) {
            return AjaxResult.error( "请勿重复提交" );
        }
        payUsdtRecharge1.setOpName( userName );
        payUsdtRecharge1.setUpdateTime( new Date() );
        payUsdtRecharge1.setRemark( payUsdtRecharge.getRemark() );
        payUsdtRecharge1.setStatus( "3" );

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
    public boolean updatePayUsdtRechargeLogic( PayUsdtRecharge payUsdtRecharge ) {
        MemberInfo memberInfo = memberInfoMapper.selectMemberInfoById( payUsdtRecharge.getMemberId() );

        boolean isFirst = memberInfo.getLevelIntegral().compareTo( BigDecimal.ZERO ) == 0
                || memberInfo.getLevelIntegral().compareTo( memberInfo.getInviteMoney() ) <= 0;

        BigDecimal firstRechargeCashBack = BigDecimal.ZERO; // 首冲赠送彩金
        if ( isFirst && sysConfigCacheUtil.getConfBool( "is_first_recharge_cash_back" ) ) {
            BigDecimal rebate = cashBackFirstRechargeMapper.selectByRechargeMoney( payUsdtRecharge.getRechargeMoney() );
            if ( rebate != null && rebate.compareTo( BigDecimal.ZERO ) > 0 ) {
                firstRechargeCashBack = rebate;
            }
        }

        BigDecimal chargeGive = BigDecimal.ZERO; // 充值彩金
        BigDecimal multi      = BigDecimal.ONE;

        //套利号无优惠
        if ( memberInfo.getStatus() != 4 ) {
            String usdtRechargeRewardRates = sysConfigCacheUtil.getConf( "usdt_next_recharge_rate" );
            if ( StringUtils.hasText( usdtRechargeRewardRates ) && usdtRechargeRewardRates.contains( "," ) ) {
                List<String> usdtNextRechargeRates = Arrays.asList( usdtRechargeRewardRates.split( ";" ) );
                // 翻转排序,先判断最大的
                Collections.reverse( usdtNextRechargeRates );
                for ( String usdtNextRechargeRate : usdtNextRechargeRates ) {
                    String[]   firstPaySplit = usdtNextRechargeRate.split( "," );
                    BigDecimal money         = new BigDecimal( firstPaySplit[ 0 ] );
                    BigDecimal rate          = new BigDecimal( firstPaySplit[ 1 ] );
                    if ( payUsdtRecharge.getRechargeMoney().compareTo( money ) >= 0 ) {
                        chargeGive = payUsdtRecharge.getRechargeMoney().multiply( rate ).setScale( 2, RoundingMode.HALF_UP );
                        if ( firstPaySplit.length >= 3 ) {
                            multi = new BigDecimal( firstPaySplit[ 2 ] );
                        }
                        log.warn( "USDT优惠比例 - 订单号:{} - 充值金额:{} - 会员ID:{} - 配置:{} - 赠送金额:{}", payUsdtRecharge.getId(),
                                payUsdtRecharge.getRechargeMoney(), payUsdtRecharge.getMemberId(), usdtNextRechargeRate,
                                chargeGive.stripTrailingZeros().toPlainString() );
                        break;
                    }
                }
            } else {
                chargeGive = payUsdtRecharge.getDiscountBill().multiply( payUsdtRecharge.getRechargeMoney() )
                        .setScale( 2, RoundingMode.HALF_UP );
            }
        }

        //优惠钱+充值金额+首冲优惠
        BigDecimal add = payUsdtRecharge.getRechargeMoney().add( chargeGive ).add( firstRechargeCashBack );

        // 首冲赠送彩金
        if ( firstRechargeCashBack.compareTo( BigDecimal.ZERO ) > 0 ) {
            logService.logMoneyAdd( null, memberInfo.getId(), memberInfo.getUserName(), EnumMoney.wongive,
                    firstRechargeCashBack, memberInfo.getTotalAccount()
                    .add( chargeGive ).add( firstRechargeCashBack ), "首冲赠送彩金；代充", payUsdtRecharge.getTransactionId() );
        }

        //充值彩金日志
        if ( chargeGive.compareTo( BigDecimal.ZERO ) > 0 ) {
            logService.logMoneyAdd( null, memberInfo.getId(), memberInfo.getUserName(), EnumMoney.chargegive, chargeGive,
                    memberInfo.getTotalAccount()
                    .add( payUsdtRecharge.getRechargeMoney() ), payUsdtRecharge.getRemark(), payUsdtRecharge.getTransactionId() );
        }

        //usdt充值日志
        logService.logMoneyAdd( "usdtRecharge-"
                + payUsdtRecharge.getId(), payUsdtRecharge.getMemberId(), memberInfo.getUserName(), EnumMoney.usdt,
                payUsdtRecharge.getRechargeMoney(), memberInfo.getTotalAccount(), payUsdtRecharge.getRemark(),
                payUsdtRecharge.getTransactionId() );

        //更新usdt充值记录表状态
        payUsdtRechargeMapper.updatePayUsdtRecharge( payUsdtRecharge );

        //新增佣金记录
        this.recommendProcess( payUsdtRecharge, memberInfo );

        //更新用户账户余额
        boolean updateMemberCharge = this.updateMemberCharge( memberInfo.getId(), add, "USDT充值", multi );
        if ( updateMemberCharge ) {
            this.paySendIm( memberInfo.getId(), payUsdtRecharge.getRechargeMoney() );
        }
        return updateMemberCharge;
    }

    @Async
    public void paySendIm( String userId, BigDecimal orderAmount ) {
        String pay_seccess_im_url = sysConfigCacheUtil.getConf( "pay_seccess_im_url" );
        if ( !StringUtils.hasText( pay_seccess_im_url ) ) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put( "userId", userId );
        params.put( "orderAmount", String.valueOf( orderAmount ) );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON );
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>( params, httpHeaders );

        Map<String, Object> resultMap = null;
        try {
            resultMap = restTemplate.execute( pay_seccess_im_url, HttpMethod.POST,
                    restTemplate.httpEntityCallback( httpEntity ), response -> {
                InputStream bodyStream = response.getBody();
                String      text;
                try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                    text = CharStreams.toString( reader );
                }
                return JsonUtil.json2Map( text );
            } );
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
    }

    private void recommendProcess( PayUsdtRecharge payUsdtRecharge, MemberInfo memberInfo ) {
        //新增佣金记录
        if ( org.apache.commons.lang3.StringUtils.isNotBlank( memberInfo.getInviterCode() ) ) {
            Map<Integer, ConfigRecommend> billMap = configRecommendMapper.selectConfigRecommendList( null ).stream()
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

    private boolean updateMemberCharge( String userId, BigDecimal money, String chargeType, BigDecimal multi ) {
        MemberBcode codeFlow = new MemberBcode();
        codeFlow.setId( UuidUtil.getRandomUuidWithoutSeparator() );
        codeFlow.setIncome( money.multiply( multi ).setScale( 2, RoundingMode.DOWN ) );//
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
     *
     * @return 结果
     */
    @Override
    public int deletePayUsdtRechargeByIds( Long[] ids ) {
        return payUsdtRechargeMapper.deletePayUsdtRechargeByIds( ids );
    }

    /**
     * 删除USDT充值提交记录信息
     *
     * @param id USDT充值提交记录ID
     *
     * @return 结果
     */
    @Override
    public int deletePayUsdtRechargeById( Long id ) {
        return payUsdtRechargeMapper.deletePayUsdtRechargeById( id );
    }
}
