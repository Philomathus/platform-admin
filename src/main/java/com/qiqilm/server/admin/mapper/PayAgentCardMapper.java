package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.PayAgentCard;

/**
 * 代充人银行卡列表Mapper接口
 *
 * @author 77tv
 * @date 2021-09-24
 */
public interface PayAgentCardMapper {
	/**
	 * 查询代充人银行卡
	 *
	 * @param id 代充人银行卡ID
	 * @return 代充人银行卡
	 */
	public PayAgentCard selectPayAgentCardById(Long id);

	/**
	 * 查询代充人银行卡列表
	 *
	 * @param payAgentCard 代充人银行卡列表
	 * @return 代充人银行卡列表集合
	 */
	public List<PayAgentCard> selectPayAgentCardList(PayAgentCard payAgentCard);

	/**
	 * 新增代充人银行卡列表
	 *
	 * @param payAgentCard 代充人银行卡列表
	 * @return 结果
	 */
	public int insertPayAgentCard(PayAgentCard payAgentCard);

	/**
	 * 修改代充人银行卡列表
	 *
	 * @param payAgentCard 代充人银行卡列表
	 * @return 结果
	 */
	public int updatePayAgentCard(PayAgentCard payAgentCard);

	/**
	 * 删除代充人银行卡列表
	 *
	 * @param id 代充人银行卡列表ID
	 * @return 结果
	 */
	public int deletePayAgentCardById(Long id);

	/**
	 * 批量删除代充人银行卡列表
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deletePayAgentCardByIds(Long[] ids );
}
