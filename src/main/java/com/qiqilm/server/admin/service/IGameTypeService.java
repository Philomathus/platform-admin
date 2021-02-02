package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.GameType;
import com.qiqilm.server.admin.domain.req.ReqTypeGame;
import com.qiqilm.server.admin.domain.rsp.RspTypeGames;

import java.util.List;

/**
 * 游戏类型Service接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface IGameTypeService {
	/**
	 * 查询游戏类型
	 *
	 * @param id 游戏类型ID
	 * @return 游戏类型
	 */
	public GameType selectGameTypeById( String id );

	/**
	 * 查询游戏类型列表
	 *
	 * @param gameType 游戏类型
	 * @return 游戏类型集合
	 */
	public List<GameType> selectGameTypeList( GameType gameType );

	/**
	 * 新增游戏类型
	 *
	 * @param gameType 游戏类型
	 * @return 结果
	 */
	public int insertGameType( GameType gameType );

	/**
	 * 修改游戏类型
	 *
	 * @param gameType 游戏类型
	 * @return 结果
	 */
	public int updateGameType( GameType gameType );

	/**
	 * 批量删除游戏类型
	 *
	 * @param ids 需要删除的游戏类型ID
	 * @return 结果
	 */
	public int deleteGameTypeByIds( String[] ids );

	/**
	 * 删除游戏类型信息
	 *
	 * @param id 游戏类型ID
	 * @return 结果
	 */
	public int deleteGameTypeById( String id );

	RspTypeGames findTypeGames(String id);

    void addTypeGames(ReqTypeGame dto);
}