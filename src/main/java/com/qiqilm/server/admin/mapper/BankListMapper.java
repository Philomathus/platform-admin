package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.BankList;

/**
 * 出款银行列表Mapper接口
 *
 * @author 77tv
 * @date 2021-04-06
 */
public interface BankListMapper {
	/**
	 * 查询出款银行列表
	 *
	 * @param id 出款银行列表ID
	 * @return 出款银行列表
	 */
	public BankList selectBankListById(Long id);

	/**
	 * 查询出款银行列表列表
	 *
	 * @param bankList 出款银行列表
	 * @return 出款银行列表集合
	 */
	public List<BankList> selectBankListList(BankList bankList);

	/**
	 * 新增出款银行列表
	 *
	 * @param bankList 出款银行列表
	 * @return 结果
	 */
	public int insertBankList(BankList bankList);

	/**
	 * 修改出款银行列表
	 *
	 * @param bankList 出款银行列表
	 * @return 结果
	 */
	public int updateBankList(BankList bankList);

	/**
	 * 删除出款银行列表
	 *
	 * @param id 出款银行列表ID
	 * @return 结果
	 */
	public int deleteBankListById(Long id);

	/**
	 * 批量删除出款银行列表
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteBankListByIds(Long[] ids );
}
