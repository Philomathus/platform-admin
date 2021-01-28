package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.PayPlatformNew;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface IPayPlatformNewService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public PayPlatformNew selectPayPlatformNewById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param payPlatformNew 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<PayPlatformNew> selectPayPlatformNewList(PayPlatformNew payPlatformNew);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param payPlatformNew 【请填写功能名称】
	 * @return 结果
	 */
	public int insertPayPlatformNew(PayPlatformNew payPlatformNew);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param payPlatformNew 【请填写功能名称】
	 * @return 结果
	 */
	public int updatePayPlatformNew(PayPlatformNew payPlatformNew);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayPlatformNewByIds(Long[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayPlatformNewById(Long id);
}
