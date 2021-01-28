package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.GameType;
import com.qiqilm.server.admin.mapper.GameTypeMapper;
import com.qiqilm.server.admin.service.IGameTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 游戏类型Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class GameTypeServiceImpl implements IGameTypeService {
	@Autowired
	private GameTypeMapper gameTypeMapper;

	/**
	 * 查询游戏类型
	 *
	 * @param id 游戏类型ID
	 * @return 游戏类型
	 */
	@Override
	public GameType selectGameTypeById( String id ) {
		return gameTypeMapper.selectGameTypeById( id );
	}

	/**
	 * 查询游戏类型列表
	 *
	 * @param gameType 游戏类型
	 * @return 游戏类型
	 */
	@Override
	public List<GameType> selectGameTypeList( GameType gameType ) {
		return gameTypeMapper.selectGameTypeList( gameType );
	}

	/**
	 * 新增游戏类型
	 *
	 * @param gameType 游戏类型
	 * @return 结果
	 */
	@Override
	public int insertGameType( GameType gameType ) {
		return gameTypeMapper.insertGameType( gameType );
	}

	/**
	 * 修改游戏类型
	 *
	 * @param gameType 游戏类型
	 * @return 结果
	 */
	@Override
	public int updateGameType( GameType gameType ) {
		return gameTypeMapper.updateGameType( gameType );
	}

	/**
	 * 批量删除游戏类型
	 *
	 * @param ids 需要删除的游戏类型ID
	 * @return 结果
	 */
	@Override
	public int deleteGameTypeByIds( String[] ids ) {
		return gameTypeMapper.deleteGameTypeByIds( ids );
	}

	/**
	 * 删除游戏类型信息
	 *
	 * @param id 游戏类型ID
	 * @return 结果
	 */
	@Override
	public int deleteGameTypeById( String id ) {
		return gameTypeMapper.deleteGameTypeById( id );
	}
}