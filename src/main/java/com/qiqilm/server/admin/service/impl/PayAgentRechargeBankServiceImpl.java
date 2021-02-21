package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.PayAgentRechargeBank;
import com.qiqilm.server.admin.mapper.PayAgentRechargeBankMapper;
import com.qiqilm.server.admin.service.IPayAgentRechargeBankService;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 【代充银行列表】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class PayAgentRechargeBankServiceImpl implements IPayAgentRechargeBankService {
	@Autowired
	private PayAgentRechargeBankMapper payAgentRechargeBankMapper;
	@Autowired
	private TokenService               tokenService;
	@Autowired
	private ConfigDomainCacheUtil      configDomainCacheUtil;

	/**
	 * 查询【代充银行列表】
	 *
	 * @param id 【代充银行列表】ID
	 * @return 【代充银行列表】
	 */
	@Override
	public PayAgentRechargeBank selectPayAgentRechargeBankById( Long id ) {
		return payAgentRechargeBankMapper.selectPayAgentRechargeBankById( id );
	}

	/**
	 * 查询【代充银行列表】列表
	 *
	 * @param payAgentRechargeBank 【代充银行列表】
	 * @return 【代充银行列表】
	 */
	@Override
	public List<PayAgentRechargeBank> selectPayAgentRechargeBankList( PayAgentRechargeBank payAgentRechargeBank ) {
		List<PayAgentRechargeBank> banks = payAgentRechargeBankMapper.selectPayAgentRechargeBankList( payAgentRechargeBank );
		if ( !CollectionUtils.isEmpty( banks ) ) {
			String domainValue = configDomainCacheUtil.getValue( "domain.oss" );
			for ( PayAgentRechargeBank bank : banks ) {
				if ( StringUtils.isNotBlank( bank.getIcon() ) && !bank.getIcon().startsWith( "http" ) ) {
					bank.setIcon( domainValue + bank.getIcon() );
				}
			}
		}
		return banks;
	}

	/**
	 * 新增【代充银行列表】
	 *
	 * @param payAgentRechargeBank 【代充银行列表】
	 * @return 结果
	 */
	@Override
	public int insertPayAgentRechargeBank( PayAgentRechargeBank payAgentRechargeBank ) {
		payAgentRechargeBank.setCreateTime( DateUtils.getNowDate() );
		payAgentRechargeBank.setStatus( "1" );
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    username  = loginUser.getUsername();
		payAgentRechargeBank.setCreator( username );
		return payAgentRechargeBankMapper.insertPayAgentRechargeBank( payAgentRechargeBank );
	}

	/**
	 * 修改【代充银行列表】
	 *
	 * @param payAgentRechargeBank 【代充银行列表】
	 * @return 结果
	 */
	@Override
	public int updatePayAgentRechargeBank( PayAgentRechargeBank payAgentRechargeBank ) {
		payAgentRechargeBank.setUpdateTime( DateUtils.getNowDate() );
		payAgentRechargeBank.setStatus( "1" );
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    username  = loginUser.getUsername();
		payAgentRechargeBank.setUpdator( username );
		return payAgentRechargeBankMapper.updatePayAgentRechargeBank( payAgentRechargeBank );
	}

	/**
	 * 批量删除【代充银行列表】
	 *
	 * @param ids 需要删除的【代充银行列表】ID
	 * @return 结果
	 */
	@Override
	public int deletePayAgentRechargeBankByIds( Long[] ids ) {
		return payAgentRechargeBankMapper.deletePayAgentRechargeBankByIds( ids );
	}

	/**
	 * 删除【代充银行列表】信息
	 *
	 * @param id 【代充银行列表】ID
	 * @return 结果
	 */
	@Override
	public int deletePayAgentRechargeBankById( Long id ) {
		return payAgentRechargeBankMapper.deletePayAgentRechargeBankById( id );
	}

	@Override
	public int changeStatus( PayAgentRechargeBank payAgentRechargeBank ) {
		return payAgentRechargeBankMapper.updatePayAgentRechargeBank( payAgentRechargeBank );
	}
}
