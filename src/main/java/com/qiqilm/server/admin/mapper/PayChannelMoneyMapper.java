package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.PayChannelMoney;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 支付通道金额Mapper接口
 *
 * @author 77tv
 * @date 2021-04-03
 */
public interface PayChannelMoneyMapper {
	/**
	 * 查询支付通道金额列表
	 *
	 * @param payChannelMoney 支付通道金额
	 * @return 支付通道金额集合
	 */
	public List<PayChannelMoney> selectPayChannelMoneyList( PayChannelMoney payChannelMoney );

	/**
	 * 新增支付通道金额
	 *
	 * @param payChannelMoney 支付通道金额
	 * @return 结果
	 */
	public int insertPayChannelMoney( PayChannelMoney payChannelMoney );

	int deleteByChannelIds( @Param( "channelIds" ) List<Long> channelIds );
}
