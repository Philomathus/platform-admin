package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.mapper.MemberWithdrawLogMapper;
import com.qiqilm.server.admin.mapper.PayAgentPlatformMapper;
import com.qiqilm.server.admin.mapper.SysUserMapper;
import com.qiqilm.server.admin.payagent.PayAgentProcessorFactoryUtil;
import com.qiqilm.server.admin.service.IPayAgentPlatformService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 代付平台Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Log4j2
@Service
public class PayAgentPlatformServiceImpl implements IPayAgentPlatformService {
	@Autowired
	private PayAgentPlatformMapper  payAgentPlatformMapper;
	@Autowired
	private MemberWithdrawLogMapper memberWithdrawLogMapper;
	@Autowired
	private SysUserMapper           sysUserMapper;
	@Autowired
	private TokenService            tokenService;

	@Autowired
	private PayAgentProcessorFactoryUtil payAgentProcessorFactoryUtil;

	/**
	 * 查询代付平台
	 *
	 * @param id 代付平台ID
	 * @return 代付平台
	 */
	@Override
	public PayAgentPlatform selectPayAgentPlatformById( Long id ) {
		PayAgentPlatform agentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById(id);
		agentPlatform.setSignMd5( "*********" );
		agentPlatform.setHeaderKey( "*********" );
		agentPlatform.setSignPublicKey( "*********" );
		agentPlatform.setSignPrivateKey( "*********" );
		return agentPlatform;
	}

	/**
	 * 查询代付平台列表
	 *
	 * @param payAgentPlatform 代付平台
	 * @return 代付平台
	 */
	@Override
	public List<PayAgentPlatform> selectPayAgentPlatformList( PayAgentPlatform payAgentPlatform ) {
		return payAgentPlatformMapper.selectPayAgentPlatformList( payAgentPlatform );
	}

	/**
	 * 新增代付平台
	 *
	 * @param payAgentPlatform 代付平台
	 * @return 结果
	 */
	@Override
	public int insertPayAgentPlatform( PayAgentPlatform payAgentPlatform ) {
		return payAgentPlatformMapper.insertPayAgentPlatform( payAgentPlatform );
	}

	/**
	 * 修改代付平台
	 *
	 * @param payAgentPlatform 代付平台
	 * @return 结果
	 */
	@Override
	public int updatePayAgentPlatform( PayAgentPlatform payAgentPlatform ) {
		return payAgentPlatformMapper.updatePayAgentPlatform( payAgentPlatform );
	}

	/**
	 * 批量删除代付平台
	 *
	 * @param ids 需要删除的代付平台ID
	 * @return 结果
	 */
	@Override
	public int deletePayAgentPlatformByIds( Long[] ids ) {
		return payAgentPlatformMapper.deletePayAgentPlatformByIds( ids );
	}

	/**
	 * 删除代付平台信息
	 *
	 * @param id 代付平台ID
	 * @return 结果
	 */
	@Override
	public int deletePayAgentPlatformById( Long id ) {
		return payAgentPlatformMapper.deletePayAgentPlatformById( id );
	}
}
