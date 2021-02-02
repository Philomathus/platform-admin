package com.qiqilm.server.admin.mapper;

import java.math.BigDecimal;
import java.util.List;

import com.qiqilm.server.admin.domain.PayAgentRechargeAccountLog;
import org.apache.ibatis.annotations.Param;

/**
 * 【代充人入款】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface PayAgentRechargeAccountLogMapper {
	/**
	 * 查询【代充人入款】
	 *
	 * @param orderNo 【代充人入款】ID
	 * @return 【代充人入款】
	 */
	public PayAgentRechargeAccountLog selectPayAgentRechargeAccountLogById(String orderNo);

	/**
	 * 查询【代充人入款】列表
	 *
	 * @param payAgentRechargeAccountLog 【代充人入款】
	 * @return 【代充人入款】集合
	 */
	public List<PayAgentRechargeAccountLog> selectPayAgentRechargeAccountLogList(PayAgentRechargeAccountLog payAgentRechargeAccountLog);

	/**
	 * 新增【代充人入款】
	 *
	 * @param payAgentRechargeAccountLog 【代充人入款】
	 * @return 结果
	 */
	public int insertPayAgentRechargeAccountLog(PayAgentRechargeAccountLog payAgentRechargeAccountLog);

	/**
	 * 修改【代充人入款】
	 *
	 * @param payAgentRechargeAccountLog 【代充人入款】
	 * @return 结果
	 */
	public int updatePayAgentRechargeAccountLog(PayAgentRechargeAccountLog payAgentRechargeAccountLog);

	/**
	 * 删除【代充人入款】
	 *
	 * @param orderNo 【代充人入款】ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeAccountLogById(String orderNo);

	/**
	 * 批量删除【代充人入款】
	 *
	 * @param orderNos 需要删除的数据ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeAccountLogByIds(String[] orderNos );

	//根据account存入加钱
	int updateByBalanceAmount(@Param("Account") String Account, @Param("BalanceAmount") BigDecimal BalanceAmount);
}
