package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.MessageSystemNotice;

/**
 * 系统公告Service接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface IMessageSystemNoticeService {
	/**
	 * 查询系统公告
	 *
	 * @param id 系统公告ID
	 * @return 系统公告
	 */
	public MessageSystemNotice selectMessageSystemNoticeById(String id);

	/**
	 * 查询系统公告列表
	 *
	 * @param messageSystemNotice 系统公告
	 * @return 系统公告集合
	 */
	public List<MessageSystemNotice> selectMessageSystemNoticeList(MessageSystemNotice messageSystemNotice);

	/**
	 * 新增系统公告
	 *
	 * @param messageSystemNotice 系统公告
	 * @return 结果
	 */
	public int insertMessageSystemNotice(MessageSystemNotice messageSystemNotice);

	/**
	 * 修改系统公告
	 *
	 * @param messageSystemNotice 系统公告
	 * @return 结果
	 */
	public int updateMessageSystemNotice(MessageSystemNotice messageSystemNotice);

	/**
	 * 批量删除系统公告
	 *
	 * @param ids 需要删除的系统公告ID
	 * @return 结果
	 */
	public int deleteMessageSystemNoticeByIds(String[] ids );

	/**
	 * 删除系统公告信息
	 *
	 * @param id 系统公告ID
	 * @return 结果
	 */
	public int deleteMessageSystemNoticeById(String id);
}
