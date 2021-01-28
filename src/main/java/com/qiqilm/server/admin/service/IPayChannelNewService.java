package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.PayChannelNew;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface IPayChannelNewService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public PayChannelNew selectPayChannelNewById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param payChannelNew 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<PayChannelNew> selectPayChannelNewList(PayChannelNew payChannelNew);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param payChannelNew 【请填写功能名称】
	 * @return 结果
	 */
	public int insertPayChannelNew(PayChannelNew payChannelNew);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param payChannelNew 【请填写功能名称】
	 * @return 结果
	 */
	public int updatePayChannelNew(PayChannelNew payChannelNew);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayChannelNewByIds(Long[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayChannelNewById(Long id);
}
