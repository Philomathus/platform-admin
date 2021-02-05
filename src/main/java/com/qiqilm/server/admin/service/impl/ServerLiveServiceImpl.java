package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.RedisCacheUtil;
import com.qiqilm.server.admin.cache.ServerLiveCacheUtil;
import com.qiqilm.server.admin.domain.ServerLive;
import com.qiqilm.server.admin.mapper.ServerLiveMapper;
import com.qiqilm.server.admin.service.IServerLiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 直播流服务配置Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class ServerLiveServiceImpl implements IServerLiveService {
	@Autowired
	private ServerLiveMapper    serverLiveMapper;
	@Autowired
	private ServerLiveCacheUtil serverLiveCacheUtil;

	/**
	 * 查询直播流服务配置
	 *
	 * @param id 直播流服务配置ID
	 * @return 直播流服务配置
	 */
	@Override
	public ServerLive selectServerLiveById( Long id ) {
		return RedisCacheUtil.me.get( id, () -> serverLiveMapper.selectServerLiveById( id ) );
	}

	/**
	 * 查询直播流服务配置列表
	 *
	 * @param serverLive 直播流服务配置
	 * @return 直播流服务配置
	 */
	@Override
	public List<ServerLive> selectServerLiveList( ServerLive serverLive ) {
		return serverLiveMapper.selectServerLiveList( serverLive );
	}

	/**
	 * 新增直播流服务配置
	 *
	 * @param serverLive 直播流服务配置
	 * @return 结果
	 */
	@Override
	public int insertServerLive( ServerLive serverLive ) {
		return serverLiveMapper.insertServerLive( serverLive );
	}

	/**
	 * 修改直播流服务配置
	 *
	 * @param serverLive 直播流服务配置
	 * @return 结果
	 */
	@Override
	public int updateServerLive( ServerLive serverLive ) {
		int i = serverLiveMapper.updateServerLive( serverLive );
		if ( i > 0 ) {
			RedisCacheUtil.me.clear( serverLive.getId(), ServerLive.class );
		}
		return i;
	}

	/**
	 * 批量删除直播流服务配置
	 *
	 * @param ids 需要删除的直播流服务配置ID
	 * @return 结果
	 */
	@Override
	public int deleteServerLiveByIds( Long[] ids ) {
		int i = serverLiveMapper.deleteServerLiveByIds( ids );
		if ( i > 0 ) {
			Arrays.stream( ids ).forEach( id -> RedisCacheUtil.me.clear( id, ServerLive.class ) );
		}
		return i;
	}

	/**
	 * 删除直播流服务配置信息
	 *
	 * @param id 直播流服务配置ID
	 * @return 结果
	 */
	@Override
	public int deleteServerLiveById( Long id ) {
		int i = serverLiveMapper.deleteServerLiveById( id );
		if ( i > 0 ) {
			RedisCacheUtil.me.clear( id, ServerLive.class );
		}
		return i;
	}

	@Override
	public int changeStatus( long id, int status ) {
		ServerLive updateServer = new ServerLive();
		updateServer.setId( id );
		updateServer.setStatus( status );
		int update = this.updateServerLive( updateServer );
		if ( update > 0 && status > 0 ) {
			// 更新到redis
			ServerLive serverLive = this.selectServerLiveById( id );
			serverLiveCacheUtil.setServerLive( serverLive );
		}
		if ( update > 0 && status <= 0 ) {
			serverLiveCacheUtil.clear( id );
		}
		return update;
	}
}
