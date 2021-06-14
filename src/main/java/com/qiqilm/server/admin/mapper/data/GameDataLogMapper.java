package com.qiqilm.server.admin.mapper.data;

import com.qiqilm.server.admin.domain.GameDataLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
	 * @return 总代理游戏注单集合
	 */
	public List<GameDataLog> selectGameDataLogList(@Param( "cxAgent" )String cxAgent,@Param( "start" )String start,@Param( "end" )String end,@Param( "account" )String account,@Param( "platformId" )String platformId);


	public List<GameDataLog> selectGameDataAgentList(@Param( "tableNode" )String tableNode,@Param( "start" )String start,@Param( "end" )String end,@Param( "account" )String account,@Param( "platformId" )String platformId);

	/**
	 * 批量删除总代理游戏注单
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteGameDataLogByIds(String[] ids );
}
