package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.ConfigRecommend;

/**
 * 推广设置Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IConfigRecommendService {
	/**
	 * 查询推广设置
	 *
	 * @param id 推广设置ID
	 * @return 推广设置
	 */
	public ConfigRecommend selectConfigRecommendById(String id);

	/**
	 * 查询推广设置列表
	 *
	 * @param configRecommend 推广设置
	 * @return 推广设置集合
	 */
	public List<ConfigRecommend> selectConfigRecommendList(ConfigRecommend configRecommend);

	/**
	 * 新增推广设置
	 *
	 * @param configRecommend 推广设置
	 * @return 结果
	 */
	public int insertConfigRecommend(ConfigRecommend configRecommend);

	/**
	 * 修改推广设置
	 *
	 * @param configRecommend 推广设置
	 * @return 结果
	 */
	public int updateConfigRecommend(ConfigRecommend configRecommend);

	/**
	 * 批量删除推广设置
	 *
	 * @param ids 需要删除的推广设置ID
	 * @return 结果
	 */
	public int deleteConfigRecommendByIds(String[] ids );

	/**
	 * 删除推广设置信息
	 *
	 * @param id 推广设置ID
	 * @return 结果
	 */
	public int deleteConfigRecommendById(String id);
}
