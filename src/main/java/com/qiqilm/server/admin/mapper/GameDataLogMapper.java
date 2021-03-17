package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.GameDataLog;

/**
 * 总代理游戏注单Mapper接口
 *
 * @author 77tv
 * @date 2021-03-17
 */
public interface GameDataLogMapper {
	/**
	 * 查询总代理游戏注单
	 *
	 * @param id 总代理游戏注单ID
	 * @return 总代理游戏注单
	 */
	public GameDataLog selectGameDataLogById(String id);

	/**
	 * 查询总代理游戏注单列表
	 *
	 * @param gameDataLog 总代理游戏注单
	 * @return 总代理游戏注单集合
	 */
	public List<GameDataLog> selectGameDataLogList(GameDataLog gameDataLog);

	/**
	 * 新增总代理游戏注单
	 *
	 * @param gameDataLog 总代理游戏注单
	 * @return 结果
	 */
	public int insertGameDataLog(GameDataLog gameDataLog);

	/**
	 * 修改总代理游戏注单
	 *
	 * @param gameDataLog 总代理游戏注单
	 * @return 结果
	 */
	public int updateGameDataLog(GameDataLog gameDataLog);

	/**
	 * 删除总代理游戏注单
	 *
	 * @param id 总代理游戏注单ID
	 * @return 结果
	 */
	public int deleteGameDataLogById(String id);

	/**
	 * 批量删除总代理游戏注单
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteGameDataLogByIds(String[] ids );
}
