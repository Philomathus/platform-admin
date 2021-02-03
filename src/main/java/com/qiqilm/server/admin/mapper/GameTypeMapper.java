package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.GameType;

import java.util.List;

/**
 * 游戏类型Mapper接口
 *
 * @author 77tv
 * @date 2021-01-24
 */
public interface GameTypeMapper {
	/**
	 * 查询游戏类型
	 *
	 * @param id 游戏类型ID
	 * @return 游戏类型
	 */
	public GameType selectGameTypeById( String id );

	/**
	 * 查询游戏类型id和name
	 *
	 * @param id 游戏类型ID
	 * @return 游戏类型
	 */
	public List<GameType> selectGameTypeName();

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
	 * 删除游戏类型
	 *
	 * @param id 游戏类型ID
	 * @return 结果
	 */
	public int deleteGameTypeById( String id );

	/**
	 * 批量删除游戏类型
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteGameTypeByIds( String[] ids );
}