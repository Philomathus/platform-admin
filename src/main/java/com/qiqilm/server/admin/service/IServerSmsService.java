package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ServerSms;

import java.util.List;

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
	public ServerSms selectServerSmsById( Long id );

	/**
	 * 查询SMS短信服务配置列表
	 *
	 * @param serverSms SMS短信服务配置
	 * @return SMS短信服务配置集合
	 */
	public List<ServerSms> selectServerSmsList( ServerSms serverSms );

	/**
	 * 新增SMS短信服务配置
	 *
	 * @param serverSms SMS短信服务配置
	 * @return 结果
	 */
	public int insertServerSms( ServerSms serverSms );

	/**
	 * 修改SMS短信服务配置
	 *
	 * @param serverSms SMS短信服务配置
	 * @return 结果
	 */
	public int updateServerSms( ServerSms serverSms );

	/**
	 * 批量删除SMS短信服务配置
	 *
	 * @param ids 需要删除的SMS短信服务配置ID
	 * @return 结果
	 */
	public int deleteServerSmsByIds( Long[] ids );

	/**
	 * 删除SMS短信服务配置信息
	 *
	 * @param id SMS短信服务配置ID
	 * @return 结果
	 */
	public int deleteServerSmsById( Long id );

	/**
	 * 激活SMS短信服务配置信息
	 *
	 * @param id SMS短信服务配置ID
	 * @return 结果
	 */
	int effect( long id );

	/**
	 * 取消激活SMS短信服务配置信息
	 *
	 * @param id SMS短信服务配置ID
	 * @return 结果
	 */
	int noEffect( long id );

	/**
	 * 测试SMS短信服务配置信息
	 *
	 * @param id     SMS短信服务配置ID
	 * @param mobile 手机号
	 * @return 结果
	 */
	AjaxResult smsTest( long id, String mobile );

}
