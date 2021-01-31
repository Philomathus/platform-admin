package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.req.ReqMemberWithdrawLog;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.mapper.MemberWithdrawLogMapper;
import com.qiqilm.server.admin.service.ILogService;
import com.qiqilm.server.admin.service.IMemberWithdrawLogService;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.UserDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 会员提现信息Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-30
 */
@Service
public class MemberWithdrawLogServiceImpl implements IMemberWithdrawLogService {
	@Autowired
	private MemberWithdrawLogMapper memberWithdrawLogMapper;
	@Autowired
	private MemberInfoMapper        memberInfoMapper;
	@Autowired
	private TokenService            tokenService;
	@Autowired
	private ILogService             logService;
	@Autowired
	private RedisUtil               redisUtil;

	/**
	 * 查询会员提现信息
	 *
	 * @param id 会员提现信息ID
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
	 * @return 会员提现信息
	 */
	@Override
	public List<MemberWithdrawLog> selectMemberWithdrawLogList( MemberWithdrawLog memberWithdrawLog ) {
		return memberWithdrawLogMapper.selectMemberWithdrawLogList( memberWithdrawLog );
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

		memberWithdrawLog.setRemark( req.getRemark() );
		memberWithdrawLog.setStatus( 2 );//审核不通过
		memberWithdrawLog.setOpName( userName );
		memberWithdrawLog.setUpdateTime( new Date() );

		this.refusedUpdateProcess( memberWithdrawLog, userName, ip );

		redisUtil.unLock( EnumLock.member, memberWithdrawLog.getMemberId() );
		return AjaxResult.success();
	}

	@Transactional( rollbackFor = Exception.class )
	void refusedUpdateProcess( MemberWithdrawLog memberWithdrawLog, String userName, String ip ) {
		memberWithdrawLogMapper.updateMemberWithdrawLog( memberWithdrawLog );
		BigDecimal old = memberInfoMapper.selectTotalAccountById( memberWithdrawLog.getMemberId() );
		//回退提现金额
		memberInfoMapper.updateMoneySelect( memberWithdrawLog.getMemberId(), memberWithdrawLog.getWithdrawMoney(), null, null
				, null, null );
		BigDecimal now = memberInfoMapper.selectTotalAccountById( memberWithdrawLog.getMemberId() );
		logService.logmarkMoney( memberWithdrawLog.getMemberId(), memberWithdrawLog.getAccount(), EnumMoney.bohui, now, old,
				"驳回人：" + userName + "-" + ip, memberWithdrawLog.getOrderNo() );
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
	public AjaxResult unlock( ReqMemberWithdrawLog req ) {
		MemberWithdrawLog memberWithdrawLog = this.selectMemberWithdrawLogById( req.getId() );
		if ( memberWithdrawLog == null ) {
			return AjaxResult.error( "订单不存在" );
		}
		if ( memberWithdrawLog.getStatus() != 1 ) {
			return AjaxResult.error( "该订单已被处理,请刷新界面" );
		}
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    userName  = loginUser.getUser().getUserName();
		if ( !StringUtils.isEmpty( memberWithdrawLog.getOpName() ) && !userName.equals( memberWithdrawLog.getOpName() ) ) {
			return AjaxResult.error( "该订单只能由" + memberWithdrawLog.getOpName() + "处理" );
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

		memberWithdrawLog.setRemark( req.getRemark() );
		memberWithdrawLog.setStatus( 3 );
		memberWithdrawLog.setOpName( userName );
		memberWithdrawLog.setUpdateTime( new Date() );
		int i = memberWithdrawLogMapper.updateMemberWithdrawLog( memberWithdrawLog );
		if ( i > 0 ) {
			redisUtil.unLock( EnumLock.member, memberWithdrawLog.getMemberId() );
			return AjaxResult.success();
		}

		return AjaxResult.error( "更新订单状态失败" );
	}
}
