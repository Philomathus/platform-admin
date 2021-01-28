package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.ServerSms;

/**
 * SMS短信服务配置Service接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface IServerSmsService {
	/**
	 * 查询SMS短信服务配置
	 *
	 * @param id SMS短信服务配置ID
	 * @return SMS短信服务配置
	 */
	public ServerSms selectServerSmsById(Long id);

	/**
	 * 查询SMS短信服务配置列表
	 *
	 * @param serverSms SMS短信服务配置
	 * @return SMS短信服务配置集合
	 */
	public List<ServerSms> selectServerSmsList(ServerSms serverSms);

	/**
	 * 新增SMS短信服务配置
	 *
	 * @param serverSms SMS短信服务配置
	 * @return 结果
	 */
	public int insertServerSms(ServerSms serverSms);

	/**
	 * 修改SMS短信服务配置
	 *
	 * @param serverSms SMS短信服务配置
	 * @return 结果
	 */
	public int updateServerSms(ServerSms serverSms);

	/**
	 * 批量删除SMS短信服务配置
	 *
	 * @param ids 需要删除的SMS短信服务配置ID
	 * @return 结果
	 */
	public int deleteServerSmsByIds(Long[] ids );

	/**
	 * 删除SMS短信服务配置信息
	 *
	 * @param id SMS短信服务配置ID
	 * @return 结果
	 */
	public int deleteServerSmsById(Long id);
}
