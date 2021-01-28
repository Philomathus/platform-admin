package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.ServerLive;

/**
 * 直播流服务配置Mapper接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface ServerLiveMapper {
	/**
	 * 查询直播流服务配置
	 *
	 * @param id 直播流服务配置ID
	 * @return 直播流服务配置
	 */
	public ServerLive selectServerLiveById(Long id);

	/**
	 * 查询直播流服务配置列表
	 *
	 * @param serverLive 直播流服务配置
	 * @return 直播流服务配置集合
	 */
	public List<ServerLive> selectServerLiveList(ServerLive serverLive);

	/**
	 * 新增直播流服务配置
	 *
	 * @param serverLive 直播流服务配置
	 * @return 结果
	 */
	public int insertServerLive(ServerLive serverLive);

	/**
	 * 修改直播流服务配置
	 *
	 * @param serverLive 直播流服务配置
	 * @return 结果
	 */
	public int updateServerLive(ServerLive serverLive);

	/**
	 * 删除直播流服务配置
	 *
	 * @param id 直播流服务配置ID
	 * @return 结果
	 */
	public int deleteServerLiveById(Long id);

	/**
	 * 批量删除直播流服务配置
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteServerLiveByIds(Long[] ids );
}
