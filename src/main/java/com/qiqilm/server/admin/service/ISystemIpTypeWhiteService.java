package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.SystemIpTypeWhite;

import java.util.List;

/**
 * IP黑名单或反作弊禁言Service接口
 *
 * @author 77tv
 * @date 2021-07-12
 */
public interface ISystemIpTypeWhiteService {
	/**
	 * 查询IP黑名单或反作弊禁言
	 *
	 * @param id IP黑名单或反作弊禁言ID
	 * @return IP黑名单或反作弊禁言
	 */
	public SystemIpTypeWhite selectSystemIpTypeWhiteById(String id);

	/**
	 * 查询IP黑名单或反作弊禁言列表
	 *
	 * @param systemIpTypeWhite IP黑名单或反作弊禁言
	 * @return IP黑名单或反作弊禁言集合
	 */
	public List<SystemIpTypeWhite> selectSystemIpTypeWhiteList(SystemIpTypeWhite systemIpTypeWhite);

	/**
	 * 新增IP黑名单或反作弊禁言
	 *
	 * @param systemIpTypeWhite IP黑名单或反作弊禁言
	 * @return 结果
	 */
	public int insertSystemIpTypeWhite(SystemIpTypeWhite systemIpTypeWhite);

	/**
	 * 修改IP黑名单或反作弊禁言
	 *
	 * @param systemIpTypeWhite IP黑名单或反作弊禁言
	 * @return 结果
	 */
	public int updateSystemIpTypeWhite(SystemIpTypeWhite systemIpTypeWhite);

	/**
	 * 批量删除IP黑名单或反作弊禁言
	 *
	 * @param ids 需要删除的IP黑名单或反作弊禁言ID
	 * @return 结果
	 */
	public int deleteSystemIpTypeWhiteByIds(String[] ids );

	/**
	 * 删除IP黑名单或反作弊禁言信息
	 *
	 * @param id IP黑名单或反作弊禁言ID
	 * @return 结果
	 */
	public int deleteSystemIpTypeWhiteById(String id);

	int exists(String value);
}
