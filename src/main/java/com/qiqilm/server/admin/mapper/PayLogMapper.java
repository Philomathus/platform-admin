package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.PayLog;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface PayLogMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public PayLog selectPayLogById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param payLog 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<PayLog> selectPayLogList(PayLog payLog);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param payLog 【请填写功能名称】
	 * @return 结果
	 */
	public int insertPayLog(PayLog payLog);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param payLog 【请填写功能名称】
	 * @return 结果
	 */
	public int updatePayLog(PayLog payLog);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayLogById(Long id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deletePayLogByIds(Long[] ids );
}
