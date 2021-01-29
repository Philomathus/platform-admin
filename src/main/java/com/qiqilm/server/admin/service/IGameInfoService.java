package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.GameInfo;
import com.qiqilm.server.admin.domain.rsp.RspGameInfo;

import java.util.List;


/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface IGameInfoService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public GameInfo selectGameInfoById(String id);


	Integer updateStatus(GameInfo gameInfo);

	int changeIsWh(GameInfo gameInfo);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param gameInfo 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<RspGameInfo> selectGameInfoList(GameInfo gameInfo);

	List<RspGameInfo> getGameListInfo();

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param gameInfo 【请填写功能名称】
	 * @return 结果
	 */
	public int insertGameInfo(GameInfo gameInfo);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param gameInfo 【请填写功能名称】
	 * @return 结果
	 */
	public int updateGameInfo(GameInfo gameInfo);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteGameInfoByIds(String id );

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteGameInfoById(String id);

    //给任务信息做的所属游戏下拉框
	List<GameInfo> selectGameInfo();

}