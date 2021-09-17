package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.PayUsdtRecharge;
import com.qiqilm.server.admin.domain.req.ReqPayUsdtRecharge;

/**
 * USDT充值提交记录Mapper接口
 *
 * @author 77tv
 * @date 2021-09-14
 */
public interface PayUsdtRechargeMapper {
	/**
	 * 查询USDT充值提交记录
	 *
	 * @param id USDT充值提交记录ID
	 * @return USDT充值提交记录
	 */
	public PayUsdtRecharge selectPayUsdtRechargeById(Long id);

	/**
	 * 查询USDT充值提交记录列表
	 *
	 * @param reqPayUsdtRecharge USDT充值提交记录
	 * @return USDT充值提交记录集合
	 */
	public List<PayUsdtRecharge> selectPayUsdtRechargeList(ReqPayUsdtRecharge reqPayUsdtRecharge);

	/**
	 * 新增USDT充值提交记录
	 *
	 * @param payUsdtRecharge USDT充值提交记录
	 * @return 结果
	 */
	public int insertPayUsdtRecharge(PayUsdtRecharge payUsdtRecharge);

	/**
	 * 修改USDT充值提交记录
	 *
	 * @param payUsdtRecharge USDT充值提交记录
	 * @return 结果
	 */
	public int updatePayUsdtRecharge(PayUsdtRecharge payUsdtRecharge);

	/**
	 * 删除USDT充值提交记录
	 *
	 * @param id USDT充值提交记录ID
	 * @return 结果
	 */
	public int deletePayUsdtRechargeById(Long id);

	/**
	 * 批量删除USDT充值提交记录
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deletePayUsdtRechargeByIds(Long[] ids );
}
