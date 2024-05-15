package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.MessageOnSite;

/**
 * 站内信息Service接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface IMessageOnSiteService {
	/**
	 * 查询站内信息
	 *
	 * @param id 站内信息ID
	 * @return 站内信息
	 */
	public MessageOnSite selectMessageOnSiteById(String id);

	/**
	 * 查询站内信息列表
	 *
	 * @param messageOnSite 站内信息
	 * @return 站内信息集合
	 */
	public List<MessageOnSite> selectMessageOnSiteList(MessageOnSite messageOnSite);

	/**
	 * 新增站内信息
	 *
	 * @param messageOnSite 站内信息
	 * @return 结果
	 */
	public int insertMessageOnSite(MessageOnSite messageOnSite);
	public int insertMultipleMessageOnSite(MessageOnSite messageOnSite);

	/**
	 * 修改站内信息
	 *
	 * @param messageOnSite 站内信息
	 * @return 结果
	 */
	public int updateMessageOnSite(MessageOnSite messageOnSite);

	/**
	 * 批量删除站内信息
	 *
	 * @param ids 需要删除的站内信息ID
	 * @return 结果
	 */
	public int deleteMessageOnSiteByIds(String[] ids );

	/**
	 * 删除站内信息信息
	 *
	 * @param id 站内信息ID
	 * @return 结果
	 */
	public int deleteMessageOnSiteById(String id);
}
