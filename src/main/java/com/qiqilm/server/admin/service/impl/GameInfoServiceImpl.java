package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.cache.GameCacheManager;
import com.qiqilm.server.admin.domain.GameInfo;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.rsp.RspGameInfo;
import com.qiqilm.server.admin.mapper.GameInfoMapper;
import com.qiqilm.server.admin.mapper.GamePlatformMapper;
import com.qiqilm.server.admin.service.IGameInfoService;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * 游戏信息Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class GameInfoServiceImpl implements IGameInfoService {
	@Autowired
	private GameInfoMapper        gameInfoMapper;
	@Autowired
	private GamePlatformMapper    gamePlatformMapper;
	@Autowired
	private ConfigDomainCacheUtil configDomainCacheUtil;
	@Autowired
	private GameCacheManager gameCacheManager;

	/**
	 * 查询游戏信息
	 *
	 * @param id 游戏信息ID
	 * @return 游戏信息
	 */
	@Override
	public GameInfo selectGameInfoById( String id ) {
		return gameInfoMapper.selectGameInfoById( id );
	}

	@Override
	public Integer updateStatus( GameInfo gameInfo ) {
        int i = gameInfoMapper.updateStatus(gameInfo);
//        int i = gameInfoMapper.updateGameInfo(gameInfo);
        updateRedis(gameInfo);
        return i;

	}

    private void updateRedis(GameInfo gameInfo) {
        gameInfo = gameInfoMapper.selectGameInfoById(gameInfo.getId());
        gameCacheManager.setGameInfo(gameInfo);
        gameCacheManager.initGameGroup();
    }

    @Override
	public int changeIsWh( GameInfo gameInfo ) {
        int i = gameInfoMapper.changeIsWh(gameInfo);
        updateRedis(gameInfo);
		return i;

	}

	/**
	 * 查询游戏信息列表
	 *
	 * @param gameInfo 游戏信息
	 * @return 游戏信息
	 */
	@Override
	public List<RspGameInfo> selectGameInfoList( GameInfo gameInfo ) {
		List<RspGameInfo> rspGameInfos = gameInfoMapper.selectGameInfoList( gameInfo );
		if ( !CollectionUtils.isEmpty( rspGameInfos ) ) {
			String domainValue = configDomainCacheUtil.getValue( "domain.oss" );
			for ( RspGameInfo info : rspGameInfos ) {
				if ( StringUtils.isNotBlank( info.getIcon() ) && !info.getIcon().startsWith( "http" ) ) {
					info.setIcon( domainValue + info.getIcon() );
				}
				if ( StringUtils.isNotBlank( info.getEditionIcon() ) && !info.getEditionIcon().startsWith( "http" ) ) {
					info.setEditionIcon( domainValue + info.getEditionIcon() );
				}
			}
		}
		return rspGameInfos;
	}

	@Override
	public List<GamePlatform> getGameListInfo() {
		return gamePlatformMapper.getGameListInfo();
	}

	/**
	 * 新增游戏信息
	 *
	 * @param gameInfo 游戏信息
	 * @return 结果
	 */
	@Override
	public int insertGameInfo( GameInfo gameInfo ) {
		int i = gameInfoMapper.insertGameInfo(gameInfo);
		gameCacheManager.setGameInfo(gameInfo);
		return i;
	}

	/**
	 * 修改游戏信息
	 *
	 * @param gameInfo 游戏信息
	 * @return 结果
	 */
	@Override
	public int updateGameInfo( GameInfo gameInfo ) {
		boolean updateOrder = gameInfo.getIndexs()!=gameInfo.getIndexs();
		int i = gameInfoMapper.updateGameInfo(gameInfo);
		gameCacheManager.setGameInfo(gameInfo);
		if(updateOrder){
			gameCacheManager.initGameGroup();
		}
		return i;
	}

	/**
	 * 批量删除游戏信息
	 *
	 * @param ids 需要删除的游戏信息ID
	 * @return 结果
	 */
	@Override
	public int deleteGameInfoByIds( String id ) {
		return gameInfoMapper.deleteGameInfoByIds( id );
	}

	/**
	 * 删除游戏信息信息
	 *
	 * @param id 游戏信息ID
	 * @return 结果
	 */
	@Override
	public int deleteGameInfoById( String id ) {
		return gameInfoMapper.deleteGameInfoById( id );
	}

	@Override
	public List<GameInfo> selectGameInfo() {
		return gameInfoMapper.selectGameInfo();
	}

	@Override
	public List<GameInfo> kindIdSelect() {
		return gameInfoMapper.kindIdSelect();
	}
}
