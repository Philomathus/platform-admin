package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.LiveCacheUtil;
import com.qiqilm.server.admin.cache.MemberCacheManager;
import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.*;
import com.qiqilm.server.admin.domain.req.ReqMemberRechargeLog;
import com.qiqilm.server.admin.domain.rsp.RspBankRecharge;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.exception.BaseException;
import com.qiqilm.server.admin.mapper.*;
import com.qiqilm.server.admin.service.ILogService;
import com.qiqilm.server.admin.service.IMemberRechargeLogService;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.UuidUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 公司入款信息Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Log4j2
@Service
public class MemberRechargeLogServiceImpl implements IMemberRechargeLogService {
	@Autowired
	private MemberRechargeLogMapper memberRechargeLogMapper;
	@Autowired
	private MemberInfoMapper        memberInfoMapper;
	@Autowired
	private MemberBcodeMapper       memberBcodeMapper;
	@Autowired
	private MemberRecommendMapper   recommendMapper;
	@Autowired
	private ConfigRecommendMapper   configRecommendMapper;
	@Autowired
	private MemberCacheManager memberCacheManager;
	@Autowired
	private ILogService             logService;
	@Autowired
	private TokenService            tokenService;
	@Autowired
	private RedisUtil               redisUtil;
	@Autowired
	private SysConfigCacheUtil sysConfigCacheUtil;


	/**
	 * 查询公司入款信息
	 *
	 * @param id 公司入款信息ID
	 * @return 公司入款信息
	 */
	@Override
	public MemberRechargeLog selectMemberRechargeLogById( String id ) {
		return memberRechargeLogMapper.selectMemberRechargeLogById( id );
	}

	/**
	 * 查询公司入款信息列表
	 *
	 * @param req 公司入款信息
	 * @return 公司入款信息
	 */
	@Override
	public List<MemberRechargeLog> selectMemberRechargeLogList( ReqMemberRechargeLog req ) {
		String[] selectDate = req.getSelectDate();
		if ( selectDate != null && selectDate.length > 0 ) {
			req.setSelectStartDate( selectDate[ 0 ] );
			req.setSelectEndDate( selectDate[ 1 ] );
		}
		return memberRechargeLogMapper.selectMemberRechargeLogList( req );
	}

	@Override
	public Map listCount( ReqMemberRechargeLog req ) {
		String[] selectDate = req.getSelectDate();
		if ( selectDate != null && selectDate.length > 0 ) {
			req.setSelectStartDate( selectDate[ 0 ] );
			req.setSelectEndDate( selectDate[ 1 ] );
		}
		return memberRechargeLogMapper.listCount( req );
	}

	@Override
	public AjaxResult firstAudit( ReqMemberRechargeLog req ) {
		MemberRechargeLog memberRechargeLog = this.selectMemberRechargeLogById( req.getId() );
		if ( memberRechargeLog == null ) {
			return AjaxResult.error( "订单不存在" );
		}
		if ( memberRechargeLog.getStatus() != 0 ) {
			return AjaxResult.error( "订单状态有误，请刷新数据后重试" );
		}

		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    userName  = loginUser.getUser().getUserName();

		MemberRechargeLog update = new MemberRechargeLog();
		update.setId( memberRechargeLog.getId() );
		update.setRemark( req.getRemark() );
		update.setStatus( 1 );//初审通过
		update.setOpName( userName );
		update.setUpdateTime( new Date() );
		int i = memberRechargeLogMapper.updateMemberRechargeLog( update );
		return i > 0 ? AjaxResult.success() : AjaxResult.error();
	}

	@Override
	@Transactional( rollbackFor = Exception.class )
	public AjaxResult finalAudit( ReqMemberRechargeLog req ) {
		MemberRechargeLog memberRechargeLog = this.selectMemberRechargeLogById( req.getId() );
		if ( memberRechargeLog == null ) {
			return AjaxResult.error( "订单不存在" );
		}
		if ( memberRechargeLog.getStatus() != 1 ) {
			return AjaxResult.error( "订单状态有误，请刷新数据后重试" );
		}
		if ( !redisUtil.lock( EnumLock.member, memberRechargeLog.getMemberId(), "1", 5 ) ) {
			return AjaxResult.error( "请勿重复提交" );
		}
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    userName  = loginUser.getUser().getUserName();
		try {
			boolean isAudit = this.finalAudit( req, userName, "审核人：" + userName );
			return isAudit ? AjaxResult.success( "审核成功" ) : AjaxResult.error( "审核失败" );

		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
			return AjaxResult.error( "订单审核有误" );
		} finally {
			redisUtil.unLock( EnumLock.member, memberRechargeLog.getMemberId() );
		}
	}

	@Transactional( rollbackFor = Exception.class )
	public boolean finalAudit( ReqMemberRechargeLog req, String userName, String mark ) {
		MemberRechargeLog memberRechargeLog = memberRechargeLogMapper.selectMemberRechargeLogById( req.getId() );
		if ( memberRechargeLog.getStatus() != 1 ) {
			throw new BaseException( "订单状态有误" );
		}

		MemberInfo memberInfo = memberInfoMapper.selectMemberInfoById( memberRechargeLog.getMemberId() );

		BigDecimal chargeGive = memberRechargeLog.getDiscountBill().multiply( memberRechargeLog.getRechargeMoney() )
				.setScale( 2, BigDecimal.ROUND_HALF_UP ); // 充值彩金

		BigDecimal ticketCattyRatio = sysConfigCacheUtil.getConfBd( "recharge_day_first_rate");

		//每日公司入款第二次优惠比例
		BigDecimal ticketCattyRatioSnd = sysConfigCacheUtil.getConfBd( "recharge_day_second_rate");


		if(memberRechargeLogMapper.countRechargeDaySucess(memberInfo.getId())==0){
			chargeGive = chargeGive.add(memberRechargeLog.getRechargeMoney().multiply( ticketCattyRatio )// 单日首次彩金
					.setScale( 2, BigDecimal.ROUND_HALF_UP ));
		}
		// 单日第二次彩金
		if(memberRechargeLogMapper.countRechargeDaySucess(memberInfo.getId())==1){
			chargeGive = chargeGive.add(memberRechargeLog.getRechargeMoney().multiply( ticketCattyRatioSnd )
					.setScale( 2, BigDecimal.ROUND_HALF_UP ));
		}
		//套利号无优惠
		if(memberInfo.getStatus()==4){
			chargeGive = BigDecimal.ZERO;
		}

		BigDecimal add = memberRechargeLog.getRechargeMoney().add( chargeGive );

		String orderId = memberRechargeLog.getOrderNo();

		if ( chargeGive.compareTo( BigDecimal.ZERO ) > 0 ) {
			logService.logMoneyAdd( null, memberInfo.getId(), memberInfo.getUserName(), EnumMoney.chargegive, chargeGive
					, memberInfo.getTotalAccount().add( memberRechargeLog.getRechargeMoney() ), mark, orderId );
		}

		logService.logMoneyAdd( orderId, memberRechargeLog.getMemberId(), memberInfo.getUserName(), EnumMoney.deposit,
				memberRechargeLog.getRechargeMoney(), memberInfo.getTotalAccount(), mark, orderId );

		MemberRechargeLog update = new MemberRechargeLog();
		update.setId( memberRechargeLog.getId() );
		update.setRemark( req.getRemark() );
		update.setStatus( 3 );//终审通过
		update.setOpName( userName );
		update.setUpdateTime( new Date() );
		memberRechargeLogMapper.updateMemberRechargeLog( update );

		//新增佣金记录
		this.recommendProcess( memberRechargeLog, memberInfo );
		try {

			memberCacheManager.bankChargeMount(memberRechargeLog.getMemberId());
			if(memberInfo.getLevelIntegral().compareTo(BigDecimal.ZERO)==0||memberInfo.getLevelIntegral().compareTo(memberInfo.getInviteMoney())<=0){
				memberCacheManager.checkFirstChargeaddWheelTimes(memberRechargeLog.getMemberId());
			}
		}catch (Exception e){
			log.error("首充报错",e);
		}

		//更新用户账户余额
		return this.updateMemberCharge( memberInfo.getId(), add, "线下存款" );
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

	private void recommendProcess( MemberRechargeLog memberRechargeLog, MemberInfo memberInfo ) {
		//新增佣金记录
		if ( StringUtils.isNotBlank( memberInfo.getInviterCode() ) ) {
			Map<Integer, ConfigRecommend> billMap = configRecommendMapper.selectConfigRecommendList( null )
					.stream()
					.collect( Collectors.toMap( ConfigRecommend::getLevel, Function.identity() ) );

			MemberInfo rd1 = memberInfoMapper.findRecommendByInviterCode( memberInfo.getInviterCode() );
			MemberInfo rd2 = null;
			if ( rd1 != null ) {
				BigDecimal      commission       = memberRechargeLog.getRechargeMoney().multiply( billMap.get( 1 ).getBill() );
				MemberRecommend recommendUserLog = new MemberRecommend();
				recommendUserLog.setId( UuidUtil.getRandomUuidWithoutSeparator() );
				recommendUserLog.setLevel( 1 );
				recommendUserLog.setMemberId( memberRechargeLog.getMemberId() );
				recommendUserLog.setMemberName( memberInfo.getUserName() );
				recommendUserLog.setInviterId( rd1.getId() );
				recommendUserLog.setInviter( rd1.getUserName() );
				recommendUserLog.setCreateTime( new Date() );
				recommendUserLog.setCommission( commission );
				recommendUserLog.setStatus( 0 );
				recommendUserLog.setCode( memberInfo.getMemberCode() );
				recommendUserLog.setOrderMoney( memberRechargeLog.getRechargeMoney() );
				recommendMapper.insertMemberRecommend( recommendUserLog );
				if ( StringUtils.isNotBlank( rd1.getInviterCode() ) ) {
					rd2 = memberInfoMapper.findRecommendByInviterCode( rd1.getInviterCode() );
				}
			}

			if ( rd2 != null ) {
				BigDecimal      commission       = memberRechargeLog.getRechargeMoney().multiply( billMap.get( 2 ).getBill() );
				MemberRecommend recommendUserLog = new MemberRecommend();
				recommendUserLog.setId( UuidUtil.getRandomUuidWithoutSeparator() );
				recommendUserLog.setLevel( 2 );
				recommendUserLog.setMemberId( memberRechargeLog.getMemberId() );
				recommendUserLog.setMemberName( memberInfo.getUserName() );
				recommendUserLog.setInviterId( rd2.getId() );
				recommendUserLog.setInviter( rd2.getUserName() );
				recommendUserLog.setCreateTime( new Date() );
				recommendUserLog.setCommission( commission );
				recommendUserLog.setStatus( 0 );
				recommendUserLog.setCode( memberInfo.getMemberCode() );
				recommendUserLog.setOrderMoney( memberRechargeLog.getRechargeMoney() );
				recommendMapper.insertMemberRecommend( recommendUserLog );
			}
		}
	}

	@Override
	public AjaxResult refusedAudit( ReqMemberRechargeLog req ) {
		MemberRechargeLog memberRechargeLog = this.selectMemberRechargeLogById( req.getId() );
		if ( memberRechargeLog == null ) {
			return AjaxResult.error( "订单不存在" );
		}
		if ( !redisUtil.lock( EnumLock.member, memberRechargeLog.getMemberId(), "1", 5 ) ) {
			return AjaxResult.error( "请勿重复提交" );
		}
		if(memberRechargeLog.getStatus()==3){
			return AjaxResult.error( "该订单已审核通过,请刷新页面" );
		}

		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    userName  = loginUser.getUser().getUserName();

		MemberRechargeLog update = new MemberRechargeLog();
		update.setId( memberRechargeLog.getId() );
		update.setRemark( req.getRemark() );
		update.setStatus( 2 );//审核不通过
		update.setOpName( userName );
		update.setUpdateTime( new Date() );
		int i = memberRechargeLogMapper.updateMemberRechargeLog( update );
		return i > 0 ? AjaxResult.success() : AjaxResult.error();
	}

	@Override
	public AjaxResult recoverAudit( ReqMemberRechargeLog req ) {
		MemberRechargeLog memberRechargeLog = this.selectMemberRechargeLogById( req.getId() );
		if ( memberRechargeLog == null ) {
			return AjaxResult.error( "订单不存在" );
		}
		if ( 3 == memberRechargeLog.getStatus() ) {
			return AjaxResult.error( "订单已终审" );
		}

		if ( memberRechargeLog.getStatus() != 2 && memberRechargeLog.getStatus() != 4 ) {
			return AjaxResult.error( "只有拒绝和超时才能恢复审核" );
		}

		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    userName  = loginUser.getUser().getUserName();

		MemberRechargeLog update = new MemberRechargeLog();
		update.setId( memberRechargeLog.getId() );
		update.setStatus( 1 );
		update.setOpName( userName );
		int i = memberRechargeLogMapper.updateMemberRechargeLog( update );
		return i > 0 ? AjaxResult.success() : AjaxResult.error();
	}
	@Override
	public int checkRechargeLogFail() {
		return memberRechargeLogMapper.checkRechargeLogFail();
	}

	@Override
	public List<RspBankRecharge> selectMemberBankRecharge(ReqMemberRechargeLog req) {
		return memberRechargeLogMapper.selectMemberBankRecharge(req);
	}

	@Override
	public Map listCounts(ReqMemberRechargeLog req) {
		return memberRechargeLogMapper.listCounts(req);
	}
}
