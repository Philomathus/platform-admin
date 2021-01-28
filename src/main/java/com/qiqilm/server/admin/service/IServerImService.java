package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.ServerIm;

/**
 * IM即时通讯服务配置Service接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface IServerImService {
	/**
	 * 查询IM即时通讯服务配置
	 *
	 * @param id IM即时通讯服务配置ID
	 * @return IM即时通讯服务配置
	 */
	public ServerIm selectServerImById(Long id);

	/**
	 * 查询IM即时通讯服务配置列表
	 *
	 * @param serverIm IM即时通讯服务配置
	 * @return IM即时通讯服务配置集合
	 */
	public List<ServerIm> selectServerImList(ServerIm serverIm);

	/**
	 * 新增IM即时通讯服务配置
	 *
	 * @param serverIm IM即时通讯服务配置
	 * @return 结果
	 */
	public int insertServerIm(ServerIm serverIm);

	/**
	 * 修改IM即时通讯服务配置
	 *
	 * @param serverIm IM即时通讯服务配置
	 * @return 结果
	 */
	public int updateServerIm(ServerIm serverIm);

	/**
	 * 批量删除IM即时通讯服务配置
	 *
	 * @param ids 需要删除的IM即时通讯服务配置ID
	 * @return 结果
	 */
	public int deleteServerImByIds(Long[] ids );

	/**
	 * 删除IM即时通讯服务配置信息
	 *
	 * @param id IM即时通讯服务配置ID
	 * @return 结果
	 */
	public int deleteServerImById(Long id);
}
