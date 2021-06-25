package com.qiqilm.server.admin.service.impl;

import com.google.common.collect.ImmutableMap;
import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.exception.BaseException;
import com.qiqilm.server.admin.mapper.MemberWithdrawLogMapper;
import com.qiqilm.server.admin.mapper.PayAgentLogMapper;
import com.qiqilm.server.admin.mapper.PayAgentPlatformMapper;
import com.qiqilm.server.admin.mapper.SysUserMapper;
import com.qiqilm.server.admin.payagent.BasePayAgent;
import com.qiqilm.server.admin.payagent.PayAgentProcessorFactoryUtil;
import com.qiqilm.server.admin.service.IPayAgentService;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@Log4j2
public class PayAgentServiceImpl implements IPayAgentService {
	@Autowired
	private PayAgentPlatformMapper  payAgentPlatformMapper;
	@Autowired
	private MemberWithdrawLogMapper withdrawLogMapper;
	@Autowired
	private PayAgentLogMapper       payAgentLogMapper;
	@Autowired
	private SysUserMapper           sysUserMapper;
	@Autowired
	private TokenService            tokenService;

	@Autowired
	private PayAgentProcessorFactoryUtil payAgentProcessorFactoryUtil;
	@Autowired
	private RedisUtil                    redisUtil;

	@Value( "${payAgentLimit:5000}" )
	private Integer payAgentLimit;
	@Value( "${payAgentLimitShunWei:5000}" )
	private Integer payAgentLimitShunWei;
	@Value( "${payAgentLimitBinLi:5000}" )
	private Integer payAgentLimitBinLi;
	@Value( "${payAgentLimitTels:5000}" )
	private Integer payAgentLimitTels;
	@Value( "${payAgentLimitLianFuBao:5000}" )
	private Integer payAgentLimitLianFuBao;
	@Value( "${payAgentLimitMaYun:5000}" )
	private Integer payAgentLimitMaYun;
	@Value( "${payAgentLimitHeZhong:5000}" )
	private Integer payAgentLimitHeZhong;

	@Override
	@Transactional( rollbackFor = Exception.class )
	public void processOrderPay( MemberWithdrawLog withdrawLog, PayAgentLog payAgentLog, String orderNo,
								 PayAgentPlatform payAgentPlatform, boolean isSuccess ) {
		Date              now            = new Date();
		MemberWithdrawLog newWithdrawLog = new MemberWithdrawLog();
		newWithdrawLog.setId( withdrawLog.getId() );
		newWithdrawLog.setStatus( isSuccess ? 6 : 5 );
		newWithdrawLog.setUpdateTime( now );
		newWithdrawLog.setRemark( "【" + payAgentPlatform.getName() + "】代付" + ( isSuccess ? "成功" : "失败" ) );
		withdrawLogMapper.updateMemberWithdrawLog( newWithdrawLog );

		PayAgentLog newPayAgentLog = new PayAgentLog();
		newPayAgentLog.setPayAgentOrderNo( orderNo );
		newPayAgentLog.setCallbackTime( now );
		newPayAgentLog.setCallbackStatus( isSuccess ? 1 : 2 );
		if ( payAgentLog == null ) {
			newPayAgentLog.setCreateTime( now );
			newPayAgentLog.setWithdrawOrderNo( withdrawLog.getOrderNo() );
			newPayAgentLog.setWithdrawMoney( withdrawLog.getWithdrawMoney() );
			newPayAgentLog.setMemberAccount( withdrawLog.getAccount() );
			newPayAgentLog.setMemberId( withdrawLog.getMemberId() );
			newPayAgentLog.setPayAgentPlatId( payAgentPlatform.getId() );
			newPayAgentLog.setPayAgentPlatName( payAgentPlatform.getName() );
			payAgentLogMapper.insertPayAgentLog( newPayAgentLog );
		} else {
			newPayAgentLog.setId( payAgentLog.getId() );
			payAgentLogMapper.updatePayAgentLog( newPayAgentLog );
		}
	}

	@Override
	public void queryAgent4Status5Min() {
		List<PayAgentLog> payAgentLogs = payAgentLogMapper.findNoCallback();

		List<PayAgentPlatform> payAgentPlatforms = payAgentPlatformMapper.selectPayAgentPlatformList( null );

		for ( PayAgentLog payAgentLog : payAgentLogs ) {
			for ( PayAgentPlatform payAgentPlatform : payAgentPlatforms ) {
				if ( payAgentLog.getPayAgentPlatId().toString().equals( payAgentPlatform.getId().toString() ) ) {
					BasePayAgent basePayAgent = payAgentProcessorFactoryUtil.createPayProcessor( payAgentPlatform.getCode() );
					try {
						log.warn( "开始批量查询代付订单 - 订单号：{}，PayAgentPlatId：{},PayAgentPlatCode:{}", payAgentLog.getWithdrawOrderNo(),
								payAgentLog.getPayAgentPlatId(), payAgentPlatform.getCode() );
						basePayAgent.queryOrderPay( payAgentLog );
					} catch ( Exception e ) {
						log.error( e.getMessage(), e );
					}
				}
			}
		}
	}

	@Override
	public AjaxResult payAgentOrder( ReqPayAgent reqPayAgent ) throws Exception {
		if ( !StringUtils.hasText( reqPayAgent.getWithdrawOrderNo() ) || reqPayAgent.getPayAgentPlatId() == null ) {
			return AjaxResult.error( "订单号或代付平台ID不能为空" );
		}
		if ( reqPayAgent.getGoogleAuthCode() == null ) {
			return AjaxResult.error( "请输入google验证码" );
		}
		MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( reqPayAgent.getWithdrawOrderNo() );

		PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( reqPayAgent.getPayAgentPlatId() );

		if ( withdrawLog == null || payAgentPlatform == null ) {
			log.warn( "提现记录或代付平台未找到 - withdrawOrderNo:{};payAgentPlatId:{}", reqPayAgent.getWithdrawOrderNo(),
					reqPayAgent.getPayAgentPlatId() );
			return AjaxResult.error( "提现记录或代付平台未找到" );
		}
		if ( withdrawLog.getWithdrawMoney() == null || withdrawLog.getWithdrawMoney().compareTo( BigDecimal.ZERO ) <= 0 ) {
			log.warn( "提现金额有误 - withdrawOrderNo:{};withdrawMoney:{}", reqPayAgent.getWithdrawOrderNo(),
					withdrawLog.getWithdrawMoney() );
			return AjaxResult.error( "提现金额不得低于0元" );
		}
		if ( withdrawLog.getStatus() != 1 ) {
			return AjaxResult.error( "审核流程非法" );
		}

		if ( payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO )
				&& withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitLianFuBao ) ) > 0 ) {
			return AjaxResult.error( "此代付暂不支持" + payAgentLimitLianFuBao + "元以上出款" );
		} else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.TE_LUN_SU )
				|| payAgentPlatform.getCode().equals( ConstantsPayAgent.TE_LUN_SU2 ) )
				&& withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitTels ) ) > 0 ) {
			return AjaxResult.error( "此代付暂不支持" + payAgentLimitTels + "元以上出款" );
		} else if ( ( payAgentPlatform.getCode().equals( ConstantsPayAgent.BINLI )
				|| payAgentPlatform.getCode().equals( ConstantsPayAgent.BINLI2 ) )
				&& withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitBinLi ) ) > 0 ) {
			return AjaxResult.error( "此代付暂不支持" + payAgentLimitBinLi + "元以上出款" );
		} else if ( payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI3 )
				&& withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitShunWei ) ) > 0 ) {
			return AjaxResult.error( "此代付暂不支持" + payAgentLimitShunWei + "元以上出款" );
		} else if ( payAgentPlatform.getCode().equals( ConstantsPayAgent.Ma_Yun )
				&& withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitMaYun ) ) > 0 ) {
			return AjaxResult.error( "此代付暂不支持" + payAgentLimitMaYun + "元以上出款" );
		} else if ( payAgentPlatform.getCode().equals( ConstantsPayAgent.HEZHONG )
				&& withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimitHeZhong ) ) > 0 ) {
			return AjaxResult.error( "此代付暂不支持" + payAgentLimitHeZhong + "元以上出款" );
		} else if ( withdrawLog.getWithdrawMoney().compareTo( new BigDecimal( payAgentLimit ) ) > 0
				&& !payAgentPlatform.getCode().equals( ConstantsPayAgent.Ma_Yun )
				&& !payAgentPlatform.getCode().equals( ConstantsPayAgent.HEZHONG )
				&& !payAgentPlatform.getCode().equals( ConstantsPayAgent.BINLI )
				&& !payAgentPlatform.getCode().equals( ConstantsPayAgent.BINLI2 )
				&& !payAgentPlatform.getCode().equals( ConstantsPayAgent.LIAN_FU_BAO )
				&& !payAgentPlatform.getCode().equals( ConstantsPayAgent.SHUN_WEI3 )
				&& !payAgentPlatform.getCode().equals( ConstantsPayAgent.TE_LUN_SU )
				&& !payAgentPlatform.getCode().equals( ConstantsPayAgent.TE_LUN_SU2 ) ) {
			return AjaxResult.error( "代付暂不支持" + payAgentLimit + "元以上出款" );
		}

		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    userName  = loginUser.getUser().getUserName();
		if ( StringUtils.hasText( withdrawLog.getOpName() ) && !userName.equals( withdrawLog.getOpName() ) ) {
			return AjaxResult.error( "该订单只能由" + withdrawLog.getOpName() + "处理" );
		}

		String googleAuthSecret = sysUserMapper.selectGoogleAuthKeyByUserName( userName );

		if ( !StringUtils.hasText( googleAuthSecret ) ) {
			return AjaxResult.error( "未绑定google验证秘钥，无法审核" );
		}
		String googleAuthKey = RSACoder.decryptByPrivateKey( googleAuthSecret,
				AuthUtil.getSecurityKeyStr( "secretkey/googleAuthPrivateKey" ) );

		if ( !GoogleAuthUtil.verifyCode( googleAuthKey, reqPayAgent.getGoogleAuthCode() ) ) {
			return AjaxResult.error( "google验证码不正确，请检查" );
		}

		if ( !redisUtil.lock( EnumLock.payAgent, reqPayAgent.getWithdrawOrderNo(), "1", 10 ) ) {
			return AjaxResult.error( "请勿重复提交代付订单:" + reqPayAgent.getWithdrawOrderNo() );
		}

		int noFailCount = payAgentLogMapper.countNoFail( withdrawLog.getOrderNo() );
		if ( noFailCount > 0 ) {
			return AjaxResult.error( "此订单已被代付，请在三方后台跟踪订单状态" );
		}
		int platOrderCount = payAgentLogMapper.countPlatOrderNo( withdrawLog.getOrderNo(), reqPayAgent.getPayAgentPlatId() );
		if ( platOrderCount > 0 ) {
			return AjaxResult.error( String.format( "此订单已被 %s 处理过，请更换代付商后重试", payAgentPlatform.getName() ) );
		}

		reqPayAgent.setCurrentTime( new Date() );
		this.processOrder( payAgentPlatform, withdrawLog, reqPayAgent.getCurrentTime(), 4, 0 );

		BasePayAgent basePayAgent = payAgentProcessorFactoryUtil.createPayProcessor( payAgentPlatform.getCode() );
		if ( basePayAgent.orderPay( withdrawLog, payAgentPlatform, reqPayAgent ) ) {
			return AjaxResult.success( "代付订单提交成功" );
		}

		redisUtil.unLock( EnumLock.payAgent, reqPayAgent.getWithdrawOrderNo() );
		return AjaxResult.error( StringUtils.hasText( reqPayAgent.getFailReason() ) ? reqPayAgent.getFailReason() : "代付失败" );
	}

	@Override
	public AjaxResult payAgentOrders( ReqPayAgent reqPayAgent ) {
		if ( CollectionUtils.isEmpty( reqPayAgent.getWithdrawOrderNos() ) || reqPayAgent.getPayAgentPlatId() == null ) {
			return AjaxResult.error( "订单号或代付平台ID不能为空" );
		}
		if ( reqPayAgent.getGoogleAuthCode() == null ) {
			return AjaxResult.error( "请输入google验证码" );
		}
		PayAgentPlatform payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( reqPayAgent.getPayAgentPlatId() );
		if ( payAgentPlatform == null ) {
			log.warn( "代付平台未找到 - payAgentPlatId:{}", reqPayAgent.getPayAgentPlatId() );
			return AjaxResult.error( "代付平台未找到" );
		}

		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    userName  = loginUser.getUser().getUserName();


		List<MemberWithdrawLog> withdrawLogs = withdrawLogMapper.selectPayAgentOrder( reqPayAgent.getWithdrawOrderNos(),
				userName );
		if ( CollectionUtils.isEmpty( withdrawLogs ) ) {
			return AjaxResult.error( "未匹配到可提现订单" );
		}

		String googleAuthSecret = sysUserMapper.selectGoogleAuthKeyByUserName( userName );

		if ( !StringUtils.hasText( googleAuthSecret ) ) {
			return AjaxResult.error( "未绑定google验证秘钥，无法审核" );
		}
		String googleAuthKey = "";
		try {
			googleAuthKey = RSACoder.decryptByPrivateKey( googleAuthSecret,
					AuthUtil.getSecurityKeyStr( "secretkey/googleAuthPrivateKey" ) );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}
		if ( !GoogleAuthUtil.verifyCode( googleAuthKey, reqPayAgent.getGoogleAuthCode() ) ) {
			return AjaxResult.error( "google验证码不正确，请检查" );
		}

		BasePayAgent        basePayAgent   = payAgentProcessorFactoryUtil.createPayProcessor( payAgentPlatform.getCode() );
		Map<String, String> failReasonList = new TreeMap<>();
		int                 sucessNum      = 0;
		for ( MemberWithdrawLog withdrawLog : withdrawLogs ) {
			int noFailCount = payAgentLogMapper.countNoFail( withdrawLog.getOrderNo() );
			if ( noFailCount > 0 ) {
				failReasonList.put( withdrawLog.getOrderNo(), "此订单已被代付，请在三方后台跟踪订单状态" );
				continue;
			}
			int platOrderCount = payAgentLogMapper.countPlatOrderNo( withdrawLog.getOrderNo(), reqPayAgent.getPayAgentPlatId() );
			if ( platOrderCount > 0 ) {
				failReasonList.put( withdrawLog.getOrderNo(), String.format( "此订单已被 %s 处理过，请更换代付商后重试",
						payAgentPlatform.getName() ) );
				continue;
			}

			ReqPayAgent newReqPayAgent = new ReqPayAgent();
			newReqPayAgent.setCurrentTime( new Date() );
			newReqPayAgent.setWithdrawOrderNo( withdrawLog.getOrderNo() );
			try {
				this.processOrder( payAgentPlatform, withdrawLog, newReqPayAgent.getCurrentTime(), 4, 0 );

				if ( basePayAgent.orderPay( withdrawLog, payAgentPlatform, newReqPayAgent ) ) {
					sucessNum++;
				} else {
					failReasonList.put( withdrawLog.getOrderNo(), newReqPayAgent.getFailReason() );
				}
			} catch ( Exception e ) {
				log.error( "代付下单失败 - 订单号：{};失败原因：{}", withdrawLog.getOrderNo(), e.getMessage(), e );
				failReasonList.put( withdrawLog.getOrderNo(), newReqPayAgent.getFailReason() );
			}
		}
		return AjaxResult.success( ImmutableMap.of( "fail", failReasonList, "sucess", sucessNum ) );
	}

	@Override
	@Transactional( rollbackFor = Exception.class )
	public void processOrder( PayAgentPlatform payAgentPlatform, MemberWithdrawLog memberWithdrawLog,
							  Date now, int status, int orderState ) {
		MemberWithdrawLog withdrawLog = withdrawLogMapper.selectMemberWithdrawLogById( memberWithdrawLog.getId() );
		PayAgentLog       payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( memberWithdrawLog.getOrderNo() );
		if ( !( withdrawLog.getStatus() == 1 || withdrawLog.getStatus() == 4 ) ) {
			throw new BaseException( "审核流程非法" );
		}
		// 更改withdrawLog状态
		MemberWithdrawLog newWithdrawLog = new MemberWithdrawLog();
		newWithdrawLog.setId( withdrawLog.getId() );
		newWithdrawLog.setBankCode( memberWithdrawLog.getBankCode() );
		newWithdrawLog.setStatus( status );
		newWithdrawLog.setType( 2 );
		newWithdrawLog.setUpdateTime( now );
		String remark;
		switch ( status ) {
		case 4:
			remark = "已交由【" + payAgentPlatform.getName() + "】出款";
			break;
		case 5:
			remark = "【" + payAgentPlatform.getName() + "】代付失败";
			break;
		case 6:
			remark = "【" + payAgentPlatform.getName() + "】代付成功";
			break;
		default:
			remark = "";
			break;
		}
		newWithdrawLog.setRemark( remark );
		log.warn( JsonUtil.object2Json( newWithdrawLog ) );
		withdrawLogMapper.updateMemberWithdrawLog( newWithdrawLog );

		// 保存代付信息日志
		PayAgentLog newPayAgentLog = new PayAgentLog();
		if ( StringUtils.hasText( memberWithdrawLog.getPayAgentOrderNo() ) ) {
			newPayAgentLog.setPayAgentOrderNo( memberWithdrawLog.getPayAgentOrderNo() );
		}
		switch ( status ) {
		case 4:
			newPayAgentLog.setCallbackStatus( 0 );
			break;
		case 5:
			newPayAgentLog.setCallbackTime( now );
			newPayAgentLog.setCallbackStatus( 2 );
			break;
		case 6:
			newPayAgentLog.setCallbackTime( now );
			newPayAgentLog.setCallbackStatus( 1 );
			break;
		default:
			break;
		}
		if ( payAgentLog == null ) {
			newPayAgentLog.setCreateTime( now );
			newPayAgentLog.setWithdrawOrderNo( withdrawLog.getOrderNo() );
			newPayAgentLog.setWithdrawMoney( withdrawLog.getWithdrawMoney() );
			newPayAgentLog.setMemberAccount( withdrawLog.getAccount() );
			newPayAgentLog.setMemberId( withdrawLog.getMemberId() );
			newPayAgentLog.setPayAgentPlatId( payAgentPlatform.getId() );
			newPayAgentLog.setPayAgentPlatName( payAgentPlatform.getName() );
			payAgentLogMapper.insertPayAgentLog( newPayAgentLog );
		} else {
			newPayAgentLog.setId( payAgentLog.getId() );
			payAgentLogMapper.updatePayAgentLog( newPayAgentLog );
		}
		log.warn( JsonUtil.object2Json( newPayAgentLog ) );
	}
}
