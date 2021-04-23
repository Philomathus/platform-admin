package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.BankCardAddress;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-04-21
 */
public interface BankCardAddressMapper {
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
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteBankCardAddressById(String id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteBankCardAddressByIds(String[] ids );

	BankCardAddress selectBankCardAddress(String province);
}