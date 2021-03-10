package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.LiveCacheUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.*;
import com.qiqilm.server.admin.domain.req.ReqMemberRechargeLog;
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
	private WheelUserMapper wheelUserMapper;
	@Autowired
	private LiveUserMountMapper liveUserMountMapper;
	@Autowired
	private ILogService             logService;
	@Autowired
	private TokenService            tokenService;
	@Autowired
	private RedisUtil               redisUtil;

	@Autowired
	private LiveCacheUtil liveCacheUtil;

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

	private void checkFirstChargeaddWheelTimes(  String pUserId ) {
		WheelUser wheelUser = wheelUserMapper.selectWheelUserById(pUserId);
		if ( wheelUser == null ) {
			wheelUser = new WheelUser();
			wheelUser.setId( pUserId );
			wheelUser.setTimes( 1 );
			wheelUser.setSkinTimes(1);
			wheelUserMapper.insertWheelUser(wheelUser);

		} else {
			wheelUser.setTimes( 1 );
			wheelUser.setSkinTimes(1);
			wheelUserMapper.updateWheelUser(wheelUser);
		}
		//加坐骑33
		LiveUserMount query = new LiveUserMount();
		query.setUserId(pUserId);
		query.setMountId(33);
		int day = 4;
		List<LiveUserMount> list = liveUserMountMapper.selectLiveUserMountList(query);
		if(list.size()==0){
			query.setIsUse(0);
			Date d    = new Date( new Date().getTime() + day * 24 * 60 * 60 * 1000L );//过期时间
			query.setEffectiveTime( d );
			liveUserMountMapper.insertLiveUserMount(query);
		}else{
			LiveUserMount db = list.get(0);
			if(db.getIsUse().equals(1)){
				if(db.getEffectiveTime().getTime()>System.currentTimeMillis()){
					db.setEffectiveTime(new Date( db.getEffectiveTime().getTime()+ day * 24 * 60 * 60 * 1000L ));
				}else{
					Date d    = new Date( new Date().getTime() + day * 24 * 60 * 60 * 1000L );//过期时间
					db.setEffectiveTime( d );
				}
			}else{
				Date d    = new Date( new Date().getTime() + day * 24 * 60 * 60 * 1000L );//过期时间
				db.setEffectiveTime( d );
				db.setIsUse(0);
			}

			liveUserMountMapper.updateLiveUserMount(db);
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
			if(memberInfo.getLevelIntegral().compareTo(BigDecimal.ZERO)==0||memberInfo.getLevelIntegral().compareTo(memberInfo.getInviteMoney())<=0){
				checkFirstChargeaddWheelTimes(memberRechargeLog.getMemberId());
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
					rd2 = memberInfoMapper.findRecommendByInviterCode( memberInfo.getInviterCode() );
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
}
