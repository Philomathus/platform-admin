package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.cache.GameCacheManager;
import com.qiqilm.server.admin.domain.GameInfo;
import com.qiqilm.server.admin.domain.GameType;
import com.qiqilm.server.admin.domain.GameTypeWith;
import com.qiqilm.server.admin.domain.req.ReqTypeGame;
import com.qiqilm.server.admin.domain.rsp.RspGameInfo;
import com.qiqilm.server.admin.domain.rsp.RspTypeGames;
import com.qiqilm.server.admin.mapper.GameInfoMapper;
import com.qiqilm.server.admin.mapper.GameTypeMapper;
import com.qiqilm.server.admin.mapper.GameTypeWithMapper;
import com.qiqilm.server.admin.service.IGameTypeService;
import com.qiqilm.server.admin.utils.StringUtils;
import com.qiqilm.server.admin.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
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
	private GameTypeMapper        gameTypeMapper;
	@Autowired
	private GameInfoMapper        gameInfoMapper;
	@Autowired
	private GameCacheManager      gameCacheManager;
	@Autowired
	private GameTypeWithMapper    gameTypeWithMapper;
	@Autowired
	private ConfigDomainCacheUtil configDomainCacheUtil;

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
		List<GameType> gameTypes = gameTypeMapper.selectGameTypeList( gameType );
		if ( !CollectionUtils.isEmpty( gameTypes ) ) {
			String domainValue = configDomainCacheUtil.getValue( "domain.oss" );
			for ( GameType type : gameTypes ) {
				if ( StringUtils.isNotBlank( type.getIcon() ) && !type.getIcon().startsWith( "http" ) ) {
					type.setIcon( domainValue + type.getIcon() );
				}
			}
		}
		return gameTypes;
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

	@Override
	public RspTypeGames findTypeGames( String id ) {
		List<RspGameInfo> allGameList  = gameInfoMapper.findTypeGames();
		List<String>      typeGameList = gameInfoMapper.findTypeHasGames( id );
		RspTypeGames      rspTypeGames = new RspTypeGames();
		rspTypeGames.setAll_games( allGameList );
		rspTypeGames.setType_games( typeGameList );
		return rspTypeGames;
	}

	@Override
	public void addTypeGames( ReqTypeGame dto ) {

		gameTypeWithMapper.deleteTypeGames( dto.getTypeId() );
		List<String> typeGames = dto.getType_games();
		for ( String id : typeGames ) {
			GameInfo     gameInfo = gameInfoMapper.selectGameInfoById( id );
			GameTypeWith typeGame = new GameTypeWith();
			typeGame.setId( UuidUtil.getRandomUuidWithoutSeparator() );
			typeGame.setTypeId( dto.getTypeId() );
			typeGame.setCreateTime( new Date() );
			typeGame.setGameId( id );
			typeGame.setKindId( gameInfo != null ? gameInfo.getKindId() : "" );
			gameTypeWithMapper.insertGameTypeWith( typeGame );
		}
		gameCacheManager.initGameGroup();
	}
}