package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.PayAgentRechargeRecord;
import com.qiqilm.server.admin.domain.PayAgentRechargeTradeLog;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.mapper.PayAgentRechargeRecordMapper;
import com.qiqilm.server.admin.mapper.PayAgentRechargeTradeLogMapper;
import com.qiqilm.server.admin.service.IPayAgentRechargeRecordService;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.ServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 代充记录Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class PayAgentRechargeRecordServiceImpl implements IPayAgentRechargeRecordService {
	@Autowired
	private PayAgentRechargeRecordMapper   payAgentRechargeRecordMapper;
	@Autowired
	private TokenService                   tokenService;
	@Autowired
	private MemberInfoMapper               memberInfoMapper;
	@Autowired
	private PayAgentRechargeTradeLogMapper payAgentRechargeTradeLogMapper;

	/**
	 * 查询代充记录
	 *
	 * @param orderNo 代充记录ID
	 * @return 代充记录
	 */
	@Override
	public PayAgentRechargeRecord selectPayAgentRechargeRecordById( String orderNo ) {
		return payAgentRechargeRecordMapper.selectPayAgentRechargeRecordById( orderNo );
	}

	/**
	 * 查询代充记录列表
	 *
	 * @param payAgentRechargeRecord 代充记录
	 * @return 代充记录
	 */
	@Override
	public List<PayAgentRechargeRecord> selectPayAgentRechargeRecordList( PayAgentRechargeRecord payAgentRechargeRecord ) {
		return payAgentRechargeRecordMapper.selectPayAgentRechargeRecordList( payAgentRechargeRecord );
	}

	/**
	 * 新增代充记录
	 *
	 * @param payAgentRechargeRecord 代充记录
	 * @return 结果
	 */
	@Override
	public int insertPayAgentRechargeRecord( PayAgentRechargeRecord payAgentRechargeRecord ) {
		payAgentRechargeRecord.setCreateTime( DateUtils.getNowDate() );
		return payAgentRechargeRecordMapper.insertPayAgentRechargeRecord( payAgentRechargeRecord );
	}

	/**
	 * 修改代充记录
	 *
	 * @param payAgentRechargeRecord 代充记录
	 * @return 结果
	 */
	@Override
	public int updatePayAgentRechargeRecord( PayAgentRechargeRecord payAgentRechargeRecord ) {
		return payAgentRechargeRecordMapper.updatePayAgentRechargeRecord( payAgentRechargeRecord );
	}

	/**
	 * 批量删除代充记录
	 *
	 * @param orderNos 需要删除的代充记录ID
	 * @return 结果
	 */
	@Override
	public int deletePayAgentRechargeRecordByIds( String[] orderNos ) {
		return payAgentRechargeRecordMapper.deletePayAgentRechargeRecordByIds( orderNos );
	}

	/**
	 * 删除代充记录信息
	 *
	 * @param orderNo 代充记录ID
	 * @return 结果
	 */
	@Override
	public int deletePayAgentRechargeRecordById( String orderNo ) {
		return payAgentRechargeRecordMapper.deletePayAgentRechargeRecordById( orderNo );
	}

	@Override
	@Transactional( rollbackFor = Exception.class )
	public AjaxResult deposit( PayAgentRechargeRecord dto ) {
		LoginUser  loginUser      = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String     userName       = loginUser.getUser().getUserName();
		String     rechargeAcount = dto.getRechargeAcount();
		MemberInfo memberInfo     = memberInfoMapper.selectMemberInfoById( rechargeAcount );
		if ( memberInfo == null ) {
			return AjaxResult.error( "会员不存在，请核实" );
		}
		String orderNo = "CT" + DateFormatUtils.formate( new Date(), "yyyyMMddHHmmss" )
				+ memberInfo.getMemberCode();
		BigDecimal             BalanceAmount = dto.getMoney();
		PayAgentRechargeRecord Record        = new PayAgentRechargeRecord();
		Record.setOrderNo( orderNo );
		Record.setRechargeAcount( rechargeAcount );
		Record.setRechargeNickName( memberInfo.getNickName() );
		Record.setType( dto.getType() );
		Record.setRemark( dto.getRemark() );
		Record.setMoney( BalanceAmount );
		Record.setCreateTime( DateUtils.getNowDate() );
		Record.setOpName( userName );
		payAgentRechargeRecordMapper.insertPayAgentRechargeRecord( Record );
		//加钱，payAgentRechargeAccount和PayAgentRechargeTrade
		payAgentRechargeRecordMapper.updateByBalanceAmount( rechargeAcount, BalanceAmount );

		//PayAgentRechargeTradelog表
		PayAgentRechargeTradeLog payAgentRechargeTradeLog = new PayAgentRechargeTradeLog();
		payAgentRechargeTradeLog.setOrderNo( orderNo );
		payAgentRechargeTradeLog.setAccount( rechargeAcount );
		payAgentRechargeTradeLog.setNickName( memberInfo.getNickName() );
		payAgentRechargeTradeLog.setIncome( BalanceAmount );
		payAgentRechargeTradeLog.setCreateTime( DateUtils.getNowDate() );
		payAgentRechargeTradeLog.setRemark( dto.getRemark() );
		payAgentRechargeTradeLog.setName( "人工存入" );
		payAgentRechargeTradeLogMapper.insertPayAgentRechargeTradeLog( payAgentRechargeTradeLog );
		return AjaxResult.success();
	}

	@Override
	@Transactional( rollbackFor = Exception.class )
	public AjaxResult proposed( PayAgentRechargeRecord dto ) {
		LoginUser              loginUser              = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String                 userName               = loginUser.getUser().getUserName();
		PayAgentRechargeRecord payAgentRechargeRecord = new PayAgentRechargeRecord();
		MemberInfo             memberInfo             = memberInfoMapper.selectMemberInfoById( dto.getRechargeAcount() );
		if ( memberInfo == null ) {
			return AjaxResult.error( "会员不存在，请核实" );
		}
		//代充人表減钱
		int a = payAgentRechargeRecordMapper.updateByBalanceAmountLess( dto.getRechargeAcount(), dto.getMoney() );
		if ( a == 0 ) {
			return AjaxResult.error( "余额不足" );
		}
		String orderNo = "CT" + DateFormatUtils.formate( new Date(), "yyyyMMddHHmmss" )
				+ memberInfo.getMemberCode();
		payAgentRechargeRecord.setOrderNo( orderNo );
		payAgentRechargeRecord.setRechargeAcount( dto.getRechargeAcount() );
		payAgentRechargeRecord.setRechargeNickName( memberInfo.getNickName() );
		payAgentRechargeRecord.setType( dto.getType() );
		payAgentRechargeRecord.setRemark( dto.getRemark() );
		BigDecimal bigDecimal = dto.getMoney().negate();
		payAgentRechargeRecord.setMoney( bigDecimal );
		payAgentRechargeRecord.setCreateTime( DateUtils.getNowDate() );

		payAgentRechargeRecord.setOpName( userName );
		payAgentRechargeRecordMapper.insertPayAgentRechargeRecord( payAgentRechargeRecord );
		//pay_agent_recharge_trade_log表记录
		PayAgentRechargeTradeLog payAgentRechargeTradeLog = new PayAgentRechargeTradeLog();
		payAgentRechargeTradeLog.setOrderNo( orderNo );
		payAgentRechargeTradeLog.setAccount( dto.getRechargeAcount() );
		payAgentRechargeTradeLog.setNickName( memberInfo.getNickName() );
		BigDecimal money = dto.getMoney().negate();
		payAgentRechargeTradeLog.setIncome( money );
		payAgentRechargeTradeLog.setCreateTime( DateUtils.getNowDate() );
		payAgentRechargeTradeLog.setRemark( dto.getRemark() );
		payAgentRechargeTradeLog.setName( "人工提出" );
		payAgentRechargeTradeLogMapper.insertPayAgentRechargeTradeLog( payAgentRechargeTradeLog );

		return AjaxResult.success();
	}
}
