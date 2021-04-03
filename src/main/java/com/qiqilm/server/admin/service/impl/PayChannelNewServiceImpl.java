package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.PayChannelMoney;
import com.qiqilm.server.admin.domain.PayChannelNew;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.PayChannelMoneyMapper;
import com.qiqilm.server.admin.mapper.PayChannelNewMapper;
import com.qiqilm.server.admin.service.IPayChannelNewService;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 支付通道Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class PayChannelNewServiceImpl implements IPayChannelNewService {
	@Autowired
	private PayChannelNewMapper   payChannelNewMapper;
	@Autowired
	private PayChannelMoneyMapper payChannelMoneyMapper;
	@Autowired
	private TokenService          tokenService;

	/**
	 * 查询支付通道
	 *
	 * @param id 支付通道ID
	 * @return 支付通道
	 */
	@Override
	public PayChannelNew selectPayChannelNewById( Long id ) {
		return payChannelNewMapper.selectPayChannelNewById( id );
	}

	/**
	 * 查询支付通道列表
	 *
	 * @param payChannelNew 支付通道
	 * @return 支付通道
	 */
	@Override
	public List<PayChannelNew> selectPayChannelNewList( PayChannelNew payChannelNew ) {
		return payChannelNewMapper.findList( payChannelNew );
	}

	/**
	 * 新增支付通道
	 *
	 * @param payChannelNew 支付通道
	 * @return 结果
	 */
	@Override
	public int insertPayChannelNew( PayChannelNew payChannelNew ) {
		payChannelNew.setCreateTime( DateUtils.getNowDate() );
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    username  = loginUser.getUsername();
		payChannelNew.setCreator( username );
		payChannelNew.setStatus( "0" );
		payChannelNew.setFailNum( 0 );
		payChannelNew.setSuccessNum( 0 );
		payChannelNew.setTotalSuccessMoney( BigDecimal.ZERO );
		return payChannelNewMapper.insertPayChannelNew( payChannelNew );
	}

	/**
	 * 修改支付通道
	 *
	 * @param payChannelNew 支付通道
	 * @return 结果
	 */
	@Override
	@Transactional( rollbackFor = Exception.class )
	public int updatePayChannelNew( PayChannelNew payChannelNew ) {
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    username  = loginUser.getUsername();
		payChannelNew.setUpdator( username );
		payChannelNew.setUpdateTime( DateUtils.getNowDate() );
		int i = payChannelNewMapper.updatePayChannelNew( payChannelNew );
		if ( i > 0 ) {
			PayChannelNew channelNew = payChannelNewMapper.selectPayChannelNewById( payChannelNew.getId() );
			if ( "1".equals( channelNew.getStatus() ) ) {
				if ( StringUtils.isBlank( channelNew.getQuickAmount() ) || channelNew.getPayRate() == null ) {
					throw new BusinessException( "快捷金额或通道费率不能为空，请补全" );
				}
				String[] moneys = channelNew.getQuickAmount().split( "," );
				for ( String money : moneys ) {
					PayChannelMoney payChannelMoney = new PayChannelMoney();
					payChannelMoney.setMoney( Long.parseLong( money ) );
					payChannelMoney.setChannelId( channelNew.getId() );
					payChannelMoney.setChannelPayRate( channelNew.getPayRate() );
					payChannelMoneyMapper.insertPayChannelMoney( payChannelMoney );
				}
			} else {
				payChannelMoneyMapper.deleteByChannelIds( Collections.singletonList( payChannelNew.getId() ) );
			}
		}
		return i;
	}

	/**
	 * 批量删除支付通道
	 *
	 * @param ids 需要删除的支付通道ID
	 * @return 结果
	 */
	@Override
	@Transactional( rollbackFor = Exception.class )
	public int deletePayChannelNewByIds( Long[] ids ) {
		int i = payChannelNewMapper.deletePayChannelNewByIds( ids );
		if ( i > 0 ) {
			payChannelMoneyMapper.deleteByChannelIds( Arrays.asList( ids ) );
		}
		return i;
	}

	/**
	 * 删除支付通道信息
	 *
	 * @param id 支付通道ID
	 * @return 结果
	 */
	@Override
	@Transactional( rollbackFor = Exception.class )
	public int deletePayChannelNewById( Long id ) {
		int i = payChannelNewMapper.deletePayChannelNewById( id );
		if ( i > 0 ) {
			payChannelMoneyMapper.deleteByChannelIds( Collections.singletonList( id ) );
		}
		return i;
	}
}
