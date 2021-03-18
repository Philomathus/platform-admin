package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void processOrderPay( String merOrderNo, String orderNo, PayAgentPlatform payAgentPlatform, boolean isSuccess ) {
		MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( merOrderNo );
		PayAgentLog       payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( merOrderNo );
		if ( withdrawLog == null ) {
			log.error( "提现相关记录丢失 - merOrderNo:{}", merOrderNo );
			throw new IllegalStateException( "提现相关记录丢失,请检查!" );
		}
		if ( withdrawLog.getStatus() != 4 ) {
			log.error( "已有代付记录 - merOrderNo:{}", merOrderNo );
			throw new IllegalStateException( "已有代付记录,请检查!" );
		}
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
		PayAgentLog payAgentLogQuery = new PayAgentLog();
		payAgentLogQuery.setCallbackStatus( 0 );
		Calendar nowTime = Calendar.getInstance();
		nowTime.add( Calendar.MINUTE, -5 );
		payAgentLogQuery.setCreateTime( nowTime.getTime() );
		List<PayAgentLog> payAgentLogs = payAgentLogMapper.selectPayAgentLogList( payAgentLogQuery );

		List<PayAgentPlatform> payAgentPlatforms = payAgentPlatformMapper.selectPayAgentPlatformList( null );

		for ( PayAgentLog payAgentLog : payAgentLogs ) {
			for ( PayAgentPlatform payAgentPlatform : payAgentPlatforms ) {
				if ( payAgentLog.getPayAgentPlatId().equals( payAgentPlatform.getId().toString() ) ) {
					BasePayAgent basePayAgent = payAgentProcessorFactoryUtil.createPayProcessor( payAgentPlatform.getCode() );
					try {
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
			return AjaxResult.error( "提现金额有误" );
		}
		if ( withdrawLog.getStatus() != 1 ) {
			return AjaxResult.error( "审核流程非法" );
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

		reqPayAgent.setCurrentTime( new Date() );
		BasePayAgent basePayAgent = payAgentProcessorFactoryUtil.createPayProcessor( payAgentPlatform.getCode() );
		if ( basePayAgent.orderPay( withdrawLog, payAgentPlatform, reqPayAgent ) ) {
			this.processOrder( payAgentPlatform, withdrawLog, reqPayAgent.getCurrentTime(), 4, 0 );
			return AjaxResult.success( "代付订单提交成功" );
		}
		return AjaxResult.error( StringUtils.hasText( reqPayAgent.getFailReason() ) ? reqPayAgent.getFailReason() : "代付失败" );
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void processOrder( PayAgentPlatform payAgentPlatform, MemberWithdrawLog memberWithdrawLog,
							  Date now, int status, int orderState ) {
		MemberWithdrawLog withdrawLog = withdrawLogMapper.selectMemberWithdrawLogById( memberWithdrawLog.getId() );
		PayAgentLog       payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( memberWithdrawLog.getOrderNo() );
		if ( withdrawLog.getStatus() != 1 && withdrawLog.getStatus() != 4 ) {
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
		if ( status != 4 ) {
			newPayAgentLog.setCallbackTime( now );
			newPayAgentLog.setCallbackStatus( orderState );
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
