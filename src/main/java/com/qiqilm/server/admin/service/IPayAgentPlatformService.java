package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.PayAgentPlatform;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IPayAgentPlatformService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public PayAgentPlatform selectPayAgentPlatformById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param payAgentPlatform 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<PayAgentPlatform> selectPayAgentPlatformList(PayAgentPlatform payAgentPlatform);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param payAgentPlatform 【请填写功能名称】
	 * @return 结果
	 */
	public int insertPayAgentPlatform(PayAgentPlatform payAgentPlatform);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param payAgentPlatform 【请填写功能名称】
	 * @return 结果
	 */
	public int updatePayAgentPlatform(PayAgentPlatform payAgentPlatform);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayAgentPlatformByIds(Long[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayAgentPlatformById(Long id);
}
