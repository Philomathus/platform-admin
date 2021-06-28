package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.PayAgentRechargeAccount;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface PayAgentRechargeAccountMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public PayAgentRechargeAccount selectPayAgentRechargeAccountById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param map 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<PayAgentRechargeAccount> selectPayAgentRechargeAccountList(Map<String,Object> map);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param payAgentRechargeAccount 【请填写功能名称】
	 * @return 结果
	 */
	public int insertPayAgentRechargeAccount(PayAgentRechargeAccount payAgentRechargeAccount);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param payAgentRechargeAccount 【请填写功能名称】
	 * @return 结果
	 */
	public int updatePayAgentRechargeAccount(PayAgentRechargeAccount payAgentRechargeAccount);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeAccountById(Long id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deletePayAgentRechargeAccountByIds(Long[] ids );

	int memberIdSearchRepeat(@Param("account") String account);

    void updateGoogle(PayAgentRechargeAccount payAgentRechargeAccount);

	void updatePassword(@Param("id") String id,@Param("password") String password);

	PayAgentRechargeAccount selectPayAgentRechargeAccount(@Param("account") String account);

}
