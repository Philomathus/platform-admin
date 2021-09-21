package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.ChatComplaint;

/**
 * 客服投诉Mapper接口
 *
 * @author 77tv
 * @date 2021-09-10
 */
public interface ChatComplaintMapper {
	/**
	 * 查询客服投诉
	 *
	 * @param id 客服投诉ID
	 * @return 客服投诉
	 */
	public ChatComplaint selectChatComplaintById(Long id);

	/**
	 * 查询客服投诉列表
	 *
	 * @param chatComplaint 客服投诉
	 * @return 客服投诉集合
	 */
	public List<ChatComplaint> selectChatComplaintList(ChatComplaint chatComplaint);

	/**
	 * 新增客服投诉
	 *
	 * @param chatComplaint 客服投诉
	 * @return 结果
	 */
	public int insertChatComplaint(ChatComplaint chatComplaint);

	/**
	 * 修改客服投诉
	 *
	 * @param chatComplaint 客服投诉
	 * @return 结果
	 */
	public int updateChatComplaint(ChatComplaint chatComplaint);

	/**
	 * 删除客服投诉
	 *
	 * @param id 客服投诉ID
	 * @return 结果
	 */
	public int deleteChatComplaintById(Long id);

	/**
	 * 批量删除客服投诉
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteChatComplaintByIds(Long[] ids );
}
