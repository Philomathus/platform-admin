package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.MessageGameNotice;

/**
 * 游戏公告Service接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface IMessageGameNoticeService {
	/**
	 * 查询游戏公告
	 *
	 * @param id 游戏公告ID
	 * @return 游戏公告
	 */
	public MessageGameNotice selectMessageGameNoticeById(String id);

	/**
	 * 查询游戏公告列表
	 *
	 * @param messageGameNotice 游戏公告
	 * @return 游戏公告集合
	 */
	public List<MessageGameNotice> selectMessageGameNoticeList(MessageGameNotice messageGameNotice);

	/**
	 * 新增游戏公告
	 *
	 * @param messageGameNotice 游戏公告
	 * @return 结果
	 */
	public int insertMessageGameNotice(MessageGameNotice messageGameNotice);

	/**
	 * 修改游戏公告
	 *
	 * @param messageGameNotice 游戏公告
	 * @return 结果
	 */
	public int updateMessageGameNotice(MessageGameNotice messageGameNotice);

	/**
	 * 批量删除游戏公告
	 *
	 * @param ids 需要删除的游戏公告ID
	 * @return 结果
	 */
	public int deleteMessageGameNoticeByIds(String[] ids );

	/**
	 * 删除游戏公告信息
	 *
	 * @param id 游戏公告ID
	 * @return 结果
	 */
	public int deleteMessageGameNoticeById(String id);
}
