package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.GamePlatform;

import java.util.List;

/**
 * 游戏平台Service接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface IGamePlatformService {
	/**
	 * 查询游戏平台
	 *
	 * @param id 游戏平台ID
	 * @return 游戏平台
	 */
	public GamePlatform selectGamePlatformById(Integer id);

	/**
	 * 查询游戏平台列表
	 *
	 * @param gamePlatform 游戏平台
	 * @return 游戏平台集合
	 */
	public List<GamePlatform> selectGamePlatformList(GamePlatform gamePlatform);
	int changeStatus(GamePlatform gamePlatform);
	/**
	 * 新增游戏平台
	 *
	 * @param gamePlatform 游戏平台
	 * @return 结果
	 */
	public int insertGamePlatform(GamePlatform gamePlatform);

	/**
	 * 修改游戏平台
	 *
	 * @param gamePlatform 游戏平台
	 * @return 结果
	 */
	public int updateGamePlatform(GamePlatform gamePlatform);

	/**
	 * 批量删除游戏平台
	 *
	 * @param ids 需要删除的游戏平台ID
	 * @return 结果
	 */
	public int deleteGamePlatformByIds(Long[] ids );

	/**
	 * 删除游戏平台信息
	 *
	 * @param id 游戏平台ID
	 * @return 结果
	 */
	public int deleteGamePlatformById(Long id);

	List<GamePlatform> platformIdSelect();

}