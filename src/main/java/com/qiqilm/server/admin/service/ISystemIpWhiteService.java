package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.SystemIpWhite;

/**
 * IP白名单Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface ISystemIpWhiteService {
	/**
	 * 查询IP白名单
	 *
	 * @param ipId IP白名单ID
	 * @return IP白名单
	 */
	public SystemIpWhite selectSystemIpWhiteById(String ipId);

	/**
	 * 查询IP白名单列表
	 *
	 * @param systemIpWhite IP白名单
	 * @return IP白名单集合
	 */
	public List<SystemIpWhite> selectSystemIpWhiteList(SystemIpWhite systemIpWhite);

	/**
	 * 新增IP白名单
	 *
	 * @param systemIpWhite IP白名单
	 * @return 结果
	 */
	public int insertSystemIpWhite(SystemIpWhite systemIpWhite);

	/**
	 * 修改IP白名单
	 *
	 * @param systemIpWhite IP白名单
	 * @return 结果
	 */
	public int updateSystemIpWhite(SystemIpWhite systemIpWhite);

	/**
	 * 批量删除IP白名单
	 *
	 * @param ipIds 需要删除的IP白名单ID
	 * @return 结果
	 */
	public int deleteSystemIpWhiteByIds(String[] ipIds );

	/**
	 * 删除IP白名单信息
	 *
	 * @param ipId IP白名单ID
	 * @return 结果
	 */
	public int deleteSystemIpWhiteById(String ipId);

	/**
	 * IP白名单判断重复
	 *
	 * @param IpAddress IP白名单IpAddress
	 * @return 结果
	 */
	int exists(String IpAddress);
}
