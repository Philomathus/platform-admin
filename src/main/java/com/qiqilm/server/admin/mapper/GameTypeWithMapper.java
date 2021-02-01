package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.GameTypeWith;
import com.qiqilm.server.admin.domain.rsp.RspGameInfo;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-02-01
 */
public interface GameTypeWithMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public GameTypeWith selectGameTypeWithById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param gameTypeWith 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<GameTypeWith> selectGameTypeWithList(GameTypeWith gameTypeWith);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param gameTypeWith 【请填写功能名称】
	 * @return 结果
	 */
	public int insertGameTypeWith(GameTypeWith gameTypeWith);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param gameTypeWith 【请填写功能名称】
	 * @return 结果
	 */
	public int updateGameTypeWith(GameTypeWith gameTypeWith);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteGameTypeWithById(String id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteGameTypeWithByIds(String[] ids );


}