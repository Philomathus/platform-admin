package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.PayCacheUtil;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.PayPlatformNew;
import com.qiqilm.server.admin.domain.rsp.RspPayPlatformNew;
import com.qiqilm.server.admin.mapper.PayPlatformNewMapper;
import com.qiqilm.server.admin.service.IPayPlatformNewService;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.ServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 支付平台Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class PayPlatformNewServiceImpl implements IPayPlatformNewService {
	@Autowired
	private PayPlatformNewMapper payPlatformNewMapper;
	@Autowired
	private TokenService         tokenService;
	@Autowired
	private PayCacheUtil         payCacheUtil;

	/**
	 * 查询支付平台
	 *
	 * @param id 支付平台ID
	 * @return 支付平台
	 */
	@Override
	public PayPlatformNew selectPayPlatformNewById( Long id ) {
		return payPlatformNewMapper.selectPayPlatformNewById( id );
	}

	/**
	 * 查询支付平台列表
	 *
	 * @param payPlatformNew 支付平台
	 * @return 支付平台
	 */
	@Override
	public List<RspPayPlatformNew> selectPayPlatformNewList(PayPlatformNew payPlatformNew ) {
		return payPlatformNewMapper.selectPayPlatformNewList( payPlatformNew );
	}

	/**
	 * 新增支付平台
	 *
	 * @param payPlatformNew 支付平台
	 * @return 结果
	 */
	@Override
	public int insertPayPlatformNew( PayPlatformNew payPlatformNew ) {
		payPlatformNew.setCreateTime( DateUtils.getNowDate() );
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    username  = loginUser.getUsername();
		payPlatformNew.setCreator( username );
		return payPlatformNewMapper.insertPayPlatformNew( payPlatformNew );
	}

	/**
	 * 修改支付平台
	 *
	 * @param payPlatformNew 支付平台
	 * @return 结果
	 */
	@Override
	public int updatePayPlatformNew( PayPlatformNew payPlatformNew ) {
		payPlatformNew.setUpdateTime( DateUtils.getNowDate() );
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    username  = loginUser.getUsername();
		payPlatformNew.setUpdator( username );
		int i = payPlatformNewMapper.updatePayPlatformNew( payPlatformNew );
		if ( i > 0 ) {
			payCacheUtil.clearPayPlatform( Long.parseLong( payPlatformNew.getId() ) );
		}
		return i;
	}

	/**
	 * 批量删除支付平台
	 *
	 * @param ids 需要删除的支付平台ID
	 * @return 结果
	 */
	@Override
	public int deletePayPlatformNewByIds( Long[] ids ) {
		int i = payPlatformNewMapper.deletePayPlatformNewByIds( ids );
		if ( i > 0 ) {
			payCacheUtil.clearPayPlatform( ids );
		}
		return i;
	}

	/**
	 * 删除支付平台信息
	 *
	 * @param id 支付平台ID
	 * @return 结果
	 */
	@Override
	public int deletePayPlatformNewById( Long id ) {
		int i = payPlatformNewMapper.deletePayPlatformNewById( id );
		if ( i > 0 ) {
			payCacheUtil.clearPayPlatform( id );
		}
		return i;
	}
}
