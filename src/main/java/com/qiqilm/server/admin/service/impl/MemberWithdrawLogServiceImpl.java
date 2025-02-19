package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.*;
import com.qiqilm.server.admin.domain.req.ReqMemberWithdrawLog;
import com.qiqilm.server.admin.domain.rsp.RspMemberInfo;
import com.qiqilm.server.admin.domain.vo.WithdrawReport;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.*;
import com.qiqilm.server.admin.payagent.BasePayAgent;
import com.qiqilm.server.admin.payagent.PayAgentProcessorFactoryUtil;
import com.qiqilm.server.admin.service.ILogService;
import com.qiqilm.server.admin.service.IMemberWithdrawLogService;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 会员提现信息Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-30
 */
@Service
@Log4j2
public class MemberWithdrawLogServiceImpl implements IMemberWithdrawLogService {
    @Autowired
    private MemberWithdrawLogMapper      memberWithdrawLogMapper;
    @Autowired
    private MemberInfoMapper             memberInfoMapper;
    @Autowired
    private PayAgentLogMapper            payAgentLogMapper;
    @Autowired
    private PayAgentPlatformMapper       payAgentPlatformMapper;
    @Autowired
    private BankCardAddressMapper        bankCardAddressMapper;
    @Autowired
    private TokenService                 tokenService;
    @Autowired
    private ILogService                  logService;
    @Autowired
    private RedisUtil                    redisUtil;
    @Autowired
    private PayAgentProcessorFactoryUtil payAgentProcessorFactoryUtil;
    @Autowired
    private SysConfigCacheUtil           sysConfigCacheUtil;
    @Autowired
    private PayAgentServiceImpl          payAgentServiceImpl;

    /**
     * 查询会员提现信息
     *
     * @param id 会员提现信息ID
     *
     * @return 会员提现信息
     */
    @Override
    public MemberWithdrawLog selectMemberWithdrawLogById( String id ) {
        return memberWithdrawLogMapper.selectMemberWithdrawLogById( id );
    }

    /**
     * 查询会员提现信息列表
     *
     * @param memberWithdrawLog 会员提现信息
     *
     * @return 会员提现信息
     */
    @Override
    public List<MemberWithdrawLog> selectMemberWithdrawLogList( MemberWithdrawLog memberWithdrawLog ) {
        List<MemberWithdrawLog> memberWithdrawLogList = memberWithdrawLogMapper.selectMemberWithdrawLogList( memberWithdrawLog );

        //查出会员状态是否为套利号
        if ( !CollectionUtils.isEmpty( memberWithdrawLogList ) ) {
            List<String> memberIds = new ArrayList<>();
            for ( MemberWithdrawLog me : memberWithdrawLogList ) {
                //入款人姓名不为空，并且入款人不包含提现人，整条数据标红警告
                if ( Strings.isNotBlank( me.getRechargeUserName() ) && Strings.isNotBlank( me.getBankUserName() ) && !me
                        .getRechargeUserName()
                        .contains( me.getBankUserName() ) ) {
                    me.setRechargeUserNameStatus( 1 );//等于1,数据警告
                } else {
                    me.setRechargeUserNameStatus( 0 );
                }
                memberIds.add( me.getMemberId() );
            }
            List<MemberWithdrawLog> Statuss = memberWithdrawLogMapper.selectMemberIdStatus( memberIds );
            for ( MemberWithdrawLog me : memberWithdrawLogList ) {
                for ( MemberWithdrawLog st : Statuss ) {
                    if ( me.getMemberId().equals( st.getMemberId() ) ) {
                        me.setMemberStatus( st.getMemberStatus() );
                    }
                }
            }
        }

        BankCardAddress bankCardAddress = new BankCardAddress();
        bankCardAddress.setStatus( "1" );
        List<BankCardAddress> bankCardAddresses = bankCardAddressMapper.selectBankCardAddressList( bankCardAddress );
        if ( !CollectionUtils.isEmpty( memberWithdrawLogList ) && !CollectionUtils.isEmpty( bankCardAddresses ) ) {
            for ( MemberWithdrawLog me : memberWithdrawLogList ) {
                if ( StringUtils.hasText( me.getRealBankAddress() ) ) {
                    String[] arr = me.getRealBankAddress().split( "/" );
                    if ( arr.length > 1 ) {
                        me.setProvince( arr[ 0 ] );
                        me.setCity( arr[ 1 ] );
                        for ( BankCardAddress ba : bankCardAddresses ) {
                            if ( ba.getProvince().contains( me.getProvince() ) ) {
                                if ( ba.getCity().contains( me.getCity() ) ) {
                                    //来到这里,是在黑名单中
                                    me.setCardBlack( "1" );
                                } else {
                                    me.setCardBlack( "0" );
                                }
                            }
                        }
                    } else {
                        me.setCardBlack( "1" );
                    }
                }
            }
        }
        //银行卡黑名单搜索
        if ( StringUtils.hasText( memberWithdrawLog.getSearchCardBlack() ) ) {
            if ( !CollectionUtils.isEmpty( memberWithdrawLogList ) ) {
                Iterator<MemberWithdrawLog> it = memberWithdrawLogList.iterator();
                if ( "1".equals( memberWithdrawLog.getSearchCardBlack() ) ) {
                    while ( it.hasNext() ) {
                        if ( "0".equals( it.next().getCardBlack() ) ) {
                            it.remove();
                        }
                    }
                } else {
                    while ( it.hasNext() ) {
                        if ( "1".equals( it.next().getCardBlack() ) ) {
                            it.remove();
                        }
                    }
                }
            }
        }
        //风控打码倍数
        String multipleCode = sysConfigCacheUtil.getConf( "multiple_code" );
        for ( MemberWithdrawLog m : memberWithdrawLogList ) {
            m.setMultipleCode( multipleCode );
        }
        //注册48小时内,显示颜色
        if ( !CollectionUtils.isEmpty( memberWithdrawLogList ) ) {
            Date date = null;
            try {
                date = DateFormatUtils.addHour( new Date(), -48 );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            StringBuilder sb = new StringBuilder();
            for ( MemberWithdrawLog m : memberWithdrawLogList ) {
                sb.append( "\"" ).append( m.getMemberId() ).append( "\"," );
            }
            String memberIds = sb.toString();
            memberIds = memberIds.substring( 0, memberIds.length() - 1 );
            List<MemberInfo> memberInfos = memberInfoMapper.selectRegisterByMemberIds( memberIds );
            for ( MemberWithdrawLog m : memberWithdrawLogList ) {
                for ( MemberInfo me : memberInfos ) {
                    if ( m.getMemberId().equals( me.getId() ) ) {
                        if ( date.before( me.getRegTime() ) ) {
                            m.setRegisterColor( 1 );
                        }
                    }
                }
            }
        }
        //提款第一次和第二次，显示颜色
        //		StringBuilder sb = new StringBuilder();
        //		for (MemberWithdrawLog m : memberWithdrawLogList) {
        //			sb = sb.append("\"").append(m.getMemberId()).append("\",");
        //		}
        //		String memberIds = String.valueOf(sb);
        //		memberIds = memberIds.substring(0, memberIds.length() - 1);
        //		List<MemberWithdrawLog> memberWithdrawLogs = memberWithdrawLogMapper.selectRegisterByMemberIds(memberIds);
        //		if(memberWithdrawLogs.size() == 0 || CollectionUtils.isEmpty(memberWithdrawLogs)){
        //			for (MemberWithdrawLog m : memberWithdrawLogList) {
        //				m.setRegisterColor(1);
        //			}
        //		} else {
        //			for (MemberWithdrawLog m : memberWithdrawLogList) {
        //				for (MemberWithdrawLog me : memberWithdrawLogs) {
        //					if (m.getMemberId().equals(me.getMemberId())) {
        //						if (me.getCount() < 3) {
        //							m.setRegisterColor(1);
        //						}
        //					}
        //				}
        //			}
        //		}
        return memberWithdrawLogList;
    }

    @Override
    public List<MemberWithdrawLogShunWei> selectMemberWithdrawLogShunWeiList( ReqMemberWithdrawLog req ) {
        return memberWithdrawLogMapper.selectMemberWithdrawLogShunWeiList( req.getIds() );
    }

    @Override
    public AjaxResult refused( ReqMemberWithdrawLog req ) {
        MemberWithdrawLog memberWithdrawLog = this.selectMemberWithdrawLogById( req.getId() );
        if ( memberWithdrawLog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        if ( memberWithdrawLog.getStatus() == 2 ) {
            return AjaxResult.error( "订单重复处理" );
        }
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();

        String ip = UserDataUtil.getIp( ServletUtil.getHttpServletRequest() );

        if ( !StringUtils.isEmpty( memberWithdrawLog.getOpName() ) && !userName.equals( memberWithdrawLog.getOpName() ) ) {
            return AjaxResult.error( "该订单只能由" + memberWithdrawLog.getOpName() + "处理" );
        }
        if ( !redisUtil.lock( EnumLock.member, memberWithdrawLog.getMemberId(), "1", 5 ) ) {
            return AjaxResult.error( "请勿重复提交" );
        }
        if ( memberWithdrawLog.getStatus() < 2 || memberWithdrawLog.getStatus() == 5 || memberWithdrawLog.getStatus() == 7
                || memberWithdrawLog.getStatus() == 8 ) {
            memberWithdrawLog.setRemark( req.getRemark() );
            memberWithdrawLog.setStatus( 2 );//审核不通过
            memberWithdrawLog.setOpName( userName );
            memberWithdrawLog.setUpdateTime( new Date() );
            IMemberWithdrawLogService memberWithdrawLogService = SpringUtils.getBean( IMemberWithdrawLogService.class );
            memberWithdrawLogService.refusedUpdateProcess( memberWithdrawLog, userName, ip );
        } else {
            return AjaxResult.error( "会员账号" + memberWithdrawLog.getAccount() + "该笔订单状态" + memberWithdrawLog.getStatus()
                    + "该状态下订单不能拒绝" );
        }


        redisUtil.unLock( EnumLock.member, memberWithdrawLog.getMemberId() );
        return AjaxResult.success();
    }

    @Transactional( rollbackFor = Exception.class )
    public void refusedUpdateProcess( MemberWithdrawLog memberWithdrawLog, String userName, String ip ) {
        int updateW = memberWithdrawLogMapper.updateMemberWithdrawLog( memberWithdrawLog );
        //回退提现金额
        BigDecimal withdrawMoney = memberWithdrawLog.getWithdrawMoney();
        String     account       = memberWithdrawLog.getAccount();
        String     memberId      = memberWithdrawLog.getMemberId();
        String     accountName   = memberInfoMapper.selectUserNameById( memberId );
        if ( StringUtils.hasText( account ) && !account.equals( accountName )
                && "VIPPAY".equalsIgnoreCase( memberWithdrawLog.getBankCode() ) ) {
            withdrawMoney = withdrawMoney.subtract( new BigDecimal( account ) );
        }

        int updateM = memberInfoMapper.updateMoneySelect( memberId, withdrawMoney, null, null, null, null );
        if ( updateW <= 0 || updateM <= 0 ) {
            throw new BusinessException( "订单拒绝失败" );
        }
        BigDecimal now = memberInfoMapper.selectTotalAccountById( memberId );
        logService.logMoneyAll( memberId, memberId, EnumMoney.bohui, now, withdrawMoney, null,
                "驳回人：" + userName + "-" + ip, memberWithdrawLog.getOrderNo() + "bohui" );
    }

    @Override
    public AjaxResult refuseds( ReqMemberWithdrawLog req ) {
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();

        String ip = UserDataUtil.getIp( ServletUtil.getHttpServletRequest() );

        if ( !redisUtil.lock( EnumLock.adminUser, userName, "1", 5 ) ) {
            return AjaxResult.error( "请勿重复提交" );
        }
        List<MemberWithdrawLog> withdrawLogList = memberWithdrawLogMapper.selectByIds( req.getIds() );
        if ( CollectionUtils.isEmpty( withdrawLogList ) ) {
            return AjaxResult.error( "该订单已被处理,请刷新界面" );
        }
        IMemberWithdrawLogService service = SpringUtils.getBean( IMemberWithdrawLogService.class );
        for ( MemberWithdrawLog memberWithdrawLog : withdrawLogList ) {
            if ( memberWithdrawLog == null ) {
                return AjaxResult.error( "订单不存在" );
            }
            if ( !StringUtils.isEmpty( memberWithdrawLog.getOpName() ) && !userName.equals( memberWithdrawLog.getOpName() ) ) {
                return AjaxResult.error(
                        "会员账号" + memberWithdrawLog.getAccount() + "该笔订单只能由" + memberWithdrawLog.getOpName() + "处理" );
            }
            if ( memberWithdrawLog.getStatus() == 2 ) {
                return AjaxResult.error( "会员账号" + memberWithdrawLog.getAccount() + "该笔订单重复处理" );
            }
            if ( memberWithdrawLog.getStatus() < 2 || memberWithdrawLog.getStatus() == 5 || memberWithdrawLog.getStatus() == 7
                    || memberWithdrawLog.getStatus() == 8 ) {
                memberWithdrawLog.setRemark( req.getRemark() );
                memberWithdrawLog.setStatus( 2 );//审核不通过
                memberWithdrawLog.setOpName( userName );
                memberWithdrawLog.setUpdateTime( new Date() );
                service.refusedUpdateProcess( memberWithdrawLog, userName, ip );
            } else {
                return AjaxResult.error(
                        "会员账号" + memberWithdrawLog.getAccount() + "该笔订单状态" + memberWithdrawLog.getStatus()
                                + "该状态下订单不能拒绝" );
            }
        }

        redisUtil.unLock( EnumLock.adminUser, userName );
        return AjaxResult.success();
    }

    @Override
    @Transactional( rollbackFor = Exception.class )
    public AjaxResult back( ReqMemberWithdrawLog req ) {
        MemberWithdrawLog memberWithdrawLog = this.selectMemberWithdrawLogById( req.getId() );
        if ( memberWithdrawLog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        if ( memberWithdrawLog.getStatus() != 4 ) {
            return AjaxResult.error( "该订单状态不是代付中" );
        }
        PayAgentLog payAgentLog = payAgentLogMapper.selectPayAgentLogByWithdrawOrderNo( memberWithdrawLog.getOrderNo() );
        if ( payAgentLog != null ) {
            int i = payAgentLogMapper.deletePayAgentLogById( payAgentLog.getId() );
            if ( i < 1 ) {
                return AjaxResult.error( "代付记录删除失败，请重试!" );
            }
        }

        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();

        MemberWithdrawLog newMemberWithdrawLog = new MemberWithdrawLog();
        newMemberWithdrawLog.setId( memberWithdrawLog.getId() );
        newMemberWithdrawLog.setRemark( "由" + userName + "操作回退" );
        newMemberWithdrawLog.setStatus( 1 );
        newMemberWithdrawLog.setOpName( userName );

        int i = memberWithdrawLogMapper.updateMemberWithdrawLog( newMemberWithdrawLog );
        if ( i < 1 ) {
            return AjaxResult.error( "回退订单状态失败" );
        }
        return AjaxResult.success();
    }

    @Override
    @Transactional( rollbackFor = Exception.class )
    public AjaxResult failBack( ReqMemberWithdrawLog req ) {
        MemberWithdrawLog memberWithdrawLog = this.selectMemberWithdrawLogById( req.getId() );
        if ( memberWithdrawLog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        if ( memberWithdrawLog.getStatus() != 5 ) {
            return AjaxResult.error( "该订单状态不是代付失败" );
        }
        PayAgentLog payAgentLog = payAgentLogMapper.selectPayAgentLogByWithdrawOrderNo( memberWithdrawLog.getOrderNo() );
        if ( payAgentLog != null ) {
            int i = payAgentLogMapper.deletePayAgentLogById( payAgentLog.getId() );
            if ( i < 1 ) {
                return AjaxResult.error( "代付记录删除失败，请重试!" );
            }
        }

        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();

        MemberWithdrawLog newMemberWithdrawLog = new MemberWithdrawLog();
        newMemberWithdrawLog.setId( memberWithdrawLog.getId() );
        newMemberWithdrawLog.setRemark( "由" + userName + "操作回退" );
        newMemberWithdrawLog.setStatus( 1 );
        newMemberWithdrawLog.setOpName( userName );

        int i = memberWithdrawLogMapper.updateMemberWithdrawLog( newMemberWithdrawLog );
        if ( i < 1 ) {
            return AjaxResult.error( "回退订单状态失败" );
        }
        return AjaxResult.success();
    }

    @Override
    public AjaxResult queryStatus( ReqMemberWithdrawLog req ) {
        PayAgentLog payAgentLog = payAgentLogMapper.selectPayAgentLogByWithdrawOrderNo( req.getOrderNo() );
        if ( payAgentLog == null ) {
            return AjaxResult.error( "代付订单不存在" );
        }
        PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
        if ( payAgentPlatform == null ) {
            return AjaxResult.error( "此代付平台不存在" );
        }
        BasePayAgent basePayAgent = payAgentProcessorFactoryUtil.createPayProcessor( payAgentPlatform.getCode() );
        String       msg          = null;
        String       msgStatus    = null;
        try {
            msg = basePayAgent.queryOrderPay( payAgentLog );
            PayAgentLog payAgentLog1   = payAgentLogMapper.selectPayAgentLogOrderNo( req.getOrderNo() );
            Integer     callbackStatus = payAgentLog1.getCallbackStatus();
            //回调状态 0 代付处理中 1 代付成功 代付失败
            if ( callbackStatus == 0 ) {
                msgStatus = "代付处理中";
            } else if ( callbackStatus == 1 ) {
                msgStatus = "代付成功";
            } else {
                msgStatus = "代付失败";
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return AjaxResult.success( msgStatus + ",查询返回结果:" + msg );
    }

    @Override
    public AjaxResult lock( ReqMemberWithdrawLog req ) {
        MemberWithdrawLog memberWithdrawLog = this.selectMemberWithdrawLogById( req.getId() );
        if ( memberWithdrawLog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        if ( memberWithdrawLog.getStatus() == 1 ) {
            return AjaxResult.error( "该订单已被锁定,请刷新界面" );
        }
        if ( memberWithdrawLog.getStatus() == 2 ) {
            return AjaxResult.error( "该订单已被拒绝" );
        }
        if ( memberWithdrawLog.getStatus() != 5 && 1 < memberWithdrawLog.getStatus() ) {
            return AjaxResult.error( "审核流程非法" );
        }
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();

        if ( !StringUtils.isEmpty( memberWithdrawLog.getOpName() ) && !userName.equals( memberWithdrawLog.getOpName() ) ) {
            return AjaxResult.error( "该订单只能由" + memberWithdrawLog.getOpName() + "处理" );
        }

        memberWithdrawLog.setRemark( req.getRemark() );
        memberWithdrawLog.setStatus( 1 );
        memberWithdrawLog.setOpName( userName );
        memberWithdrawLog.setUpdateTime( new Date() );
        int i = memberWithdrawLogMapper.updateMemberWithdrawLog( memberWithdrawLog );
        if ( i > 0 ) {
            return AjaxResult.success();
        }

        return AjaxResult.error( "更新订单状态失败" );
    }

    @Override
    @Transactional( rollbackFor = Exception.class )
    public AjaxResult locks( ReqMemberWithdrawLog req ) {
        List<MemberWithdrawLog> memberWithdrawLogList = memberWithdrawLogMapper.selectLocksByIds( req.getIds() );
        for ( MemberWithdrawLog memberWithdrawLog : memberWithdrawLogList ) {
            if ( memberWithdrawLog == null ) {
                return AjaxResult.error( memberWithdrawLog.getOrderNo() + "订单不存在" );
            }
            if ( memberWithdrawLog.getStatus() == 1 ) {
                return AjaxResult.error( memberWithdrawLog.getOrderNo() + "订单已被锁定,请刷新界面" );
            }
            if ( memberWithdrawLog.getStatus() == 2 ) {
                return AjaxResult.error( memberWithdrawLog.getOrderNo() + "订单已被拒绝" );
            }
            if ( memberWithdrawLog.getStatus() != 5 && 1 < memberWithdrawLog.getStatus() ) {
                return AjaxResult.error( memberWithdrawLog.getOrderNo() + "审核流程非法" );
            }
            LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
            String    userName  = loginUser.getUser().getUserName();

            if ( !StringUtils.isEmpty( memberWithdrawLog.getOpName() ) && !userName.equals( memberWithdrawLog.getOpName() ) ) {
                return AjaxResult.error( memberWithdrawLog.getOrderNo() + "订单只能由" + memberWithdrawLog.getOpName() + "处理" );
            }

            memberWithdrawLog.setRemark( req.getRemark() );
            memberWithdrawLog.setStatus( 1 );
            memberWithdrawLog.setOpName( userName );
            memberWithdrawLog.setUpdateTime( new Date() );
            int i = memberWithdrawLogMapper.updateMemberWithdrawLog( memberWithdrawLog );
            if ( i > 0 ) {
                continue;
            } else {
                return AjaxResult.error( memberWithdrawLog.getOrderNo() + "更新订单状态失败" );
            }
        }
        return AjaxResult.success( "批量锁定成功" );
    }

    @Override
    public AjaxResult unlock( ReqMemberWithdrawLog req ) {
        MemberWithdrawLog memberWithdrawLog = this.selectMemberWithdrawLogById( req.getId() );
        if ( memberWithdrawLog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        if ( memberWithdrawLog.getStatus() != 1 && memberWithdrawLog.getStatus() != 5 ) {
            return AjaxResult.error( "该订单已被处理,请刷新界面" );
        }
        LoginUser     loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String        userName  = loginUser.getUser().getUserName();
        List<SysRole> roles     = loginUser.getUser().getRoles();
        boolean       contains  = roles.stream().anyMatch( m -> "common".equals( m.getRoleKey() ) );
        if ( !contains ) {
            if ( !StringUtils.isEmpty( memberWithdrawLog.getOpName() ) && !userName.equals( memberWithdrawLog.getOpName() ) ) {
                return AjaxResult.error( "该订单只能由" + memberWithdrawLog.getOpName() + "处理" );
            }
        }
        if ( !redisUtil.lock( EnumLock.member, memberWithdrawLog.getMemberId(), "1", 5 ) ) {
            return AjaxResult.error( "请勿重复提交" );
        }

        memberWithdrawLog.setRemark( "取消锁定人：" + userName );
        memberWithdrawLog.setStatus( 0 );
        memberWithdrawLog.setOpName( "" );
        memberWithdrawLog.setUpdateTime( new Date() );
        int i = memberWithdrawLogMapper.updateMemberWithdrawLog( memberWithdrawLog );
        if ( i > 0 ) {
            return AjaxResult.success();
        }

        return AjaxResult.error( "更新订单状态失败" );
    }

    @Override
    public AjaxResult artificial( ReqMemberWithdrawLog req ) {
        MemberWithdrawLog memberWithdrawLog = this.selectMemberWithdrawLogById( req.getId() );
        if ( memberWithdrawLog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        if ( memberWithdrawLog.getStatus() == 2 ) {
            return AjaxResult.error( "该订单已被拒绝" );
        }
        if ( memberWithdrawLog.getStatus() == 3 ) {
            return AjaxResult.error( "该订单已被终审,请刷新界面" );
        }
        if ( memberWithdrawLog.getStatus() != 5 && 3 < memberWithdrawLog.getStatus() ) {
            return AjaxResult.error( "审核流程非法" );
        }
        if ( !redisUtil.lock( EnumLock.member, memberWithdrawLog.getMemberId(), "1", 5 ) ) {
            return AjaxResult.error( "请勿重复提交" );
        }
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        if ( StringUtils.hasText( memberWithdrawLog.getOpName() ) && !userName.equals( memberWithdrawLog.getOpName() ) ) {
            return AjaxResult.error( "该订单已被" + memberWithdrawLog.getOpName() + "锁定" );
        }
        //判断是代付成功还是出款成功
        int status = 3;
        if ( req.getPayAgentPlatId() != null ) {
            if ( memberWithdrawLog.getStatus() == 6 ) {
                return AjaxResult.error( "该订单已被终审,请刷新界面" );
            }
            //设定状态为代付成功
            status = 6;
            PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( req.getPayAgentPlatId() );
            req.setRemark( "人工代付:" + payAgentPlatform.getName() );
        }
        MemberWithdrawLog update = new MemberWithdrawLog();
        update.setId( memberWithdrawLog.getId() );
        update.setRemark( req.getRemark() );
        update.setStatus( status );
        update.setOpName( userName );
        update.setUpdateTime( new Date() );
        int i = memberWithdrawLogMapper.updateMemberWithdrawLog( update );

        //gopay提现彩金
        payAgentServiceImpl.gopayWithdraw( memberWithdrawLog, true );

        if ( i > 0 ) {
            redisUtil.unLock( EnumLock.member, memberWithdrawLog.getMemberId() );
            log.info( JsonUtil.object2Json( memberWithdrawLog ) );
            return AjaxResult.success();
        }

        return AjaxResult.error( "更新订单状态失败" );
    }

    @Override
    public AjaxResult updateRemark( ReqMemberWithdrawLog req ) {
        MemberWithdrawLog memberWithdrawLog = new MemberWithdrawLog();
        memberWithdrawLog.setId( req.getId() );
        memberWithdrawLog.setRemark( req.getRemark() );
        memberWithdrawLogMapper.updateMemberWithdrawLog( memberWithdrawLog );
        return AjaxResult.success();
    }

    @Override
    public AjaxResult abnormalWithdrawal( ReqMemberWithdrawLog req ) {
        MemberWithdrawLog memberWithdrawLog = this.selectMemberWithdrawLogById( req.getId() );
        if ( memberWithdrawLog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        if ( memberWithdrawLog.getStatus() == 2 ) {
            return AjaxResult.error( "该订单已被拒绝" );
        }
        if ( memberWithdrawLog.getStatus() == 3 ) {
            return AjaxResult.error( "该订单已被终审,请刷新界面" );
        }
        if ( memberWithdrawLog.getStatus() != 5 && 3 < memberWithdrawLog.getStatus() ) {
            return AjaxResult.error( "审核流程非法" );
        }
        if ( !redisUtil.lock( EnumLock.member, memberWithdrawLog.getMemberId(), "1", 5 ) ) {
            return AjaxResult.error( "请勿重复提交" );
        }

        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        if ( StringUtils.hasText( memberWithdrawLog.getOpName() ) && !userName.equals( memberWithdrawLog.getOpName() ) ) {
            return AjaxResult.error( "该订单已被" + memberWithdrawLog.getOpName() + "锁定" );
        }

        memberWithdrawLog.setRemark( req.getRemark() );
        memberWithdrawLog.setStatus( 7 );
        memberWithdrawLog.setOpName( userName );
        memberWithdrawLog.setUpdateTime( new Date() );
        int i = memberWithdrawLogMapper.updateMemberWithdrawLog( memberWithdrawLog );
        if ( i > 0 ) {
            redisUtil.unLock( EnumLock.member, memberWithdrawLog.getMemberId() );
            return AjaxResult.success();
        }

        return AjaxResult.error( "更新订单状态失败" );
    }

    @Override
    public AjaxResult manualWithdrawal( ReqMemberWithdrawLog req ) {
        MemberWithdrawLog memberWithdrawLog = this.selectMemberWithdrawLogById( req.getId() );
        if ( memberWithdrawLog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        if ( memberWithdrawLog.getStatus() == 2 ) {
            return AjaxResult.error( "该订单已被拒绝" );
        }
        if ( memberWithdrawLog.getStatus() == 3 ) {
            return AjaxResult.error( "该订单已被终审,请刷新界面" );
        }
        if ( memberWithdrawLog.getStatus() != 5 && 3 < memberWithdrawLog.getStatus() ) {
            return AjaxResult.error( "审核流程非法" );
        }
        if ( !redisUtil.lock( EnumLock.member, memberWithdrawLog.getMemberId(), "1", 5 ) ) {
            return AjaxResult.error( "请勿重复提交" );
        }

        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        if ( StringUtils.hasText( memberWithdrawLog.getOpName() ) && !userName.equals( memberWithdrawLog.getOpName() ) ) {
            return AjaxResult.error( "该订单已被" + memberWithdrawLog.getOpName() + "锁定" );
        }

        // memberWithdrawLog.setRemark( req.getRemark() );
        memberWithdrawLog.setStatus( 8 );
        memberWithdrawLog.setOpName( userName );
        memberWithdrawLog.setUpdateTime( new Date() );
        int i = memberWithdrawLogMapper.updateMemberWithdrawLog( memberWithdrawLog );
        if ( i > 0 ) {
            redisUtil.unLock( EnumLock.member, memberWithdrawLog.getMemberId() );
            return AjaxResult.success();
        }

        return AjaxResult.error( "更新订单状态失败" );
    }

    /**
     * 取报告
     *
     * @param id id
     *
     * @return {@link AjaxResult}
     */
    @Override
    public AjaxResult withdrawReport( String id ) {
        if ( !redisUtil.lock( EnumLock.member, id, "1", 10 ) ) {
            return AjaxResult.error( "请勿重复查询" );
        }
        //        memberInfoMapper.call_pro_useranalysis(id);
        //        List<WithdrawReport> withdrawReports = memberInfoMapper.userWithdrawReportList();

        //取会员id最后一个字符
        String tableLast = id.substring( id.length() - 1 );


        RspMemberInfo rspMemberInfo1  = memberInfoMapper.selectMemberInfoWithdrawByIda( id, tableLast );
        RspMemberInfo rspMemberInfo2  = memberInfoMapper.selectMemberInfoWithdrawByIdb( id, tableLast );
        RspMemberInfo rspMemberInfo3  = memberInfoMapper.selectMemberInfoWithdrawByIdc( id, tableLast );
        RspMemberInfo rspMemberInfo4  = memberInfoMapper.selectMemberInfoWithdrawByIdd( id, tableLast );
        RspMemberInfo rspMemberInfo5  = memberInfoMapper.selectMemberInfoWithdrawByIde( id, tableLast );
        RspMemberInfo rspMemberInfo6  = memberInfoMapper.selectMemberInfoWithdrawByIdf( id, tableLast );
        RspMemberInfo rspMemberInfo7  = memberInfoMapper.selectMemberInfoWithdrawByIdg( id, tableLast );
        RspMemberInfo rspMemberInfo8  = memberInfoMapper.selectMemberInfoWithdrawByIdh( id, tableLast );
        RspMemberInfo rspMemberInfo9  = memberInfoMapper.selectMemberInfoWithdrawByIdi( id, tableLast );
        RspMemberInfo rspMemberInfo10 = memberInfoMapper.selectMemberInfoWithdrawByIdj( id, tableLast );
        RspMemberInfo rspMemberInfo11 = memberInfoMapper.selectMemberInfoWithdrawByIdk( id, tableLast );
        //游戏投注详细
        List<RspMemberInfo> rspMemberInfo12 = memberInfoMapper.selectMemberInfoWithdrawByIdl( id, tableLast );
        RspMemberInfo       rspMemberInfo13 = memberInfoMapper.selectMemberInfoWithdrawByIdz( id, tableLast );

        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();

        log.info( "{}查询资金明细,会员ID:{}, 请求头：{}", userName, id,
                JsonUtil.object2Json( UserDataUtil.getRequestInfo( ServletUtil.getHttpServletRequest() ) ) );

        List<WithdrawReport> withdrawReports = new LinkedList<>();
        WithdrawReport       withdrawReporta = new WithdrawReport();
        withdrawReporta.setClass_twoname( "禁言原因" );
        withdrawReporta.setT_value( rspMemberInfo1.getEmail() );
        withdrawReports.add( withdrawReporta );

        WithdrawReport withdrawEmail = new WithdrawReport();
        withdrawEmail.setClass_twoname( "会员备注" );
        withdrawEmail.setT_value( rspMemberInfo1.getEmail() );
        withdrawReports.add( withdrawEmail );

        WithdrawReport withdrawReportb = new WithdrawReport();
        withdrawReportb.setClass_twoname( "会员编号" );
        withdrawReportb.setT_value( rspMemberInfo1.getId() );
        withdrawReports.add( withdrawReportb );

        //		WithdrawReport withdrawReportc = new WithdrawReport();
        //		withdrawReportc.setClass_twoname( "会员名称" );
        //		String phone = rspMemberInfo1.getPhone();
        //		if ( !StringUtils.isEmpty( phone ) ) {
        //			withdrawReportc.setT_value( PhoneUtil.getEncPhone( phone ) );
        //			withdrawReports.add( withdrawReportc );
        //		}

        WithdrawReport withdrawReportc = new WithdrawReport();
        withdrawReportc.setClass_twoname( "用户类型" );
        if ( !StringUtils.isEmpty( rspMemberInfo1.getChannelCode() ) ) {
            if ( "0".equals( rspMemberInfo1.getChannelCode() ) ) {
                withdrawReportc.setT_value( "游客" );
            } else {
                withdrawReportc.setT_value( "会员" );
            }
        }
        withdrawReports.add( withdrawReportc );

        WithdrawReport withdrawReportd = new WithdrawReport();
        withdrawReportd.setClass_twoname( "会员VIP" );
        withdrawReportd.setT_value( rspMemberInfo1.getVip() );
        withdrawReports.add( withdrawReportd );

        WithdrawReport withdrawReportv = new WithdrawReport();
        withdrawReportv.setClass_twoname( "登录时间" );
        withdrawReportv.setT_value( rspMemberInfo1.getLogin_time() );
        withdrawReports.add( withdrawReportv );

        WithdrawReport withdrawReporte = new WithdrawReport();
        withdrawReporte.setClass_twoname( "会员注册时间" );
        withdrawReporte.setT_value( rspMemberInfo1.getReg_time() );
        withdrawReports.add( withdrawReporte );

        WithdrawReport withdrawReportf = new WithdrawReport();
        withdrawReportf.setClass_twoname( "会员积分" );
        withdrawReportf.setT_value( rspMemberInfo1.getTotal_account() );
        withdrawReports.add( withdrawReportf );

        WithdrawReport withdrawReportg = new WithdrawReport();
        withdrawReportg.setClass_twoname( "会员注单" );
        withdrawReportg.setT_value( rspMemberInfo1.getCode_total() );
        withdrawReports.add( withdrawReportg );

        WithdrawReport withdrawReporth = new WithdrawReport();
        withdrawReporth.setClass_twoname( "会员打码" );
        withdrawReporth.setT_value( rspMemberInfo1.getCode_account() );
        withdrawReports.add( withdrawReporth );

        WithdrawReport withdrawReporti = new WithdrawReport();
        withdrawReporti.setClass_twoname( "登陆IP" );
        withdrawReporti.setT_value( rspMemberInfo1.getLogin_ip() );
        withdrawReports.add( withdrawReporti );

        //        WithdrawReport withdrawReportj = new WithdrawReport();
        //        withdrawReportj.setClass_twoname("登陆地址");
        //        withdrawReportj.setT_value(rspMemberInfo1.getIpaddress());
        //        withdrawReports.add(withdrawReportj);

        WithdrawReport withdrawReportk = new WithdrawReport();
        withdrawReportk.setClass_twoname( "线下充值金额" );
        withdrawReportk.setT_value( rspMemberInfo2.getRechargemoney() );
        withdrawReports.add( withdrawReportk );

        WithdrawReport withdrawReportz = new WithdrawReport();
        withdrawReportz.setClass_twoname( "USDT充值金额" );
        withdrawReportz.setT_value( rspMemberInfo13.getUsdtrechargemoney() );
        withdrawReports.add( withdrawReportz );

        WithdrawReport withdrawReportl = new WithdrawReport();
        withdrawReportl.setClass_twoname( "线上金额" );
        withdrawReportl.setT_value( rspMemberInfo3.getSubmoney() );
        withdrawReports.add( withdrawReportl );

        WithdrawReport withdrawReportm = new WithdrawReport();
        withdrawReportm.setClass_twoname( "人工代充金额" );
        withdrawReportm.setT_value( rspMemberInfo4.getP_money() );
        withdrawReports.add( withdrawReportm );

        WithdrawReport withdrawReportn = new WithdrawReport();
        withdrawReportn.setClass_twoname( "手动增加金额" );
        withdrawReportn.setT_value( rspMemberInfo5.getRg_income() );
        withdrawReports.add( withdrawReportn );

        WithdrawReport withdrawReporto = new WithdrawReport();
        withdrawReporto.setClass_twoname( "平台赠送金额" );
        withdrawReporto.setT_value( rspMemberInfo6.getZs_income() );
        withdrawReports.add( withdrawReporto );

        WithdrawReport withdrawReportp = new WithdrawReport();
        withdrawReportp.setClass_twoname( "充值总的金额" );
        BigDecimal rechargeAmount = new BigDecimal( rspMemberInfo13.getUsdtrechargemoney() );
        BigDecimal totalAmount    = new BigDecimal( rspMemberInfo7.getTotalincom() );
        withdrawReportp.setT_value( totalAmount.add( rechargeAmount ).toString() );
        withdrawReports.add( withdrawReportp );

        WithdrawReport withdrawReportq = new WithdrawReport();
        withdrawReportq.setClass_twoname( "会员提现次数" );
        withdrawReportq.setT_value( rspMemberInfo8.getW_count() );
        withdrawReports.add( withdrawReportq );

        WithdrawReport withdrawReportr = new WithdrawReport();
        withdrawReportr.setClass_twoname( "会员提现金额" );
        withdrawReportr.setT_value( rspMemberInfo9.getW_sum() );
        withdrawReports.add( withdrawReportr );

        WithdrawReport withdrawReportu = new WithdrawReport();
        withdrawReportu.setClass_twoname( "彩票异常投注次数" );
        withdrawReportu.setT_value( rspMemberInfo10.getGcount() );
        withdrawReports.add( withdrawReportu );

        WithdrawReport withdrawReportt = new WithdrawReport();
        withdrawReportt.setClass_twoname( "彩票总投注笔数" );
        withdrawReportt.setT_value( rspMemberInfo11.getGtcount() );
        withdrawReports.add( withdrawReportt );

        //游戏
        if ( rspMemberInfo12 != null && !rspMemberInfo12.isEmpty() ) {
            for ( RspMemberInfo rs : rspMemberInfo12 ) {
                WithdrawReport withdrawReportTwo = new WithdrawReport();
                withdrawReportTwo.setClass_twoname( rs.getClass_twoname() );
                withdrawReportTwo.setT_value( "投注:" + rs.getTouZhu() + "盈利:" + rs.getYingLi() );
                withdrawReports.add( withdrawReportTwo );
            }
        }
        return AjaxResult.success( withdrawReports );
    }

    @Override
    public AjaxResult getTotal( MemberWithdrawLog memberWithdrawLog ) {
        return AjaxResult.success( memberWithdrawLogMapper.getTotal( memberWithdrawLog ) );
    }

    @Override
    public AjaxResult countAll() {
        return AjaxResult.success( memberWithdrawLogMapper.countAll() );
    }

    @Override
    public List<MemberWithdrawLog> getWithdrawLogList() {
        String date      = getTime();
        String beginTime = date.split( " " )[ 0 ] + " 00:00:00";
        return memberWithdrawLogMapper.getWithdrawLogList( date, beginTime );
    }

    public String getTime() {
        SimpleDateFormat sdf     = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Calendar         nowTime = Calendar.getInstance();
        nowTime.add( Calendar.MINUTE, -10 );
        return sdf.format( nowTime.getTime() );
    }

    @Override
    public List<MemberWithdrawLog> selectMemberWithdrawLogCount( MemberWithdrawLog memberWithdrawLog ) {
        if ( Objects.isNull( memberWithdrawLog.getSearchTime() ) ) {
            Date     nowTime    = new Date();
            String   stringDate = DateFormatUtils.formate( nowTime, DateFormatUtils.SPLIT_PATTERN_DATE );
            String[] searchTime = new String[] { stringDate + " 00:00:00", stringDate + " 23:59:59" };
            memberWithdrawLog.setSearchTime( searchTime );
        }
        List<MemberWithdrawLog> memberWithdrawLogList = memberWithdrawLogMapper.countOpNameOrder( memberWithdrawLog );
        for ( MemberWithdrawLog m : memberWithdrawLogList ) {
            //状态(0申请中1锁定2审核不通过3人工入款成功 4代付中5代付失败6代付成功 7出款异常 8人工代付中)
            if ( m.getStatus() == 0 ) {
                m.setStatusName( "申请中" );
            } else if ( m.getStatus() == 1 ) {
                m.setStatusName( "锁定" );
            } else if ( m.getStatus() == 2 ) {
                m.setStatusName( "审核不通过" );
            } else if ( m.getStatus() == 3 ) {
                m.setStatusName( "人工入款成功" );
            } else if ( m.getStatus() == 4 ) {
                m.setStatusName( "代付中" );
            } else if ( m.getStatus() == 5 ) {
                m.setStatusName( "代付失败" );
            } else if ( m.getStatus() == 6 ) {
                m.setStatusName( "代付成功" );
            } else if ( m.getStatus() == 7 ) {
                m.setStatusName( "出款异常" );
            } else if ( m.getStatus() == 8 ) {
                m.setStatusName( "人工代付中" );
            }
        }
        return memberWithdrawLogList;
    }

}
