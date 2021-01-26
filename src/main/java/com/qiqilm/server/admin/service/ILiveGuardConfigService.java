package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveGuardConfig;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface ILiveGuardConfigService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public LiveGuardConfig selectLiveGuardConfigById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param liveGuardConfig 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<LiveGuardConfig> selectLiveGuardConfigList(LiveGuardConfig liveGuardConfig);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param liveGuardConfig 【请填写功能名称】
	 * @return 结果
	 */
	public int insertLiveGuardConfig(LiveGuardConfig liveGuardConfig);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param liveGuardConfig 【请填写功能名称】
	 * @return 结果
	 */
	public int updateLiveGuardConfig(LiveGuardConfig liveGuardConfig);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteLiveGuardConfigByIds(Long[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteLiveGuardConfigById(Long id);
}
