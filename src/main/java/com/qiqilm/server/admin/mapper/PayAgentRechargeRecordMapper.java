package com.qiqilm.server.admin.mapper;

import java.math.BigDecimal;
import java.util.List;

import com.qiqilm.server.admin.domain.LiveVideoProp;
import com.qiqilm.server.admin.domain.PayAgentRechargeRecord;
import org.apache.ibatis.annotations.Param;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface PayAgentRechargeRecordMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param orderNo 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public PayAgentRechargeRecord selectPayAgentRechargeRecordById(String orderNo);

	PayAgentRechargeRecord getCount(PayAgentRechargeRecord payAgentRechargeRecord );

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param payAgentRechargeRecord 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<PayAgentRechargeRecord> selectPayAgentRechargeRecordList(PayAgentRechargeRecord payAgentRechargeRecord);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param payAgentRechargeRecord 【请填写功能名称】
	 * @return 结果
	 */
	public int insertPayAgentRechargeRecord(PayAgentRechargeRecord payAgentRechargeRecord);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param payAgentRechargeRecord 【请填写功能名称】
	 * @return 结果
	 */
	public int updatePayAgentRechargeRecord(PayAgentRechargeRecord payAgentRechargeRecord);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param orderNo 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeRecordById(String orderNo);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param orderNos 需要删除的数据ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeRecordByIds(String[] orderNos );

	//根据account存入加钱
	int updateByBalanceAmount(@Param("Account") String Account, @Param("BalanceAmount") BigDecimal BalanceAmount);
	//根据account存入減钱

	int updateByBalanceAmountLess(@Param("Account") String Account,@Param("BalanceAmount") BigDecimal BalanceAmount);
}
