package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.ConfigGametype;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.rsp.RspGameInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface GamePlatformMapper {



	List<RspGameInfo> getGameListInfo();
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public GamePlatform selectGamePlatformById(Long id);

	int changeStattus(GamePlatform gamePlatform);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param gamePlatform 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<GamePlatform> selectGamePlatformList(GamePlatform gamePlatform);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param gamePlatform 【请填写功能名称】
	 * @return 结果
	 */
	public int insertGamePlatform(GamePlatform gamePlatform);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param gamePlatform 【请填写功能名称】
	 * @return 结果
	 */
	public int updateGamePlatform(GamePlatform gamePlatform);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteGamePlatformById(Long id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteGamePlatformByIds(Long[] ids );

	GamePlatform findAgentList(@Param("req") ConfigGametype configGametype);


	List<GamePlatform> findSimpleList();
}