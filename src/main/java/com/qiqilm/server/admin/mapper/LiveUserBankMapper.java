package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveUserBank;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-04-23
 */
public interface LiveUserBankMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public LiveUserBank selectLiveUserBankById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param liveUserBank 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<LiveUserBank> selectLiveUserBankList(LiveUserBank liveUserBank);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param liveUserBank 【请填写功能名称】
	 * @return 结果
	 */
	public int insertLiveUserBank(LiveUserBank liveUserBank);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param liveUserBank 【请填写功能名称】
	 * @return 结果
	 */
	public int updateLiveUserBank(LiveUserBank liveUserBank);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteLiveUserBankById(Long id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLiveUserBankByIds(Long[] ids );

	List<LiveUserBank> getLiveUserBankInfo();
}