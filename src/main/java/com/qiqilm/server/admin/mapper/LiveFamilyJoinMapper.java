package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveFamilyJoin;

/**
 * 家族申请Mapper接口
 *
 * @author 77tv
 * @date 2021-06-09
 */
public interface LiveFamilyJoinMapper {
	/**
	 * 查询家族申请
	 *
	 * @param id 家族申请ID
	 * @return 家族申请
	 */
	public LiveFamilyJoin selectLiveFamilyJoinById(String id);

	/**
	 * 查询家族申请列表
	 *
	 * @param liveFamilyJoin 家族申请
	 * @return 家族申请集合
	 */
	public List<LiveFamilyJoin> selectLiveFamilyJoinList(LiveFamilyJoin liveFamilyJoin);

	/**
	 * 新增家族申请
	 *
	 * @param liveFamilyJoin 家族申请
	 * @return 结果
	 */
	public int insertLiveFamilyJoin(LiveFamilyJoin liveFamilyJoin);

	/**
	 * 修改家族申请
	 *
	 * @param liveFamilyJoin 家族申请
	 * @return 结果
	 */
	public int updateLiveFamilyJoin(LiveFamilyJoin liveFamilyJoin);

	/**
	 * 删除家族申请
	 *
	 * @param id 家族申请ID
	 * @return 结果
	 */
	public int deleteLiveFamilyJoinById(String id);

	/**
	 * 批量删除家族申请
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLiveFamilyJoinByIds(String[] ids );
}
