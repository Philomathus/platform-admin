package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.GamePlatform;

import java.util.List;


/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface IGamePlatformService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public GamePlatform selectGamePlatformById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param gamePlatform 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<GamePlatform> selectGamePlatformList(GamePlatform gamePlatform);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param gamePlatform 【请填写功能名称】
	 * @return 结果
	 */
	public int insertGamePlatform(GamePlatform gamePlatform);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param gamePlatform 【请填写功能名称】
	 * @return 结果
	 */
	public int updateGamePlatform(GamePlatform gamePlatform);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteGamePlatformByIds(Long[] ids );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteGamePlatformById(Long id);
}