package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.PayType;

/**
 * 支付类型Mapper接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface PayTypeMapper {
	/**
	 * 查询支付类型
	 *
	 * @param id 支付类型ID
	 * @return 支付类型
	 */
	public PayType selectPayTypeById(String id);

	/**
	 * 查询支付类型列表
	 *
	 * @param payType 支付类型
	 * @return 支付类型集合
	 */
	public List<PayType> selectPayTypeList(PayType payType);

	/**
	 * 新增支付类型
	 *
	 * @param payType 支付类型
	 * @return 结果
	 */
	public int insertPayType(PayType payType);

	/**
	 * 修改支付类型
	 *
	 * @param payType 支付类型
	 * @return 结果
	 */
	public int updatePayType(PayType payType);

	/**
	 * 删除支付类型
	 *
	 * @param id 支付类型ID
	 * @return 结果
	 */
	public int deletePayTypeById(String id);

	/**
	 * 批量删除支付类型
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deletePayTypeByIds(String[] ids );
}
