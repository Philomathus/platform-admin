package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.BankCardAddress;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-04-21
 */
public interface IBankCardAddressService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public BankCardAddress selectBankCardAddressById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param bankCardAddress 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<BankCardAddress> selectBankCardAddressList(BankCardAddress bankCardAddress);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param bankCardAddress 【请填写功能名称】
	 * @return 结果
	 */
	public int insertBankCardAddress(BankCardAddress bankCardAddress);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param bankCardAddress 【请填写功能名称】
	 * @return 结果
	 */
	public int updateBankCardAddress(BankCardAddress bankCardAddress);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteBankCardAddressByIds(String[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteBankCardAddressById(String id);
}