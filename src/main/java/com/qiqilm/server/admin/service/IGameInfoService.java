package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.GameInfo;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.rsp.RspGameInfo;

import java.util.List;


/**
 * 游戏信息Service接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface IGameInfoService {
	/**
	 * 查询游戏信息
	 *
	 * @param id 游戏信息ID
	 * @return 游戏信息
	 */
	public GameInfo selectGameInfoById(String id);


	Integer updateStatus(GameInfo gameInfo);

	int changeIsWh(GameInfo gameInfo);

	/**
	 * 查询游戏信息列表
	 *
	 * @param gameInfo 游戏信息
	 * @return 游戏信息集合
	 */
	public List<RspGameInfo> selectGameInfoList(GameInfo gameInfo);

	List<GamePlatform> getGameListInfo();

	/**
	 * 新增游戏信息
	 *
	 * @param gameInfo 游戏信息
	 * @return 结果
	 */
	public int insertGameInfo(GameInfo gameInfo);

	/**
	 * 修改游戏信息
	 *
	 * @param gameInfo 游戏信息
	 * @return 结果
	 */
	public int updateGameInfo(GameInfo gameInfo);

	/**
	 * 批量删除游戏信息
	 *
	 * @param ids 需要删除的游戏信息ID
	 * @return 结果
	 */
	public int deleteGameInfoByIds(String id );

	/**
	 * 删除游戏信息信息
	 *
	 * @param id 游戏信息ID
	 * @return 结果
	 */
	public int deleteGameInfoById(String id);

    //给任务信息做的所属游戏下拉框
	List<GameInfo> selectGameInfo();

}