package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.MemberCacheManager;
import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.*;
import com.qiqilm.server.admin.enums.EnumAction;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.mapper.*;
import com.qiqilm.server.admin.service.ILogService;
import com.qiqilm.server.admin.service.IPayService;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Service
public class PayServiceImpl implements IPayService {
	@Autowired
	private MemberPayJourMapper     payJourMapper;
	@Autowired
	private SysUserMapper           sysUserMapper;
	@Autowired
	private MemberInfoMapper        memberInfoMapper;
	@Autowired
	private MemberActionLogsMapper  actionLogsMapper;
	@Autowired
	private MemberRechargeLogMapper rechargeLogMapper;
	@Autowired
	private MemberBcodeMapper       memberBcodeMapper;
	@Autowired
	private MemberRecommendMapper   recommendMapper;
	@Autowired
	private ConfigRecommendMapper   configRecommendMapper;
	@Autowired
	private TokenService            tokenService;
	@Autowired
	private ILogService             logService;
	@Autowired
	private MemberCacheManager      memberCacheManager;
	@Autowired
	private SysConfigCacheUtil      sysConfigCacheUtil;


	@Override
	@Transactional( rollbackFor = Exception.class )
	public AjaxResult payPatchOrder( Map<String, Object> requestMap ) throws Exception {
		String orderNo        = ( String ) requestMap.get( "orderNo" );
		String subMoney       = ( String ) requestMap.get( "subMoney" );
		String googleAuthCode = ( String ) requestMap.get( "googleAuthCode" );
		if ( !StringUtils.hasText( googleAuthCode ) ) {
			return AjaxResult.error( "请输入google验证码" );
		}
		MemberPayJour payJour = payJourMapper.findByOrderNo( orderNo );
		if ( "1".equals( payJour.getStatus() ) ) {
			return AjaxResult.error( "订单状态有误，补单失败" );
		}

		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    userName  = loginUser.getUser().getUserName();

		String googleAuthSecret = sysUserMapper.selectGoogleAuthKeyByUserName( userName );

		if ( !StringUtils.hasText( googleAuthSecret ) ) {
			return AjaxResult.error( "未绑定google验证秘钥，无法审核" );
		}
		String googleAuthKey = RSACoder.decryptByPrivateKey( googleAuthSecret, AuthUtil.getSecurityKeyStr(
				"secretkey/googleAuthPrivateKey" ) );

		if ( !GoogleAuthUtil.verifyCode( googleAuthKey, Integer.parseInt( googleAuthCode ) ) ) {
			return AjaxResult.error( "google验证码不正确，请检查" );
		}

		payJour.setSubMoney( new BigDecimal( subMoney ) );
		payJour.setIsPatchOrder( 1 );
		payJour.setRemark( "操作人：" + userName + "人工补单" );

		if ( !"1".equals( payJour.getStatus() ) ) {
			MemberActionLogs log = new MemberActionLogs();
			log.setId( UuidUtil.getRandomUuidWithoutSeparator() );
			log.setUserId( payJour.getMemberId() );
			log.setUserName( payJour.getUserName() );
			log.setcTime( new Date() );
			log.setType( EnumAction.gm.getType() );
			log.setDes( EnumAction.gm.getDes() );
			log.setParam1( "加分资金：" + payJour.getSubMoney() );
			log.setParam2( "payJour订单号：" + orderNo );
			log.setParam3( "操作人：" + userName );
			log.setParam4( "备注：人工补单" );
			log.setParamIp( UserDataUtil.getIp( ServletUtil.getHttpServletRequest() ) );
			actionLogsMapper.insertMemberActionLogs( log );
			this.updatePayJourStatus( payJour, "操作人：" + userName );
		}
		return AjaxResult.success();
	}

	@Transactional( rollbackFor = Exception.class )
	public boolean updatePayJourStatus( MemberPayJour payJour, String name ) {
		MemberPayJour memberPayJour = payJourMapper.selectMemberPayJourById( payJour.getId() );
		if ( "1".equals( memberPayJour.getStatus() ) ) {
			return false;
		}

		//更新支付订单状态
		MemberPayJour updatePayJour = new MemberPayJour();
		updatePayJour.setId( payJour.getId() );
		updatePayJour.setStatus( "1" );
		updatePayJour.setUpdateTime( DateFormatUtils.formate( new Date() ) );
		updatePayJour.setSubMoney( payJour.getSubMoney() );
		updatePayJour.setTradeSn( payJour.getTradeSn() );
		updatePayJour.setIsPatchOrder( payJour.getIsPatchOrder() );
		updatePayJour.setRemark( payJour.getRemark() );
		payJourMapper.updateMemberPayJour( updatePayJour );

		MemberInfo memberInfo = memberInfoMapper.selectMemberInfoById( payJour.getMemberId() );

		BigDecimal payJourMoney = payJour.getIsPatchOrder() == 1 ? payJour.getSubMoney() :
				memberPayJour.getMoney();

		BigDecimal payjourDiscountRate = new BigDecimal( sysConfigCacheUtil.getConf( "payjour_discount_rate" ) );

		// 充值彩金
		BigDecimal chargeGive = payjourDiscountRate.multiply( payJourMoney ).setScale( 2, BigDecimal.ROUND_HALF_UP );

		//套利号无优惠
		if(memberInfo.getStatus()==4){
			chargeGive = new BigDecimal(0);
		}

		BigDecimal money = payJourMoney.add( chargeGive );

		BigDecimal nowmoney = memberInfo.getTotalAccount().add( payJourMoney );

		String orderId = payJour.getOrderNo();

		if ( chargeGive.compareTo( BigDecimal.ZERO ) > 0 ) {
			logService.logMoneyAll( memberInfo.getId(), memberInfo.getUserName(), EnumMoney.chargegive,
					nowmoney.add( chargeGive ), chargeGive
					, null, name, orderId );
		}

		logService.logMoneyAll( memberInfo.getId(), memberInfo.getUserName(), EnumMoney.charge, nowmoney, payJourMoney
				, null, name, orderId );

		//新增佣金记录
		this.recommendProcess( payJour, memberInfo );
		//更新用户账户余额
		boolean isUpdate = this.updateMemberCharge( memberInfo.getId(), money, "线上充值" );
		if ( isUpdate ) {
			MemberRechargeLog memberRechargeLog = new MemberRechargeLog();
			memberRechargeLog.setId( payJour.getId() );
			memberRechargeLog.setStatus( 3 );
			memberRechargeLog.setUpdateTime( new Date() );
			memberRechargeLog.setRechargeMoney( payJourMoney );
			rechargeLogMapper.updateMemberRechargeLog( memberRechargeLog );

			log.warn( "会员线上充值上分成功 - orderNo:{}", payJour.getOrderNo() );
			try {
				if ( memberInfo.getLevelIntegral().compareTo( BigDecimal.ZERO ) == 0 || memberInfo.getLevelIntegral().compareTo( memberInfo.getInviteMoney() ) <= 0 ) {
					if(memberInfo.getStatus() != 4 && memberInfo.getStatus() != 7) {
						memberCacheManager.checkFirstChargeaddWheelTimes(memberInfo.getId());
					}
				}
			} catch ( Exception e ) {
				log.error( "首充报错", e );
			}
		}
		return isUpdate;
	}


	private boolean updateMemberCharge( String userId, BigDecimal money, String chargeType ) {
		MemberBcode codeFlow = new MemberBcode();
		codeFlow.setId( UuidUtil.getRandomUuidWithoutSeparator() );
		codeFlow.setIncome( money );
		codeFlow.setCreateTime( new Date() );
		codeFlow.setStatus( 0 );
		codeFlow.setCur( BigDecimal.ZERO );
		codeFlow.setUserId( userId );
		codeFlow.setDes( chargeType );
		return memberBcodeMapper.insertMemberBcode( codeFlow ) > 0
				&& memberInfoMapper.updateMoneySelect( userId, money, null, money, null, null ) > 0;
	}

	private void recommendProcess( MemberPayJour payJour, MemberInfo memberInfo ) {
		if ( StringUtils.hasText( memberInfo.getInviterCode() ) ) {
			Map<Integer, ConfigRecommend> billMap = configRecommendMapper.selectConfigRecommendList( null )
					.stream()
					.collect( Collectors.toMap( ConfigRecommend::getLevel, Function.identity() ) );

			MemberInfo rd1 = memberInfoMapper.findRecommendByInviterCode( memberInfo.getInviterCode() );
			MemberInfo rd2 = null;

			//一级分佣
			if ( rd1 != null ) {
				BigDecimal      commission       = payJour.getMoney().multiply( billMap.get( 1 ).getBill() );
				MemberRecommend recommendUserLog = new MemberRecommend();
				recommendUserLog.setId( UuidUtil.getRandomUuidWithoutSeparator() );
				recommendUserLog.setCreateTime( new Date() );
				recommendUserLog.setMemberId( payJour.getMemberId() );
				recommendUserLog.setMemberName( payJour.getUserName() );
				recommendUserLog.setLevel( 1 );
				recommendUserLog.setInviterId( rd1.getId() );
				recommendUserLog.setInviter( rd1.getUserName() );
				recommendUserLog.setCommission( commission );
				recommendUserLog.setStatus( 0 );
				recommendUserLog.setCode( memberInfo.getMemberCode() );
				recommendUserLog.setOrderMoney( payJour.getMoney() );
				recommendMapper.insertMemberRecommend( recommendUserLog );
				if ( StringUtils.hasText( rd1.getInviterCode() ) ) {
					rd2 = memberInfoMapper.findRecommendByInviterCode( memberInfo.getInviterCode() );
				}
			}
			//二级分佣
			if ( rd2 != null ) {
				BigDecimal      commission       = payJour.getMoney().multiply( billMap.get( 2 ).getBill() );
				MemberRecommend recommendUserLog = new MemberRecommend();
				recommendUserLog.setId( UuidUtil.getRandomUuidWithoutSeparator() );
				recommendUserLog.setCreateTime( new Date() );
				recommendUserLog.setMemberId( payJour.getMemberId() );
				recommendUserLog.setMemberName( payJour.getUserName() );
				recommendUserLog.setLevel( 2 );
				recommendUserLog.setCommission( commission );
				recommendUserLog.setInviterId( rd2.getId() );
				recommendUserLog.setInviter( rd2.getUserName() );
				recommendUserLog.setCode( memberInfo.getMemberCode() );
				recommendUserLog.setStatus( 0 );
				recommendUserLog.setOrderMoney( payJour.getMoney() );
				recommendMapper.insertMemberRecommend( recommendUserLog );
			}
		}
	}
}
