package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.PayPlatformNew;
import com.qiqilm.server.admin.domain.rsp.RspPayPlatformNew;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface PayPlatformNewMapper {
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
	public List<RspPayPlatformNew> selectPayPlatformNewList(PayPlatformNew payPlatformNew);

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
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayPlatformNewById(Long id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deletePayPlatformNewByIds(Long[] ids );
}
