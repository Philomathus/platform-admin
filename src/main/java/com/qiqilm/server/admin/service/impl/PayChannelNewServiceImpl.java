package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.PayCacheUtil;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.MemberPayJour;
import com.qiqilm.server.admin.domain.PayChannelMoney;
import com.qiqilm.server.admin.domain.PayChannelNew;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.PayChannelMoneyMapper;
import com.qiqilm.server.admin.mapper.PayChannelNewMapper;
import com.qiqilm.server.admin.mapper.PayTypeMapper;
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
	private PayTypeMapper         payTypeMapper;
	@Autowired
	private TokenService          tokenService;
	@Autowired
	private PayCacheUtil          payCacheUtil;

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
		List<PayChannelNew> list = payChannelNewMapper.findList(payChannelNew);
//		for ( PayChannelNew me : list ) {
//			if(me.getStatus().equals("1")){
//				//前50笔成功率
//				Integer countSuc = payChannelNewMapper.sucRate(me.getId());
//				String rateSuc = "0%";
//				//if(count != 0){
//				double d = ((float)countSuc/50)*100;
//				rateSuc = String.format("%.2f",  d)+"%";
//				//}
//				me.setPayTypeName(me.getPayTypeName()+":"+rateSuc);
//			}
//			//前50笔成功率
//			//Integer count = payChannelNewMapper.countRate(me.getId());
//			//Integer countSuc = payChannelNewMapper.sucRate(me.getId());
//			//String rateSuc = "0%";
//			//if(count != 0){
////				double d = ((float)countSuc/50)*100;
////				rateSuc = String.format("%.2f",  d)+"%";
//			//}
//			//今日成功率
////			Integer countd = payChannelNewMapper.todaycount(me.getId(),0);
////			Integer countdSuc = payChannelNewMapper.todaycount(me.getId(),1);
////			String todaySuc = "0%";
////			if(countd != 0){
////				double c = ((float)countdSuc/countd)*100;
////				todaySuc = String.format("%.2f", c)+"%";
////			}
//			//me.setPayPlatformName(me.getPayPlatformName()+":"+rateSuc);
//			//me.setPayTypeName(me.getPayTypeName()+"前50笔:"+rateSuc);
//		}
		return list;
	}

	/**
	 * 新增支付通道
	 *
	 * @param payChannelNew 支付通道
	 * @return 结果
	 */
	@Override
	public int insertPayChannelNew( PayChannelNew payChannelNew ) {
		if ( payChannelNew.getPayRate() == null ) {
			throw new BusinessException( "通道费率不得为空" );
		}
		if ( payChannelNew.getPayRate().compareTo( new BigDecimal( "0.4" ) ) > 0
				|| payChannelNew.getPayRate().compareTo( new BigDecimal( "0.02" ) ) < 0 ) {
			throw new BusinessException( "通道费率不得大于0.4或小于0.02" );
		}
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    username  = loginUser.getUsername();
		payChannelNew.setCreator( username );
		payChannelNew.setCreateTime( DateUtils.getNowDate() );
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
				if ( StringUtils.isBlank( channelNew.getQuickAmount() ) ) {
					throw new BusinessException( "快捷金额不能为空，请补全" );
				}
				if ( channelNew.getPayRate() == null ) {
					throw new BusinessException( "通道费率不得为空" );
				}
				if ( channelNew.getPayRate().compareTo( new BigDecimal( "0.4" ) ) > 0
						|| channelNew.getPayRate().compareTo( new BigDecimal( "0.02" ) ) < 0 ) {
					throw new BusinessException( "通道费率不得大于0.4或小于0.02" );
				}
				payChannelMoneyMapper.deleteByChannelIds( Collections.singletonList( payChannelNew.getId() ) );
				Integer  typeCode = payTypeMapper.selectCodeById( channelNew.getPayTypeId() );
				String[] moneys   = channelNew.getQuickAmount().split( "," );
				for ( String money : moneys ) {
					PayChannelMoney payChannelMoney = new PayChannelMoney();
					payChannelMoney.setMoney( Long.parseLong( money ) );
					payChannelMoney.setChannelId( channelNew.getId() );
					payChannelMoney.setChannelPayRate( channelNew.getPayRate() );
					payChannelMoney.setTypeCode( typeCode );
					payChannelMoney.setOpenLevelMin( channelNew.getOpenLevel() == null ? 1 : channelNew.getOpenLevel() );
					payChannelMoney.setOpenLevelMax( channelNew.getOpenLevelMax() == null ? 50 : channelNew.getOpenLevelMax() );
					payChannelMoneyMapper.insertPayChannelMoney( payChannelMoney );
				}
			} else {
				payChannelMoneyMapper.deleteByChannelIds( Collections.singletonList( payChannelNew.getId() ) );
			}
			// 更新缓存
			payCacheUtil.clearPayTypeList();
			payCacheUtil.setPayChannel( channelNew );
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
			payCacheUtil.clearPayChannel( ids );
			payCacheUtil.clearPayTypeList();
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
			payCacheUtil.clearPayChannel( id );
			payCacheUtil.clearPayTypeList();
		}
		return i;
	}
}
